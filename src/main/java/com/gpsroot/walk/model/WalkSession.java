package com.gpsroot.walk.model;

import com.gpsroot.walk.enums.WalkType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "walk_session")
@Data
public class WalkSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;

    @Enumerated(EnumType.STRING)
    private WalkType activityType;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private Duration durationMinutes;

    private Double distanceMeters;

    private Long steps;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
