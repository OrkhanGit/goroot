package com.gpsroot.qr;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {
    java.util.Optional<AttendanceLog> findTopByEmployeeIdOrderByTimestampDesc(String employeeId);
}
