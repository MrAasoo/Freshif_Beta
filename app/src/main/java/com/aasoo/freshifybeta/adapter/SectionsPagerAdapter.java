package com.aasoo.freshifybeta.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SectionsPagerAdapter  extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;
    private ArrayList<String> titels;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titels = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String titel) {
        fragments.add(fragment);
        titels.add(titel);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titels.get(position);
    }
}
