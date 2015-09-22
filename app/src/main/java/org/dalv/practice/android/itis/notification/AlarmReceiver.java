package org.dalv.practice.android.itis.notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {


    public final static String ON_APP_CLOSE = "org.dalv.practice.android.itis.notification.AlarmReceiver.ON_APP_CLOSE";
    public final static String ON_NOTIFICATION_CLOSE = "org.dalv.practice.android.itis.notification.AlarmReceiver.ON_NOTIFICATION_CLOSE";
    public final static String ON_WIFI_NAME_PASS = "org.dalv.practice.android.itis.notification.AlarmReceiver.ON_WIFI_NAME_PASS";
    private static String mWifiName = "";
    private final int BOOT_COMPLETED_ID_NOTIFIER = 322;
    private final int BATTERY_LOW_ID_NOTIFIER = 228;
    private final int WIFI_CONNECTED_ID_NOTIFIER = 777;
    private final int ALARM_ID_NOTIFIER = 222;
    private int APP_CLOSE_ID_NOTIFIER = 312;


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String action = intent.getAction();
        if (action.equals(ON_APP_CLOSE)) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.star_on)
                            .setContentTitle(context.getString(R.string.app_is_shutdown))
                    .setContentText(context.getString(R.string.dont_go_away));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(APP_CLOSE_ID_NOTIFIER, mBuilder.build());
        } else

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.star_on)
                            .setContentTitle(context.getString(R.string.wake_app))
                            .setOngoing(true)
                            .setContentText(context.getString(R.string.could_not_wake_app));
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(BOOT_COMPLETED_ID_NOTIFIER, mBuilder.build());
        } else

        if (action.equals(Intent.ACTION_BATTERY_LOW)) {
            Intent intent1 = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
            Intent intent2 = new Intent(ON_NOTIFICATION_CLOSE);
            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent1, 0);
            PendingIntent pIntent2 = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent2, 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.star_on)
                            .setContentTitle(context.getString(R.string.battery_is_go_away))
                            .addAction(android.R.drawable.ic_dialog_info, context.getString(R.string.yes), pIntent)
                            .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.no), pIntent2)
                            .setContentText(context.getString(R.string.hurry));
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(BATTERY_LOW_ID_NOTIFIER, mBuilder.build());
        } else

        if (action.equals(ON_NOTIFICATION_CLOSE)) {
            mNotificationManager.cancel(BATTERY_LOW_ID_NOTIFIER);
        } else

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.getState()== NetworkInfo.State.CONNECTED){
                if(netInfo.getExtraInfo().contains(mWifiName)){
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(android.R.drawable.star_on)
                                    .setContentTitle(context.getString(R.string.you_are_connect))
                                    .setContentText(mWifiName);

                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    mNotificationManager.notify(WIFI_CONNECTED_ID_NOTIFIER, mBuilder.build());
                }
            }
        } else if (action.equals(ON_WIFI_NAME_PASS)) {
            mWifiName = intent.getExtras().getString(ON_WIFI_NAME_PASS);
        } else if (action.equals(MainActivity.ON_ALARM_ACTION)) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.star_on)
                            .setContentTitle(context.getString(R.string.current_time))
                            .setContentText(intent.getExtras().getString(MainActivity.ON_ALARM_ACTION));
            mNotificationManager.notify(ALARM_ID_NOTIFIER, mBuilder.build());
        }

    }
}