package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
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
    private String[] creditCards = {"4485872600441719", "4716319930319174", "4024007103826169", "4916169588657609", "4014735555784856",
            "4916090704447552", "4485364917348258", "4916971584452871", "4929087640101710", "4485355147668275"};

    public RSAEncryptionDecryption(Context context) {
        mKeyRetriever = new OwnKeyGenerator(context);
    }

    public void startDecryption() {
        LinkedList<byte[]> encryptedDataList = encryptData(creditCards);
        try {
            saveFileIntoPublicStorage(encryptedDataList);
            decryptData(readCreditCardFromEncryptedFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LinkedList<byte[]> encryptData(String[] creditCards) {
        Log.i(TAG, "----------------ENCRYPTION STARTED------------");

        LinkedList<byte[]> encryptedDataList = new LinkedList<>();
        try {
            KeySpecData privateKeySpecData = mKeyRetriever.getKeySpecData(R.raw.privatekey);
            PrivateKey privateKey = mKeyRetriever.getPrivateKey(privateKeySpecData);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            long duration = 0;
            for (String card : creditCards) {
                byte[] dataToEncrypt = card.getBytes();
                byte[] encryptedData;
                long oneCycleStartTime = System.currentTimeMillis();
                encryptedData = cipher.doFinal(dataToEncrypt);
                long localDuration = (System.currentTimeMillis() - oneCycleStartTime);
                duration += localDuration;
//                Log.i(TAG, "Card has been encrypted in " + localDuration + " milliseconds");
                encryptedDataList.add(encryptedData);
                Log.i(TAG, "Encrypted data: " + Arrays.toString(encryptedData));
            }
            Log.i(TAG, "----------------ENCRYPTION COMPLETED------------");
//            Log.i(TAG, "TOTAL TIME: " + duration + " milliseconds");

        } catch (Exception e) {
            Log.e(TAG, "Error during encryptData()", e);
        }
        return encryptedDataList;
    }

    private void decryptData(LinkedList<byte[]> encryptedDataList) {
        Log.i(TAG, "----------------DECRYPTION STARTED------------");
        byte[] decryptedData;

        try {
            KeySpecData publicKeySpecData = mKeyRetriever.getKeySpecData(R.raw.publickey);
            PublicKey privateKey = mKeyRetriever.getPublicKey(publicKeySpecData);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            long startTime = System.currentTimeMillis();
            for (byte[] data : encryptedDataList) {
                long oneCycleStartTime = System.currentTimeMillis();
                decryptedData = cipher.doFinal(data);
                Log.i(TAG, "Decrypted Data: " + new String(decryptedData));
//                Log.i(TAG, "Card has been decrypted in " + (System.currentTimeMillis() - oneCycleStartTime) + " milliseconds");
            }
            Log.i(TAG, "----------------DECRYPTION COMPLETED------------");
//            Log.i(TAG, "TOTAL TIME: " + (System.currentTimeMillis() - startTime) + " milliseconds");

        } catch (Exception e) {
            Log.e(TAG, "Error during decryptData()", e);
        }
    }

    public void saveFileIntoPublicStorage(LinkedList<byte[]> creditCards) throws IOException {
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

    public LinkedList<byte[]> readCreditCardFromEncryptedFile() throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "CreditCards.txt");
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        LinkedList<byte[]> cards = new LinkedList<>();
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
}