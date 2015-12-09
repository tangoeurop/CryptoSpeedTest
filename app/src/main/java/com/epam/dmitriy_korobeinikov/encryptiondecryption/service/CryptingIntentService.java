package com.epam.dmitriy_korobeinikov.encryptiondecryption.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.os.ResultReceiver;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.CryptingInfo;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.util.RSAEncryptionDecryption;
import com.opencsv.CSVWriter;

import org.apache.commons.lang.time.DurationFormatUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_DATE_FORMAT;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_EXTENSION;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_FILE_NAME_PREFIX;
import static com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants.CSV_ROW_TIME_FORMAT;

/**
 * Created by Dmitriy_Korobeinikov on 12/9/2015.
 */
public class CryptingIntentService extends IntentService {
    private static final String TAG = CryptingIntentService.class.getSimpleName();
    public static final String ARG_CRYPTING_RESULT_RECEIVER = "ARG_CRYPTING_RESULT_RECEIVER";
    public static final String ARG_CRYPTING_INFO = "ARG_CRYPTING_INFO";
    public static final String CRYPTO_SPEED_TEST_FOLDER_NAME = "CryptoSpeedTest";
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
        writeDataToFile(cryptingInfo);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void writeDataToFile(CryptingInfo cryptingInfo) {
        File folder = new File(Environment.getExternalStorageDirectory(), CRYPTO_SPEED_TEST_FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/" + CRYPTO_SPEED_TEST_FOLDER_NAME, "DropBoxTest.csv");
        try {
            FileWriter fileWriter = new FileWriter(file);
            CSVWriter csvWriter = new CSVWriter(fileWriter, CSV_SEPARATOR, '\u0000');

            String[] infoRow = new String[6];
            fillConstantlyParameters(cryptingInfo, infoRow);
            for (int i = 0; i < 10; i++) {
                infoRow[1] = DurationFormatUtils.formatDuration(cryptingInfo.decryptedIntervals.get(i), CSV_ROW_TIME_FORMAT);
                infoRow[2] = DurationFormatUtils.formatDuration(cryptingInfo.encryptedIntervals.get(i), CSV_ROW_TIME_FORMAT);
                csvWriter.writeNext(infoRow);
            }
            csvWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error during writeDataToFile()", e);
        }
    }

    private String getFileName(CryptingInfo cryptingInfo) {
        return CSV_FILE_NAME_PREFIX + DateFormat.format(CSV_FILE_NAME_DATE_FORMAT, cryptingInfo.startTime) + CSV_FILE_NAME_EXTENSION;
    }

    private void fillConstantlyParameters(CryptingInfo cryptingInfo, String[] dataRow) {
        String startDate = DateFormat.format(Constants.CSV_ROW_DATE_FORMAT, cryptingInfo.startTime).toString();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        dataRow[0] = startDate;
        dataRow[3] = Build.MANUFACTURER;
        dataRow[4] = Build.MODEL;
        dataRow[5] = telephonyManager.getDeviceId();
    }
}
