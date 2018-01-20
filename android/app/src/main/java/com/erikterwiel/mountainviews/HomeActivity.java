package com.erikterwiel.mountainviews;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity.java";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getSupportActionBar().hide();

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.home_tabs);

        HomePagesAdapter adapter = new HomePagesAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedFragment(), "Feed");
        adapter.addFragment(new ProfileFragment(), "Profile");
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

}
