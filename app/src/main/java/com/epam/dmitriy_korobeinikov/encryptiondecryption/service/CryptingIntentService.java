package com.epam.dmitriy_korobeinikov.encryptiondecryption.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.CryptingInfo;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.util.RSAEncryptionDecryption;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.io.ByteArrayInputStream;

import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_DATE_FORMAT;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_EXTENSION;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_PREFIX;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_ROW_TIME_FORMAT;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.DropBoxApiSingleton.getInstance;

/**
 * Created by Dmitriy_Korobeinikov on 12/9/2015.
 */
public class CryptingIntentService extends IntentService {
    private static final String TAG = CryptingIntentService.class.getSimpleName();
    public static final String ARG_CRYPTING_RESULT_RECEIVER = "ARG_CRYPTING_RESULT_RECEIVER";
    public static final String ARG_CRYPTING_INFO = "ARG_CRYPTING_INFO";
    private static final char CSV_SEPARATOR = ';';

    public CryptingIntentService() {
        super(TAG);
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

        putDataToDropBox(cryptingInfo);
    }

    private void putDataToDropBox(CryptingInfo cryptingInfo) {
        try {
            String resultData = composeResultData(cryptingInfo);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(resultData.getBytes());
            DropboxAPI.Entry response = getInstance().DBApi.putFile("/" + getFileName(cryptingInfo), inputStream, resultData.length(), null, null);
            Log.i(TAG, "File was successfully uploaded: " + response.path);
        } catch (DropboxException e) {
            Log.e(TAG, "Error during .putDataToDropBox()", e);
        }
    }

    private String composeResultData(CryptingInfo cryptingInfo) {
        StringBuilder sb = new StringBuilder();
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
        return CSV_FILE_NAME_PREFIX + DateFormat.format(CSV_FILE_NAME_DATE_FORMAT, cryptingInfo.startTime) + CSV_FILE_NAME_EXTENSION;
    }

    private void fillConstantParameters(CryptingInfo cryptingInfo, String[] dataRow) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String startDate = DateFormat.format(Constants.CSV_ROW_DATE_FORMAT, cryptingInfo.startTime).toString();
        dataRow[0] = startDate;
        dataRow[3] = Build.MANUFACTURER;
        dataRow[4] = Build.MODEL;
        dataRow[5] = telephonyManager.getDeviceId();
    }
}
