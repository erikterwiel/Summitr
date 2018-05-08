package com.erikterwiel.summitr;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
        setContentView(com.erikterwiel.summitr.R.layout.activity_home);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                Constants.cognitoUnauthPoolID,
                Regions.US_EAST_1);
        mDDBClient = new AmazonDynamoDBClient(credentialsProvider);
        mMapper = new DynamoDBMapper(mDDBClient);
        new PullUser().execute();

        mViewPager = (ViewPager) findViewById(com.erikterwiel.summitr.R.id.home_viewpager);
        mTabLayout = (TabLayout) findViewById(com.erikterwiel.summitr.R.id.home_tabs);
        mPlan = (FloatingActionButton) findViewById(com.erikterwiel.summitr.R.id.home_new_plan);
        mPhoto = (FloatingActionButton) findViewById(com.erikterwiel.summitr.R.id.home_new_photo);
        mReport = (FloatingActionButton) findViewById(com.erikterwiel.summitr.R.id.home_new_report);
        mStart = (FloatingActionButton) findViewById(com.erikterwiel.summitr.R.id.home_start_trip);

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
                startIntent.putExtra("username", mUser.getUsername());
                startIntent.putExtra("latitude", mUser.getLatitude());
                startIntent.putExtra("longitude", mUser.getLongitude());
                startActivity(startIntent);
            }
        });
    }

    private class PullUser extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(HomeActivity.this,
                    getString(com.erikterwiel.summitr.R.string.home_pulling),
                    getString(com.erikterwiel.summitr.R.string.home_wait));
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

    public TransferUtility getTransferUtility(Context context) {
        AmazonS3Client s3Client = getS3Client(context.getApplicationContext());
        TransferUtility mTransferUtility = new TransferUtility(
                s3Client, context.getApplicationContext());
        return mTransferUtility;
    }

    public static AmazonS3Client getS3Client(Context context) {
        AmazonS3Client sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
        return sS3Client;
    }

    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        CognitoCachingCredentialsProvider sCredProvider = new CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                Constants.cognitoUnauthPoolID,
                Regions.US_EAST_1);
        return sCredProvider;
    }
}
