package com.gpsroot.walk.model;

import com.gpsroot.walk.enums.WalkType;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class ViewWalkDto {

    private WalkType activityType;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Duration durationMinutes;
    private Double distanceMeters;
    private Long steps;


}
