package com.gpsroot.service;

import com.gpsroot.exception.ConflictException;
import com.gpsroot.exception.NotFoundException;
import com.gpsroot.mapper.CoordinateMapper;
import com.gpsroot.model.dto.CoordinateDto;
import com.gpsroot.model.entity.Coordinate;
import com.gpsroot.repository.CoordinateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CoordinateService {

    private final CoordinateRepository coordinateRepository;
    private final CoordinateMapper coordinateMapper;

    public CoordinateDto getCoordinateDto(Long storeName) {

        Coordinate coordinate = coordinateRepository.getCoordinateByStoreNumberAndIsActiveTrue(storeName)
                .orElseThrow(()-> new NotFoundException("coordinate not found"));

        return coordinateMapper.toCoordinateDto(coordinate);

    }

    public void deleteStore(Long storeName) {

        Coordinate store = coordinateRepository.getCoordinateByStoreNumberAndIsActiveTrue(storeName)
                .orElseThrow(()-> new NotFoundException("coordinate not found"));

        store.setActive(false);
        coordinateRepository.save(store);

    }

    public CoordinateDto createCoordinate(CoordinateDto coordinateDto) {

        Optional<Coordinate> storeNumber = coordinateRepository.getCoordinateByStoreNumber(coordinateDto.getStoreNumber());

        if (storeNumber.isPresent()) {
            throw new ConflictException("Store already exists");
        }
        Coordinate coordinate = new Coordinate();
        coordinate.setAddress(coordinateDto.getAddress());
        coordinate.setStoreNumber(coordinateDto.getStoreNumber());
        coordinate.setLatitude(coordinateDto.getLatitude());
        coordinate.setLongitude(coordinateDto.getLongitude());
        coordinate.setCreatedAt(LocalDateTime.now());
        coordinateRepository.save(coordinate);

        return coordinateMapper.toCoordinateDto(coordinate);

    }

    public List<CoordinateDto> getAllCoordinates() {

        return coordinateRepository.getAllByIsActiveTrue()
                .stream()
                .map(coordinateMapper::toCoordinateDto)
                .toList();

    }
}
