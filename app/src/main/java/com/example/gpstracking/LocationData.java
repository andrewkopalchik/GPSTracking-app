package com.example.gpstracking;

import java.io.Serializable;

public class LocationData implements Serializable {
    private double latitude;
    private double longitude;
    private long timestamp;

    public LocationData(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
