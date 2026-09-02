package com.gpsroot.walk.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class ViewWalkDto {

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Duration durationMinutes;
    private Double distanceMeters;
    private Long steps;


}
