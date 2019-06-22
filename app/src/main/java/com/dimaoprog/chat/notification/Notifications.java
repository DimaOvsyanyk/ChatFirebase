package com.dimaoprog.chat.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.dimaoprog.chat.MainActivity;
import com.dimaoprog.chat.R;

public class Notifications {

    private static final int NOTIF_INTENT_REQ_CODE = 5;
    private static final String NEW_MESSAGE_CHANNEL_ID = "new_message_channel_id";

    public static Notification getNewMessageNotification(Context context, NotificationManager manager) {
        Intent resIntent = new Intent(context, MainActivity.class);
        PendingIntent resPendingIntent = PendingIntent.getActivity(context, NOTIF_INTENT_REQ_CODE,
                resIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel = new NotificationChannel(NEW_MESSAGE_CHANNEL_ID,
                    "New Message", NotificationManager.IMPORTANCE_HIGH);
            notifChannel.setDescription("New Message");
            notifChannel.enableLights(true);
            notifChannel.setLightColor(Color.GREEN);
            manager.createNotificationChannel(notifChannel);
        }
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NEW_MESSAGE_CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setSmallIcon(R.drawable.ic_chat_grey_48dp)
                        .setContentTitle("Chat")
                        .setContentText("You have new message")
                        .setColor(context.getResources().getColor(R.color.colorAccent, context.getTheme()))
                        .setContentIntent(resPendingIntent);
        return builder.build();
    }
}