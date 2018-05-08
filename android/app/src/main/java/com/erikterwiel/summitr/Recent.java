package com.erikterwiel.summitr;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;
import java.util.List;

@DynamoDBTable(tableName = "recents")
public class Recent {

    private long mTime;
    private String mIdentifier;
    private String mType;
    private String mUsername;

    @DynamoDBHashKey (attributeName = "time")
    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    @DynamoDBAttribute (attributeName = "identifier")
    public String getIdentifier() {
        return mIdentifier;
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
    }

    @DynamoDBAttribute (attributeName = "type")
    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    @DynamoDBAttribute (attributeName = "username")
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }
}
