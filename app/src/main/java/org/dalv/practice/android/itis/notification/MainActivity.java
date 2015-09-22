package org.dalv.practice.android.itis.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private EditText mWifiName;
    public final static String ON_ALARM_ACTION = "org.dalv.practice.android.itis.notification.MainActivity.ON_ALARM_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiName = (EditText) findViewById(R.id.et_wifi_name);

        findViewById(R.id.bt_empty_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(android.R.drawable.star_on)
                                .setContentTitle(getString(R.string.see_notifier))
                                .setContentText(getString(R.string.be_proud_of));
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(123, mBuilder.build());
            }
        });


        findViewById(R.id.bt_wifi_name_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmReceiver.ON_WIFI_NAME_PASS);
                intent.putExtra(AlarmReceiver.ON_WIFI_NAME_PASS, mWifiName.getText().toString());
                sendBroadcast(intent);
            }
        });


        findViewById(R.id.bt_alarm_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, 0);
                AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                intent.setAction(ON_ALARM_ACTION);
                calendar.set(Calendar.HOUR_OF_DAY, 15);
                intent.putExtra(ON_ALARM_ACTION, 15);
                if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                    intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    alarmMgr.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 12);
                    alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                    intent.putExtra(ON_ALARM_ACTION, 12);
                    alarmMgr.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

                } else if (calendar.get(Calendar.HOUR_OF_DAY) > 12 && calendar.get(Calendar.HOUR_OF_DAY) < 15) {
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                    alarmMgr.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ON_APP_CLOSE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.set(AlarmManager.RTC, System.currentTimeMillis() + (5 * 1000), alarmIntent);
        super.onPause();
    }

}
