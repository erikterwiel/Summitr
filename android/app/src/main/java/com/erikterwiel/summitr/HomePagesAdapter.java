package com.erikterwiel.summitr;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class HomePagesAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> mFragments = new ArrayList<>();
    private final ArrayList<String> mStrings = new ArrayList<>();

    public void addFragment(Fragment fragment, String title) {
        mFragments.add(fragment);
        mStrings.add(title);
    }

    public HomePagesAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mStrings.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
