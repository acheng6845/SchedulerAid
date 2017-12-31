package com.example.computerlab.projectaid.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import java.util.UUID;

/**
 * Created by achen on 4/26/2017.
 */

public class DataNode implements Parcelable {

    private String nodeId;
    private String nodeName;
    private int duration;
    private String parent0;
    private String parent1;
    private String parent2;
    private String parent3;
    private String parent4;
    private String parent5;
    private String parent6;
    private String parent7;
    private String parent8;

    public DataNode() {}

    public DataNode(String nodeId, String nodeName, int duration, String parent0, String parent1,
                    String parent2, String parent3, String parent4, String parent5,
                    String parent6, String parent7, String parent8) {
        if (nodeId == null) nodeId = UUID.randomUUID().toString();

        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.duration = duration;
        this.parent0 = parent0;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.parent3 = parent3;
        this.parent4 = parent4;
        this.parent5 = parent5;
        this.parent6 = parent6;
        this.parent7 = parent7;
        this.parent8 = parent8;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
