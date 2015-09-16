package com.googlemaps.template.myapplication.model;


public class GeoObject {

    private String location;
    private String name;


    public GeoObject(String location, String name) {
        this.location = location;
        this.name = name;
    }


    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
