package com.erikterwiel.mountainviews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

public class NewPhotoActivity extends AppCompatActivity {

    private static final String TAG = "NewPhotoActivity.java";

    private Bitmap mBitmap;

    private ImageView mCancel;
    private ImageView mDone;
    private Button mAdd;
    private ImageView mImage;
    private EditText mCaption;
    private EditText mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_photo);

        mCancel = (ImageView) findViewById(R.id.new_photo_cancel);
        mDone = (ImageView) findViewById(R.id.new_photo_done);
        mAdd = (Button) findViewById(R.id.new_photo_add);
        mImage = (ImageView) findViewById(R.id.new_photo_image);
        mCaption = (EditText) findViewById(R.id.new_photo_caption);
        mLocation = (EditText) findViewById(R.id.new_photo_location);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent();
                imageIntent.setType("image/*");
                imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(imageIntent, "Select Photo"), Constants.photoID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.photoID && resultCode == RESULT_OK && data != null) {
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                mImage.setImageBitmap(mBitmap);
                mImage.setVisibility(View.VISIBLE);
                mAdd.setVisibility(View.GONE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
