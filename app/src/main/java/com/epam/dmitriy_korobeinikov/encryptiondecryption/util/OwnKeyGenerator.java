package com.epam.dmitriy_korobeinikov.encryptiondecryption.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.epam.dmitriy_korobeinikov.encryptiondecryption.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 */
public class OwnKeyGenerator {
    private static final String TAG = OwnKeyGenerator.class.getSimpleName();
    private Context mContext;

    public OwnKeyGenerator(Context context) {
        mContext = context;
    }

    public PrivateKey getPrivateKey() {
        byte[] encoded = Base64.decode(readPrivateKeyPem(), Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encoded);
        PrivateKey privateKey = null;
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e(TAG, "Error during getPrivateKey()", e);
        }
        return privateKey;
    }

    public PublicKey getPublicKey() {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(mContext.getResources().openRawResource(R.raw.dk));
            return certificate.getPublicKey();
        } catch (CertificateException e) {
            Log.e(TAG, "Error during getPublicKey()", e);
        }
        return null;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private String readPrivateKeyPem() {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.privatekeypem));
        BufferedReader br = new BufferedReader(isr);

        String line;
        try {
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error during readPrivateKeyPem()", e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                Log.e(TAG, "Error in finally block during readPrivateKeyPem()", e);
            }
        }
        return stringBuilder.toString();
    }
}
