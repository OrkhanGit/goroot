package com.gpsroot.qr;

import com.gpsroot.exception.AttendanceCalculationException;
import com.gpsroot.exception.InvalidQrException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

import java.time.*;
import java.util.Base64;
import java.util.List;

@Service
public class AttendanceService {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.qr-ttl-seconds}")
    private long qrTtlSeconds; // məsələn 30

    @Value("${app.office-lat}")
    private double officeLat;

    @Value("${app.office-lng}")
    private double officeLng;

    @Value("${app.allowed-radius-meters}")
    private double allowedRadiusMeters; // məsələn 150

    private final StringRedisTemplate redisTemplate;
    private final AttendanceRepository attendanceRepository;

    private static final ZoneId ZONE = ZoneId.of("Asia/Baku");
    private static final LocalTime WORK_START = LocalTime.of(9, 0);
    private static final LocalTime WORK_END = LocalTime.of(18, 0);
    private static final LocalTime LUNCH_START = LocalTime.of(13, 0);
    private static final LocalTime LUNCH_END = LocalTime.of(14, 0);

    public AttendanceService(
            StringRedisTemplate redisTemplate,
            AttendanceRepository attendanceRepository
    ) {
        this.redisTemplate = redisTemplate;
        this.attendanceRepository = attendanceRepository;
    }

    public ScanResult processScan(ScanRequest request) {

        // --- 1. İmza yoxlaması ---
        String rawPayload = request.officeId() + "|" + request.issuedAt() + "|" + request.nonce();
        String expectedSignature = signHmac(rawPayload, secretKey);
        if (!expectedSignature.equals(request.signature())) {
            throw new InvalidQrException("QR imzası uyğun deyil");
        }

        // --- 2. TTL yoxlaması ---
        long now = System.currentTimeMillis() / 1000;
        if (now - request.issuedAt() > qrTtlSeconds) {
            throw new InvalidQrException("QR-in vaxtı bitib, yenidən skan edin");
        }

        // --- 3. Təkrar istifadə yoxlaması (Redis) ---
        String redisKey = "qr_nonce:" + request.nonce();
        Boolean isNew = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "used", Duration.ofSeconds(qrTtlSeconds));
        if (Boolean.FALSE.equals(isNew)) {
            throw new InvalidQrException("Bu QR artıq istifadə olunub");
        }

        // --- 4. GPS yoxlaması ---
        double distance = haversineDistance(
                officeLat, officeLng,
                request.gpsLat(), request.gpsLng()
        );
        if (distance > allowedRadiusMeters) {
            throw new InvalidQrException("Siz ofis ərazisində deyilsiniz");
        }

        // --- 5. Giriş/Çıxış məntiqi ---
        // Günün başlanğıcını təyin edirik (00:00, Bakı vaxtı ilə)
        ZoneId zone = ZoneId.of("Asia/Baku");
        Instant startOfToday = LocalDate.now(zone).atStartOfDay(zone).toInstant();

        // Yalnız BU GÜNƏ aid sonuncu qeydi axtarırıq
        var lastLogToday = attendanceRepository
                .findTopByEmployeeIdAndTimestampAfterOrderByTimestampDesc(
                        request.employeeId(), startOfToday);

        // Bu günə aid qeyd yoxdursa VƏ YA sonuncusu "out"-dursa → "in"
        // Bu günə aid sonuncusu "in"-dirsə → "out"
        String type = (lastLogToday.isPresent() && lastLogToday.get().getType().equals("in"))
                ? "out" : "in";

        AttendanceLog log = new AttendanceLog();
        log.setEmployeeId(request.employeeId());
        log.setOfficeId(request.officeId());
        log.setType(type);
        log.setTimestamp(Instant.now());
        attendanceRepository.save(log);

        return new ScanResult(type, log.getTimestamp());
    }

    // Haversine formula — iki GPS nöqtəsi arasındakı məsafə (metrlə)
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // yer radiusu, metr
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String signHmac(String data, String key) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
            );
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC hesablama xətası", e);
        }
    }

    /**
     * Konkret gün üçün işçinin işlədiyi ümumi vaxtı hesablayır.
     * 09:00-18:00 arası, nahar fasiləsi (13:00-14:00) çıxılmaqla.
     */
    public Duration calculateWorkedHours(String employeeId, LocalDate date) {
        Instant startOfDay = date.atStartOfDay(ZONE).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(ZONE).toInstant();

        List<AttendanceLog> logs = attendanceRepository
                .findByEmployeeIdAndTimestampBetweenOrderByTimestampAsc(employeeId, startOfDay, endOfDay);

        Duration total = Duration.ZERO;
        Instant pendingIn = null;

        for (AttendanceLog log : logs) {
            if ("in".equals(log.getType())) {
                if (pendingIn != null) {
                    // ard-arda iki "in" gəlirsə - deməli əvvəlkinin çıxışı yoxdur
                    throw new AttendanceCalculationException(
                            "İşçinin (ID: " + employeeId + ") " + date + " tarixli çıxışı yoxdur");
                }
                pendingIn = log.getTimestamp();
            } else if ("out".equals(log.getType())) {
                if (pendingIn == null) {
                    // "in" olmadan "out" gəlibsə - girişi yoxdur
                    throw new AttendanceCalculationException(
                            "İşçinin (ID: " + employeeId + ") " + date + " tarixli girişi yoxdur");
                }
                total = total.plus(calculateSegment(pendingIn, log.getTimestamp(), date));
                pendingIn = null;
            }
        }

        // dövr sonunda hələ bağlanmamış "in" qalıbsa - çıxışı yoxdur
        if (pendingIn != null) {
            throw new AttendanceCalculationException(
                    "İşçinin (ID: " + employeeId + ") " + date + " tarixli çıxışı yoxdur");
        }

        return total;
    }

    /**
     * Bir giriş-çıxış cütünü iş saatları (09-18) daxilinə sıxışdırır
     * və nahar fasiləsi ilə üst-üstə düşən hissəni çıxır.
     */
    private Duration calculateSegment(Instant checkIn, Instant checkOut, LocalDate date) {
        Instant workStart = date.atTime(WORK_START).atZone(ZONE).toInstant();
        Instant workEnd = date.atTime(WORK_END).atZone(ZONE).toInstant();
        Instant lunchStart = date.atTime(LUNCH_START).atZone(ZONE).toInstant();
        Instant lunchEnd = date.atTime(LUNCH_END).atZone(ZONE).toInstant();

        Instant start = checkIn.isBefore(workStart) ? workStart : checkIn;
        Instant end = checkOut.isAfter(workEnd) ? workEnd : checkOut;

        if (!start.isBefore(end)) {
            return Duration.ZERO;
        }

        Duration segment = Duration.between(start, end);

        Instant overlapStart = start.isBefore(lunchStart) ? lunchStart : start;
        Instant overlapEnd = end.isAfter(lunchEnd) ? lunchEnd : end;

        if (overlapStart.isBefore(overlapEnd)) {
            segment = segment.minus(Duration.between(overlapStart, overlapEnd));
        }

        return segment;
    }
}
