package com.fmi.fcmtestapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.fmi.fcmtestapp.R;
import com.fmi.fcmtestapp.util.HttpUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class MedicalPersonnelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        final TextView textView = (TextView) findViewById(R.id.loginMsg);

        final AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(String.format("\nstatus code: %d", statusCode));
                textView.setText(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(String.format("\nstatus code: %d\nerror: %s", statusCode, error.getMessage()));
                textView.setText(Integer.toString(statusCode));
            }
        };

        final SharedPreferences sharedPreferences = this.getSharedPreferences("user", 0);

        Runnable scheduleRunnable = () -> {
            final RequestParams params = new RequestParams();
            params.put("token", FirebaseInstanceId.getInstance().getToken());
            params.setUseJsonStreamer(true);

            HttpUtil.get(sharedPreferences.getString("token", ""),
                    "/account/workSchedule/current", params, asyncHttpResponseHandler);
        };

        final Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(scheduleRunnable);
    }

    public void notifications(View view) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        final SharedPreferences sharedPreferences = this.getSharedPreferences("user", 0);

        final String token = sharedPreferences.getString("token", "");

        final AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(String.format("\nstatus code: %d", statusCode));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();

                Intent intent = new Intent(MedicalPersonnelActivity.this, MainActivity.class);
                finishAffinity();
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(String.format("\nstatus code: %d\nerror: %s", statusCode, error.getMessage()));
            }
        };

        final Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable loginRunnable = () -> HttpUtil.delete(token, "/account/stopNotifications", null, asyncHttpResponseHandler);

        mainHandler.post(loginRunnable);
    }

    public void onSwitch(View view) {
        boolean on = ((Switch) view).isChecked();

        final SharedPreferences sharedPreferences = this.getSharedPreferences("user", 0);

        final String token = sharedPreferences.getString("token", "");

        final AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(String.format("\nstatus code: %d", statusCode));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(String.format("\nstatus code: %d\nerror: %s", statusCode, error.getMessage()));
            }
        };

        final Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable loginRunnable;

        if (on) {
            loginRunnable = () -> {
                final RequestParams params = new RequestParams();
                params.put("token", FirebaseInstanceId.getInstance().getToken());
                params.setUseJsonStreamer(true);

                HttpUtil.post(token, "/account/updateToken", params, asyncHttpResponseHandler);
            };
        } else {
            loginRunnable = () -> HttpUtil.delete(token, "/account/stopNotifications", null, asyncHttpResponseHandler);
        }

        mainHandler.post(loginRunnable);
    }

    @Override
    public void onBackPressed() {
    }
}
