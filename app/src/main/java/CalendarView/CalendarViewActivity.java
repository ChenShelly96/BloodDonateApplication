package CalendarView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidapp.blooddonate.R;

public class CalendarViewActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    String[] time = { "08:00", "08:15", "08:45", "09:00", "09:15", "09:30", "09:45"};
    CalendarView calendar;
    TextView date_view;


    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        calendar = findViewById(R.id.calendar);
        date_view = findViewById(R.id.date_view);

        ArrayAdapter<String> aa = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, time);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        // Add Listener in calendar
        calendar.setOnDateChangeListener(
                ( view, year, month, dayOfMonth ) -> {


                    String Date = dayOfMonth + "-"
                            + (month + 1) + "-" + year;

                    date_view.setText(Date);
                });
    }


    @Override
    public void onItemSelected ( AdapterView<?> parent, View view, int position, long id ) {
        Toast.makeText(getApplicationContext(),time[position] , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected ( AdapterView<?> parent ) {

    }
}