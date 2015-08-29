package com.kostya.scale_sms_control.service;

import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import com.konst.sms_commander.GsmAlphabet;
import com.konst.sms_commander.OnSmsCommandListener;
import com.konst.sms_commander.SMS;
import com.konst.sms_commander.SmsCommander;
import com.kostya.scale_sms_control.provider.CheckTable;
import com.kostya.scale_sms_control.provider.TaskTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Kostya
 */
public class ServiceSmsCommand extends Service {

    /**
     * Экземпляр приемника смс сообщений.
     */
    final IncomingSMSReceiver incomingSMSReceiver = new IncomingSMSReceiver();
    /**
     * Кодовое слово для дешифрации сообщения
     */
    final String codeword = "weightcheck";
    /**
     * Таг сообщений типа команды.
     */
    final String COMMAND_TAG = "command";
    /**
     * Таг сообщения типа весовой чек.
     */
    final String CHECK_TAG = "check";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter(IncomingSMSReceiver.SMS_DELIVER_ACTION);
        intentFilter.addAction(IncomingSMSReceiver.SMS_RECEIVED_ACTION);
        intentFilter.addAction(IncomingSMSReceiver.SMS_COMPLETED_ACTION);
        intentFilter.setPriority(9999);
        registerReceiver(incomingSMSReceiver, intentFilter);

        Date date = new Date();
        String msg = "check(dateCreate=10.08.2000 timeCreate="+new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date)+" numberBt=00:00:00:00 weightFirst=12587 weightSecond=236 weightNetto=4587 vendor=Конь type=смешаный )";
        try {
            //String str = SMS.encrypt(codeword, msg);
            GsmAlphabet.createFakeSms(this, "380503285426", SMS.encrypt(codeword, msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /** Обрабатываем смс на наличие необработаных */
        new ProcessingSmsThread(this).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //processingSmsThread.cancel();
        //while(processingSmsThread.isStart());
        unregisterReceiver(incomingSMSReceiver);
    }

    /**
     * Приемник смс сообщений.
     */
    public class IncomingSMSReceiver extends BroadcastReceiver {

        /**
         * Входящее сообщение.
         */
        public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
        /**
         * Принятые непрочитаные сообщения.
         */
        public static final String SMS_DELIVER_ACTION = "android.provider.Telephony.SMS_DELIVER";
        /**
         * Транзакция завершена.
         */
        public static final String SMS_COMPLETED_ACTION = "android.intent.action.TRANSACTION_COMPLETED_ACTION";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {

                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                        try {
                            new SmsCommander(codeword, pdus, onSmsCommandListener);
                            abortBroadcast();
                        } catch (Exception e) { }
                    }
                }
            }
        }
    }

    /**
     * Слушатель обработчика смс команд.
     * Возвращяем событие если смс это команда.
     */
    final OnSmsCommandListener onSmsCommandListener = new OnSmsCommandListener() {
        StringBuilder result = new StringBuilder();

        /** Событие есть смс команда.
         *  @param commands Обьект с смс командами.
         */
        @Override
        public void onEvent(SmsCommander.Commands commands) {
            if (COMMAND_TAG.equals(commands.getTAG())){
                if(isValidCommands(commands)){
                    try {
                        /** Обрабатываем лист команд и возвращяем результат */
                        //result = new SmsCommand(getApplicationContext(), commands.getMap()).process();//todo
                    } catch (Exception e) {
                        result.append(e.getMessage());
                    }

                    try {
                        /** Отправляем результат выполнения команд адресату */
                        SMS.sendSMS(commands.getAddress(), result.toString());
                    } catch (Exception e) {}
                }
            }else if (CHECK_TAG.equals(commands.getTAG())){
                /**Запускаем поток обработки данных весового чека*/
                new Thread(new RunnableCheck(commands.getMap())).start();
            }
        }

        private boolean isValidCommands(SmsCommander.Commands commands){
            String address1;
            String address2;
            address1 = commands.getAddress();
            if(commands.getMap().containsKey("sender")){
                address2 = commands.getMap().get("sender");
                if (!address2.isEmpty()) {
                    if (address2.length() > address1.length()) {
                        address2 = address2.substring(address2.length() - address1.length(), address2.length());
                    } else if (address2.length() < address1.length()) {
                        address1 = address1.substring(address1.length() - address2.length(), address1.length());
                    }
                    if (address1.equals(address2)) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    /**
     * Обработка данных смс весового чека.
     */
    class RunnableCheck implements Runnable {
        Map<String,String> checkValue;
        CheckTable checkTable;
        TaskTable taskTable;
        ContentValues values;

        RunnableCheck(Map<String,String> map){
            checkValue = map;
            checkTable = new CheckTable(getApplicationContext());
            taskTable = new TaskTable(getApplicationContext());
            values = new ContentValues();
        }

        @Override
        public void run() {
            if(checkValue != null){
                for (String key : CheckTable.All_COLUMN_TABLE){
                    if(checkValue.containsKey(key)){
                        values.put(key, checkValue.get(key));
                    }
                }
                if (values.size() > 0){
                    String id = checkTable.insertNewEntry(values).getLastPathSegment();
                    taskTable.setCheckReady(Integer.valueOf(id));
                    startService(new Intent(getApplicationContext(), ServiceProcessTask.class));
                }
            }
        }
    };

    /**
     * Процесс обработки смс команд.
     * Обрабатывам команды которые приняты и не обработаные.
     */
    public class ProcessingSmsThread extends Thread {
        private boolean start;
        private boolean cancelled;
        private final SMS sms;
        private final List<SMS.SmsObject> smsInboxList;
        private final Context mContext;

        ProcessingSmsThread(Context context) {
            mContext = context;
            sms = new SMS(mContext);
            smsInboxList = sms.getInboxSms();
        }

        @Override
        public synchronized void start() {
            super.start();
            start = true;
        }

        private void cancel() {
            cancelled = true;
        }

        public boolean isStart() {
            return start;
        }

        @Override
        public void run() {

            for (final SMS.SmsObject smsObject : smsInboxList) {
                try {
                    new SmsCommander(codeword, smsObject.getAddress(), smsObject.getMsg(), onSmsCommandListener);
                    smsObject.delete(getApplicationContext());
                } catch (Exception e) {
                }
            }
            start = false;
        }
    }

}
