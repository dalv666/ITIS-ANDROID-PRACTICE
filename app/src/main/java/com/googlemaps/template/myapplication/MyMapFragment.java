package com.googlemaps.template.myapplication;


import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.googlemaps.template.myapplication.model.GeoObject;
import com.googlemaps.template.myapplication.services.YandexMapApiService;
import com.googlemaps.template.myapplication.util.HttpConnection;
import com.googlemaps.template.myapplication.util.PathJSONParser;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyMapFragment extends MapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private GoogleMap mGoogleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //prepare google map
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Temporary method
     */
    private void addPoints(final List<GeoObject> locations) {
        final MarkerOptions options = new MarkerOptions();


        for (GeoObject loc : locations) {
            options.position(loc.getLocation());
        }

        String url = getMapsApiDirectionsUrl(locations);
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGoogleMap.addMarker(options);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0).getLocation(),
                        10));
                addMarkers(locations);
            }
        });
    }


    private void addMarkers(List<GeoObject> geoObjects) {
        if (mGoogleMap != null) {
            mGoogleMap.addMarker(new MarkerOptions().position(geoObjects.get(0).getLocation())
                    .title(getResources().getText(R.string.you_here_msg)+"").alpha(0.3f));
            for (int i = 1; i < geoObjects.size(); i++) {
                mGoogleMap.addMarker(new MarkerOptions().position(geoObjects.get(i).getLocation())
                        .title(geoObjects.get(i).getName()));
            }

        }
    }


    private String getMapsApiDirectionsUrl(List<GeoObject> geoObjects) {
        String waypoints = "waypoints=optimize:true|";

        for (int i = 1; i < geoObjects.size()-1; i++) {
            waypoints+=geoObjects.get(i).getLocation().latitude+ "," + geoObjects.get(i).getLocation().longitude;
            if(i != geoObjects.size()-1){
                waypoints+= "|";
            }
        }
        String originDest = "origin="+geoObjects.get(0).getLocation().latitude+","+geoObjects.get(0).getLocation().longitude+"&destination="+geoObjects.get(geoObjects.size()-1).getLocation().latitude+","+geoObjects.get(geoObjects.size()-1).getLocation().longitude;
        String sensor = "sensor=false";
        String params = originDest +"&"+ waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }



    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mGoogleMap.addPolyline(finalPolyLineOptions);
                }
            });
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    private void updateUI() {
            YandexMapApiService.getInstance().getNearLocations(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 10, new AsyncHttpClient.JSONObjectCallback() {
                @Override
                public void onCompleted(Exception e, AsyncHttpResponse source, JSONObject result) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    try {
                        List<GeoObject> geoObjectsFromJson = YandexMapApiService.getInstance().getGeoObjectsFromJson(result);
                        if(geoObjectsFromJson.size()==0){
                            Toast.makeText(getActivity(), getResources().getText(R.string.alone_msg), Toast.LENGTH_LONG).show();
                        }
                        addPoints(geoObjectsFromJson);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });
    }

    /**
     * Refresh user position
     */
    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if(mGoogleMap!=null){
            updateUI();
        }else{
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), getResources().getText(R.string.error_msg), Toast.LENGTH_LONG).show();
    }



}
