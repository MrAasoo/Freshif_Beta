package com.aasoo.freshifybeta.ui.home;

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
import androidx.viewpager.widget.ViewPager;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.SlidePagerAdapter;
import com.aasoo.freshifybeta.jewellery.ShowProductActivity;
import com.aasoo.freshifybeta.model.SlideImage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {
    private ViewPager slide_image;
    private String[] slideImages;
    private LinearLayout dot_layout;
    private ImageView[] dots;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //Women's jewellery
        TextView txt_women_ring = view.findViewById(R.id.txt_women_ring);
        TextView txt_women_earring = view.findViewById(R.id.txt_women_earring);
        TextView txt_women_pendants = view.findViewById(R.id.txt_women_pendents);
        TextView txt_women_bangles = view.findViewById(R.id.txt_women_bangles);
        TextView txt_women_nosepin = view.findViewById(R.id.txt_women_nosepin);
        TextView txt_women_all = view.findViewById(R.id.txt_women_all);
        TextView txt_women_payal = view.findViewById(R.id.txt_women_payal);
        TextView txt_women_chain = view.findViewById(R.id.txt_women_chain);

        //Men's jewellery
        TextView txt_men_ring = view.findViewById(R.id.txt_men_ring);
        TextView txt_men_chain = view.findViewById(R.id.txt_men_chain);
        TextView txt_men_kada = view.findViewById(R.id.txt_men_kada);
        TextView txt_men_bracelets = view.findViewById(R.id.txt_men_bracelets);
        TextView txt_men_all = view.findViewById(R.id.txt_men_all);

        //Kid's jewellery
        TextView txt_kid_earring = view.findViewById(R.id.txt_kid_earring);
        TextView txt_kid_necklace = view.findViewById(R.id.txt_kid_necklace);
        TextView txt_kid_pendant = view.findViewById(R.id.txt_kid_pendant);
        TextView txt_kid_bracelets = view.findViewById(R.id.txt_kid_bracelets);
        TextView txt_kid_all = view.findViewById(R.id.txt_kid_all);

        //Trending categories
        TextView tt_k_gold = view.findViewById(R.id.tt_k_gold);
        TextView mangalsutra = view.findViewById(R.id.mangalsutra);
        TextView pendant = view.findViewById(R.id.pendant);
        TextView ring = view.findViewById(R.id.ring);
        TextView chains = view.findViewById(R.id.chains);
        TextView bangles = view.findViewById(R.id.bangles);



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
                timer.scheduleAtFixedRate(new HomeFragment.SlideTimer(total),8000,8000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View.OnClickListener womensClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.clear();
                bundle.putString("item_for", "Women's");
                bundle.putString("find_from", "home");
                switch (v.getId()) {
                    case R.id.txt_women_ring:
                        bundle.putString("item_type", "Ring");
                        break;
                    case R.id.txt_women_earring:
                        bundle.putString("item_type", "Ear Rings");
                        break;
                    case R.id.txt_women_pendents:
                        bundle.putString("item_type", "Pendant");
                        break;
                    case R.id.txt_women_bangles:
                        bundle.putString("item_type", "Bangles");
                        break;
                    case R.id.txt_women_nosepin:
                        bundle.putString("item_type", "Nose Pin");
                        break;
                    case R.id.mangalsutra:
                        bundle.putString("item_type", "Mangalsutra");
                        break;
                    case R.id.txt_women_chain:
                        bundle.putString("item_type", "Chain");
                        break;
                        case R.id.txt_women_all:
                        bundle.putString("item_type", "All");
                        break;
                    case R.id.txt_women_payal:
                        bundle.putString("item_type", "Anklets(Payal)");
                        break;
                }
                Intent intent = new Intent(getActivity(), ShowProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };
        txt_women_ring.setOnClickListener(womensClick);
        txt_women_earring.setOnClickListener(womensClick);
        txt_women_pendants.setOnClickListener(womensClick);
        txt_women_bangles.setOnClickListener(womensClick);
        txt_women_nosepin.setOnClickListener(womensClick);
        txt_women_payal.setOnClickListener(womensClick);
        txt_women_all.setOnClickListener(womensClick);
        mangalsutra.setOnClickListener(womensClick);
        txt_women_chain.setOnClickListener(womensClick);

        View.OnClickListener menClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.clear();
                bundle.putString("item_for", "Men's");
                bundle.putString("find_from", "home");
                switch (v.getId()) {
                    case R.id.txt_men_ring:
                        bundle.putString("item_type", "Ring");
                        break;
                    case R.id.txt_men_chain:
                        bundle.putString("item_type", "Chain");
                        break;
                    case R.id.txt_men_kada:
                        bundle.putString("item_type", "Kada");
                        break;
                    case R.id.txt_men_bracelets:
                        bundle.putString("item_type", "Bracelet");
                        break;
                    case R.id.txt_men_all:
                        bundle.putString("item_type", "All");
                        break;
                }
                Intent intent = new Intent(getActivity(), ShowProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };

        txt_men_ring.setOnClickListener(menClick);
        txt_men_chain.setOnClickListener(menClick);
        txt_men_kada.setOnClickListener(menClick);
        txt_men_bracelets.setOnClickListener(menClick);
        txt_men_all.setOnClickListener(menClick);

        View.OnClickListener kidClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.clear();
                bundle.putString("item_for", "Kid's");
                bundle.putString("find_from", "home");
                switch (v.getId()) {
                    case R.id.txt_kid_pendant:
                        bundle.putString("item_type", "Pendant");
                        break;
                    case R.id.txt_kid_necklace:
                        bundle.putString("item_type", "Necklace");
                        break;
                    case R.id.txt_kid_earring:
                        bundle.putString("item_type", "Ear Rings");
                        break;
                    case R.id.txt_kid_bracelets:
                        bundle.putString("item_type", "Bracelets");
                        break;
                    case R.id.txt_kid_all:
                        bundle.putString("item_type", "All");
                        break;
                }
                Intent intent = new Intent(getActivity(), ShowProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };

        txt_kid_all.setOnClickListener(kidClick);
        txt_kid_bracelets.setOnClickListener(kidClick);
        txt_kid_earring.setOnClickListener(kidClick);
        txt_kid_necklace.setOnClickListener(kidClick);
        txt_kid_pendant.setOnClickListener(kidClick);



        View.OnClickListener trending = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.clear();
                bundle.putString("find_from", "trending");
                switch (v.getId()){
                    case  R.id.tt_k_gold:
                        bundle.putString("item_type", "22kGold");
                        break;
                    case R.id.ring:
                        bundle.putString("item_type", "Ring");
                        break;
                    case R.id.pendant:
                        bundle.putString("item_type", "Pendant");
                        break;
                    case R.id.chains:
                        bundle.putString("item_type", "Chain");
                        break;
                    case R.id.bangles:
                        bundle.putString("item_type", "Bangles");
                        break;
                }
                Intent intent = new Intent(getActivity(), ShowProductActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };

        tt_k_gold.setOnClickListener(trending);
        pendant.setOnClickListener(trending);
        ring.setOnClickListener(trending);
        chains.setOnClickListener(trending);
        bangles.setOnClickListener(trending);


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
            getActivity().runOnUiThread(new Runnable() {
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
