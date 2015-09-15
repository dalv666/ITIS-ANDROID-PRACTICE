package com.googlemaps.template.myapplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.googlemaps.template.myapplication.services.YandexMapApiService;
import com.googlemaps.template.myapplication.util.HttpConnection;
import com.googlemaps.template.myapplication.util.PathJSONParser;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpResponse;


import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {


    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private GoogleMap mGoogleMap;
    private static boolean boolka = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If playServices is not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //prepare google map
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    /**
     * Temporary method
     */
    private void addPoints(ArrayList<String> locations) {
        final MarkerOptions options = new MarkerOptions();

        final ArrayList<LatLng> latLngs = new ArrayList<>();

        for (String loc : locations) {
            Double latitude = Double.valueOf(loc.substring(0, 10));
            Double longitude = Double.valueOf(loc.substring(10));

            LatLng sydney = new LatLng(longitude,latitude);
            latLngs.add(sydney);
            options.position(sydney);
        }



       String url = getMapsApiDirectionsUrl(latLngs);
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGoogleMap.addMarker(options);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0),
                        10));
                addMarkers(latLngs);
            }
        });
    }





    private void addMarkers(ArrayList<LatLng> latLngs) {
        if (mGoogleMap != null) {
            for (LatLng latLng : latLngs) {
                mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(latLng.toString()));
            }
        }
    }


    private String getMapsApiDirectionsUrl(ArrayList<LatLng> latLngs) {
        String waypoints = "waypoints=optimize:true|";

        for (int i = 1; i < latLngs.size()-1; i++) {
            waypoints+=latLngs.get(i).latitude+ "," + latLngs.get(i).longitude;
            if(i != latLngs.size()-1){
                waypoints+= "|";
            }
        }
        String originDest = "origin="+latLngs.get(0).latitude+","+latLngs.get(0).longitude+"&destination="+latLngs.get(latLngs.size()-1).latitude+","+latLngs.get(latLngs.size()-1).longitude;
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

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

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
            runOnUiThread(new Runnable() {
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
        if(!boolka) {
            YandexMapApiService.getInstance().getNearLocations(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 10, new AsyncHttpClient.JSONObjectCallback() {
                @Override
                public void onCompleted(Exception e, AsyncHttpResponse source, JSONObject result) {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    try {
                        ArrayList<String> locations = new ArrayList<>(10);
                        JSONArray jsonArray = result.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject geoObject = (JSONObject) ((JSONObject) jsonArray.get(i)).get("GeoObject");
                            JSONObject point = geoObject.getJSONObject("Point");
                            String pos = point.getString("pos");
                            locations.add(pos);
                        }
                        addPoints(locations);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        boolka=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
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


    /**
     *  Returns true if google play is available, otherwise false
     */
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        if(mGoogleMap!=null){
            updateUI();
        }else{
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println();
    }



}