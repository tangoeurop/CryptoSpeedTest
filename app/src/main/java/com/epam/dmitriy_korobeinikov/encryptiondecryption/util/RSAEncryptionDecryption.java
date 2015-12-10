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

    private OwnKeyGenerator mKeyRetriever;
    private Context mContext;
    private CryptingInfo mCryptingInfo;

    public RSAEncryptionDecryption(Context context) {
        mContext = context;
        mKeyRetriever = new OwnKeyGenerator(context);
    }

    public void startCrypting() {
        try {
            mCryptingInfo = new CryptingInfo(System.currentTimeMillis());
            ArrayList<String> decryptCards = decryptData(readEncryptedCreditCardsFromResource());
            encryptData(decryptCards);
        } catch (IOException e) {
            Log.e(TAG, "Error during startDecryptionFromResource()", e);
        }
    }

    private ArrayList<String> decryptData(ArrayList<byte[]> encryptedCards) {
        Log.i(TAG, "----------------DECRYPTION STARTED------------");

        ArrayList<String> decryptedCards = new ArrayList<>();
        try {
            PublicKey key = mKeyRetriever.getPublicKey();
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

    private ArrayList<byte[]> encryptData(ArrayList<String> creditCards) {
        Log.i(TAG, "----------------ENCRYPTION STARTED------------");

        ArrayList<byte[]> encryptedDataList = new ArrayList<>();
        try {
            PrivateKey key = mKeyRetriever.getPrivateKey();
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