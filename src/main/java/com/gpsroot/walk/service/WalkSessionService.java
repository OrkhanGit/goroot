package com.gpsroot.walk.service;

import com.gpsroot.walk.enums.WalkType;
import com.gpsroot.walk.mapper.WalkSessionMapper;
import com.gpsroot.walk.model.RequestWalkDto;
import com.gpsroot.walk.model.ViewWalkDto;
import com.gpsroot.walk.model.WalkSession;
import com.gpsroot.walk.repository.WalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkSessionService {

    private final WalkSessionRepository walkSessionRepository;
    private final WalkSessionMapper walkSessionMapper;

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
}
