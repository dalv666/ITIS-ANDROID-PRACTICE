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
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;


import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {


    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private GoogleMap mGoogleMap;

    /**
     * Temporary list
     */
    private ArrayList<String> locations = new ArrayList<>(10);


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

        //****For test****
        Button button = (Button) findViewById(R.id.my_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        Button button2 = (Button) findViewById(R.id.do_it);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoints();
            }
        });
        //***************

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
    private void addPoints() {
        for (String loc : locations) {

            Double latitude = Double.valueOf(loc.substring(0, 6));
            Double longitude = Double.valueOf(loc.substring(10, 16));


            LatLng sydney = new LatLng(latitude, longitude);
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

            mGoogleMap.addMarker(new MarkerOptions()
                    .title("Sydney" + loc)
                    .position(sydney));
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
        yaRequest();
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
        updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println();
    }


    /**
     * Get 10 closes locations
     * (!refactoring!)
     *
     */
    public void yaRequest() {
        Uri uri = Uri.parse("https://geocode-maps.yandex.ru/1.x/?geocode=49.1634695,55.7852423&format=json&rspn=0&results=10&kind=house");
        AsyncHttpRequest asyncHttpRequest = new AsyncHttpRequest(uri, "GET");
        AsyncHttpClient.getDefaultInstance().executeJSONObject(asyncHttpRequest, new AsyncHttpClient.JSONObjectCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, JSONObject result) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                try {
                    JSONArray jsonArray = result.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject geoObject = (JSONObject) ((JSONObject) jsonArray.get(i)).get("GeoObject");
                        JSONObject point = geoObject.getJSONObject("Point");
                        String pos = point.getString("pos");
                        locations.add(pos);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

}