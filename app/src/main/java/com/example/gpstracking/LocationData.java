package com.example.gpstracking;

public class LocationData {

    private String deviceName;
    private double latitude;
    private double longitude;
    private long timestamp;




    // Required empty constructor for Firebase
    public LocationData() {
    }

    public LocationData(String deviceName, double latitude, double longitude, long timestamp) {
        this.deviceName = deviceName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }




}
