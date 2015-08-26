package com.kostya.scale_sms_control;


import android.os.Handler;

/*
 * Created by Kostya on 04.05.2015.
 */
public abstract class HandlerTaskNotification extends Handler {

    public abstract void handleRemoveEntry(int what, int arg1);

    public abstract void handleNotificationError(int what, int arg1, TaskCommand.MessageNotify msg);

    //public abstract void handleError(int what, String msg);
}
