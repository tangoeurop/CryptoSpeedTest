package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RSAEncryptionDecryption decryption = new RSAEncryptionDecryption(this);
        decryption.startDecryptionFromResource();
    }
}
