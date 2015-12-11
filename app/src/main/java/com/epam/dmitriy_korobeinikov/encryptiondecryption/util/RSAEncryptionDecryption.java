package com.epam.dmitriy_korobeinikov.encryptiondecryption.util;

import android.content.Context;
import android.util.Log;

import com.epam.dmitriy_korobeinikov.encryptiondecryption.R;
import com.epam.dmitriy_korobeinikov.encryptiondecryption.model.CryptingInfo;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 * Makes encryption and decryption with RSA algorithm.
 */
public class RSAEncryptionDecryption {
    private static final String TAG = RSAEncryptionDecryption.class.getSimpleName();

    private KeyProducer mKeyProducer;
    private Context mContext;
    private CryptingInfo mCryptingInfo;

    private String[] creditCards = {"4485872600441719", "4716319930319174", "4024007103826169", "4916169588657609",
            "4014735555784856", "4916090704447552", "4485364917348258", "4916971584452871", "4929087640101710", "4485355147668275"};

    public RSAEncryptionDecryption(Context context, KeyProducer keyProducer) {
        mContext = context;
        mKeyProducer = keyProducer;
    }

    public void startCrypting() {
        mCryptingInfo = new CryptingInfo(System.currentTimeMillis());
        mCryptingInfo.keystoreType = mKeyProducer.getKeystoreType();
        ArrayList<byte[]> encryptData = encryptData(creditCards);
        decryptData(encryptData);
    }

    private ArrayList<byte[]> encryptData(String[] creditCards) {
        Log.i(TAG, "----------------ENCRYPTION STARTED------------");

        ArrayList<byte[]> encryptedDataList = new ArrayList<>();
        try {
            PublicKey key = mKeyProducer.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            TimeLogger timeLogger = new TimeLogger(TAG, "encryptData()");
            byte[] encryptedData;
            byte[] dataToEncrypt;
            for (String card : creditCards) {
                dataToEncrypt = card.getBytes();
                encryptedData = cipher.doFinal(dataToEncrypt);
                timeLogger.addSplit("encryption completed");
                encryptedDataList.add(encryptedData);
                Log.i(TAG, "Encrypted data: " + Arrays.toString(encryptedData));
            }
            Log.i(TAG, "----------------ENCRYPTION COMPLETED------------");
            timeLogger.dumpToLog();
            mCryptingInfo.encryptedIntervals = timeLogger.getIntervals();

        } catch (Exception e) {
            Log.e(TAG, "Error during encryptData()", e);
        }
        return encryptedDataList;
    }

    private ArrayList<String> decryptData(ArrayList<byte[]> encryptedCards) {
        Log.i(TAG, "----------------DECRYPTION STARTED------------");

        ArrayList<String> decryptedCards = new ArrayList<>();
        try {
            PrivateKey key = mKeyProducer.getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            TimeLogger timeLogger = new TimeLogger(TAG, "decryptData()");
            byte[] decryptedData;
            for (byte[] card : encryptedCards) {
                decryptedData = cipher.doFinal(card);
                timeLogger.addSplit("decryption completed");
                decryptedCards.add(new String(decryptedData));
                Log.i(TAG, "Decrypted Data: " + new String(decryptedData));
            }
            Log.i(TAG, "----------------DECRYPTION COMPLETED------------");
            timeLogger.dumpToLog();
            mCryptingInfo.decryptedIntervals = timeLogger.getIntervals();

        } catch (Exception e) {
            Log.e(TAG, "Error during decryptData()", e);
        }
        return decryptedCards;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public ArrayList<byte[]> readEncryptedCreditCardsFromResource() throws IOException {
        InputStream is = null;
        ObjectInputStream ois = null;
        ArrayList<byte[]> cards = new ArrayList<>();
        try {
            is = mContext.getResources().openRawResource(R.raw.encryptedcreditcards);
            ois = new ObjectInputStream(is);

            while (true) {
                cards.add((byte[]) ois.readObject());
            }
        } catch (EOFException e) {
            return cards;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                ois.close();
                is.close();
            }
        }
        return cards;
    }

    public CryptingInfo getCryptingInfo() {
        return mCryptingInfo;
    }
}