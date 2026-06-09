package com.massinond.menon.goatlocator;

public class Goat {
    public int id;
    public String photoPath;
    public double latitude;
    public double longitude;
    public String date;

    public Goat(int id, String photoPath, double latitude, double longitude, String date) {
        this.id = id;
        this.photoPath = photoPath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }
}