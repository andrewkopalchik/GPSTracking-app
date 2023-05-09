package com.example.gpstracking;

import androidx.annotation.NonNull;

import java.util.Date;

public class LocationData {
    private String deviceName;
    private String latitude;
    private String longitude;
    private long timestamp;

    public LocationData() {
    }

    public LocationData(String deviceName, String latitude, String longitude, long timestamp) {
        this.deviceName = deviceName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Device: " + deviceName + "\n" +
                "Lat: " + latitude + ", Lng: " + longitude + "\n" +
                "Time: " + new Date(timestamp).toString();
    }
}
