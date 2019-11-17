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
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NotificationPullRunnable implements Runnable {

    private static final String query = "?page=%d&size=%d";
    private final Handler handler;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private String token;
    private volatile ListView notificationsList;
    private volatile AsyncHttpResponseHandler responseHandler;
    private String base = "/notifications";
    private volatile String nextPage = "/notifications?page=1&size=5";


    public NotificationPullRunnable(Handler handler, ListView notificationsList, String token) {
        this.handler = handler;
        this.token = token;
        this.notificationsList = notificationsList;
        this.responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                NotificationsJson json;
                try {
                    json = mapper.readValue(new String(responseBody), NotificationsJson.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                NotificationAdapter notificationAdapter =
                        (NotificationAdapter) NotificationPullRunnable.this.notificationsList.getAdapter();
                if (json.embedded.notificationDtoList.size() != 0) {
                    nextPage = base.concat(String.format(query, 2, notificationAdapter.getCount() +
                            json.embedded.notificationDtoList.size()));
                } else if (notificationsList.getCount() == 0) {
                    nextPage = "/notifications?page=1&size=5";
                }

                boolean exists;
                List<NotificationDto> notifications = new ArrayList<>();
                for (NotificationDto notificationDto : json.embedded.notificationDtoList) {
                    exists = false;
                    for (int i = 0; i < notificationAdapter.getCount(); i++) {
                        if (notificationAdapter.getItem(i).getId().equals(notificationDto.getId())) {
                            notificationAdapter.getItem(i).setAccepted(notificationDto.getAccepted());
                            exists = true;
                        }
                    }

                    if (exists) {
                        continue;
                    }

                    notifications.add(notificationDto);

                    Runnable runnable = new NotificationSyncRunnable(handler, notificationsList,
                            token, notificationDto.getId());
                    handler.post(runnable);
                }

                if (notifications.size() != 0) {
                    notificationAdapter.addAll(notifications);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        };
    }

    @Override
    public void run() {
        handler.postDelayed(this, 3 * 1000 - SystemClock.elapsedRealtime() % 1000);
        handler.post(() -> HttpUtil.get(token, nextPage, null, responseHandler));
    }
}
