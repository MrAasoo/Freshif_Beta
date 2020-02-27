package com.aasoo.freshifybeta.admin_ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.aasoo.freshifybeta.ManageOrdersActivity;
import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.WelcomeActivity;
import com.aasoo.freshifybeta.adapter.SlidePagerAdapter;
import com.aasoo.freshifybeta.image_slide.AddSlideActivity;
import com.aasoo.freshifybeta.jewellery.AddProductActivity;
import com.aasoo.freshifybeta.jewellery.ShowProductActivity;
import com.aasoo.freshifybeta.model.SlideImage;
import com.aasoo.freshifybeta.ui.todaysoffer.TodaysOfferFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminHomeFragment extends Fragment {

    Fragment fragment = null;
    private ViewPager slide_image;
    private String[] slideImages;

    private LinearLayout dot_layout;
    private ImageView[] dots;

    public AdminHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        TextView add_product = view.findViewById(R.id.add_product);
        TextView manage_slide_view = view.findViewById(R.id.manage_slide_view);
        TextView show_product = view.findViewById(R.id.show_product);
        TextView show_offer = view.findViewById(R.id.show_offer);
        TextView out_of_stock = view.findViewById(R.id.out_of_stock);
        TextView manage_orders = view.findViewById(R.id.manage_orders);


        slide_image = view.findViewById(R.id.slide_image);
        dot_layout = view.findViewById(R.id.dot_layout);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("slide_detail");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int total = (int) dataSnapshot.getChildrenCount();
                slideImages = new String[total];
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SlideImage image_slide = snapshot.getValue(SlideImage.class);
                    if (image_slide != null) {
                        slideImages[i] = image_slide.getImage_url();
                        i++;
                    }
                }
                SlidePagerAdapter adapter = new SlidePagerAdapter(getContext(), slideImages);
                slide_image.setAdapter(adapter);
                slide_image.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        createDots(position, total);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                createDots(0, total);

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new AdminHomeFragment.SlideTimer(total),8000,8000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        show_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Bundle bundle = new Bundle();
                bundle.putString("find_from","admin_home");
                bundle.putString("item_for","daliy");
                Intent intent = new Intent(getActivity(), ShowProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        show_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {Bundle bundle = new Bundle();
                bundle.putString("find_from","admin_home");
                bundle.putString("item_for","all");
                Intent intent = new Intent(getActivity(), ShowProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        manage_slide_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddSlideActivity.ManageSlidingActivity.class));
            }
        });
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddProductActivity.class));
            }
        });

        out_of_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("find_from","admin_home");
                bundle.putString("item_for","out");
                Intent intent = new Intent(getActivity(), ShowProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        manage_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ManageOrdersActivity.class));
            }
        });

        return view;
    }

    private void createDots(int current_position, int length) {
        if (dot_layout != null) {
            dot_layout.removeAllViews();
        }

        dots = new ImageView[length];

        for (int i = 0; i < length; i++) {
            dots[i] = new ImageView(getContext());
            if (i == current_position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.selected_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.default_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dot_layout.addView(dots[i], params);
        }
    }


    private class SlideTimer extends TimerTask{
        private int total;

        private SlideTimer(int total) {
            this.total = total;
        }

        @Override
        public void run() {
            if(getActivity()!=null){
            (getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (slide_image.getCurrentItem() < total - 1) {
                        slide_image.setCurrentItem(slide_image.getCurrentItem() + 1);
                    } else {
                        slide_image.setCurrentItem(0);
                    }
                }
            });}
        }
    }
}
