package com.kostya.scale_sms_control;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Kostya on 16.08.2015.
 */
public class Main extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationContext().startService(new Intent(getApplicationContext(), ServiceSmsCommand.class));
    }
}
