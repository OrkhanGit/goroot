package com.gpsroot.qr;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attendance_logs")
@Data
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;
    private String officeId;
    private String type; // "in" və ya "out"
    private java.time.Instant timestamp;
}

