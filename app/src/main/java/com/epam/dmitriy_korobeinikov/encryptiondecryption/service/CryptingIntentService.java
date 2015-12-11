package com.epam.dmitriy_korobeinikov.encryptiondecryption.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.CryptingInfo;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.util.RSAEncryptionDecryption;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_DATE_FORMAT;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_EXTENSION;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_PREFIX;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_ROW_DATE_FORMAT;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_ROW_TIME_FORMAT;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.DropBoxApiSingleton.getInstance;

/**
 * Created by Dmitriy_Korobeinikov on 12/9/2015.
 * Performs encryption/decryption process in the background thread. Uploads benchmark data to DropBox.
 */
public class CryptingIntentService extends IntentService {
    private static final String TAG = CryptingIntentService.class.getSimpleName();
    public static final String ARG_CRYPTING_RESULT_RECEIVER = "ARG_CRYPTING_RESULT_RECEIVER";
    public static final String ARG_CRYPTING_INFO = "ARG_CRYPTING_INFO";
    private static final char CSV_SEPARATOR = ';';

    private Handler mHandler;

    public CryptingIntentService() {
        super(TAG);
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver cryptingResultReceiver = intent.getParcelableExtra(ARG_CRYPTING_RESULT_RECEIVER);
        RSAEncryptionDecryption rsaEncryptionDecryption = new RSAEncryptionDecryption(this);
        rsaEncryptionDecryption.startCrypting();

        CryptingInfo cryptingInfo = rsaEncryptionDecryption.getCryptingInfo();

        Bundle result = new Bundle();
        result.putParcelable(ARG_CRYPTING_INFO, cryptingInfo);
        cryptingResultReceiver.send(Activity.RESULT_OK, result);

//        putDataToDropBox(cryptingInfo);
//        getKeyStoreFromDropBox();
    }

    private void putDataToDropBox(CryptingInfo cryptingInfo) {
        try {
            String resultData = composeResultData(cryptingInfo);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(resultData.getBytes());
            DropboxAPI.Entry response = getInstance().DBApi.putFile("/" + getFileName(cryptingInfo), inputStream, resultData.length(), null, null);
            mHandler.post(new DisplayToast(this, "File was successfully uploaded: " + response.path));
        } catch (DropboxException e) {
            Log.e(TAG, "Error during .putDataToDropBox()", e);
        }
    }

    private void getKeyStoreFromDropBox() {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "crypto.txt");
        try {
            FileOutputStream fos = new FileOutputStream(path);
            DropboxAPI.DropboxFileInfo info = getInstance().DBApi.getFile("/crypto.txt", null, fos, null);
            Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
        } catch (FileNotFoundException | DropboxException e) {
            e.printStackTrace();
        }
    }

    private String composeResultData(CryptingInfo cryptingInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.CSV_FILE_HEADER);
        String[] infoRow = new String[6];
        fillConstantParameters(cryptingInfo, infoRow);
        for (int x = 0; x < 10; x++) {
            infoRow[1] = DurationFormatUtils.formatDuration(cryptingInfo.decryptedIntervals.get(x), CSV_ROW_TIME_FORMAT);
            infoRow[2] = DurationFormatUtils.formatDuration(cryptingInfo.encryptedIntervals.get(x), CSV_ROW_TIME_FORMAT);
            for (int y = 0; y < infoRow.length; y++) {
                sb.append(infoRow[y]);
                if (y < infoRow.length - 1) {
                    sb.append(CSV_SEPARATOR);
                } else {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    private String getFileName(CryptingInfo cryptingInfo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CSV_FILE_NAME_DATE_FORMAT);
        return CSV_FILE_NAME_PREFIX + dateFormat.format(new Date(cryptingInfo.startTime)) + CSV_FILE_NAME_EXTENSION;
    }

    private void fillConstantParameters(CryptingInfo cryptingInfo, String[] dataRow) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        SimpleDateFormat dateFormat = new SimpleDateFormat(CSV_ROW_DATE_FORMAT);
        String startDate = dateFormat.format(cryptingInfo.startTime);
        dataRow[0] = startDate;
        dataRow[3] = Build.MANUFACTURER;
        dataRow[4] = Build.MODEL;
        dataRow[5] = telephonyManager.getDeviceId();
    }

    private class DisplayToast implements Runnable {
        private final Context mContext;
        String mText;

        public DisplayToast(Context context, String text) {
            this.mContext = context;
            mText = text;
        }

        public void run() {
            Toast.makeText(mContext, mText, Toast.LENGTH_LONG).show();
        }
    }
}
