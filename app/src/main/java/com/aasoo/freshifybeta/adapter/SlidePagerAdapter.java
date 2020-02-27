package com.aasoo.freshifybeta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.model.SlideImage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SlidePagerAdapter extends PagerAdapter {

    private Context context;
    private String[] slideImages;

    public SlidePagerAdapter(Context context, String[] slideImages) {
        this.context = context;
        this.slideImages = slideImages;
    }

    @Override
    public int getCount() {
        return slideImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.get()
                .load(slideImages[position])
                .placeholder(R.drawable.placeholder)
                .fit()
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
