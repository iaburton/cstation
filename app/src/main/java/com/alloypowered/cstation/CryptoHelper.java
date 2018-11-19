package com.alloypowered.cstation;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.security.KeyStore;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;

public final class CryptoHelper {

    private static final String TAG = "CryptoHelper";
    private static final String ALGO = "AES/GCM/NoPadding";;
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String CHARSET = "UTF-8";

    private static final SecretKey key = getKey();
    public static final boolean SecureHardware = getSecureHardwareInfo();

    private CryptoHelper() {}

    private static SecretKey getKey() {
        final String ALIAS = "CryptoHelperAlias";

        try {
            //since we're targeting a high android API level
            //exceptions here shouldn't happen
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);

            if (keyStore.containsAlias(ALIAS)) {
                return ((KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS, null)).getSecretKey();
            }

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            keyGenerator.init(new KeyGenParameterSpec.Builder(ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());

            return keyGenerator.generateKey();
        } catch (Exception e) {
            Log.e(TAG, "Exception when finding or generating Key: ", e);
        }

        //should never happen
        return null;
    }

    private static boolean getSecureHardwareInfo() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(key.getAlgorithm(), ANDROID_KEY_STORE);
            KeyInfo ki = (KeyInfo) factory.getKeySpec(key, KeyInfo.class);
            return ki.isInsideSecureHardware();
        } catch (Exception e) {
            Log.w(TAG, "Could not get KeyInfo: ", e);
        }

        return false;
    }

    public static String encryptText(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] iv = cipher.getIV();
            if (iv == null) {
                Log.e(TAG, "Cipher.init returned null IV");
                return "";
            }

            byte[] asUTF8 = plaintext.getBytes(CHARSET);
            byte[] finalBuf = new byte[iv.length + cipher.getOutputSize(asUTF8.length)];
            System.arraycopy(iv, 0, finalBuf, 0, iv.length);

            cipher.doFinal(asUTF8, 0, asUTF8.length, finalBuf, iv.length);
            //Base64 it as well for SharedPreferences sake
            return Base64.encodeToString(finalBuf, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(TAG, "Could not encrypt text: ", e);
        }

        return "";
    }

    public static String decryptText(String encrypted) {
        byte[] mainBuf = Base64.decode(encrypted, Base64.NO_WRAP);
        //iv for AES-GCM is default size of 12 bytes
        byte[] iv = Arrays.copyOfRange(mainBuf, 0, 12);

        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));

            return new String(cipher.doFinal(mainBuf, 12, mainBuf.length-12), CHARSET);
        } catch (Exception e) {
            Log.e(TAG, "Could not decrypt text: ", e);
        }

        return "";
    }
}
