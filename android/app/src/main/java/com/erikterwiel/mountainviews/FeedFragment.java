package com.erikterwiel.mountainviews;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment.java";

    private LinearLayout mOuterLayout;
    private LinearLayout mInnerLayout;
    private TextView mPhotoHead;

    private User mUser;
    private AmazonS3Client mS3Client;
    private AmazonDynamoDBClient mDDBClient;
    private DynamoDBMapper mMapper;
    private TransferUtility mTransferUtility;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View outer = inflater.inflate(R.layout.fragment_feed, container, false);
        mOuterLayout = (LinearLayout) outer.findViewById(R.id.feed_layout);
        View inner = inflater.inflate(R.layout.layout_photo, container, false);
        mInnerLayout = (LinearLayout) inner.findViewById(R.id.layout_photo_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(32, 32, 32, 32);

        mPhotoHead = (TextView) inner.findViewById(R.id.layout_photo_head);
        mPhotoHead.setText("wqerhkwrkjqherhqrj");
        mOuterLayout.addView(mInnerLayout, params);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getActivity().getApplicationContext(),
                Constants.cognitoUnauthPoolID,
                Regions.US_EAST_1);
        mDDBClient = new AmazonDynamoDBClient(credentialsProvider);
        mMapper = new DynamoDBMapper(mDDBClient);
        mTransferUtility = getTransferUtility(getActivity());
        new PullUser().execute();
        return outer;
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
            mUser = mMapper.load(User.class, getActivity().getIntent().getStringExtra("username"));
            return null;
        }
    }
}
