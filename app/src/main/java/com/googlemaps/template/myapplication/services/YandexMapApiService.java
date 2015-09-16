package com.googlemaps.template.myapplication.services;


import android.net.Uri;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpRequest;

import java.util.Formatter;
import java.util.Locale;


public final class YandexMapApiService {

    private final String URI = "https://geocode-maps.yandex.ru/1.x/?geocode=%f,%f&format=json&rspn=0&results=%d&kind=locality";

    private static YandexMapApiService yandexMapApiService;

    private YandexMapApiService(){
        super();
    }

    public static YandexMapApiService getInstance(){
        if(yandexMapApiService==null) {
            yandexMapApiService = new YandexMapApiService();
        }
        return yandexMapApiService;
    }


    public void getNearLocations(double latitude, double longitude, int count, AsyncHttpClient.JSONObjectCallback jsonObjectCallback){

        Formatter fmt = new Formatter();

        String url = fmt.format(Locale.US,URI,longitude,latitude, count).toString();
        Uri uri = Uri.parse(url);

        AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(uri, "GET");
        AsyncHttpClient.getDefaultInstance().executeJSONObject(asyncHttpRequest, jsonObjectCallback);
    }


}
