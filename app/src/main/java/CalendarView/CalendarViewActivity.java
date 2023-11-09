package CalendarView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import com.androidapp.blooddonate.ConfirmationActivity;
import com.androidapp.blooddonate.MapLocation;
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

    final private int LOCATION_REQUEST_CODE = 100;

    //String[] locs = {"אשקלון" ,"בחירה מהרשימה", "שדרות", "נירים", "תל אביב-יפו", "זיקים", "רעננה", "הוד השרון", "באר שבע"};
    String[] locs = {"טבריה ישוב", "היכל פיס ארנה י-ם", "מרכז דתי חיפה", "חריש אולם גפן", "בחירה מהרשימה"};

    // Define the variable of CalendarView type
    // and TextView type;
    CalendarView calendar;
    TextView no_appointment_view;
    ActivityCalendarViewBinding binding;
    GridAdapter gridAdapter;
    Button continueBtn, mapBtn;
    Spinner locationsSpinner;

    String location = null, date = null;

    private DatabaseReference databaseReference;
    private List<BloodAppointment> appointmentList;
    private int selectedTimeIndex;

    private ActivityResultLauncher<Intent> mapLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        databaseReference = FirebaseDatabase.getInstance().getReference();
        appointmentList = new ArrayList<>();

        locationsSpinner = (Spinner) findViewById(R.id.location_spinner);
        mapBtn = (Button) findViewById(R.id.chose_on_map_button);

        calendar = (CalendarView)findViewById(R.id.calendar);
        no_appointment_view = (TextView) findViewById(R.id.no_appointment_text);
        continueBtn = (Button)findViewById(R.id.continue_button);

        gridAdapter = new GridAdapter(CalendarViewActivity.this, appointmentList);
        binding.appointmentGridView.setAdapter(gridAdapter);

        //  initializing adapter for our spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locs);
        //  setting drop down view resource for our adapter.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  setting adapter for spinner.
        locationsSpinner.setAdapter(adapter);

        //  adding click listener for our spinner
        locationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                location = locs[position];
                getAppointments(location, date);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String selection = "בחירה מהרשימה";
        int spinnerPosition = adapter.getPosition(selection);
        locationsSpinner.setSelection(spinnerPosition);

        mapLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if(data != null) {
                    int i = data.getIntExtra("result", spinnerPosition);
                    location = locs[i];
                    locationsSpinner.setSelection(i);
                }
            }
        } );

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarViewActivity.this, MapLocation.class);
                intent.putExtra("locations", locs);
                //startActivityForResult(intent, LOCATION_REQUEST_CODE);
                mapLauncher.launch(intent);
            }
        });


        //calendar.setMinDate(System.currentTimeMillis() - 1000);//TODO remove from comment

        // Add Listener in calendar
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            // In this Listener have one method
            // and in this method we will
            // get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                date = year + "-" + (month + 1) + "-" +dayOfMonth;
                setSelectedTimeIndex(-1);
                getAppointments(location, date);
            }
        });

        binding.appointmentGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectedTimeIndex(position);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTimeIndex < 0 || selectedTimeIndex >= appointmentList.size()){
                    Toast.makeText(CalendarViewActivity.this, "לא נבחרה שעה", Toast.LENGTH_SHORT).show();
                    return;
                }

                BloodAppointment appointment = appointmentList.get(selectedTimeIndex);

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

        if(location == null || date == null){
            return;
        }

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

    private void setSelectedTimeIndex(int selectedTimeIndex){
        this.selectedTimeIndex = selectedTimeIndex;
        gridAdapter.setSelected(selectedTimeIndex);
        gridAdapter.notifyDataSetChanged();
    }

    private boolean makeAppointment(BloodAppointment appointment){
        if(selectedTimeIndex < 0 || selectedTimeIndex >= appointmentList.size()){
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