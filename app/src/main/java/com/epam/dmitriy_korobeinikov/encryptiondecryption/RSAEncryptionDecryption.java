package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.content.Context;
import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 * Makes encryption and decryption with RSA algorithm.
 */
public class RSAEncryptionDecryption {
    private static final String TAG = RSAEncryptionDecryption.class.getSimpleName();

    private OwnKeyGenerator mKeyRetriever;

    public RSAEncryptionDecryption(Context context) {
        mKeyRetriever = new OwnKeyGenerator(context);
    }

    public void startDecryption() {
        byte[] encryptedData = encryptData("JohnKuper test data");
        decryptData(encryptedData);
    }

    private byte[] encryptData(String data) {
        Log.i(TAG, "----------------ENCRYPTION STARTED------------");

        Log.i(TAG, "Data Before Encryption: " + data);
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = null;
        try {
            KeySpecData privateKeySpecData = mKeyRetriever.getKeySpecData(R.raw.privatekey);
            PrivateKey pubKey = mKeyRetriever.getPrivateKey(privateKeySpecData);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encryptedData = cipher.doFinal(dataToEncrypt);
            Log.i(TAG, "Encryted Data: " + Arrays.toString(encryptedData));

        } catch (Exception e) {
            Log.e(TAG, "Error during encryptData()", e);
        }

        Log.i(TAG, "----------------ENCRYPTION COMPLETED------------");
        return encryptedData;
    }

    private void decryptData(byte[] data) {
        Log.i(TAG, "----------------DECRYPTION STARTED------------");
        byte[] decryptedData;

        try {
            KeySpecData publicKeySpecData = mKeyRetriever.getKeySpecData(R.raw.publickey);
            PublicKey privateKey = mKeyRetriever.getPublicKey(publicKeySpecData);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(data);
            Log.i(TAG, "Decrypted Data: " + new String(decryptedData));

        } catch (Exception e) {
            Log.e(TAG, "Error during decryptData()", e);
        }

        Log.i(TAG, "----------------DECRYPTION COMPLETED------------");
    }
}