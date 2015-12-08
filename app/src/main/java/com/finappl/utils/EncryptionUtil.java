package com.finappl.utils;

import android.util.Log;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

    private static final String CLASS_NAME = EncryptionUtil.class.getName();
    static final String ALGORITHM = "AES";
    static final String KEY = "DreamBigThinkBig"; // 128 bit key... 16 digits

    public static String encrypt(String string) throws Exception {
        try{
            // Create key and cipher
            Key aesKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(string.getBytes());
            return new String(encrypted);
        }
        catch(Exception e) {
            Log.e(CLASS_NAME, "ERROR!! in encryption : "+e);
        }
        return null;
    }

    public static String decrypt(String string) throws Exception {
        try {
            // Create key and cipher
            Key aesKey = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text
            byte[] encrypted = cipher.doFinal(string.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(encrypted));
        }
        catch(Exception e) {
            Log.e(CLASS_NAME, "ERROR!! in Decryption : "+e);
        }
        return null;
    }

    private static Key generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey;
    }
}
