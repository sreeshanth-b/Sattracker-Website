package com.sattracker.backend.controller;

import com.sattracker.backend.model.*;
import com.sattracker.backend.repository.*;
import com.sattracker.backend.service.OrbitService;
import org.springframework.web.bind.annotation.*;
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
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String utc
    ) {

        Tle tle = tleRepository
                .findByNameIgnoreCase(satellite)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Satellite TLE not found")
                );

        GroundStation station = resolveStation(city, country);

        return orbitService.computePosition(tle, station, utc);
    }

    @GetMapping("/pass/{satellite}")
    public SatellitePassResponse getNextPass(
            @PathVariable String satellite,
            @RequestParam String city
    ) {

        Tle tle = tleRepository
                .findByNameIgnoreCase(satellite)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Satellite TLE not found")
                );

        GroundStation station =
                groundStationRepository
                        .findByNameIgnoreCase(city)
                        .orElseThrow(() ->
                                new RuntimeException("City not found")
                        );

        return orbitService.computeNextPass(tle, station);
    }

    @GetMapping("/live/{satellite}")
    public RadarResponse getLiveRadar(
            @PathVariable String satellite,
            @RequestParam String city
    ) {

        Tle tle = tleRepository
                .findByNameIgnoreCase(satellite)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Satellite TLE not found")
                );

        GroundStation station =
                groundStationRepository
                        .findByNameIgnoreCase(city)
                        .orElseThrow(() ->
                                new RuntimeException("City not found")
                        );

        return orbitService.computeLiveRadar(tle, station);
    }

    private GroundStation resolveStation(String city, String country) {

        Optional<GroundStation> stationOpt = Optional.empty();

        if (city != null) {
            stationOpt =
                    groundStationRepository.findByNameIgnoreCase(city);
        } else if (country != null) {
            stationOpt =
                    groundStationRepository.findFirstByCountryIgnoreCase(country);
        }

        return stationOpt.orElseThrow(() ->
                new RuntimeException("Ground station not found")
        );
    }
}
