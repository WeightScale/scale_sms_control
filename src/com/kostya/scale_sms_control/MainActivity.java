package com.kostya.scale_sms_control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;

public class MainActivity extends Activity {
    Button buttonSent;
    EditText editTextAddress, editTextBody, editTextUser;
    private static final String CIPHER_ALGORITHM = "AES";
    private static final String RANDOM_GENERATOR_ALGORITHM = "SHA1PRNG";
    private static final int RANDOM_KEY_SIZE = 128;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextBody = (EditText) findViewById(R.id.editTextBody);
        editTextUser = (EditText) findViewById(R.id.editTextUser);

        buttonSent = (Button) findViewById(R.id.buttonSent);
        buttonSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = String.valueOf(editTextAddress.getText());
                StringBuilder msg = new StringBuilder();
                msg.append(String.valueOf(editTextUser.getText())).append(" ").append(String.valueOf(editTextBody.getText()));

                //sendSMS(address, encodeMessage(msg.toString()));
                try {
                    sendSMS(address, encrypt("htcehc", msg.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        try {
            sms.sendMultipartTextMessage(phoneNumber, "scale", parts, null, null);
        } catch (RuntimeException ignored) {
            ignored.printStackTrace();
        }
    }

    String encodeMessage(String msg) {
        byte[] bytes = msg.getBytes();
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            bytes[i] += (byte) 3;
        }
        return new String(bytes, Charset.forName("UTF-8"));
    }

    // Encrypts string and encodes in Base64
    public static String encrypt(String password, String data) throws Exception {
        byte[] secretKey = generateKey(password.getBytes());
        byte[] clear = data.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, CIPHER_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encrypted = cipher.doFinal(clear);
        String encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);

        return encryptedString;
    }

    // Decrypts string encoded in Base64
    public static String decrypt(String password, String encryptedData) throws Exception {
        byte[] secretKey = generateKey(password.getBytes());
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, CIPHER_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] encrypted = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted);
    }

    public static byte[] generateKey(byte[] seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance(RANDOM_GENERATOR_ALGORITHM);
        secureRandom.setSeed(seed);
        keyGenerator.init(RANDOM_KEY_SIZE, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }
}
