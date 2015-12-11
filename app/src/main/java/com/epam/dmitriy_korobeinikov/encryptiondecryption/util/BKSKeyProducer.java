package com.epam.dmitriy_korobeinikov.encryptiondecryption.util;

import android.content.Context;
import android.util.Log;

import com.epam.dmitriy_korobeinikov.encryptiondecryption.R;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

/**
 * Created by Dmitriy_Korobeinikov on 12/11/2015.
 * Generated public and private keys from BKS type keystore.
 */
public class BKSKeyProducer implements KeyProducer {
    private static final String TAG = BKSKeyProducer.class.getSimpleName();
    private Context mContext;

    public BKSKeyProducer(Context context) {
        mContext = context;
    }

    @Override
    public PublicKey getPublicKey() {
        KeyPair keyPair = getKeyPair();
        if (keyPair != null) {
            return keyPair.getPublic();
        }
        return null;
    }

    @Override
    public PrivateKey getPrivateKey() {
        KeyPair keyPair = getKeyPair();
        if (keyPair != null) {
            return keyPair.getPrivate();
        }
        return null;
    }

    @Override
    public String getKeystoreType() {
        return Constants.BKS_KEYSTORE_TYPE;
    }

    private KeyPair getKeyPair() {
        InputStream is = null;
        try {
            is = mContext.getResources().openRawResource(R.raw.sha_256_rsa_2048);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, Constants.KEY_STORE_PASSWORD.toCharArray());

            Key key = keystore.getKey(Constants.KEY_STORE_ALIAS, Constants.PRIVATE_KEY_PASSWORD.toCharArray());
            if (key instanceof PrivateKey) {
                Certificate cert = keystore.getCertificate(Constants.KEY_STORE_ALIAS);
                PublicKey publicKey = cert.getPublicKey();
                return new KeyPair(publicKey, (PrivateKey) key);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during getKeyPair()", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error in finally block during getKeyPair()", e);
            }
        }
        return null;
    }
}
