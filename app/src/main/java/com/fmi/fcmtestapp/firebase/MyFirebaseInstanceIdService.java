package com.fmi.fcmtestapp.firebase;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", 0);
        if (!sharedPreferences.contains("token")) {
            return;
        }

        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println(String.format("REG_TOKEN: '%s'", token));

        final RequestParams params = new RequestParams();
        params.put("token", token);
        params.setUseJsonStreamer(true);

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = () -> {
            final AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Content-Type", "application/json");
            client.post("https://dcff30e5.ngrok.io/account/updateToken", params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    System.out.println(String.format("status code: %d\n response body: %s\n error: %s",
                            statusCode, Arrays.toString(responseBody), error.getMessage()));
                }

            });
        };
        mainHandler.post(myRunnable);
    }
}
