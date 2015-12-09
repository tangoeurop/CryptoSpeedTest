package com.epam.dmitriy_korobeinikov.encryptiondecryption.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.epam.dmitriy_korobeinikov.encryptiondecryption.R;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.Cipher;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 * Makes encryption and decryption with RSA algorithm.
 */
public class RSAEncryptionDecryption {
    private static final String TAG = RSAEncryptionDecryption.class.getSimpleName();

    private OwnKeyGenerator mKeyRetriever;
    private Context mContext;

    private ArrayList<Long> mDecryptedIntervals;
    private ArrayList<Long> mEncryptedIntervals;

    public RSAEncryptionDecryption(Context context) {
        mContext = context;
        mKeyRetriever = new OwnKeyGenerator(context);
    }

    public void startCrypting() {
        try {
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
            mDecryptedIntervals = (timeLogger.getIntervals());

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
            mEncryptedIntervals = (timeLogger.getIntervals());

        } catch (Exception e) {
            Log.e(TAG, "Error during encryptData()", e);
        }
        return encryptedDataList;
    }

    public void saveCreditCardsToEncryptedFile(LinkedList<byte[]> creditCards) throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "CreditCards.txt");
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            for (byte[] card : creditCards) {
                oos.writeObject(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();
                fos.close();
            }
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public ArrayList<byte[]> readCreditCardsFromEncryptedFile() throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "CreditCards.txt");
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        ArrayList<byte[]> cards = new ArrayList<>();
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);

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
                fis.close();
            }
        }
        return cards;
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

    public ArrayList<Long> getDecryptedIntervals() {
        return mDecryptedIntervals;
    }

    public ArrayList<Long> getEncryptedIntervals() {
        return mEncryptedIntervals;
    }
}