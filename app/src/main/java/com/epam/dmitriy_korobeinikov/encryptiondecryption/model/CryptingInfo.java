package com.epam.dmitriy_korobeinikov.encryptiondecryption.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitriy_Korobeinikov on 12/9/2015.
 * Holds benchmark data after encryption/decryption process
 */

public class CryptingInfo implements Parcelable {
    public long startTime;
    public ArrayList<Long> decryptedIntervals;
    public ArrayList<Long> encryptedIntervals;

    public CryptingInfo(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.startTime);
        dest.writeList(this.decryptedIntervals);
        dest.writeList(this.encryptedIntervals);
    }

    protected CryptingInfo(Parcel in) {
        this.startTime = in.readLong();
        this.decryptedIntervals = new ArrayList<>();
        in.readList(this.decryptedIntervals, List.class.getClassLoader());
        this.encryptedIntervals = new ArrayList<>();
        in.readList(this.encryptedIntervals, List.class.getClassLoader());
    }

    public static final Creator<CryptingInfo> CREATOR = new Creator<CryptingInfo>() {
        public CryptingInfo createFromParcel(Parcel source) {
            return new CryptingInfo(source);
        }

        public CryptingInfo[] newArray(int size) {
            return new CryptingInfo[size];
        }
    };
}
