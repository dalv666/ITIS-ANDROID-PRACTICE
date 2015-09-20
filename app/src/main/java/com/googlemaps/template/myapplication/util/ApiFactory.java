package com.googlemaps.template.myapplication.util;

import android.support.annotation.NonNull;

import com.googlemaps.template.myapplication.services.GoogleApiService;
import com.googlemaps.template.myapplication.services.YandexMapApiService;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class ApiFactory {


    private static final String API_ENDPOINT = "https://geocode-maps.yandex.ru";
    private static final String GOOGLE_API_ENDPOINT = "https://maps.googleapis.com/";

    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    private static final OkHttpClient CLIENT = new OkHttpClient();

    static {
        CLIENT.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    @NonNull
    public static YandexMapApiService getAirportsService() {
        return getRetrofit(API_ENDPOINT).create(YandexMapApiService.class);
    }

    @NonNull
    public static GoogleApiService getCoordinateService() {
        return getRetrofit(GOOGLE_API_ENDPOINT).create(GoogleApiService.class);
    }

    @NonNull
    private static Retrofit getRetrofit(String endPoint) {
        return new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(CLIENT)
                .build();
    }


}
