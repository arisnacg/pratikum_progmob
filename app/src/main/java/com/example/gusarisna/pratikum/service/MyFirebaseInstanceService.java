package com.example.gusarisna.pratikum.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.gusarisna.pratikum.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseInstanceService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("DEVELOP", remoteMessage.getNotification().getBody());
        showNotif(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }


    private void showNotif(String title, String body) {
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFCATION_CHANNEL_ID = "com.example.gusarisna.pratikum";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notifChannel = new NotificationChannel(
                    NOTIFCATION_CHANNEL_ID,
                    "Notifikasi",
                    notifManager.IMPORTANCE_DEFAULT
            );
            notifChannel.setDescription("EDMT Channel");
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.BLUE);
            notifChannel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
            notifManager.createNotificationChannel(notifChannel);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(
                this, NOTIFCATION_CHANNEL_ID
        );
        notifBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        notifManager.notify(new Random().nextInt(), notifBuilder.build());
    }

}
