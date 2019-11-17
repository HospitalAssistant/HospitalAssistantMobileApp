package com.fmi.fcmtestapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PatientIntentRecyclerViewItemHolder extends RecyclerView.ViewHolder {
    private TextView patientIntentTextView = null;

    private ImageView patientIntentImageView = null;

    private TextView patientIntentHiddenTextView = null;

    public PatientIntentRecyclerViewItemHolder(View itemView) {
        super(itemView);

        if (itemView != null) {
            patientIntentTextView = (TextView) itemView.findViewById(R.id.card_view_image_title);

            patientIntentImageView = (ImageView) itemView.findViewById(R.id.card_view_image);

            patientIntentHiddenTextView = (TextView) itemView.findViewById(R.id.card_view_hidden_intent);
        }
    }

    public TextView getPatientIntentTextView() {
        return patientIntentTextView;
    }

    public ImageView getPatientIntentImageView() {
        return patientIntentImageView;
    }

    public TextView getPatientIntentHiddenTextView() {
        return patientIntentHiddenTextView;
    }
}
