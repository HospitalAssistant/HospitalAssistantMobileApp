package com.fmi.fcmtestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmi.fcmtestapp.util.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PatientIntentRecyclerViewDataAdapter extends RecyclerView.Adapter<PatientIntentRecyclerViewItemHolder> {
    private List<PatientIntentRecyclerViewItem> patientIntentList;
    private Context context;

    public PatientIntentRecyclerViewDataAdapter(Context context, List<PatientIntentRecyclerViewItem> patientIntentList) {
        this.context = context;
        this.patientIntentList = patientIntentList;
    }

    @Override
    public PatientIntentRecyclerViewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        View patientIntentView = layoutInflater.inflate(R.layout.activity_card_view_item, parent, false);

        final ImageView patientIntentImageView = (ImageView) patientIntentView.findViewById(R.id.card_view_image);
        patientIntentImageView.setBackgroundColor(Color.rgb(255, 255, 255));

        final TextView patientIntentTitleHiddenView = (TextView) patientIntentView.findViewById(R.id.card_view_hidden_intent);
        // When click the image.
        patientIntentImageView.setOnClickListener(v -> {
            final String patientIntentHiddenTitle = patientIntentTitleHiddenView.getText().toString();

            final SharedPreferences sharedPreferences = context.getSharedPreferences("user", 0);

            final String token = sharedPreferences.getString("token", "");

            final Handler mainHandler = new Handler(Looper.getMainLooper());

            final AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Snackbar snackbar = Snackbar.make(patientIntentImageView, new String(responseBody), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Snackbar snackbar = Snackbar.make(patientIntentImageView, "Something went wrong", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    System.out.println(String.format("\nsuccess\nstatus code: %d\nerror: %s", statusCode, error.getMessage()));
                }
            };

            Runnable scheduleRunnable = () -> {
                final RequestParams params = new RequestParams();
                params.put("intent", patientIntentHiddenTitle);
                params.setUseJsonStreamer(true);

                HttpUtil.post(token, "/hospital/assistant/patient", params, asyncHttpResponseHandler);
            };

            mainHandler.post(scheduleRunnable);
        });

        PatientIntentRecyclerViewItemHolder ret = new PatientIntentRecyclerViewItemHolder(patientIntentView);
        return ret;
    }

    @Override
    public void onBindViewHolder(PatientIntentRecyclerViewItemHolder holder, int position) {
        if (patientIntentList != null) {
            PatientIntentRecyclerViewItem patientIntentItem = patientIntentList.get(position);

            if (patientIntentItem != null) {
                holder.getPatientIntentTextView().setText(patientIntentItem.getPatientIntentName());
                holder.getPatientIntentImageView().setImageResource(patientIntentItem.getPatientIntentImageId());
                holder.getPatientIntentHiddenTextView().setText(patientIntentItem.getPatientIntentHidden());
            }
        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (patientIntentList != null) {
            ret = patientIntentList.size();
        }
        return ret;
    }
}
