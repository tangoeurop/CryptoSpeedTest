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
import java.io.InputStream;
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
    private String[] creditCards = {"4485872600441719", "4716319930319174", "4024007103826169", "4916169588657609", "4014735555784856", "4916090704447552", "4485364917348258", "4916971584452871", "4929087640101710", "4485355147668275"};
    private Context mContext;

    public RSAEncryptionDecryption(Context context) {
        mContext = context;
        mKeyRetriever = new OwnKeyGenerator(context);
    }

    public void startDecryption() {
        LinkedList<byte[]> encryptedDataList = encryptData(creditCards);
        try {
            saveCreditCardsToEncryptedFile(encryptedDataList);
            decryptData(readCreditCardsFromEncryptedFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LinkedList<byte[]> encryptData(String[] creditCards) {
        Log.i(TAG, "----------------ENCRYPTION STARTED------------");

        LinkedList<byte[]> encryptedDataList = new LinkedList<>();
        try {
            PrivateKey key = mKeyRetriever.getPrivateKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            for (String card : creditCards) {
                byte[] dataToEncrypt = card.getBytes();
                byte[] encryptedData;
                encryptedData = cipher.doFinal(dataToEncrypt);
                encryptedDataList.add(encryptedData);
                Log.i(TAG, "Encrypted data: " + Arrays.toString(encryptedData));
            }
            Log.i(TAG, "----------------ENCRYPTION COMPLETED------------");

        } catch (Exception e) {
            Log.e(TAG, "Error during encryptData()", e);
        }
        return encryptedDataList;
    }

    private void decryptData(LinkedList<byte[]> encryptedDataList) {
        Log.i(TAG, "----------------DECRYPTION STARTED------------");
        byte[] decryptedData;

        try {
            PublicKey key = mKeyRetriever.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            for (byte[] data : encryptedDataList) {
                decryptedData = cipher.doFinal(data);
                Log.i(TAG, "Decrypted Data: " + new String(decryptedData));
            }
            Log.i(TAG, "----------------DECRYPTION COMPLETED------------");

        } catch (Exception e) {
            Log.e(TAG, "Error during decryptData()", e);
        }
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

    public LinkedList<byte[]> readCreditCardsFromEncryptedFile() throws IOException {
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

    public LinkedList<byte[]> readEncryptedCreditCardsFromResource() throws IOException {
        InputStream is = null;
        ObjectInputStream ois = null;
        LinkedList<byte[]> cards = new LinkedList<>();
        try {
            is = mContext.getResources().openRawResource(R.raw.creditcards);
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
}