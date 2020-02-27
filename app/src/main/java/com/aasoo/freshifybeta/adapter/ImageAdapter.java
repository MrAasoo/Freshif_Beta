package com.aasoo.freshifybeta.adapter;


import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aasoo.freshifybeta.R;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Uri> mImagesUri;

    public ImageAdapter(Context mContext, ArrayList<Uri> mImagesUri) {
        this.mContext = mContext;
        this.mImagesUri = mImagesUri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_layout, parent, false);
        return new ImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mImagesUri != null) {
            try {
                holder.image.setImageBitmap(MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), mImagesUri.get(position)));
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mImagesUri.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
        }
    }
}
