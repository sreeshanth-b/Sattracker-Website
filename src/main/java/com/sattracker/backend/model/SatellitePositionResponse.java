package com.sattracker.backend.model;

import lombok.Data;

@Data
public class SatellitePositionResponse {
    private double latitude;
    private double longitude;
    private double altitude;
    private double velocity;
    private String timestamp;
}
