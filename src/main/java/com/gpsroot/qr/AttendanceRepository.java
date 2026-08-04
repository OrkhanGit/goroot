package com.gpsroot.qr;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {
    Optional<AttendanceLog> findTopByEmployeeIdOrderByTimestampDesc(String employeeId);

    Optional<List<AttendanceLog>> findByEmployeeId(String employeeId);


}
