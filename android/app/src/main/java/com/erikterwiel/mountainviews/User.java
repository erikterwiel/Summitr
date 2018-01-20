package com.erikterwiel.mountainviews;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;

@DynamoDBTable(tableName = "users")
public class User {

    private String mUsername;
    private String mEmail;
    private List<String> mFollowing;
    private List<String> mFollowers;
    private List<String> mPlans;
    private List<String> mPosts;
    private List<String> mPhotos;
    private List<String> mActivity;
    private double mLongitude;
    private double mLatitude;

    @DynamoDBHashKey(attributeName = "username")
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    @DynamoDBAttribute (attributeName = "email")
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    @DynamoDBAttribute (attributeName = "following")
    public List<String> getFollowing() {
        return mFollowing;
    }

    public void setFollowing(List<String> following) {
        mFollowing = following;
    }

    @DynamoDBAttribute (attributeName = "followers")
    public List<String> getFollowers() {
        return mFollowers;
    }

    public void setFollowers(List<String> followers) {
        mFollowers = followers;
    }

    @DynamoDBAttribute (attributeName = "plans")
    public List<String> getPlans() {
        return mPlans;
    }

    public void setPlans(List<String> plans) {
        mPlans = plans;
    }

    @DynamoDBAttribute (attributeName = "posts")
    public List<String> getPosts() {
        return mPosts;
    }

    public void setPosts(List<String> posts) {
        mPosts = posts;
    }

    @DynamoDBAttribute (attributeName = "photos")
    public List<String> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(List<String> photos) {
        mPhotos = photos;
    }

    @DynamoDBAttribute (attributeName = "activities")
    public List<String> getActivity() {
        return mActivity;
    }

    public void setActivity(List<String> activity) {
        mActivity = activity;
    }

    @DynamoDBAttribute (attributeName = "longitude")
    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    @DynamoDBAttribute (attributeName = "latitude")
    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }
}
