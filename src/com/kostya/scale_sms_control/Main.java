package com.kostya.scale_sms_control;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import com.kostya.scale_sms_control.service.ServiceSmsCommand;

/**
 * @author Kostya
 */
public class Main extends Application {

    static String user;
    String password;

    /**
     * Максимальное количество дней для удвления чеков дней.
     */
    protected static final int default_day_delete_check = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.load(getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE)); //загрузить настройки
        user = Preferences.read(ActivityPreferences.KEY_USER, "");
        password = Preferences.read(ActivityPreferences.KEY_PASSWORD, "");
        getApplicationContext().startService(new Intent(getApplicationContext(), ServiceSmsCommand.class));
    }
}
