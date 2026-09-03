package com.gpsroot.qr;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

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


    @GetMapping("/worked-hours/{employeeId}")
    public ResponseEntity<String> getWorkedHours(
            @PathVariable String employeeId,
            @RequestParam(required = false) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now(ZoneId.of("Asia/Baku"));
        Duration duration = attendanceService.calculateWorkedHours(employeeId, targetDate);
        return ResponseEntity.ok(duration.toHours() + " saat " + duration.toMinutesPart() + " dəqiqə");
    }
}