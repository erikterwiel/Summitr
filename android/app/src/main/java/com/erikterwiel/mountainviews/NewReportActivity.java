package com.erikterwiel.mountainviews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NewReportActivity extends AppCompatActivity {

    private static final String TAG = "NewReportActivity.java";

    private ImageView mCancel;
    private ImageView mDone;
    private Button mAdd;
    private EditText mReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        mCancel = (ImageView) findViewById(R.id.new_report_cancel);
        mDone = (ImageView) findViewById(R.id.new_report_done);
        
    }
}
