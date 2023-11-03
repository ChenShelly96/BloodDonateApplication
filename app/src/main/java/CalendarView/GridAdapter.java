package CalendarView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.androidapp.blooddonate.ConfirmationActivity;
import com.androidapp.blooddonate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

import models.BloodApointment;

public class GridAdapter extends BaseAdapter {
    Context context;
    List<BloodApointment> apointmentList;
    //String[] times;

    LayoutInflater inflater;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    public GridAdapter(Context context, List<BloodApointment> apointmentList) {
        this.context = context;
        this.apointmentList = apointmentList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return apointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BloodApointment apointment = apointmentList.get(position);

        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = inflater.inflate(R.layout.apointment_grid_button, null);
        }

        Button btn = convertView.findViewById(R.id.grid_button);
        btn.setText(apointment.getTime());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                String msg = "האם ברצונך לקבוע תור ל" + "\n"
                        + "מיקום: " + apointment.getLocation() + "\n"
                        + "תאריך: " + apointment.getDate() + "\n"
                        +  "שעה: " + apointment.getTime();
                builder.setMessage(msg).setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // confirm button
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if(user != null) {
                            databaseReference.child(apointment.getId()).child("occupied").setValue(user.getUid() + " " + user.getDisplayName());
                            Intent intent = new Intent(context, ConfirmationActivity.class);
                            intent.putExtra("appointment", apointment);
                            context.startActivity(intent);
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

        return convertView;
    }

    public void setApointmentList(List<BloodApointment> apointmentList) {
        this.apointmentList = apointmentList;
    }
}
