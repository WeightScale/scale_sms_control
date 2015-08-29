package com.kostya.scale_sms_control;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.konst.sms_commander.SMS;

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

                try {
                    sendSMS(address, SMS.encrypt("htcehc", msg.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.commands:
                startActivity(new Intent(this, ActivityPreferences.class));
            break;
            case R.id.preferences:
                startActivity(new Intent(this, ActivityPreferences.class));
            break;
            default:

        }
        return true;
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

}
