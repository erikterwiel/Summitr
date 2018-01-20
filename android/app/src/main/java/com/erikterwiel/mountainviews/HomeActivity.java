package com.erikterwiel.mountainviews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity.java";

    private User mUser;
    private AmazonDynamoDBClient mDDBClient;
    private DynamoDBMapper mMapper;

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

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                Constants.cognitoUnauthPoolID,
                Regions.US_EAST_1);
        mDDBClient = new AmazonDynamoDBClient(credentialsProvider);
        mMapper = new DynamoDBMapper(mDDBClient);
        new PullUser().execute();

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

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(HomeActivity.this, NewPhotoActivity.class);
                photoIntent.putExtra("username", mUser.getUsername());
                startActivity(photoIntent);
            }
        });

        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reportIntent = new Intent(HomeActivity.this, NewReportActivity.class);
                reportIntent.putExtra("username", mUser.getUsername());
                startActivity(reportIntent);
            }
        });

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(HomeActivity.this, StartActivity.class);
                startActivity(startIntent);
            }
        });
    }

    private class PullUser extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(HomeActivity.this,
                    getString(R.string.home_pulling),
                    getString(R.string.home_wait));
        }

        @Override
        protected Void doInBackground(Void... inputs) {
            mUser = mMapper.load(User.class, getIntent().getStringExtra("username"));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            if (mUser == null) {
                mUser = new User();
                mUser.setUsername(getIntent().getStringExtra("username"));
            }
            try {
                FusedLocationProviderClient locationClient =
                        LocationServices.getFusedLocationProviderClient(HomeActivity.this);
                locationClient.getLastLocation().addOnSuccessListener(HomeActivity.this,
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Log.i(TAG, Double.toString(location.getLatitude()));
                                    Log.i(TAG, Double.toString(location.getLongitude()));
                                    mUser.setLatitude(location.getLatitude());
                                    mUser.setLongitude(location.getLongitude());
                                    new PushUser().execute();
                                }
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class PushUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... inputs) {
            mMapper.save(mUser);
            return null;
        }
    }
}
