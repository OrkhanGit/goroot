package com.gpsroot.walk.controller;

import com.gpsroot.walk.model.RequestBikeDto;
import com.gpsroot.walk.model.RequestWalkDto;
import com.gpsroot.walk.model.ViewBikeDto;
import com.gpsroot.walk.model.ViewWalkDto;
import com.gpsroot.walk.service.WalkSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/walkSession")
@RequiredArgsConstructor
public class WalkSessionController {

    private final WalkSessionService walkSessionService;

    @PostMapping("/createWalk")
    public ResponseEntity<String> createWalk(@RequestBody RequestWalkDto requestWalkDto,
                                             Authentication authentication) {

        String employeeId = authentication.getName();

        walkSessionService.createWalk(requestWalkDto,employeeId);

        return ResponseEntity.ok("Created walk session");

    }

    @GetMapping("/getAllWalk")
    public ResponseEntity<List<ViewWalkDto>> viewWalk(@RequestParam(required = false) String date,
                                                      Authentication authentication) {

        String employeeId = authentication.getName();

        return ResponseEntity.ok(walkSessionService.viewWalk(date, employeeId));
    }

    @PostMapping("/createBike")
    public ResponseEntity<String> createBike(@RequestBody RequestBikeDto requestBikeDto,
                                             Authentication authentication) {

        String employeeId = authentication.getName();

        walkSessionService.createBike(requestBikeDto,employeeId);

        return ResponseEntity.ok("Created bike session");

    }

    @GetMapping("/getAllBike")
    public ResponseEntity<List<ViewBikeDto>> viewBike(@RequestParam(required = false) String date,
                                                      Authentication authentication) {

        String employeeId = authentication.getName();

        return ResponseEntity.ok(walkSessionService.viewBike(date, employeeId));
    }

    @GetMapping("/getWalkDates")
    public ResponseEntity<Set<String>> getWalkDates(Authentication authentication) {

        String employeeId = authentication.getName();

        return ResponseEntity.ok(walkSessionService.getWalkDates(employeeId));
    }


}
