package com.omolayoseun.fuprett;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    private NotificationManager nManger = null;
    private ServiceHandler serviceHandler;
    final int NOTIFICATION_ID = 142;
    int RETURN_MESSAGE = 1;
    final String channel_id = "my_notification";
    final String channel_name = "Channel_name";
    @SuppressLint("InlinedApi")
    final int importance = NotificationManager.IMPORTANCE_HIGH;
    Intent receivedIntent;

    NotificationCompat.Builder builder;
    private Messenger messageHandler;
    Bundle extras;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            MakeTimeTable.startTimetableOperation(percentage -> {
                builder.setContentText(percentage + "/100");
                builder.setProgress(100, percentage, false);
                nManger.notify(NOTIFICATION_ID, builder.build());
            }, err -> {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                RETURN_MESSAGE = 2;
                stopSelf();
            }, receivedIntent);

            stopSelf();
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        extras = intent.getExtras();
        messageHandler = (Messenger) extras.get(IntentKey.MESSAGE.name());
        receivedIntent = intent;

        sendMessage(0);

        createNotification();

        // For each start request, send a message to start a job and deliver
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        sendMessage(RETURN_MESSAGE);
        builder.setOngoing(false);
        if (RETURN_MESSAGE == 1) builder.setContentText("Successful");
        else {
            builder.setProgress(100, 100, false);
            builder.setContentText("Failed operation");
        }
        nManger.notify(NOTIFICATION_ID, builder.build());
    }

    public void sendMessage(int state){
        Message message = Message.obtain();
        message.arg1 = state;

        try{
            messageHandler.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public interface HandleMessage{
        void getMessage(int percentage);
    }

    public interface HandleError{
        void getError(String err);
    }

    private void createNotification(){

        nManger = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(channel_id, channel_name, importance);
            nManger.createNotificationChannel(mChannel);
        }
        //noinspection deprecation
        builder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSmallIcon(R.drawable.ic_baseline_info_24)
                .setContentTitle("Creating Personalized Time Table")
                .setAutoCancel(false)
                .setOngoing(true)
                .setProgress(100, 0, true)
                .setChannelId(channel_id)
                .setContentText("0/100");


        /*
        *Intent targetIntent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                        0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
*/
        //builder.setContentIntent(contentIntent);
        nManger.notify(NOTIFICATION_ID, builder.build());
    }
}
