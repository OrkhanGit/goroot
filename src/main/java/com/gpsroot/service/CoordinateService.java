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
    private final MapLinkParserService mapLinkParserService;

    public CoordinateDto getCoordinateDto(Long storeName) {
        Coordinate coordinate = coordinateRepository.getCoordinateByStoreNumberAndIsActiveTrue(storeName)
                .orElseThrow(() -> new NotFoundException("coordinate not found"));
        return coordinateMapper.toCoordinateDto(coordinate);
    }

    public void deleteStore(Long storeName) {
        Coordinate store = coordinateRepository.getCoordinateByStoreNumberAndIsActiveTrue(storeName)
                .orElseThrow(() -> new NotFoundException("coordinate not found"));
        store.setActive(false);
        coordinateRepository.save(store);
    }

    public CoordinateDto createCoordinate(CoordinateDto coordinateDto) {

        Optional<Coordinate> existing = coordinateRepository.getCoordinateByStoreNumber(coordinateDto.getStoreNumber());
        if (existing.isPresent()) {
            throw new ConflictException("Store already exists");
        }

        // Əgər link göndərilibsə və lat/lng boşdursa, linkdən koordinatı çıxar
        Double latitude = coordinateDto.getLatitude();
        Double longitude = coordinateDto.getLongitude();

        if ((latitude == null || longitude == null) && coordinateDto.getMapLink() != null) {
            double[] coords = mapLinkParserService.extractCoordinates(coordinateDto.getMapLink())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Linkdən koordinat tapılmadı. Zəhmət olmasa əl ilə daxil edin."));
            latitude = coords[0];
            longitude = coords[1];
        }

        Coordinate coordinate = new Coordinate();
        coordinate.setAddress(coordinateDto.getAddress());
        coordinate.setStoreNumber(coordinateDto.getStoreNumber());
        coordinate.setLatitude(latitude);
        coordinate.setLongitude(longitude);
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

    public CoordinateDto updateCoordinate(Long storeName, CoordinateDto coordinateDto) {

        Coordinate coordinate = coordinateRepository.getCoordinateByStoreNumberAndIsActiveTrue(storeName)
                .orElseThrow(() -> new NotFoundException("coordinate not found"));

        Double latitude = coordinateDto.getLatitude();
        Double longitude = coordinateDto.getLongitude();

        if ((latitude == null || longitude == null) && coordinateDto.getMapLink() != null) {
            double[] coords = mapLinkParserService.extractCoordinates(coordinateDto.getMapLink())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Linkdən koordinat tapılmadı. Zəhmət olmasa əl ilə daxil edin."));
            latitude = coords[0];
            longitude = coords[1];
        }

        coordinate.setStoreNumber(coordinateDto.getStoreNumber());
        coordinate.setLatitude(latitude);
        coordinate.setLongitude(longitude);
        coordinate.setAddress(coordinateDto.getAddress());
        coordinate.setActive(true);
        coordinate.setUpdatedAt(LocalDateTime.now());

        coordinateRepository.save(coordinate);

        return coordinateMapper.toCoordinateDto(coordinate);
    }

    public Long countStores() {

        return coordinateRepository.countAllByIsActiveTrue();

    }
}