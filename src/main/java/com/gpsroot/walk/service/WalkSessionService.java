package com.gpsroot.walk.service;

import com.gpsroot.walk.enums.WalkType;
import com.gpsroot.walk.mapper.WalkSessionMapper;
import com.gpsroot.walk.model.*;
import com.gpsroot.walk.repository.WalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalkSessionService {

    private final WalkSessionRepository walkSessionRepository;
    private final WalkSessionMapper walkSessionMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String DATES_KEY_PREFIX = "walk:dates:"; // + type + ":" + employeeId

    public void createWalk(RequestWalkDto requestWalkDto, String employeeId) {

        WalkSession walkSession = new WalkSession();

        Duration duration = Duration.between(requestWalkDto.getStartedAt(), requestWalkDto.getEndedAt());

        walkSession.setEmployeeId(employeeId);
        walkSession.setActivityType(requestWalkDto.getActivityType());
        walkSession.setStartedAt(requestWalkDto.getStartedAt());
        walkSession.setEndedAt(requestWalkDto.getEndedAt());
        walkSession.setDurationMinutes(duration);
        walkSession.setDistanceMeters(requestWalkDto.getDistanceMeters());
        walkSession.setSteps(requestWalkDto.getSteps());
        walkSession.setCreatedAt(LocalDateTime.now());

        walkSessionRepository.save(walkSession);

        markDateAsHavingData(employeeId, WalkType.WALK, requestWalkDto.getStartedAt().toLocalDate());
    }

    public List<ViewWalkDto> viewWalk(String date, String employeeId) {

        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        return walkSessionRepository
                .findAllByEmployeeIdAndActivityTypeAndStartedAtBetweenOrderByStartedAtDesc(
                        employeeId,
                        WalkType.WALK,
                        start,
                        end
                )
                .stream()
                .map(walkSessionMapper::toviewWalkDto)
                .toList();
    }

    public void createBike(RequestBikeDto requestBikeDto, String employeeId) {

        WalkSession walkSession = new WalkSession();

        Duration duration = Duration.between(requestBikeDto.getStartedAt(), requestBikeDto.getEndedAt());

        walkSession.setEmployeeId(employeeId);
        walkSession.setActivityType(requestBikeDto.getActivityType());
        walkSession.setStartedAt(requestBikeDto.getStartedAt());
        walkSession.setEndedAt(requestBikeDto.getEndedAt());
        walkSession.setDurationMinutes(duration);
        walkSession.setDistanceMeters(requestBikeDto.getDistanceMeters());
        walkSession.setCreatedAt(LocalDateTime.now());

        walkSessionRepository.save(walkSession);

        markDateAsHavingData(employeeId, WalkType.BIKE, requestBikeDto.getStartedAt().toLocalDate());
    }

    public List<ViewBikeDto> viewBike(String date, String employeeId) {

        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        return walkSessionRepository
                .findAllByEmployeeIdAndActivityTypeAndStartedAtBetweenOrderByStartedAtDesc(
                        employeeId,
                        WalkType.BIKE,
                        start,
                        end
                )
                .stream()
                .map(walkSessionMapper::toviewBikeDto)
                .toList();
    }

    // MARK: - Dates-with-data (Redis, cache-aside)

    private void markDateAsHavingData(String employeeId, WalkType type, LocalDate date) {
        redisTemplate.opsForSet().add(
                redisKey(type, employeeId),
                date.toString()
        );
    }

    public Set<String> getWalkDates(String employeeId) {

        String key = redisKey(WalkType.WALK, employeeId);

        // Set heç vaxt yaradılmayıbsa (yeni istifadəçi ya köhnə data hələ Redis-ə köçməyib) — bir dəfə Postgres-dən doldur
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {

            List<LocalDate> datesFromDb = walkSessionRepository
                    .findDistinctDatesByEmployeeIdAndActivityType(employeeId, WalkType.WALK);

            if (!datesFromDb.isEmpty()) {
                Set<String> asStrings = datesFromDb.stream()
                        .map(LocalDate::toString)
                        .collect(Collectors.toSet());
                redisTemplate.opsForSet().add(key, asStrings.toArray(new String[0]));
            } else {
                // data yoxdursa belə, key-i boş yaradaq ki, hər dəfə Postgres-ə getməsin
                redisTemplate.opsForSet().add(key, "");
                redisTemplate.opsForSet().remove(key, "");
            }
        }

        Set<String> result = redisTemplate.opsForSet().members(key);
        return result != null ? result : Set.of();
    }

    private String redisKey(WalkType type, String employeeId) {
        return DATES_KEY_PREFIX + type.name() + ":" + employeeId;
    }
}