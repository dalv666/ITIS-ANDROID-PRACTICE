package com.googlemaps.template.myapplication.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.googlemaps.template.myapplication.R;
import com.googlemaps.template.myapplication.database.GeoObjectHelper;
import com.googlemaps.template.myapplication.model.GeoObject;

import io.realm.Realm;


public class DetailLocationActivity extends Activity {

    private String mText = "";
    private TextView mTvName;
    private GeoObject mGeoObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.locality_detail_activity);
        mTvName = (TextView) findViewById(R.id.ld_tv_name);
        this.mGeoObject = GeoObjectHelper.findByPosition(Realm.getInstance(this), getIntent().getStringExtra(MyMapFragment.POSITION_KEY));
        mTvName.setText(mGeoObject.getName());
        Button button = (Button) findViewById(R.id.ld_bt_edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.ld_bt_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    private void delete() {
        String name = mGeoObject.getName();
        GeoObjectHelper.delete(Realm.getInstance(this), mGeoObject);
        Toast.makeText(this, name + getString(R.string.delete_from_route), Toast.LENGTH_LONG).show();
        finish();
    }

    private void edit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.edit));

        final EditText input = new EditText(getApplication());
        input.setText(mGeoObject.getName());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = input.getText().toString();
                updateGeoObject(mText);
                mTvName.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void updateGeoObject(String text) {
        GeoObject geoObject2 = new GeoObject(text,mGeoObject.getPos());
        GeoObjectHelper.update(Realm.getInstance(this), geoObject2);
    }


}
