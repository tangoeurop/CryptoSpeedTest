package com.epam.dmitriy_korobeinikov.encryptiondecryption.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.epam.dmitriy_korobeinikov.encryptiondecryption.R;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.util.RSAEncryptionDecryption;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RSAEncryptionDecryption.OnCryptingListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BenchmarkAdapter mEncryptedDataAdapter;
    private BenchmarkAdapter mDecryptedDataAdapter;
    private RSAEncryptionDecryption mRSAEncryptionDecryption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView encryptedDataList = (ListView) findViewById(R.id.lwEncryptionData);
        ListView decryptedDataList = (ListView) findViewById(R.id.lwDecryptionData);
        mEncryptedDataAdapter = new BenchmarkAdapter(this, null);
        mDecryptedDataAdapter = new BenchmarkAdapter(this, null);

        encryptedDataList.setAdapter(mEncryptedDataAdapter);
        decryptedDataList.setAdapter(mDecryptedDataAdapter);

        mRSAEncryptionDecryption = new RSAEncryptionDecryption(this);
        mRSAEncryptionDecryption.setOnCryptingListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                mRSAEncryptionDecryption.startCrypting();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEncryptionCompleted(ArrayList<Long> intervals) {
        mEncryptedDataAdapter.setIntervals(intervals);
        mEncryptedDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDecryptionCompleted(ArrayList<Long> intervals) {
        mDecryptedDataAdapter.setIntervals(intervals);
        mDecryptedDataAdapter.notifyDataSetChanged();
    }

    private class BenchmarkAdapter extends ArrayAdapter<Long> {
        private ArrayList<Long> mIntervals;
        private LayoutInflater mInflater;

        public BenchmarkAdapter(Context context, ArrayList<Long> objects) {
            super(context, 0, objects);
            mIntervals = objects;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = mInflater.inflate(R.layout.list_view_row, parent, false);
            }
            TextView cardNumber = (TextView) v.findViewById(R.id.tvCardNumber);
            cardNumber.setText(getString(R.string.generic_card_number, position + 1));
            TextView duration = (TextView) v.findViewById(R.id.tvDuration);
            duration.setText(String.valueOf(mIntervals.get(position)));
            return v;
        }

        @Override
        public int getCount() {
            if (mIntervals != null) {
                return mIntervals.size();
            }
            return 0;
        }

        public void setIntervals(ArrayList<Long> intervals) {
            mIntervals = intervals;
        }
    }
}
