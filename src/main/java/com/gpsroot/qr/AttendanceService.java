package com.gpsroot.qr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import javax.crypto.*;

import java.time.Duration;
import java.util.Base64;

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
        var lastLog = attendanceRepository
                .findTopByEmployeeIdOrderByTimestampDesc(request.employeeId());

        String type = (lastLog.isPresent() && lastLog.get().getType().equals("in"))
                ? "out" : "in";

        AttendanceLog log = new AttendanceLog();
        log.setEmployeeId(request.employeeId());
        log.setOfficeId(request.officeId());
        log.setType(type);
        log.setTimestamp(java.time.Instant.now());
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
}
