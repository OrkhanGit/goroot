package com.gpsroot.walk.repository;

import com.gpsroot.walk.model.WalkSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WalkSessionRepository extends JpaRepository<WalkSession, Long> {

    List<WalkSession> findAllByOrderByCreatedAtDesc();

    List<WalkSession> findAllByEmployeeIdAndStartedAtBetweenOrderByStartedAtDesc(
            String employeeId,
            LocalDateTime start,
            LocalDateTime end
    );

}
