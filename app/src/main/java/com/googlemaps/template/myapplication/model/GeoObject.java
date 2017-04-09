package com.googlemaps.template.myapplication.model;


import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class GeoObject extends RealmObject {

    @SerializedName("name")
    private String name;

    @SerializedName("pos")
    private String pos;


    public GeoObject() {
        super();
    }


    public GeoObject(String name, String pos) {
        this.name = name;
        this.pos = pos;
    }

    public GeoObject(Parcel in) {
        this.name = in.readString();
        this.pos = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }
}
