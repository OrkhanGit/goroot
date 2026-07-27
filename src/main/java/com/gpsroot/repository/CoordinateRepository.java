package com.gpsroot.repository;

import com.gpsroot.model.entity.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoordinateRepository extends JpaRepository<Coordinate, Long> {

    Optional<Coordinate> getCoordinateByStoreNumberAndIsActiveTrue(Long storeNumber);

    Optional<Coordinate> getCoordinateByStoreNumber(Long storeNumber);

    List<Coordinate> getAllByIsActiveTrue();


}
