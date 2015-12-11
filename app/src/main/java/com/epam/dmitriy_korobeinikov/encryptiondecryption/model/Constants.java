package com.epam.dmitriy_korobeinikov.encryptiondecryption.model;

/**
 * Created by Dmitriy_Korobeinikov on 12/9/2015.
 * Holds basic constants
 */
public class Constants {
    public static final String CSV_ROW_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CSV_ROW_TIME_FORMAT = "HH:mm:ss:SSS";
    public static final String CSV_FILE_NAME_DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    public static final String CSV_FILE_NAME_PREFIX = "decrypt_";
    public static final String CSV_FILE_NAME_EXTENSION = ".csv";
    public static final String CSV_FILE_HEADER = "Date;decrypt_time;encrypt_time;marka;model;imei;keystore_type\n";
    public static final String JKS_KEYSTORE_TYPE = "JKS";
    public static final String BKS_KEYSTORE_TYPE = "BKS";
    public static final String KEY_STORE_PASSWORD = "android";
    public static final String KEY_STORE_ALIAS = "business";
    public static final String PRIVATE_KEY_PASSWORD = "android";
}
