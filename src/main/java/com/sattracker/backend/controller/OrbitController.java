package com.sattracker.backend.controller;

import com.sattracker.backend.model.SatellitePositionResponse;
import com.sattracker.backend.model.Tle;
import com.sattracker.backend.repository.TleRepository;
import com.sattracker.backend.service.OrbitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orbit")
@CrossOrigin
public class OrbitController {

    private final TleRepository tleRepository;
    private final OrbitService orbitService;

    public OrbitController(TleRepository tleRepository, OrbitService orbitService) {
        this.tleRepository = tleRepository;
        this.orbitService = orbitService;
    }

    @GetMapping("/pos/{name}")
    public SatellitePositionResponse getPosition(@PathVariable String name) {

        // findByName returns List<Tle>
        List<Tle> tles = tleRepository.findByName(name);

        if (tles == null || tles.isEmpty()) {
            throw new RuntimeException("TLE not found for satellite: " + name);
        }

        // use the first matching TLE
        Tle tle = tles.get(0);

        return orbitService.computePosition(tle);
    }
}
