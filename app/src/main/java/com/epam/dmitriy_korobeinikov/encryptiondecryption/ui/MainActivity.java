package com.epam.dmitriy_korobeinikov.encryptiondecryption.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.R;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.CryptingInfo;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.service.CryptingIntentService;

import java.util.ArrayList;

import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.DropBoxApiSingleton.getInstance;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String APP_KEY = "vlvd92u9h0s60tf";
    private static final String APP_SECRET = "e8kllhglzgr20ql";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

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

        AndroidAuthSession session = buildSession();
        getInstance().DBApi = new DropboxAPI<>(session);
        getInstance().DBApi.getSession().startOAuth2Authentication(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = getInstance().DBApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                // Store it locally in our app for later use
                storeAuthInPref(session);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Couldn't authenticate with Dropbox:" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error authenticating", e);
            }
        }
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
                Intent cryptingIntent = new Intent(this, CryptingIntentService.class);
                cryptingIntent.putExtra(CryptingIntentService.ARG_CRYPTING_RESULT_RECEIVER, mResultReceiver);
                startService(cryptingIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        loadAuthFromPref(session);
        return session;
    }

    private void storeAuthInPref(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(getPackageName(), 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
        }
    }

    private void loadAuthFromPref(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(getPackageName(), 0);
        String key = prefs.getString(getPackageName(), null);
        String secret = prefs.getString(getPackageName(), null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;
        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        }
    }

    private class CryptingResultReceiver extends ResultReceiver {

        public CryptingResultReceiver(Handler handler) {
            super(handler);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            CryptingInfo cryptingInfo = resultData.getParcelable(CryptingIntentService.ARG_CRYPTING_INFO);
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
