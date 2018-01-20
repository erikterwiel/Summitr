package com.erikterwiel.mountainviews;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity.java";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FloatingActionButton mPlan;
    private FloatingActionButton mPhoto;
    private FloatingActionButton mReport;
    private FloatingActionButton mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mViewPager = (ViewPager) findViewById(R.id.home_viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.home_tabs);
        mPlan = (FloatingActionButton) findViewById(R.id.home_new_plan);
        mPhoto = (FloatingActionButton) findViewById(R.id.home_new_photo);
        mReport = (FloatingActionButton) findViewById(R.id.home_new_report);
        mStart = (FloatingActionButton) findViewById(R.id.home_start_trip);

        HomePagesAdapter adapter = new HomePagesAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedFragment(), "Feed");
        adapter.addFragment(new ReportsFragment(), "Reports");
        adapter.addFragment(new PlansFragment(), "Plans");
        adapter.addFragment(new TrackerFragment(), "Tracker");
        adapter.addFragment(new ProfileFragment(), "Profile");
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(HomeActivity.this, StartActivity.class);
                startActivity(startIntent);
            }
        });
    }

}
