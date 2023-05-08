package com.example.gpstracking;
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

    // Геттери та сеттери для полів зберігання даних

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
