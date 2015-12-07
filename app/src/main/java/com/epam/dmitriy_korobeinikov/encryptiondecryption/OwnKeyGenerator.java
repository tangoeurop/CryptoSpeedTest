package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 */
public class OwnKeyGenerator {
    private static final String TAG = OwnKeyGenerator.class.getSimpleName();
    private Context mContext;

    public OwnKeyGenerator(Context context) {
        mContext = context;
    }

    public KeySpecData getKeySpecData(@RawRes int fileId) {
        KeySpecData keySpecData = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            keySpecData = mapper.readValue(mContext.getResources().openRawResource(fileId), KeySpecData.class);
        } catch (IOException e) {
            Log.e(TAG, "Error during getPublicKye()", e);
        }
        return keySpecData;
    }

    public PublicKey getPublicKey(KeySpecData keySpecData) {
        PublicKey publicKey = null;
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(keySpecData.modulus, keySpecData.exponent);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public PrivateKey getPrivateKey(KeySpecData keySpecData) {
        PrivateKey publicKey = null;
        RSAPrivateKeySpec rsaPublicKeySpec = new RSAPrivateKeySpec(keySpecData.modulus, keySpecData.exponent);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePrivate(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

}
