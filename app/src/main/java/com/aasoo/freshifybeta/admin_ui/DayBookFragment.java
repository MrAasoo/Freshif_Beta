package com.aasoo.freshifybeta.admin_ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.SectionsPagerAdapter;
import com.aasoo.freshifybeta.model.UserData;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayBookFragment extends Fragment {


    public DayBookFragment() {
        // Required empty public constructor
    }

    private CardView member_msg;
    private LinearLayout admin_view;


    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_book, container, false);
        member_msg = view.findViewById(R.id.member_msg);
        admin_view = view.findViewById(R.id.admin_view);

        mTabLayout = view.findViewById(R.id.user_tablayout);
        mViewPager = view.findViewById(R.id.user_viewPager);

        String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("user_info").child(mCurrentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                if(userData!=null){
                    if(userData.getUser_type().equals("admin")){
                        admin_view.setVisibility(View.VISIBLE);
                        member_msg.setVisibility(View.GONE);


                        mSectionPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

                        mSectionPagerAdapter.addFragment(new AllUserFragment(), "All User");
                        mSectionPagerAdapter.addFragment(new VerifyUserFragment(), "Verify");
                        mSectionPagerAdapter.addFragment(new DueUserFragment(), "Advance / Due");
                        mSectionPagerAdapter.addFragment(new MemberFragment(), "Members");

                        mViewPager.setAdapter(mSectionPagerAdapter);
                        mTabLayout.setupWithViewPager(mViewPager);

                    }else {
                        admin_view.setVisibility(View.GONE);
                        member_msg.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
