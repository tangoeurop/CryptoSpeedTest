package com.epam.dmitriy_korobeinikov.encryptiondecryption.crypting;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Dmitriy_Korobeinikov on 12/8/2015.
 * Helps to make time logging in convenient way.
 */
public class TimeLogger {

    private String mTag;
    private String mLabel;
    ArrayList<Long> mSplits;
    ArrayList<String> mSplitLabels;

    public TimeLogger(String tag, String label) {
        reset(tag, label);
    }

    public void reset(String tag, String label) {
        mTag = tag;
        mLabel = label;
        reset();
    }

    public void reset() {
        if (mSplits == null) {
            mSplits = new ArrayList<>();
            mSplitLabels = new ArrayList<>();
        } else {
            mSplits.clear();
            mSplitLabels.clear();
        }
        addSplit(null);
    }

    public void addSplit(String splitLabel) {
        long now = System.currentTimeMillis();
        mSplits.add(now);
        mSplitLabels.add(splitLabel);
    }

    public void dumpToLog() {
        Log.d(mTag, mLabel + ": begin");
        final long first = mSplits.get(0);
        long now = 0;
        for (int i = 1; i < mSplits.size(); i++) {
            now = mSplits.get(i);
            final String splitLabel = mSplitLabels.get(i);
            final long prev = mSplits.get(i - 1);

            Log.d(mTag, mLabel + ":      " + (now - prev) + " ms, " + splitLabel);
        }
        Log.d(mTag, mLabel + ": end, " + (now - first) + " ms");
    }

    public ArrayList<Long> getIntervals() {
        ArrayList<Long> intervals = new ArrayList<>();
        long now;
        for (int i = 1; i < mSplits.size(); i++) {
            now = mSplits.get(i);
            final long prev = mSplits.get(i - 1);
            intervals.add(now - prev);
        }
        return intervals;
    }
}

