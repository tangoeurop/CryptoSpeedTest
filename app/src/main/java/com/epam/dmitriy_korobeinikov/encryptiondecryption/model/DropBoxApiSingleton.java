package com.epam.dmitriy_korobeinikov.encryptiondecryption.model;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

/**
 * Created by Dmitriy_Korobeinikov on 12/10/2015.
 */
public class DropBoxApiSingleton {
    private static final DropBoxApiSingleton INSTANCE = new DropBoxApiSingleton();
    public DropboxAPI<AndroidAuthSession> DBApi;

    private DropBoxApiSingleton() {
    }

    public static DropBoxApiSingleton getInstance() {
        return INSTANCE;
    }
}
