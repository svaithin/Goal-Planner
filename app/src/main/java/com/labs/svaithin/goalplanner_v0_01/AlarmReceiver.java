package com.labs.svaithin.goalplanner_v0_01;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Siddharth on 20/01/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    int MID=0;
    NotificationCompat.Builder notification;
    TaskStackBuilder stackBuilder;
    Intent resultIntent;
    PendingIntent pIntent;
    NotificationManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {

        notification = new NotificationCompat.Builder(context);
        notification.setContentTitle("Goal Planner");
        notification.setContentText("Conquer your Goal");
        notification.setTicker("Conquer your Goal!");
        notification.setSmallIcon(getNotificationIcon());
        notification.setAutoCancel(true);
        stackBuilder = TaskStackBuilder.create(context);
        resultIntent = new Intent(context,MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        pIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pIntent);
        manager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }

    //Notification Icon need to have new silhouette
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }
}
