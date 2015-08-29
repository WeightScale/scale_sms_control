package com.kostya.scale_sms_control.provider;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author Kostya
 */
public class ScalesTable {
    Context mContext;
    public static final String TABLE = "scalesTable";
    public static final String KEY_ID = BaseColumns._ID;
    public static final String KEY_SCALE = "scale";
    public static final String KEY_BLUETOOTH = "bluetooth";
    public static final String KEY_PHONE = "phone";


    public static final String TABLE_CREATE = "create table "
            + TABLE + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_SCALE + " text, "
            + KEY_BLUETOOTH + " text, "
            + KEY_PHONE + " text );";

    private static final Uri CONTENT_URI = Uri.parse("content://" + BaseProviderSmsControl.AUTHORITY + '/' + TABLE);

    ScalesTable(Context context){
        mContext = context;
    }

}
