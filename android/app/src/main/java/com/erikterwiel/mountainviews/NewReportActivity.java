package com.erikterwiel.mountainviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class NewReportActivity extends AppCompatActivity {

    private static final String TAG = "NewReportActivity.java";

    private ArrayList<Bitmap> mBitmaps = new ArrayList<>();

    private User mUser;
    private Photo mPhoto;
    private Report mReport;
    private AmazonS3Client mS3Client;
    private AmazonDynamoDBClient mDDBClient;
    private DynamoDBMapper mMapper;
    private TransferUtility mTransferUtility;

    private LinearLayout mLayout;
    private ImageView mCancel;
    private ImageView mDone;
    private RecyclerView mRecycler;
    private Button mAdd;
    private EditText mReportInput;
    private PictureAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        mLayout = (LinearLayout) findViewById(R.id.new_report_layout);
        mCancel = (ImageView) findViewById(R.id.new_report_cancel);
        mDone = (ImageView) findViewById(R.id.new_report_done);

        mRecycler = (RecyclerView) findViewById(R.id.new_report_recycler);
        mAdd = (Button) findViewById(R.id.new_report_add);
        mReport = (EditText) findViewById(R.id.new_report_report);
        mRecycler.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new PictureAdapter(mBitmaps);
        mRecycler.setAdapter(mAdapter);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                Constants.cognitoUnauthPoolID,
                Regions.US_EAST_1);
        mDDBClient = new AmazonDynamoDBClient(credentialsProvider);
        mMapper = new DynamoDBMapper(mDDBClient);
        mTransferUtility = getTransferUtility(this);
        mUser = new User();
        mReport = new Report();
        new PullUser().execute();

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReport.getText().toString().equals("")) {
                    Toast.makeText(NewReportActivity.this, "Please write a trip report", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(NewReportActivity.this, "Preparing upload...", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                String time = "" + (calendar.getTimeInMillis());
                File folder = new File("sdcard/Pictures/MountainViews/temp");
                if (!folder.exists()) folder.mkdir();
                for (int i = 0; i < mBitmaps.size(); i++) {
                    File toSend = new File(folder, "toSend.png");
                    try {
                        toSend.createNewFile();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        mBitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 0, bos);
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
                    observer.setTransferListener(new NewReportActivity.UploadListener());
                    mPhoto = new Photo();
                    mPhoto.setFilename(mUser.getUsername() + "/" + time);
                    mReport.addPhoto(mPhoto.getFilename());
                    new PushPhoto().execute();
                }

                new PushReport().execute();
                Toast.makeText(NewReportActivity.this, "Trip report uploading!", Toast.LENGTH_LONG).show();
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                mBitmaps.add(bitmap);
                mAdapter.itemAdded(mBitmaps.size() - 1);
                Log.i(TAG, "item fucking isnert");
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

    private class PushReport extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... inputs) {
            mMapper.save(mUser);
            mMapper.save(mReport);
            return null;
        }
    }

    private class PushPhoto extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... inputs) {
            mMapper.save(mPhoto);
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

    private class PictureAdapter extends RecyclerView.Adapter<PictureHolder> {
        private ArrayList<Bitmap> imageList;

        public PictureAdapter(ArrayList<Bitmap> incomingList) {
            imageList = incomingList;
        }

        @Override
        public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(NewReportActivity.this);
            View view = layoutInflater.inflate(R.layout.list_item_photo, parent, false);
            return new NewReportActivity.PictureHolder(view);
        }

        @Override
        public void onBindViewHolder(PictureHolder holder, int position) {
            Bitmap bitmap = imageList.get(position);
            holder.bindPicture(bitmap);
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        public void itemAdded(int position) {
            notifyItemInserted(position);
        }
    }

    private class PictureHolder extends RecyclerView.ViewHolder {
        private ImageView mPicture;

        public PictureHolder(View itemView) {
            super(itemView);
            mPicture = (ImageView) itemView.findViewById(R.id.new_report_image);
        }

        public void bindPicture(Bitmap bitmap) {
            mPicture.setImageBitmap(bitmap);
        }
    }
}
