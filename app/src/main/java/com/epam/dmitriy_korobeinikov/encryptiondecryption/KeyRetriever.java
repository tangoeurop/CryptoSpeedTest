package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 */
public class KeyRetriever {
    private static final String TAG = KeyRetriever.class.getSimpleName();

    private Context mContext;

    public KeyRetriever(Context context) {
        mContext = context;
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

    public PrivateKey getPrivateKey() {
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.korobeinikov);

        return null;
    }
}
