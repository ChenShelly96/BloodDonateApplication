package CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import com.androidapp.blooddonate.ConfirmationActivity;
import com.androidapp.blooddonate.databinding.ActivityCalendarViewBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.androidapp.blooddonate.R;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.BloodAppointment;

public class CalendarViewActivity extends AppCompatActivity {
    // Define the variable of CalendarView type
    // and TextView type;
    CalendarView calendar;
    TextView date_view, no_appointment_view;
    ActivityCalendarViewBinding binding;
    GridAdapter gridAdapter;
    Button continueBtn;

    String location;

    private DatabaseReference databaseReference;
    private List<BloodAppointment> appointmentList;
    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        location = getIntent().getStringExtra("Location");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        appointmentList = new ArrayList<>();

        calendar = (CalendarView)findViewById(R.id.calendar);
        date_view = (TextView)findViewById(R.id.date_view);
        no_appointment_view = (TextView) findViewById(R.id.no_appointment_text);
        continueBtn = (Button)findViewById(R.id.continue_button);

        gridAdapter = new GridAdapter(CalendarViewActivity.this, appointmentList);
        binding.appointmentGridView.setAdapter(gridAdapter);

        //calendar.setMinDate(System.currentTimeMillis() - 1000);

        // Add Listener in calendar
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            // In this Listener have one method
            // and in this method we will
            // get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                setSelected(-1);
                getAppointments(location, year + "-" + (month + 1) + "-" +dayOfMonth);
                // Store the value of date with
                // format in String type Variable
                // Add 1 in month because month
                // index is start with 0
                String Date = dayOfMonth + "-" + (month + 1) + "-" + year;
                // set this date in TextView for Display
                date_view.setText(Date);
            }
        });

        binding.appointmentGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelected(position);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected < 0 || selected >= appointmentList.size()){
                    Toast.makeText(CalendarViewActivity.this, "לא נבחרה שעה", Toast.LENGTH_SHORT).show();
                    return;
                }

                BloodAppointment appointment = appointmentList.get(selected);

                AlertDialog.Builder builder = new AlertDialog.Builder(CalendarViewActivity.this);
                String msg = "האם ברצונך לקבוע תור ל" + "\n"
                        + "מיקום: " + appointment.getLocation() + "\n"
                        + "תאריך: " + appointment.getDate() + "\n"
                        +  "שעה: " + appointment.getTime();
                builder.setMessage(msg).setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // confirm button
                        if(makeAppointment(appointment)) {
                            Intent intent = new Intent(CalendarViewActivity.this, ConfirmationActivity.class);
                            intent.putExtra("appointment", appointment);
                            startActivity(intent);
                        }
                        else{
                            Log.e("firestore", "didnt save appointment");
                        }
                    }
                }).setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel button
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void getAppointments(String location, String date) {
        appointmentList.clear();
        Query getAppointmentsSlots = databaseReference.orderByChild("date").equalTo(date);
        getAppointmentsSlots.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.getKey();
                    String date = ds.child("date").getValue(String.class);
                    String time = ds.child("time").getValue(String.class);
                    String location1 = ds.child("location").getValue(String.class);
                    String occupied = ds.child("occupied").getValue(String.class);

                    if (location.equals(location1) && (occupied == null || occupied.isEmpty())) {
                        appointmentList.add(new BloodAppointment(id, date, time, location1, occupied));
                    }
                }

                if (appointmentList.isEmpty()) {
                    no_appointment_view.setVisibility(View.VISIBLE);
                } else {
                    no_appointment_view.setVisibility(View.INVISIBLE);
                }
                gridAdapter.setApointmentList(appointmentList);
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSelected(int selected){
        this.selected = selected;
        gridAdapter.setSelected(selected);
        gridAdapter.notifyDataSetChanged();
    }

    private boolean makeAppointment(BloodAppointment appointment){
        if(selected < 0 || selected >= appointmentList.size()){
            return false;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            return false;
        }

        databaseReference.child(appointment.getId()).child("occupied")
                .setValue(user.getUid() + " " + user.getDisplayName());

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection(getString(R.string.appointments_database_name));

        Map<String, Object> data = appointment.asMap();
        data.put("userId", user.getUid());

        collectionReference.add(data);

        return true;
    }
}