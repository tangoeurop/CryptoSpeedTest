package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.app.Application;

import java.security.Security;

/**
 * Created by Dmitriy_Korobeinikov on 12/11/2015.
 */
public class CryptingApplication extends Application {
    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
}
