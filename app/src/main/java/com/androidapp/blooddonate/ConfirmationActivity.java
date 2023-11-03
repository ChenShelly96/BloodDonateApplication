package com.androidapp.blooddonate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import models.BloodApointment;

public class ConfirmationActivity extends AppCompatActivity {

    BloodApointment apointment;

    TextView nameText, locText, dateText, timeText;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        apointment = (BloodApointment) getIntent().getSerializableExtra("appointment");

        nameText = findViewById(R.id.cofirm_name_text);
        locText = findViewById(R.id.confirm_location_text);
        dateText = findViewById(R.id.confirm_date_text);
        timeText = findViewById(R.id.confirm_time_text);
        backBtn = findViewById(R.id.confirm_back_btn);

        nameText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        locText.setText(apointment.getLocation());
        dateText.setText(apointment.getDate());
        timeText.setText(apointment.getTime());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}