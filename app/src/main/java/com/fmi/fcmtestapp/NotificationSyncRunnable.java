package com.fmi.fcmtestapp;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.ListView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmi.fcmtestapp.util.HttpUtil;
import com.hospital.assistant.model.NotificationDto;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class NotificationSyncRunnable implements Runnable {

    private final Handler handler;
    private final Long id;
    private final ObjectMapper mapper = new ObjectMapper();
    private String token;
    private String base = "/notifications/%d";
    private volatile ListView notificationsList;
    private AsyncHttpResponseHandler responseHandler;

    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public NotificationSyncRunnable(Handler handler, ListView notificationsList, String token, Long id) {
        this.handler = handler;
        this.notificationsList = notificationsList;
        this.token = token;
        this.id = id;
        this.responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                NotificationDto notification;
                try {
                    notification = mapper.readValue(new String(responseBody), NotificationDto.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                NotificationAdapter notificationAdapter = (NotificationAdapter) NotificationSyncRunnable.this.notificationsList.getAdapter();
                for (int i = 0; i < notificationAdapter.getCount(); i++) {
                    NotificationDto notificationDto = notificationAdapter.getItem(i);
                    if (notificationDto.getId().equals(id)) {

                        notificationDto.setMilliseconds(notification.getMilliseconds());

                        if (notification.getAccepted()) {
                            notificationAdapter.remove(notificationDto);
                            handler.removeCallbacks(NotificationSyncRunnable.this);
                        } else {
                            notificationAdapter.notifyDataSetChanged();
                        }

                        break;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handler.removeCallbacks(NotificationSyncRunnable.this);
            }
        };
    }

    @Override
    public void run() {
        handler.postDelayed(this, 3 * 1000 - SystemClock.elapsedRealtime() % 1000);
        handler.post(() -> HttpUtil.get(token, String.format(base, id), null, responseHandler));
    }
}
