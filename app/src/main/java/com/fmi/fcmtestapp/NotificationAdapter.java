package com.fmi.fcmtestapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fmi.fcmtestapp.util.HttpUtil;
import com.fmi.fcmtestapp.util.TimeUtil;
import com.hospital.assistant.model.NotificationDto;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NotificationAdapter extends ArrayAdapter<NotificationDto> {

    public NotificationAdapter(Context context, List<NotificationDto> notifications) {
        super(context, R.layout.item_notification, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationDto notification = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_notification, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder();

        viewHolder.id = (TextView) convertView.findViewById(R.id.value_notification_id);
        viewHolder.id.setVisibility(View.INVISIBLE);

        viewHolder.message = (TextView) convertView.findViewById(R.id.value_notification_message);
        viewHolder.time = (TextView) convertView.findViewById(R.id.value_notification_time);

        convertView.setTag(viewHolder);

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("user", 0);

        convertView.setOnClickListener(v -> {
            ViewHolder tag = (ViewHolder) v.getTag();

            Runnable scheduleRunnable = () -> HttpUtil.patch(sharedPreferences.getString("token", ""),
                    "/notifications/" + tag.id.getText().toString() + "/accept", null, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            ViewGroup parentView = (ViewGroup) v.getParent();
                            parentView.removeViewInLayout(v);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        }
                    });

            final Handler mainHandler = new Handler(Looper.getMainLooper());

            mainHandler.post(scheduleRunnable);
        });

        viewHolder.id.setText(notification.getId().toString());
        viewHolder.message.setText(notification.getIntent().getMessage());
        viewHolder.time.setText(TimeUtil.getTimeAgo(notification.getMilliseconds()));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        TextView id;
        TextView message;
        TextView time;
    }
}
