package org.dalv.practice.android.itis.customgallery;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.dalv.practice.android.itis.customgallery.utils.Constants;

public class ImageDetailActivity extends Activity {

    private ImageView mImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.photo_transition));
        }

        setContentView(R.layout.activity_image_detail);
        mImage = (ImageView) findViewById(R.id.photo_detail);
        Bundle bundle = getIntent().getExtras();
        int value = bundle.getInt("key");
        Picasso.with(this).load(Constants.getImagesUrls().get(value)).into(mImage);
    }


}
