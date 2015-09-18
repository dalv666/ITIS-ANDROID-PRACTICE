package org.dalv.practice.android.itis.customgallery;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.View;

import org.dalv.practice.android.itis.customgallery.utils.Constants;


public class ImageGalleryActivity extends AppCompatActivity implements View.OnClickListener{


    private RecyclerView mRecyclerView;

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    public static boolean isPortrait(Context context){
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            return true;
        }
        return false;
    }

    private int countColumn(Context context){
        if(isTablet(context)){
            if(isPortrait(context)){
                return 2;
            }else{
                return 3;
            }
        }else{
            if(isPortrait(context)){
                return 1;
            }else{
                return 2;
            }
        }

    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        if(Build.VERSION.SDK_INT >= 23){
            mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
        }else{
            mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.photo_transition));
        }
        setContentView(R.layout.activity_image_gallery);
        initToolbar();
        mRecyclerView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        int columnCount =countColumn(this);



        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter imageAdapter = new ImageAdapter(Constants.getImagesUrls(),columnCount,this);
        mRecyclerView.setAdapter(imageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mRecyclerView.getChildAdapterPosition(v);
        Intent intent = new Intent(ImageGalleryActivity.this,ImageDetailActivity.class);
        intent.putExtra("key", itemPosition);
        if(Build.VERSION.SDK_INT >= 21){
            v.setTransitionName("imageTransition");
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,v,v.getTransitionName());
            startActivity(intent,activityOptionsCompat.toBundle());
        }else{
            startActivity(intent);
        }
    }
}