package com.googlemaps.template.myapplication.services;

import com.google.gson.JsonElement;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;


public interface GoogleApiService {

    @GET("/maps/api/directions/json")
    Call<JsonElement> directions(@QueryMap Map<String, String> options);

}
