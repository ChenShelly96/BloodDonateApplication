package com.androidapp.blooddonate;

import static com.androidapp.blooddonate.R.id;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

public class SpinnerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Spinner locationsSpinner;
    Button btn;
    String[] locs = {"אשקלון" ,"בחירה מהרשימה", "שדרות", "נירים", "תל אביב-יפו", "זיקים", "רעננה", "הוד השרון", "באר שבע"};
    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner_choose_location);
        btn = (Button) findViewById(R.id.btnMap);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(view.getContext(), MapsActivity1.class);
                //view.getContext().startActivity(intent);
                Navigation.findNavController(view).navigate(id.map);
            }
        });
        locationsSpinner=(Spinner) findViewById(id.id_locations_spinner);

        //  initializing adapter for our spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locs);
        //  setting drop down view resource for our adapter.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  setting adapter for spinner.
        locationsSpinner.setAdapter(adapter);

        //  adding click listener for our spinner
        locationsSpinner.setOnItemSelectedListener(this);

        String selection = "בחירה מהרשימה";
        int spinnerPosition = adapter.getPosition(selection);
        locationsSpinner.setSelection(spinnerPosition);
    }

    @Override
    public void onItemSelected ( AdapterView<?> parent, View view, int position, long id ) {
        Toast.makeText(SpinnerActivity.this, "" + locs[position] + " Selected..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected ( AdapterView<?> parent ) {

    }
}