package com.googlemaps.template.myapplication.services;


import com.google.gson.JsonElement;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;


public interface YandexMapApiService {

    @GET("/1.x")
    Call<JsonElement> locations(@QueryMap Map<String, String> options);

}
