package com.googlemaps.template.myapplication.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.googlemaps.template.myapplication.R;
import com.googlemaps.template.myapplication.database.GeoObjectHelper;

import io.realm.Realm;


public class MainActivity extends Activity {

    public static Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If playServices is not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        mRealm = Realm.getInstance(this);
        GeoObjectHelper.findAll(mRealm);
        setContentView(R.layout.activity_main);
    }


    /**
     * Returns true if google play is available, otherwise false
     */
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_LONG).show();
            return false;
        }
    }


}