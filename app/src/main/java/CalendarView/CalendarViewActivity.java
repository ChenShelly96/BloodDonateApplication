package CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import com.androidapp.blooddonate.databinding.ActivityCalendarViewBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.androidapp.blooddonate.R;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.BloodApointment;

public class CalendarViewActivity extends AppCompatActivity {
    // Define the variable of CalendarView type
    // and TextView type;
    CalendarView calendar;
    TextView date_view, no_apointment_view;

    ActivityCalendarViewBinding binding;
    GridAdapter gridAdapter;

    private DatabaseReference databaseReference;

    private List<BloodApointment> apointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        apointmentList = new ArrayList<>();

        calendar = (CalendarView)findViewById(R.id.calendar);
        date_view = (TextView)findViewById(R.id.date_view);
        no_apointment_view = (TextView) findViewById(R.id.no_apointment_text);

        // Add Listener in calendar
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            // In this Listener have one method
            // and in this method we will
            // get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                getAppointements("חריש אולם גפן", year + "-" + (month + 1) + "-" +dayOfMonth);

                // Store the value of date with
                // format in String type Variable
                // Add 1 in month because month
                // index is start with 0
                String Date = dayOfMonth + "-" + (month + 1) + "-" + year;
                // set this date in TextView for Display
                date_view.setText(Date);
            }
        });

        gridAdapter = new GridAdapter(CalendarViewActivity.this, apointmentList);
        binding.apointmentGridView.setAdapter(gridAdapter);

        binding.apointmentGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CalendarViewActivity.this, apointmentList.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAppointements(String location, String date) {
        apointmentList.clear();
        Query getApintmentsSlots = databaseReference.orderByChild("date").equalTo(date);
        getApintmentsSlots.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.getKey();
                    String date = ds.child("date").getValue(String.class);
                    String time = ds.child("time").getValue(String.class);
                    String location1 = ds.child("location").getValue(String.class);
                    String occupied = ds.child("occupied").getValue(String.class);

                    if (location.equals(location1) && (occupied == null || occupied.isEmpty())) {
                        apointmentList.add(new BloodApointment(id, date, time, location1, occupied));
                    }
                }

                if (apointmentList.isEmpty()) {
                    no_apointment_view.setVisibility(View.VISIBLE);
                } else {
                    no_apointment_view.setVisibility(View.INVISIBLE);
                }
                gridAdapter.setApointmentList(apointmentList);
                binding.apointmentGridView.setAdapter(gridAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}