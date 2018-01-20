package com.erikterwiel.mountainviews;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;
import java.util.List;

@DynamoDBTable(tableName = "reports")
public class Report {

    private String mTitle;
    private String mLocation;
    private String mDate;
    private String mDistance;
    private String mReport;
    private List<String> mPhotos;

    public Report() {
        mPhotos = new ArrayList<>();
    }

    @DynamoDBHashKey (attributeName = "title")
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @DynamoDBAttribute (attributeName = "location")
    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    @DynamoDBAttribute (attributeName = "date")
    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    @DynamoDBAttribute (attributeName = "distance")
    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    @DynamoDBAttribute (attributeName = "report")
    public String getReport() {
        return mReport;
    }

    public void setReport(String report) {
        mReport = report;
    }

    public void addPhoto(String toAdd) {
        mPhotos.add(toAdd);
    }

    @DynamoDBAttribute (attributeName = "photos")
    public List<String> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(List<String> photos) {
        mPhotos = photos;
    }
}
