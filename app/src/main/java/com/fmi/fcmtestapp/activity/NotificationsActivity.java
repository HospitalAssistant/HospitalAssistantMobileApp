package com.fmi.fcmtestapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.fmi.fcmtestapp.NotificationAdapter;
import com.fmi.fcmtestapp.NotificationPullRunnable;
import com.fmi.fcmtestapp.R;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private ListView notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        getNotifications();
    }

    private void getNotifications() {
        final SharedPreferences sharedPreferences = this.getSharedPreferences("user", 0);

        NotificationAdapter noteAdapter = new NotificationAdapter(NotificationsActivity.this,
                new ArrayList<>());

        notificationsList = (ListView) findViewById(R.id.list_notifications);
        notificationsList.setAdapter(noteAdapter);

        runnable = new NotificationPullRunnable(handler, notificationsList,
                sharedPreferences.getString("token", ""));
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }
}
