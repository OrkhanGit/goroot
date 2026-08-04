package com.gpsroot.qr;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResult> scan(@RequestBody ScanRequest request) {
        return ResponseEntity.ok(attendanceService.processScan(request));
    }
}