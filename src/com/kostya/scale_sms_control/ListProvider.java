package com.kostya.scale_sms_control;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.kostya.scale_sms_control.provider.CheckTable;

import java.util.ArrayList;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 *
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<ListItem> listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;
    Cursor cursor;
    CheckTable checkTable;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        checkTable = new CheckTable(context);
        if(cursor != null)
            cursor.close();
        cursor = checkTable.getAllEntries();
        //populateListItem();
    }

    /*private void populateListItem() {
        for (int i = 0; i < 10; i++) {
            ListItem listItem = new ListItem();
            listItem.heading = "Heading" + i;
            listItem.content = String.valueOf(i);
            listItemList.add(listItem);
        }

    }*/

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if(cursor.moveToPosition(position)){
            long rowId = cursor.getLong(0);
            return rowId;
        }else{
            return 0;
        }
        //return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {

        cursor.moveToPosition(position);
        RemoteViews row=new RemoteViews(context.getPackageName(), R.layout.row);
        row.setTextViewText(R.id.listCheck, cursor.getString(2));

        /*final RemoteViews remoteView = new RemoteViews( context.getPackageName(), R.layout.item_check);
        ListItem listItem = listItemList.get(position);
        remoteView.setTextViewText(R.id.vendor, listItem.heading);
        remoteView.setTextViewText(R.id.check_id, listItem.content);

        return remoteView;*/

        /*ListItem listItem = listItemList.get(position);
        RemoteViews row=new RemoteViews(context.getPackageName(), R.layout.row);

        row.setTextViewText(R.id.listCheck, listItem.heading);*/

        /*Intent i=new Intent();
        Bundle extras=new Bundle();

        extras.putString(WidgetProvider.EXTRA_WORD, items[position]);
        i.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, i);*/

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    class ListItem{

        public String heading;
        public String content;
    }
}