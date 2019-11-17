package com.fmi.fcmtestapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fmi.fcmtestapp.R;
import com.fmi.fcmtestapp.util.HttpUtil;
import com.hospital.assistant.model.RoleName;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Arrays;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Spinner spinnerRoles = (Spinner) findViewById(R.id.roles);
        spinnerRoles.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                Arrays.stream(RoleName.values())
                        .map(RoleName::toString)
                        .collect(Collectors.toList())));
    }

    public void register(View view) {
        final EditText email = (EditText) findViewById(R.id.email);
        final String emailValue = email.getText().toString();
        final EditText password = (EditText) findViewById(R.id.password);
        final String passwordValue = password.getText().toString();
        final String roleValue = ((Spinner) findViewById(R.id.roles)).getSelectedItem().toString();

        final SharedPreferences sharedPreferences = this.getSharedPreferences("user", 0);

        final RequestParams params = new RequestParams();
        params.put("email", emailValue);
        params.put("password", passwordValue);
        params.put("role", roleValue);
        params.setUseJsonStreamer(true);

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable loginRunnable = () -> HttpUtil.post(null, "/api/auth/signup", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(String.format("\nsuccess\nstatus code: %d", statusCode));

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(String.format("\nfailure\nstatus code: %d\n%s", statusCode, new String(responseBody)));
            }
        });

        mainHandler.post(loginRunnable);
    }
}
