package com.androidapp.blooddonate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

import models.BloodAppointment;

public class ConfirmationActivity extends AppCompatActivity {

    BloodAppointment appointment;

    TextView nameText, locText, dateText, timeText;
    Button backBtn, addToCalenderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        appointment = (BloodAppointment) getIntent().getSerializableExtra("appointment");

        nameText = findViewById(R.id.cofirm_name_text);
        locText = findViewById(R.id.confirm_location_text);
        dateText = findViewById(R.id.confirm_date_text);
        timeText = findViewById(R.id.confirm_time_text);
        backBtn = findViewById(R.id.confirm_back_btn);
        addToCalenderBtn = findViewById(R.id.add_to_calender);

        nameText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        locText.setText(appointment.getLocation());
        dateText.setText(appointment.getDate());
        timeText.setText(appointment.getTime());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addToCalenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE,  "תרומת דם");
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, appointment.getLocation());

                Date time = appointment.getDateFromString();
                if(time != null) {
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time.getTime());
                    long endTime = time.getTime() + BloodAppointment.APPOINTMENT_TIME_MINUTES * 60 * 1000;
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
                }

                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }else{
                    Toast.makeText(ConfirmationActivity.this, "There is no app that can support this action", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}