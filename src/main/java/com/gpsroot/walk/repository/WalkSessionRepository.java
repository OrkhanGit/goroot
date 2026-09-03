package com.gpsroot.walk.repository;

import com.gpsroot.walk.enums.WalkType;
import com.gpsroot.walk.model.WalkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkSessionRepository extends JpaRepository<WalkSession, Long> {

    List<WalkSession> findAllByOrderByCreatedAtDesc();

    List<WalkSession> findAllByEmployeeIdAndActivityTypeAndStartedAtBetweenOrderByStartedAtDesc(
            String employeeId,
            WalkType type,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT DISTINCT CAST(w.startedAt AS date) FROM WalkSession w " +
            "WHERE w.employeeId = :employeeId AND w.activityType = :type")
    List<java.sql.Date> findDistinctDatesByEmployeeIdAndActivityType(
            @Param("employeeId") String employeeId,
            @Param("type") WalkType type
    );

}
