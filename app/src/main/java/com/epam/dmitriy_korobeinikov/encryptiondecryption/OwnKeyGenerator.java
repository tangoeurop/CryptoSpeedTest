package com.epam.dmitriy_korobeinikov.encryptiondecryption;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by Dmitriy_Korobeinikov on 12/7/2015.
 */
public class OwnKeyGenerator {
    private static final String TAG = OwnKeyGenerator.class.getSimpleName();
    private Context mContext;

    public OwnKeyGenerator(Context context) {
        mContext = context;
    }

    private String mPrivateKeyData = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDgzh8Z/CuzxPNb\n" +
            "DoL6u+SGPcOAWGLNYiXqvjMzWSYei2G8pSQipkPO2Lx9IirMT3596zLmM1NtkChV\n" +
            "yRJiuSWjxO0cTtEgqKQ5nQ2OkI8CalbN5CHUviMefI7KfEjm9zoq3k+Ji1OgtcWV\n" +
            "FmFxvchVXOnPZiw/xH1G2MyA/THFkQQk9Will2eQmgXxWYGJqFfcwIC16FcNUvI6\n" +
            "KkBNhiWtRP3fUspyeA+n+YiB+ihjuQ5NgRXwVy0Buz6hDy7hUGvkiTX+GxL3bRyz\n" +
            "VAa93Qv1vvkzDfxhI72mlC/rXln3nFVYlqtb912fAgkQuQ5DQBJcfRx12YQCH6Wa\n" +
            "ZPl+Vvi3AgMBAAECggEBALfysaxXlYMB84dctnxoZrQZsfFBYYdhelk8zTSFDBYO\n" +
            "QylTj9/yYKXO310dKhwIKB7s57dJ5EhQn1CiuTKjAVOifcqeC61HkSm7gy5Wx05Y\n" +
            "qTLMK4qjEqWNkmHJlPW48pXFVxvbL9DfLA+0QoQJoPWIk6Ern6WGKGRiXeRmSeW4\n" +
            "HF871Po9X0ZBXSUU33DFXr+Bo5TkaX3px6daicFbdkRqlcdzielv6KRvRRNLluNp\n" +
            "dPVQ5J6QD1wUQaYGIU3JbEWSa6wuTwkAlL6LdpaIMS/2Eoh2mTzdkuPbgKtDY8cx\n" +
            "iXZeTEP1y1W/s/PeMzRgcCVxnNWV8zTbzgJg8JqyztECgYEA/706jZDr578Y/GC9\n" +
            "Ehsh2EKSZRXEeFs04vDVRtilTVAFmSIy36OssH/q4MGUSOOPaY33i0Hr2LRkQTyP\n" +
            "IG8pvK/PT+2v14MUIZZSYOdCg+hzqTfHzQ8MFSGnSoGXAKcDtB/NgL305YLGeU7h\n" +
            "Fgny+dgeT8BYFgXp0LpRu01QFNsCgYEA4QjQ8Dp1Kop58kQ/zUlS9AWIFFGv/R9Y\n" +
            "dHdyKm0+c6M3zV7uZZXadOAqB9YCs4AfcedLUf1/tEsH9y9viGD4s+1iFVm+nYCN\n" +
            "HyGM09HRCkLvqZ8YnZQ5ja8CZRSErrmVwLhGsgUpZeydIUY64yPUjgKvsfhQ5TY8\n" +
            "XhVFGImX5FUCgYEAzkdV+WZRwkYT404sc3RLImLgLoUWSnZW/E6B2XlaLDhFq7U4\n" +
            "D8e3vU4QqGW8M+bJgLywVTVBBTTMQQqV7/V6VuYAdFomIdFq5YSYwe0Ha9qNoIqL\n" +
            "sM1YU5snkyNgJ3iPtjSI0DVoWc/YQ3TqfZpc0EYQfQeKhYkThfcjgc7kr00CgYEA\n" +
            "z1SNFFf3rTsqQYCJ+/yLdhJGntnoOGER4TRPtjEycd42QnwtTuDzBe2mK9QMZw8r\n" +
            "aYk9Rww1BYp/i0lkDBWz7ipOzPcDfR98ZetGUb4lR7wGDZRsXHq9UAGnRjPSTxzj\n" +
            "kUz2rXu9+Y838fk/thhD9JcbAvCSr6v5kIEuSP40OIUCgYAS8zwOHtKpOAUt31Lu\n" +
            "ao8YGgF4DK7U9sKxN4TG5s0qzLZnT/2u83r3aBMB4d/EomQ0D1pYqddjG5l1NZOO\n" +
            "K7ZOmWdQsq/UPJFohETxv8fpX7BElb6Wsp4Wv9L1+NwjXpPAqGvwrxnRIxw5V3tD\n" +
            "ayIoHkHlVhBDubDxzEQIcSGBug==";

    public PrivateKey getPrivateKey() {
        byte[] encoded = Base64.decode(mPrivateKeyData, Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encoded);
        PrivateKey privateKey = null;
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e(TAG, "Error during getPrivateKey()", e);
        }
        return privateKey;
    }

    public PublicKey getPublicKey() {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) factory.generateCertificate(mContext.getResources().openRawResource(R.raw.dk));
            return certificate.getPublicKey();
        } catch (CertificateException e) {
            Log.e(TAG, "Error during getPublicKey()", e);
        }
        return null;
    }

}
