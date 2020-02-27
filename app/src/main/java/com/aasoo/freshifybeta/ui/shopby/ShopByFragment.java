package com.aasoo.freshifybeta.ui.shopby;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.aasoo.freshifybeta.R;
import com.aasoo.freshifybeta.adapter.SectionsPagerAdapter;
import com.aasoo.freshifybeta.adapter.UserProductAdapter;
import com.aasoo.freshifybeta.admin_ui.AllUserFragment;
import com.aasoo.freshifybeta.admin_ui.DueUserFragment;
import com.aasoo.freshifybeta.admin_ui.MemberFragment;
import com.aasoo.freshifybeta.admin_ui.VerifyUserFragment;
import com.aasoo.freshifybeta.model.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ShopByFragment extends Fragment {


    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_by, container, false);
        mTabLayout = view.findViewById(R.id.user_tablayout);
        mViewPager = view.findViewById(R.id.user_viewPager);
        mSectionPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mSectionPagerAdapter.addFragment(new ShopByWomenFragment(), "Women's");
        mSectionPagerAdapter.addFragment(new ShopByMenFragment(), "Men's");
        mSectionPagerAdapter.addFragment(new ShopByAllFragment(), "All");
        mSectionPagerAdapter.addFragment(new ShopByKidFragment(), "Kid's");

        mViewPager.setAdapter(mSectionPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

}