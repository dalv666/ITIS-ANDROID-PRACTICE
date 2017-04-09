package com.googlemaps.template.myapplication.loader;


import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonElement;
import com.googlemaps.template.myapplication.response.PolylineOptionsResponse;
import com.googlemaps.template.myapplication.util.ConvertUtil;
import com.googlemaps.template.myapplication.util.RequestResult;
import com.googlemaps.template.myapplication.response.Response;
import com.googlemaps.template.myapplication.database.GeoObjectHelper;
import com.googlemaps.template.myapplication.model.GeoObject;
import com.googlemaps.template.myapplication.model.GeoObjectVO;
import com.googlemaps.template.myapplication.services.GoogleApiService;
import com.googlemaps.template.myapplication.util.ApiFactory;
import com.googlemaps.template.myapplication.util.HttpConnection;
import com.googlemaps.template.myapplication.util.PathJSONParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import retrofit.Call;

public class BuildRouteLoader extends BaseLoader {


    private final GoogleApiService mGoogleApiService;
    private Context mContext;

    public BuildRouteLoader(Context context) {
        super(context);
        this.mContext = context;
        mGoogleApiService = ApiFactory.getCoordinateService();
    }


    @Override
    protected Response apiCall() throws IOException {

        List<GeoObject> all = GeoObjectHelper.findAll(Realm.getInstance(mContext));
        List<GeoObjectVO> geoObjects = ConvertUtil.toVoGeoObjects(all);

        String waypoints = "optimize:true|";
        for (int i = 1; i < geoObjects.size() - 1; i++) {
            waypoints += geoObjects.get(i).getLocation().latitude + "," + geoObjects.get(i).getLocation().longitude;
            if (i != geoObjects.size() - 1) {
                waypoints += "|";
            }
        }
        String originDest = geoObjects.get(0).getLocation().latitude + "," + geoObjects.get(0).getLocation().longitude ;
        String destination = geoObjects.get(geoObjects.size() - 1).getLocation().latitude + "," + geoObjects.get(geoObjects.size() - 1).getLocation().longitude;

        GoogleApiService service = ApiFactory.getCoordinateService();
        HashMap<String,String> param = new HashMap<>();

        param.put("origin",originDest);
        param.put("destination",destination);
        param.put("waypoints", waypoints);
        param.put("sensor","false");
        Call<JsonElement> call = service.directions(param);
        //JsonElement elements = call.execute().body();

        //Spike - retrofit '|' uri problem
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + "origin=" + originDest +"&" + "destination=" + destination + "&" + "waypoints=" + waypoints + "&sensor=false";
        HttpConnection http = new HttpConnection();

        List<List<HashMap<String, String>>> routes = null;

        try {
            String data = http.readUrl(url);
            JSONObject jObject;
            jObject = new JSONObject(data);
            PathJSONParser parser = new PathJSONParser();
            routes = parser.parse(jObject);
        } catch (Exception e) {
            return new PolylineOptionsResponse().setRequestResult(RequestResult.ERROR);
        }

        ArrayList<LatLng> points = null;
        PolylineOptions polyLineOptions = null;

        // traversing through routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<>();
            polyLineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(2);
            polyLineOptions.color(Color.BLUE);
        }

        final PolylineOptions finalPolyLineOptions = polyLineOptions;
        return new PolylineOptionsResponse()
                .setRequestResult(RequestResult.SUCCESS)
                .setAnswer(finalPolyLineOptions);

    }


}
