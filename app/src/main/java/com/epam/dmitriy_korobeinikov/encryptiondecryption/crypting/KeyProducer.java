package com.epam.dmitriy_korobeinikov.encryptiondecryption.crypting;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by Dmitriy_Korobeinikov on 12/11/2015.
 */
public interface KeyProducer {
    public PublicKey getPublicKey();
    public PrivateKey getPrivateKey();
    public String getKeystoreType();
}

