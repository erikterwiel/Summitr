package com.erikterwiel.mountainviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class NewPhotoActivity extends AppCompatActivity {

    private static final String TAG = "NewPhotoActivity.java";

    private Bitmap mBitmap;
    private User mUser;
    private Photo mPhoto;
    private Recent mRecent;
    private AmazonS3Client mS3Client;
    private TransferUtility mTransferUtility;
    private AmazonDynamoDBClient mDDBClient;
    private DynamoDBMapper mMapper;

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

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                Constants.cognitoUnauthPoolID,
                Regions.US_EAST_1);
        mDDBClient = new AmazonDynamoDBClient(credentialsProvider);
        mMapper = new DynamoDBMapper(mDDBClient);
        mTransferUtility = getTransferUtility(this);
        mUser = new User();
        mPhoto = new Photo();
        mRecent = new Recent();
        new PullUser().execute();


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
                if (mCaption.getText().toString().equals("")) {
                    Toast.makeText(NewPhotoActivity.this, "Please enter a caption", Toast.LENGTH_LONG).show();
                    return;
                } else if (mLocation.getText().toString().equals("")) {
                    Toast.makeText(NewPhotoActivity.this, "Preparing enter a location", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(NewPhotoActivity.this, "Preparing upload...", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                String time = "" + (calendar.getTimeInMillis());
                File folder = new File("sdcard/Pictures/MountainViews/temp");
                if (!folder.exists()) folder.mkdir();
                File toSend = new File(folder, "toSend.png");
                try {
                    toSend.createNewFile();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapData = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(toSend);
                    fos.write(bitmapData);
                    fos.flush();
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                TransferObserver observer = mTransferUtility.upload(
                        Constants.s3BucketName,
                        mUser.getUsername() + "/" + time,
                        new File(Constants.fileOutputPath));
                observer.setTransferListener(new UploadListener());
                mPhoto = new Photo();
                mPhoto.setFilename(mUser.getUsername() + "/" + time);
                mPhoto.setCaption(mCaption.getText().toString());
                mPhoto.setLocation(mLocation.getText().toString());
                mUser.addPhoto(mPhoto.getFilename());
                mRecent.setTime(Calendar.getInstance().getTimeInMillis());
                mRecent.setIdentifier(mPhoto.getFilename());
                mRecent.setType("photo");
                mRecent.setUsername(mUser.getUsername());
                new PushPhoto().execute();
                Toast.makeText(NewPhotoActivity.this, "Photo uploading!", Toast.LENGTH_LONG).show();
                finish();
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
                mImage.setImageBitmap(mBitmap.createScaledBitmap(
                        mBitmap, mBitmap.getWidth()/ 5, mBitmap.getHeight()/ 5, false));
                mImage.setVisibility(View.VISIBLE);
                mAdd.setVisibility(View.GONE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public TransferUtility getTransferUtility(Context context) {
        mS3Client = getS3Client(context.getApplicationContext());
        TransferUtility mTransferUtility = new TransferUtility(
                mS3Client, context.getApplicationContext());
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

    private class PullUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... inputs) {
            mUser = mMapper.load(User.class, getIntent().getStringExtra("username"));
            return null;
        }
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.i(TAG, state + "");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            int percentage = (int) (bytesCurrent / bytesTotal * 100);
            Log.i(TAG, Integer.toString(percentage) + "% uploaded");
        }

        @Override
        public void onError(int id, Exception ex) {
            ex.printStackTrace();
            Log.i(TAG, "Error detected");
        }
    }

    private class PushPhoto extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... inputs) {
            mMapper.save(mPhoto);
            mMapper.save(mUser);
            mMapper.save(mRecent);
            return null;
        }
    }
}
