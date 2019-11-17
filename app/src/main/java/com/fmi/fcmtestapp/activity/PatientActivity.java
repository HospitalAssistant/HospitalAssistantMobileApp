package com.fmi.fcmtestapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fmi.fcmtestapp.PatientIntentRecyclerViewDataAdapter;
import com.fmi.fcmtestapp.PatientIntentRecyclerViewItem;
import com.fmi.fcmtestapp.R;

import java.util.ArrayList;
import java.util.List;

public class PatientActivity extends AppCompatActivity {

    private List<PatientIntentRecyclerViewItem> patientIntentList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        initializePatientIntentList();

        RecyclerView patientIntentRecyclerView = (RecyclerView) findViewById(R.id.card_view_recycler_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        patientIntentRecyclerView.setLayoutManager(gridLayoutManager);

        PatientIntentRecyclerViewDataAdapter carDataAdapter = new PatientIntentRecyclerViewDataAdapter(this, patientIntentList);
        patientIntentRecyclerView.setAdapter(carDataAdapter);
    }

    private void initializePatientIntentList() {
        if (patientIntentList == null) {
            patientIntentList = new ArrayList<>();
            patientIntentList.add(new PatientIntentRecyclerViewItem("EMERGENCY_INTENT", "Спешна помощ", R.drawable.patient_emergency));
            patientIntentList.add(new PatientIntentRecyclerViewItem("BATHROOM_INTENT", "Помощ в банята", R.drawable.patient_bathroom));
            patientIntentList.add(new PatientIntentRecyclerViewItem("BLOOD_PRESSURE_INTENT", "Измерване на кръвно", R.drawable.patient_bloodpressure));
            patientIntentList.add(new PatientIntentRecyclerViewItem("MEDICAL_DRIP_BAG_INTENT", "Проблем с банката", R.drawable.patient_medicaldrip));
            patientIntentList.add(new PatientIntentRecyclerViewItem("TEMPERATURE_INTENT", "Измерване на температура", R.drawable.patient_thermometer));
            patientIntentList.add(new PatientIntentRecyclerViewItem("DIAPER_INTENT", "Смяна на памперс", R.drawable.patient_diaper));
        }
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = this.getSharedPreferences("user", 0).edit();
        editor.remove("token");
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
