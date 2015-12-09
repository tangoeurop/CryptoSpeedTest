package com.epam.dmitriy_korobeinikov.encryptiondecryption.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
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
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.CryptingInfo;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.service.CryptingService;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BenchmarkAdapter mEncryptedDataAdapter;
    private BenchmarkAdapter mDecryptedDataAdapter;
    private CryptingResultReceiver mResultReceiver;

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

        mResultReceiver = new CryptingResultReceiver(new Handler());
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
                Intent cryptingIntent = new Intent(this, CryptingService.class);
                cryptingIntent.putExtra(CryptingService.ARG_CRYPTING_RESULT_RECEIVER, mResultReceiver);
                startService(cryptingIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void writeToFile() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File csvFile = new File(path, "test");
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csvFile));

            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[]{"India", "New Delhi"});
            data.add(new String[]{"United States", "Washington D.C"});
            data.add(new String[]{"Germany", "Berlin"});

            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CryptingResultReceiver extends ResultReceiver {

        public CryptingResultReceiver(Handler handler) {
            super(handler);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            CryptingInfo cryptingInfo = resultData.getParcelable(CryptingService.ARG_CRYPTING_INFO);
            if (cryptingInfo != null) {
                mDecryptedDataAdapter.setIntervals(cryptingInfo.decryptedIntervals);
                mEncryptedDataAdapter.setIntervals(cryptingInfo.encryptedIntervals);
                mDecryptedDataAdapter.notifyDataSetChanged();
                mEncryptedDataAdapter.notifyDataSetChanged();
            }
        }
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
            duration.setText(getString(R.string.duration_time, mIntervals.get(position)));
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
