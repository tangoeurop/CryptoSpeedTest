package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            RSAEncryptionDecryption decryption = new RSAEncryptionDecryption(this);
            decryption.startDecryption();
        } catch (IOException e) {
            Log.e(TAG, "Error during encryption/decryption");
        }

//        KeyRetriever retriever = new KeyRetriever(this);
//        PublicKey publicKey = retriever.getPublicKey();
//        PrivateKey privateKey = retriever.getPrivateKey();
//
//        try {
//            KeyFactory factory = KeyFactory.getInstance("RSA");
//            RSAPublicKeySpec rsaPubKeySpec = factory.getKeySpec(publicKey, RSAPublicKeySpec.class);
//            RSAPrivateKeySpec rsaPrivateKeySpec = factory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
//            Log.i(TAG, "PubKey Modulus : " + rsaPubKeySpec.getModulus());
//            Log.i(TAG, "PubKey Exponent : " + rsaPubKeySpec.getPublicExponent());
//            Log.i(TAG, "PrivateKey Modulus : " + rsaPrivateKeySpec.getModulus());
//            Log.i(TAG, "PrivateKey Exponent : " + rsaPrivateKeySpec.getPrivateExponent());
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
    }
}
