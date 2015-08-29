package com.kostya.scale_sms_control.provider;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * @author Kostya
 */
public class BaseProviderSmsControl extends ContentProvider {
    private static final String DATABASE_NAME = "smsControl.db";
    private static final int DATABASE_VERSION = 1;
    static final String AUTHORITY = "com.kostya.scale_sms_control.controller";
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROWS = 2;

    private enum TableList {
        CHECK_LIST,
        CHECK_ID,
        SCALES_LIST,
        SCALES_ID,
        SENDER_LIST,
        SENDER_ID,
        TASK_LIST,
        TASK_ID
    }

    private static final UriMatcher uriMatcher;
    private SQLiteDatabase db;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CheckTable.TABLE, TableList.CHECK_LIST.ordinal());
        uriMatcher.addURI(AUTHORITY, CheckTable.TABLE + "/#", TableList.CHECK_ID.ordinal());
        uriMatcher.addURI(AUTHORITY, ScalesTable.TABLE, TableList.SCALES_LIST.ordinal());
        uriMatcher.addURI(AUTHORITY, ScalesTable.TABLE + "/#", TableList.SCALES_ID.ordinal());
        uriMatcher.addURI(AUTHORITY, SenderTable.TABLE, TableList.SENDER_LIST.ordinal());
        uriMatcher.addURI(AUTHORITY, SenderTable.TABLE + "/#", TableList.SENDER_ID.ordinal());
        uriMatcher.addURI(AUTHORITY, TaskTable.TABLE, TableList.TASK_LIST.ordinal());
        uriMatcher.addURI(AUTHORITY, TaskTable.TABLE + "/#", TableList.TASK_ID.ordinal());
    }

    private String getTable(Uri uri) {
        switch (TableList.values()[uriMatcher.match(uri)]) {
            case CHECK_LIST:
            case CHECK_ID:
                return CheckTable.TABLE; // return
            case SCALES_LIST:
            case SCALES_ID:
                return ScalesTable.TABLE; // return
            case SENDER_LIST:
            case SENDER_ID:
                return SenderTable.TABLE; // return
            case TASK_LIST:
            case TASK_ID:
                return TaskTable.TABLE; // return
            /** PROVIDE A DEFAULT CASE HERE **/
            default:
                // If the URI doesn't match any of the known patterns, throw an exception.
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();
        if (db != null) {
            db.setLockingEnabled(false);
        }
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                return "vnd.android.cursor.dir/vnd.";
            case SINGLE_ROWS:
                return "vnd.android.cursor.item/vnd.";
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (TableList.values()[uriMatcher.match(uri)]) {
            case CHECK_LIST: // общий Uri
                queryBuilder.setTables(CheckTable.TABLE);
                break;
            case CHECK_ID: // Uri с ID
                queryBuilder.setTables(CheckTable.TABLE);
                queryBuilder.appendWhere(BaseColumns._ID + '=' + uri.getLastPathSegment());
                break;
            case SCALES_LIST: // общий Uri
                queryBuilder.setTables(ScalesTable.TABLE);
                break;
            case SCALES_ID: // Uri с ID
                queryBuilder.setTables(ScalesTable.TABLE);
                queryBuilder.appendWhere(BaseColumns._ID + '=' + uri.getLastPathSegment());
                break;
            case SENDER_LIST: // общий Uri
                queryBuilder.setTables(SenderTable.TABLE);
                break;
            case SENDER_ID: // Uri с ID
                queryBuilder.setTables(SenderTable.TABLE);
                queryBuilder.appendWhere(BaseColumns._ID + '=' + uri.getLastPathSegment());
                break;
            case TASK_LIST: // общий Uri
                queryBuilder.setTables(TaskTable.TABLE);
                break;
            case TASK_ID: // Uri с ID
                queryBuilder.setTables(TaskTable.TABLE);
                queryBuilder.appendWhere(BaseColumns._ID + '=' + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sort);
        if (cursor == null) {
            return null;
        }
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                cursor.setNotificationUri(contentResolver, uri);
            }
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = db.insert(getTable(uri), null, contentValues);
        if (rowID > 0L) {
            Uri resultUri = ContentUris.withAppendedId(uri, rowID);
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
        }
        throw new SQLiteException("Ошибка добавления записи " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArg) {
        int delCount;
        String id;
        switch (TableList.values()[uriMatcher.match(uri)]) {
            case CHECK_LIST: // общий Uri
                delCount = db.delete(CheckTable.TABLE, where, whereArg);
                break;
            case CHECK_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                delCount = db.delete(CheckTable.TABLE, where, whereArg);
                break;
            case SCALES_LIST: // общий Uri
                delCount = db.delete(ScalesTable.TABLE, where, whereArg);
                break;
            case SCALES_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                delCount = db.delete(ScalesTable.TABLE, where, whereArg);
                break;
            case SENDER_LIST: // общий Uri
                delCount = db.delete(SenderTable.TABLE, where, whereArg);
                break;
            case SENDER_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                delCount = db.delete(SenderTable.TABLE, where, whereArg);
                break;
            case TASK_LIST: // общий Uri
                delCount = db.delete(TaskTable.TABLE, where, whereArg);
                break;
            case TASK_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                delCount = db.delete(TaskTable.TABLE, where, whereArg);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db.execSQL("VACUUM");
        if (delCount > 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereArg) {
        int updateCount;
        String id;
        switch (TableList.values()[uriMatcher.match(uri)]) {
            case CHECK_LIST: // общий Uri
                updateCount = db.update(CheckTable.TABLE, contentValues, where, whereArg);
                break;
            case CHECK_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                updateCount = db.update(CheckTable.TABLE, contentValues, where, whereArg);
                break;
            case SCALES_LIST: // общий Uri
                updateCount = db.update(ScalesTable.TABLE, contentValues, where, whereArg);
                break;
            case SCALES_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                updateCount = db.update(ScalesTable.TABLE, contentValues, where, whereArg);
                break;
            case SENDER_LIST: // общий Uri
                updateCount = db.update(SenderTable.TABLE, contentValues, where, whereArg);
                break;
            case SENDER_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                updateCount = db.update(SenderTable.TABLE, contentValues, where, whereArg);
                break;
            case TASK_LIST: // общий Uri
                updateCount = db.update(TaskTable.TABLE, contentValues, where, whereArg);
                break;
            case TASK_ID:
                id = uri.getLastPathSegment();
                where = TextUtils.isEmpty(where) ? BaseColumns._ID + " = " + id : where + " AND " + BaseColumns._ID + " = " + id;
                updateCount = db.update(TaskTable.TABLE, contentValues, where, whereArg);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        if (updateCount > 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return updateCount;
    }

    private static class DBHelper extends SQLiteOpenHelper {
        SenderTable senderTable;

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            senderTable = new SenderTable(context);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CheckTable.TABLE_CREATE);
            db.execSQL(ScalesTable.TABLE_CREATE);
            db.execSQL(SenderTable.TABLE_CREATE);
            db.execSQL(TaskTable.TABLE_CREATE);

            senderTable.addSystemSheet(db);
            senderTable.addSystemHTTP(db);
            senderTable.addSystemMail(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE_IF_EXISTS + CheckTable.TABLE);
            db.execSQL(DROP_TABLE_IF_EXISTS + ScalesTable.TABLE);
            db.execSQL(DROP_TABLE_IF_EXISTS + SenderTable.TABLE);
            db.execSQL(DROP_TABLE_IF_EXISTS + TaskTable.TABLE);
            onCreate(db);
        }
    }
}
