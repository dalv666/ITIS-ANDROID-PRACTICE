package com.googlemaps.template.myapplication.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class GeoObjectVO implements Parcelable {


    private String mName;

    private String mPos;

    private LatLng mLocation;


    protected GeoObjectVO(Parcel in) {
        mName = in.readString();
        mPos = in.readString();
        mLocation = in.readParcelable(LatLng.class.getClassLoader());
    }


    public String getPos() {
        return mPos;
    }

    public void setPos(String pos) {
        this.mPos = pos;
    }


    private LatLng stringToLatLng(String position) {
        int sepIndex = position.indexOf(" ");
        Double latitude = Double.valueOf(position.substring(0, sepIndex));
        Double longitude = Double.valueOf(position.substring(sepIndex));
        return new LatLng(longitude, latitude);

    }

    public GeoObjectVO(String name, String location) {
        this.mPos = location;
        this.mLocation = stringToLatLng(location);
        this.mName = name;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public LatLng getLocation() {
        return stringToLatLng(mPos);
    }

    public void setLocation(LatLng location) {
        this.mLocation = location;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPos);
        dest.writeParcelable(mLocation, flags);
    }

    public static final Creator<GeoObject> CREATOR = new Creator<GeoObject>() {
        @Override
        public GeoObject createFromParcel(Parcel in) {
            return new GeoObject(in);
        }

        @Override
        public GeoObject[] newArray(int size) {
            return new GeoObject[size];
        }
    };

}
