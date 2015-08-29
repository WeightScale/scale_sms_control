package com.kostya.scale_sms_control;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.kostya.scale_sms_control.provider.SenderTable;
import com.kostya.scale_sms_control.service.ServiceSmsCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kostya
 */
public class ActivityPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    Preferences preferences;
    protected Dialog dialog;


    public static final String KEY_USER         = "key_user";
    public static final String KEY_PASSWORD     = "key_password";
    public static final String KEY_GOOGLE_DISK  = "key_google_disk";
    public static final String KEY_CLOUD        = "key_cloud";
    public static final String KEY_EMAIL        = "key_email";
    public static final String KEY_SENDER       = "key_sender";

    public ActivityPreferences() {
        mapPreferences.put(KEY_USER, new User());
        mapPreferences.put(KEY_PASSWORD, new Password());
        mapPreferences.put(KEY_GOOGLE_DISK, new GoogleDisk());
        mapPreferences.put(KEY_CLOUD, new Cloud());
        mapPreferences.put(KEY_EMAIL, new Email());
        mapPreferences.put(KEY_SENDER, new Sender());
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

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("my_preferences");
        preferenceManager.setSharedPreferencesMode(MODE_PRIVATE);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        preferences = new Preferences(this, Preferences.PREFERENCES);
        process();
        //getApplicationContext().startService(new Intent(getApplicationContext(), ServiceSmsCommand.class));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    class User implements InterfacePreference{

        @Override
        public void setup(Preference name) throws Exception {
            //name.setTitle("Account Google: " + ' ' + Preferences.read(KEY_USER, "null"));
            name.setSummary("Account name: " + Preferences.read(KEY_USER, "null"));
            name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o.toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.preferences_no, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    preference.setSummary("Account name: " + o);
                    Preferences.write(KEY_USER, o.toString());
                    Toast.makeText(getApplicationContext(), R.string.preferences_yes, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    class Password implements InterfacePreference{

        @Override
        public void setup(Preference name) throws Exception {
            name.setSummary("Password account - " + "*******");
            name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o.toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.preferences_no, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    preference.setSummary("Password account - " + "*******");
                    Preferences.write(KEY_PASSWORD, o.toString());
                    Toast.makeText(getApplicationContext(), R.string.preferences_yes, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    class GoogleDisk implements InterfacePreference{

        @Override
        public void setup(Preference name) throws Exception {

            name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (o.toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.preferences_no, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    //Preferences.write(KEY_GOOGLE_DISK, (boolean)o);
                    Toast.makeText(getApplicationContext(), R.string.preferences_yes, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    class Cloud implements InterfacePreference{

        @Override
        public void setup(Preference name) throws Exception {

        }
    }

    class Email implements InterfacePreference{

        @Override
        public void setup(Preference name) throws Exception {

        }
    }

    class Sender implements InterfacePreference{
        Context mContext;
        SenderTable senderTable;



        @Override
        public void setup(Preference name) throws Exception {
            mContext = getApplicationContext();
            senderTable = new SenderTable(mContext);

            name.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openListDialog();
                    return false;
                }
            });
        }

        public void openListDialog() {
            final Cursor senders = senderTable.getAllEntries();
            //final Cursor emails = contentResolver.query(CommonDataKinds.Email.CONTENT_URI, null,CommonDataKinds.Email.CONTACT_ID + " = " + mContactId, null, null);
            if (senders == null) {
                return;
            }
            if (senders.moveToFirst()) {
                String[] columns = {SenderTable.KEY_TYPE};
                int[] to = {R.id.text1};
                SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(mContext, R.layout.item_list_sender, senders, columns, to);
                cursorAdapter.setViewBinder(new ListBinder());
                //LayoutInflater layoutInflater = mContext.getLayoutInflater();
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = layoutInflater.inflate(R.layout.dialog_sender, null);
                ListView listView = (ListView) convertView.findViewById(R.id.component_list);
                TextView dialogTitle = (TextView) convertView.findViewById(R.id.dialog_title);
                dialogTitle.setText("Выбрать отсылатель");
                listView.setAdapter(cursorAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Checkable v = (Checkable) view;
                        v.toggle();
                        if (v.isChecked())
                            senderTable.updateEntry((int)id, SenderTable.KEY_SYS, 1);
                        else
                            senderTable.updateEntry((int) id, SenderTable.KEY_SYS, 0);
                    }
                });
                dialog.setContentView(convertView);
                dialog.setCancelable(false);
                ImageButton buttonSelectAll = (ImageButton) dialog.findViewById(R.id.buttonSelectAll);
                buttonSelectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        selectedAll();
                    }
                });
                ImageButton buttonUnSelect = (ImageButton) dialog.findViewById(R.id.buttonUnselect);
                buttonUnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        unselectedAll();
                    }
                });
                ImageButton buttonBack = (ImageButton) dialog.findViewById(R.id.buttonBack);
                buttonBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }

        private void selectedAll(){
            Cursor cursor = senderTable.getAllEntries();
            try {
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                        senderTable.updateEntry(id,SenderTable.KEY_SYS, 1);
                    } while (cursor.moveToNext());
                }
            }catch (Exception e){ }
        }

        private void unselectedAll(){
            Cursor cursor = senderTable.getAllEntries();
            try {
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                        senderTable.updateEntry(id, SenderTable.KEY_SYS, 0);
                    } while (cursor.moveToNext());
                }
            }catch (Exception e){ }
        }

        private class ListBinder implements SimpleCursorAdapter.ViewBinder {
            int enable;
            int type;
            String text;

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                switch (view.getId()) {
                    case R.id.text1:
                        enable = cursor.getInt(cursor.getColumnIndex(SenderTable.KEY_SYS));
                        type = cursor.getInt(cursor.getColumnIndex(SenderTable.KEY_TYPE));
                        text = SenderTable.TypeSender.values()[type].toString();
                        //text = cursor.getString(cursor.getColumnIndex(SenderTable.KEY_TYPE));
                        setViewText((TextView) view, text);
                        if(enable > 0)
                            ((Checkable) view).setChecked(true);
                        else
                            ((Checkable) view).setChecked(false);
                        break;
                    default:
                        return false;
                }
                return true;
            }

            public void setViewText(TextView v, CharSequence text) {
                v.setText(text);
            }
        }

    }
}
