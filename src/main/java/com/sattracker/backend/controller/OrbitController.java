package com.sattracker.backend.controller;

import com.sattracker.backend.model.*;
import com.sattracker.backend.repository.*;
import com.sattracker.backend.service.OrbitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orbit")
@CrossOrigin
public class OrbitController {

    private final TleRepository tleRepository;
    private final GroundStationRepository groundStationRepository;
    private final OrbitService orbitService;

    public OrbitController(
            TleRepository tleRepository,
            GroundStationRepository groundStationRepository,
            OrbitService orbitService
    ) {
        this.tleRepository = tleRepository;
        this.groundStationRepository = groundStationRepository;
        this.orbitService = orbitService;
    }

    @GetMapping("/pos/{satellite}")
    public SatellitePositionResponse getPosition(
            @PathVariable String satellite,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country
    ) {

        // TLE
        List<Tle> tles = tleRepository.findByNameIgnoreCase(satellite);
        if (tles.isEmpty()) {
            throw new RuntimeException("Satellite TLE not found");
        }

        Tle tle = tles.get(0);

        // Ground station
        Optional<GroundStation> stationOpt = Optional.empty();

        if (city != null) {
            stationOpt = groundStationRepository.findByNameIgnoreCase(city);
        } else if (country != null) {
            stationOpt = groundStationRepository.findFirstByCountryIgnoreCase(country);
        }

        GroundStation station = stationOpt.orElseThrow(
                () -> new RuntimeException("Ground station not found")
        );

        return orbitService.computePosition(tle, station);
    }
}
