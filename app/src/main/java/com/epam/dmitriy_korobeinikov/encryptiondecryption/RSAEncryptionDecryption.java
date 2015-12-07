package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 * Makes encryption and decryption with RSA algorithm.
 */
public class RSAEncryptionDecryption {
    private static final String TAG = RSAEncryptionDecryption.class.getSimpleName();

    private static final String PUBLIC_KEY_FILE = "Public.key";
    private static final String PRIVATE_KEY_FILE = "Private.key";

    private Context mContext;

    public RSAEncryptionDecryption(Context context) {
        mContext = context;
    }

    public void startDecryption() throws IOException {

        try {
            Log.i(TAG, "-------GENERATE PUBLIC and PRIVATE KEY-------------");
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); //1024 used for normal securities
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            Log.i(TAG, "Public Key - " + publicKey);
            Log.i(TAG, "Private Key - " + privateKey);

            //Pulling out parameters which makes up Key
            Log.i(TAG, "------- PULLING OUT PARAMETERS WHICH MAKES KEYPAIR----------");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            Log.i(TAG, "PubKey Modulus : " + rsaPubKeySpec.getModulus());
            Log.i(TAG, "PubKey Exponent : " + rsaPubKeySpec.getPublicExponent());
            Log.i(TAG, "PrivateKey Modulus : " + rsaPrivKeySpec.getModulus());
            Log.i(TAG, "PrivateKey Exponent : " + rsaPrivKeySpec.getPrivateExponent());

            //Share public key with other so they can encrypt data and decrypt those using private key(Don't share with Other)
            Log.i(TAG, "--------SAVING PUBLIC KEY AND PRIVATE KEY TO FILES-------");
            saveKeys(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(), rsaPubKeySpec.getPublicExponent());
            saveKeys(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(), rsaPrivKeySpec.getPrivateExponent());

            //Encrypt Data using Public Key
            byte[] encryptedData = encryptData("JohnKuper test data");

            //Decrypt Data using Private Key
            decryptData(encryptedData);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private void saveKeys(String fileName, BigInteger mod, BigInteger exp) throws IOException {
        File file = new File(mContext.getFilesDir(), fileName);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            Log.i(TAG, "Generating " + fileName + "...");
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));

            oos.writeObject(mod);
            oos.writeObject(exp);

            Log.i(TAG, fileName + " generated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();

                fos.close();
            }
        }
    }

    private byte[] encryptData(String data) throws IOException {
        Log.i(TAG, "----------------ENCRYPTION STARTED------------");

        Log.i(TAG, "Data Before Encryption: " + data);
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = null;
        try {
            PrivateKey pubKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encryptedData = cipher.doFinal(dataToEncrypt);
            Log.i(TAG, "Encryted Data: " + Arrays.toString(encryptedData));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "----------------ENCRYPTION COMPLETED------------");
        return encryptedData;
    }

    private void decryptData(byte[] data) throws IOException {
        Log.i(TAG, "----------------DECRYPTION STARTED------------");
        byte[] decryptedData;

        try {
            PublicKey privateKey = readPublicKeyFromFile(PUBLIC_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedData = cipher.doFinal(data);
            Log.i(TAG, "Decrypted Data: " + new String(decryptedData));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "----------------DECRYPTION COMPLETED------------");
    }

    public PublicKey readPublicKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(mContext.getFilesDir(), fileName));
            ois = new ObjectInputStream(fis);

            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //Get Public Key
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");

            return fact.generatePublic(rsaPublicKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                ois.close();
                fis.close();
            }
        }
        return null;
    }

    public PrivateKey readPrivateKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(mContext.getFilesDir(), fileName));
            ois = new ObjectInputStream(fis);

            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();

            //Get Private Key
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");

            return fact.generatePrivate(rsaPrivateKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                ois.close();
                fis.close();
            }
        }
        return null;
    }
}