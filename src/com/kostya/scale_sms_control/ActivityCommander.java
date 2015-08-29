package com.kostya.scale_sms_control;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.kostya.scale_sms_control.service.ServiceSmsCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kostya
 */
public class ActivityCommander extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    String KEY_GET_ERROR    = "get_error";
    String KEY_DELETE_ERROR = "delete_error";

    public ActivityCommander() {
        mapPreferences.put(KEY_GET_ERROR, new GetError());
        mapPreferences.put(KEY_DELETE_ERROR, new DeleteError());
        /*mapPreferences.put(KEY_NULL, new PreferenceNull());
        mapPreferences.put(KEY_FILTER, new PreferenceFilter());
        mapPreferences.put(KEY_UPDATE, new PreferenceUpdate());
        mapPreferences.put(KEY_TIMER, new PreferenceTimer());
        mapPreferences.put(KEY_TIMER_NULL, new PreferenceTimerNull());
        mapPreferences.put(KEY_MAX_NULL, new PreferenceMaxNull());
        mapPreferences.put(KEY_STEP, new PreferenceStep());
        mapPreferences.put(KEY_AUTO_CAPTURE, new PreferenceAutoCapture());
        mapPreferences.put(KEY_DAY_CLOSED_CHECK, new PreferenceDayClosedCheck());
        mapPreferences.put(KEY_DAY_CHECK_DELETE, new PreferenceDayCheckDelete());
        mapPreferences.put(KEY_ABOUT, new PreferenceAbout());*/
    }

    interface InterfacePreference {
        void setup(Preference name) throws Exception;
    }

    final Map<String, InterfacePreference> mapPreferences = new HashMap<>();

    void process() {
        for (Map.Entry<String, InterfacePreference> preferenceEntry : mapPreferences.entrySet()) {
            Preference name = findPreference(preferenceEntry.getKey());
            if (name != null) {
                try {
                    preferenceEntry.getValue().setup(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("my_preferences");
        preferenceManager.setSharedPreferencesMode(MODE_PRIVATE);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        process();
        getApplicationContext().startService(new Intent(getApplicationContext(), ServiceSmsCommand.class));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    class GetError implements InterfacePreference{

        @Override
        public void setup(Preference name) throws Exception {

        }
    }

    class DeleteError implements InterfacePreference{

        @Override
        public void setup(Preference name) throws Exception {

        }
    }
}
