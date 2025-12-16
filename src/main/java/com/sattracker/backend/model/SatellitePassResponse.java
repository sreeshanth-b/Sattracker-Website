package com.sattracker.backend.model;

public class SatellitePassResponse {

    private String aos;
    private String los;
    private double maxElevation;
    private long durationSec;

    public String getAos() {
        return aos;
    }

    public void setAos(String aos) {
        this.aos = aos;
    }

    public String getLos() {
        return los;
    }

    public void setLos(String los) {
        this.los = los;
    }

    public double getMaxElevation() {
        return maxElevation;
    }

    public void setMaxElevation(double maxElevation) {
        this.maxElevation = maxElevation;
    }

    public long getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(long durationSec) {
        this.durationSec = durationSec;
    }
}
