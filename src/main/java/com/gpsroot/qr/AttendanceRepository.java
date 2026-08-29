package com.gpsroot.qr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {
    Optional<AttendanceLog> findTopByEmployeeIdOrderByTimestampDesc(String employeeId);

    Optional<List<AttendanceLog>> findByEmployeeId(String employeeId);

    Optional<AttendanceLog> findTopByEmployeeIdAndTimestampAfterOrderByTimestampDesc(
            String employeeId, Instant after);

    @Query("SELECT DISTINCT a.employeeId FROM AttendanceLog a")
    List<String> findDistinctEmployeeIds();


}
