package org.dalv.practice.android.itis.customgallery;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;


import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> mData;
    private int mColumn;

    public ImageAdapter(List<String> data, int column, View.OnClickListener mItemClickListener) {
        this.mColumn = column;
        this.mData = data;
        this.mItemClickListener = mItemClickListener;
    }

    private View.OnClickListener mItemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_images_relative_layout, parent, false);
        view.setOnClickListener(mItemClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WindowManager wm = (WindowManager) holder.getImageView().getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        holder.getImageView().getLayoutParams().width = width / mColumn;
        Picasso.with(holder.getImageView().getContext()).load(mData.get(position)).placeholder(R.mipmap.placeholder).into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageGalleryView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }


}