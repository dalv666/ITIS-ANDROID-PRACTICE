package com.googlemaps.template.myapplication.loader;


import android.content.Context;

import com.google.gson.JsonElement;
import com.googlemaps.template.myapplication.response.GeoObjectsResponse;
import com.googlemaps.template.myapplication.util.RequestResult;
import com.googlemaps.template.myapplication.response.Response;
import com.googlemaps.template.myapplication.services.YandexMapApiService;
import com.googlemaps.template.myapplication.util.ApiFactory;

import java.io.IOException;
import java.util.HashMap;

import retrofit.Call;

public class GeoObjectsLoader extends BaseLoader {

    private final String mGps;

    private final YandexMapApiService mAirportsService;

    private final Context mContext;

    public GeoObjectsLoader(Context context, String gps) {
        super(context);
        this.mContext = context;
        mGps = gps;
        mAirportsService = ApiFactory.getAirportsService();
    }


    @Override
    protected Response apiCall() throws IOException {
        YandexMapApiService service = ApiFactory.getAirportsService();
        HashMap<String,String> param = new HashMap<>();
        param.put("geocode",mGps);
        param.put("format","json");
        param.put("rspn","0");
        param.put("results","10");
        param.put("kind","locality");
        Call<JsonElement> call = service.locations(param);
        JsonElement body = call.execute().body();

        return new GeoObjectsResponse()
                .setRequestResult(RequestResult.SUCCESS)
                .setAnswer(body);
    }

}
