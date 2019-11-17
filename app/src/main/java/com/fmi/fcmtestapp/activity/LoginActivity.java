package com.fmi.fcmtestapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmi.fcmtestapp.R;
import com.fmi.fcmtestapp.util.HttpUtil;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hospital.assistant.model.JwtAuthenticationResponse;
import com.hospital.assistant.model.RoleName;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("user", 0);

        final RequestParams params = new RequestParams();
        params.put("email", email.getText().toString());
        params.put("password", password.getText().toString());
        params.setUseJsonStreamer(true);

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable loginRunnable = () -> HttpUtil.post(null, "/api/auth/signin", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(String.format("\nsuccess\nstatus code: %d", statusCode));

                final JwtAuthenticationResponse response;
                try {
                    response = new ObjectMapper().readValue(new String(responseBody),
                            JwtAuthenticationResponse.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", response.getAccessToken());
                editor.apply();

                if (response.getRoles().contains(RoleName.ROLE_PATIENT)) {
                    Intent intent = new Intent(LoginActivity.this, PatientActivity.class);
                    startActivity(intent);
                    email.setText("");
                    password.setText("");
                    return;
                }

                final RequestParams params1 = new RequestParams();
                params1.put("token", FirebaseInstanceId.getInstance().getToken());
                params1.setUseJsonStreamer(true);
                Runnable tokenRunnable = () -> HttpUtil.post(response.getAccessToken(), "/account/updateToken", params1,
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode1, Header[] headers1, byte[] responseBody1) {
                                System.out.println(String.format("\nsuccess\nnested status code: %d", statusCode1));

                                Intent intent = new Intent(LoginActivity.this, MedicalPersonnelActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(int statusCode1, Header[] headers1, byte[] responseBody1, Throwable error) {
                                System.out.println(String.format("\nfailure\nnested status code: %d", statusCode1));
                            }
                        });
                mainHandler.post(tokenRunnable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(String.format("\nfailure\nstatus code: %d", statusCode));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();
            }
        });

        mainHandler.post(loginRunnable);
    }
}
