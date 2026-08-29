package com.gpsroot.controller;

import com.gpsroot.model.dto.CoordinateDto;
import com.gpsroot.service.CoordinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/gpsroot")
public class CoordinateController {

    private final CoordinateService coordinateService;

    @GetMapping("/coordinate/{storeName}")
    public ResponseEntity<CoordinateDto> getCoordinateDto(@PathVariable  Long storeName) {

        return ResponseEntity.ok().body(coordinateService.getCoordinateDto(storeName));

    }

    @DeleteMapping("/delete/{storeName}")
    public void deleteStore(@PathVariable  Long storeName) {

        coordinateService.deleteStore(storeName);

    }

    @PostMapping("/create")
    public ResponseEntity<CoordinateDto> createCoordinate(@RequestBody CoordinateDto coordinateDto){

        coordinateService.createCoordinate(coordinateDto);
        return ResponseEntity.ok().body(coordinateDto);

    }

    @GetMapping("/all")
    public ResponseEntity<List<CoordinateDto>> getAllCoordinates(){

        return ResponseEntity.ok().body(coordinateService.getAllCoordinates());

    }

    @PutMapping("/update/{storeName}")
    public ResponseEntity<CoordinateDto> updateCoordinate(@PathVariable Long storeName,
                                                          @RequestBody CoordinateDto coordinateDto){

        return ResponseEntity.ok(coordinateService.updateCoordinate(storeName,coordinateDto));

    }

    @GetMapping("/count")
    public ResponseEntity<Long> countStores(){

        return ResponseEntity.ok().body(coordinateService.countStores());

    }

}
