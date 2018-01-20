package com.erikterwiel.mountainviews;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

public class StartActivity extends AppCompatActivity {

    public static final String TAG = "StartActivity.java";

    private DeviceListener mListener;

    private FloatingActionButton mHome;
    private FloatingActionButton mStart;
    private FloatingActionButton mConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mHome = (FloatingActionButton) findViewById(R.id.start_home);
        mStart = (FloatingActionButton) findViewById(R.id.start_start);
        mConnect = (FloatingActionButton) findViewById(R.id.start_connect);

        mListener = new AbstractDeviceListener() {
            @Override
            public void onConnect(Myo myo, long timestamp) {
                Toast.makeText(StartActivity.this, "Myo Connected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnect(Myo myo, long timestamp) {
                Toast.makeText(StartActivity.this, "Myo Disconnected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPose(Myo myo, long timestamp, Pose pose) {
                Log.i(TAG, "Pose: " + pose);
            }
        };

        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e(TAG, "Could not initialize the Hub.");
            finish();
            return;
        }
        hub.addListener(mListener);
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }
}
