package com.googlemaps.template.myapplication.model;


import com.google.android.gms.maps.model.LatLng;

public class GeoObject {

    private String name;

    private LatLng location;


    public GeoObject(String location, String name) {
        Double latitude = Double.valueOf(location.substring(0, 10));
        Double longitude = Double.valueOf(location.substring(10));
        this.location = new LatLng(longitude,latitude);
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
