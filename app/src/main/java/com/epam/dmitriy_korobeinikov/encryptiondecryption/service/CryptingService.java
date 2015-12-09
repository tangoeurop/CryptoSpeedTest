package com.epam.dmitriy_korobeinikov.encryptiondecryption.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.epam.dmitriy_korobeinikov.encryptiondecryption.util.RSAEncryptionDecryption;

/**
 * Created by Dmitriy_Korobeinikov on 12/9/2015.
 */
public class CryptingService extends IntentService {
    private static final String TAG = CryptingService.class.getSimpleName();
    public static final String ARG_CRYPTING_RESULT_RECEIVER = "ARG_CRYPTING_RESULT_RECEIVER";
    public static final String ARG_DECRYPTED_INTERVALS = "ARG_DECRYPTED_INTERVALS";
    public static final String ARG_ENCRYPTED_INTERVALS = "ARG_ENCRYPTED_INTERVALS";

    public CryptingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver cryptingResultReceiver = intent.getParcelableExtra(ARG_CRYPTING_RESULT_RECEIVER);
        RSAEncryptionDecryption rsaEncryptionDecryption = new RSAEncryptionDecryption(this);
        rsaEncryptionDecryption.startCrypting();

        Bundle result = new Bundle();
        result.putSerializable(ARG_DECRYPTED_INTERVALS, rsaEncryptionDecryption.getDecryptedIntervals());
        result.putSerializable(ARG_ENCRYPTED_INTERVALS, rsaEncryptionDecryption.getEncryptedIntervals());

        cryptingResultReceiver.send(Activity.RESULT_OK, result);
    }
}
