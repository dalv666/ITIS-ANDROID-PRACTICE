package com.googlemaps.template.myapplication.ui;


import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonElement;
import com.googlemaps.template.myapplication.R;
import com.googlemaps.template.myapplication.database.GeoObjectHelper;
import com.googlemaps.template.myapplication.loader.BuildRouteLoader;
import com.googlemaps.template.myapplication.loader.GeoObjectsLoader;
import com.googlemaps.template.myapplication.model.GeoObject;
import com.googlemaps.template.myapplication.model.GeoObjectVO;
import com.googlemaps.template.myapplication.response.Response;
import com.googlemaps.template.myapplication.util.ConvertUtil;

import java.util.List;

import io.realm.Realm;


public class MyMapFragment extends MapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<Response>, GoogleMap.OnInfoWindowClickListener, DialogInterface.OnClickListener {

    public static final String USER_LOCATION_BUNDLE = "USER_LOCATION_BUNDLE";
    public static final String POSITION_KEY = "POSITION_KEY";
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private boolean mConnect;
    private boolean isLocalStorageEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //prepare google map
        createLocationRequest();

        //For test
        GeoObjectHelper.deleteAll(Realm.getInstance(getActivity()));


        isLocalStorageEmpty = GeoObjectHelper.findAll(Realm.getInstance(getActivity())).isEmpty();
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

    @Override
    public Loader<Response> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.id.geo_objects_loader:
                return new GeoObjectsLoader(getActivity(), args.getString(USER_LOCATION_BUNDLE));
            case R.id.build_route_loader:
                return new BuildRouteLoader(getActivity());
            default:
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Response> loader, Response data) {
        int id = loader.getId();

        if (data == null || data.getTypedAnswer() == null) {
            createRepeatDialog();
            return;
        }

        if (id == R.id.geo_objects_loader) {
            JsonElement answer = data.getTypedAnswer();
            List<GeoObject> geoObjects = ConvertUtil.jsonElementToGeoObjectsList(answer);
            GeoObjectHelper.deleteAll(Realm.getInstance(getActivity()));
            GeoObjectHelper.save(Realm.getInstance(getActivity()), geoObjects);
            showLocalStoragePlaces();
            getLoaderManager().initLoader(R.id.build_route_loader, Bundle.EMPTY, this);
        } else if (id == R.id.build_route_loader) {
            PolylineOptions finalPolyLineOptions = data.getTypedAnswer();
            if (finalPolyLineOptions == null) {
                createRepeatDialog();
            } else {
                mGoogleMap.addPolyline(finalPolyLineOptions);
            }
        }
        getLoaderManager().destroyLoader(id);
    }

    private void createRepeatDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle(getString(R.string.server_error));
        ad.setMessage(getString(R.string.server_error_description));
        ad.setPositiveButton(getString(R.string.repeat), this);
        ad.setNegativeButton(getString(R.string.cancel), this);
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getActivity(), getString(R.string.not_selected),
                        Toast.LENGTH_LONG).show();
            }
        });
        ad.create().show();
    }

    @Override
    public void onLoaderReset(Loader<Response> loader) {

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
        map.setMyLocationEnabled(true);
        mGoogleMap.setOnInfoWindowClickListener(this);
        //If local storage is not empty
        if (!isLocalStorageEmpty) {
            showLocalStoragePlaces();
        }
    }

    private void showLocalStoragePlaces() {
        mGoogleMap.clear();
        List<GeoObject> locations = GeoObjectHelper.findAll(Realm.getInstance(getActivity()));
        List<GeoObjectVO> geoObjectVOs = ConvertUtil.toVoGeoObjects(locations);
        for (int i = 1; i < geoObjectVOs.size(); i++) {
            mGoogleMap.addMarker(
                    new MarkerOptions()
                            .position(geoObjectVOs.get(i).getLocation())
                            .title(geoObjectVOs.get(i).getName())
            );
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geoObjectVOs.get(0).getLocation(), 10));
        }


    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }


    @Override
    public void onConnectionSuspended(int i) {
        createRepeatDialog();
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
        } else {
            if (mGoogleMap != null) {
                if (!GeoObjectHelper.isTableEmpty(Realm.getInstance(getActivity()))) {
                    showLocalStoragePlaces();
                    getLoaderManager().initLoader(R.id.build_route_loader, Bundle.EMPTY, this);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mGoogleMap != null) {
            if (!mConnect) {
                mConnect = true;
                getLocationsFromServer(location);
            }
        } else {
            startLocationUpdates();
        }
    }

    private void getLocationsFromServer(Location location) {
        Bundle bundle = new Bundle();
        String stringLocation = String.valueOf(location.getLongitude() + "," + location.getLatitude());
        bundle.putString(USER_LOCATION_BUNDLE, stringLocation);
        getLoaderManager().initLoader(R.id.geo_objects_loader, bundle, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), getResources().getText(R.string.error_msg), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        GeoObject position = GeoObjectHelper.findByPosition(Realm.getInstance(getActivity()), marker.getPosition().longitude + " " + marker.getPosition().latitude);
        Intent intent = new Intent(getActivity(), DetailLocationActivity.class);
        intent.putExtra(POSITION_KEY, position.getPos());
        startActivity(intent);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_POSITIVE:
                if (mGoogleMap != null && mGoogleMap.getMyLocation() != null) {
                    getLocationsFromServer(mGoogleMap.getMyLocation());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.sorry),
                            Toast.LENGTH_LONG).show();
                }
                break;
        }


    }
}
