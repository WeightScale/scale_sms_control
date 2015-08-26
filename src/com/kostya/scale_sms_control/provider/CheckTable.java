package com.kostya.scale_sms_control.provider;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class CheckTable {
    //final TaskTable taskTable;
    private final Context mContext;
    final ContentResolver contentResolver;
    public static int day;
    public static int day_closed;

    public static final String TABLE = "checkTable";

    public static final String KEY_ID = BaseColumns._ID;
    public static final String KEY_DATE_CREATE = "dateCreate";
    public static final String KEY_TIME_CREATE = "timeCreate";
    public static final String KEY_NUMBER_BT = "numberBt";
    public static final String KEY_WEIGHT_FIRST = "weightFirst";
    public static final String KEY_WEIGHT_SECOND = "weightSecond";
    public static final String KEY_WEIGHT_NETTO = "weightNetto";
    public static final String KEY_VENDOR = "vendor";
    public static final String KEY_VENDOR_ID = "vendorId";
    public static final String KEY_TYPE = "type";
    public static final String KEY_TYPE_ID = "typeId";
    public static final String KEY_PRICE = "price";
    public static final String KEY_PRICE_SUM = "priceSum";
    public static final String KEY_CHECK_ON_SERVER = "checkOnServer";
    public static final String KEY_IS_READY = "checkIsReady";
    public static final String KEY_VISIBILITY = "visibility";
    public static final String KEY_DIRECT = "direct";

    public static final int INVISIBLE = 0;
    public static final int VISIBLE = 1;

    public static final int DIRECT_DOWN = 1;
    public static final int DIRECT_UP = 2;

    public static final String[] All_COLUMN_TABLE = {
            KEY_ID,
            KEY_DATE_CREATE,
            KEY_TIME_CREATE,
            KEY_NUMBER_BT,
            KEY_WEIGHT_FIRST,
            KEY_WEIGHT_SECOND,
            KEY_WEIGHT_NETTO,
            KEY_VENDOR,
            KEY_VENDOR_ID,
            KEY_TYPE,
            KEY_TYPE_ID,
            KEY_PRICE,
            KEY_PRICE_SUM,
            KEY_CHECK_ON_SERVER,
            KEY_IS_READY,
            KEY_VISIBILITY,
            KEY_DIRECT};

    public static final String[] COLUMNS_SMS_ADMIN = {
            /*KEY_ID,*/
            KEY_DATE_CREATE,
            KEY_TIME_CREATE,
            KEY_NUMBER_BT,
            KEY_WEIGHT_FIRST,
            KEY_WEIGHT_SECOND,
            KEY_WEIGHT_NETTO,
            KEY_VENDOR,
            /*KEY_VENDOR_ID,*/
            KEY_TYPE,
            /*KEY_TYPE_ID,*/
            /*KEY_PRICE,*/
            /*KEY_PRICE_SUM,*/
            /*KEY_CHECK_ON_SERVER,*/
            KEY_IS_READY,
            /*KEY_VISIBILITY,*/
            KEY_DIRECT};

    public static final String[] COLUMNS_SMS_CONTACT = {
            /*KEY_ID,*/
            KEY_DATE_CREATE,
            KEY_TIME_CREATE,
            /*KEY_NUMBER_BT,*/
            KEY_WEIGHT_FIRST,
            KEY_WEIGHT_SECOND,
            KEY_WEIGHT_NETTO,
            KEY_VENDOR,
            /*KEY_VENDOR_ID,*/
            KEY_TYPE,
            /*KEY_TYPE_ID,*/
            KEY_PRICE,
            KEY_PRICE_SUM
            /*KEY_CHECK_ON_SERVER,*/
            /*KEY_IS_READY,*/
            /*KEY_VISIBILITY,*/
            /*KEY_DIRECT*/};

    public static final String TABLE_CREATE = "create table "
            + TABLE + " ("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_DATE_CREATE + " text,"
            + KEY_TIME_CREATE + " text,"
            + KEY_NUMBER_BT + " text,"
            + KEY_WEIGHT_FIRST + " integer,"
            + KEY_WEIGHT_SECOND + " integer,"
            + KEY_WEIGHT_NETTO + " integer,"
            + KEY_VENDOR + " text,"
            + KEY_VENDOR_ID + " integer,"
            + KEY_TYPE + " text,"
            + KEY_TYPE_ID + " integer,"
            + KEY_PRICE + " integer,"
            + KEY_PRICE_SUM + " real,"
            + KEY_CHECK_ON_SERVER + " integer,"
            + KEY_IS_READY + " integer,"
            + KEY_VISIBILITY + " integer,"
            + KEY_DIRECT + " integer );";

    //static final String TABLE_CHECKS_PATH = TABLE_CHECKS;
    private static final Uri CONTENT_URI = Uri.parse("content://" + BaseProviderSmsControl.AUTHORITY + '/' + TABLE);

    public CheckTable(Context context) {
        mContext = context;
        contentResolver = mContext.getContentResolver();
        //taskTable = new TaskTable(context);
    }

    public CheckTable(Context context, int d) {
        mContext = context;
        contentResolver = mContext.getContentResolver();
        day = d;
        //taskTable = new TaskTable(context);
    }

    public Uri insertNewEntry(ContentValues values){
        return contentResolver.insert(CONTENT_URI, values);
    }

    void removeEntry(int _rowIndex) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        contentResolver.delete(uri, null, null);
    }

    public void deleteCheckIsServer(/*long  dayAfter*/) {
        try {
            Cursor result = contentResolver.query(CONTENT_URI, new String[]{KEY_ID, KEY_DATE_CREATE},
                    KEY_CHECK_ON_SERVER + "= 1" + " and " + KEY_VISIBILITY + "= " + INVISIBLE, null, null);
            if (result.getCount() > 0) {
                result.moveToFirst();
                if (!result.isAfterLast()) {
                    do {
                        int id = result.getInt(result.getColumnIndex(KEY_ID));
                        removeEntry(id);
                    } while (result.moveToNext());
                }
            }
            result.close();
        } catch (Exception e) {
        }
    }

    public void invisibleCheckIsReady(long dayAfter) {
        try {
            Cursor result = contentResolver.query(CONTENT_URI, new String[]{KEY_ID, KEY_DATE_CREATE},
                    KEY_IS_READY + "= 1" /*and " + KEY_VISIBILITY + "= " + VISIBLE*/, null, null);
            result.moveToFirst();
            if (!result.isAfterLast()) {
                do {
                    int id = result.getInt(result.getColumnIndex(KEY_ID));
                    String date = result.getString(result.getColumnIndex(KEY_DATE_CREATE));
                    long day = 0;
                    try {
                        day = dayDiff(new Date(), new SimpleDateFormat("dd.MM.yy").parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (day > dayAfter) {
                        updateEntry(id, KEY_VISIBILITY, INVISIBLE);
                    } else {
                        updateEntry(id, KEY_VISIBILITY, VISIBLE);
                    }
                } while (result.moveToNext());
            }
            result.close();
        } catch (Exception e) {
        }
    }

    long dayDiff(Date d1, Date d2) {
        final long DAY_MILLIS = 1000 * 60 * 60 * 24;
        long day1 = d1.getTime() / DAY_MILLIS;
        long day2 = d2.getTime() / DAY_MILLIS;
        return day1 - day2;
    }

    private String getKeyString(int _rowIndex, String key) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            Cursor result = contentResolver.query(uri, new String[]{KEY_ID, key}, null, null, null);
            result.moveToFirst();
            String str = result.getString(result.getColumnIndex(key));
            result.close();
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    public Cursor getAllEntries(int view) {
        return contentResolver.query(CONTENT_URI, All_COLUMN_TABLE, KEY_IS_READY + "= 1" + " and " + KEY_VISIBILITY + "= " + view, null, null);
    }

    public Cursor getAllEntries() {
        return contentResolver.query(CONTENT_URI, null, null, null, null);
    }

    public Cursor getAllNoReadyCheck() {
        return contentResolver.query(CONTENT_URI, All_COLUMN_TABLE, KEY_CHECK_ON_SERVER + "= 0" + " and " + KEY_IS_READY + "= 0", null, null);
    }

    public Cursor getNotReady() {
        return contentResolver.query(CONTENT_URI, All_COLUMN_TABLE, KEY_IS_READY + "= 0", null, null);
    }

    public Cursor getEntryItem(int _rowIndex) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            Cursor result = contentResolver.query(uri, All_COLUMN_TABLE, null, null, null);
            result.moveToFirst();
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public Cursor getEntryItem(int _rowIndex, String... columns) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            Cursor result = contentResolver.query(uri, columns, null, null, null);
            result.moveToFirst();
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public ContentValues getValuesItem(int _rowIndex) throws Exception {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            Cursor result = contentResolver.query(uri, All_COLUMN_TABLE, null, null, null);
            result.moveToFirst();
            ContentQueryMap mQueryMap = new ContentQueryMap(result, BaseColumns._ID, true, null);
            Map<String, ContentValues> map = mQueryMap.getRows();
            return map.get(String.valueOf(_rowIndex));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public boolean updateEntry(int _rowIndex, String key, int in) {
        //boolean b;
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            ContentValues newValues = new ContentValues();
            newValues.put(key, in);
            return contentResolver.update(uri, newValues, null, null) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateEntry(int _rowIndex, ContentValues values) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            return contentResolver.update(uri, values, null, null) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateEntry(int _rowIndex, String key, float fl) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            ContentValues newValues = new ContentValues();
            newValues.put(key, fl);
            contentResolver.update(uri, newValues, null, null);
        } catch (Exception e) {
        }
    }

    public boolean updateEntry(int _rowIndex, String key, String st) {
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, _rowIndex);
        try {
            ContentValues newValues = new ContentValues();
            newValues.put(key, st);
            return contentResolver.update(uri, newValues, null, null) > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
