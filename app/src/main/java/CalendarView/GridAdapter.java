package CalendarView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.androidapp.blooddonate.R;

import java.util.List;

import models.BloodApointment;

public class GridAdapter extends BaseAdapter {
    Context context;
    List<BloodApointment> apointmentList;
    //String[] times;

    LayoutInflater inflater;

    public GridAdapter(Context context, List<BloodApointment> apointmentList) {
        this.context = context;
        this.apointmentList = apointmentList;
    }

//    public  GridAdapter(Context context, String[] times){
//        this.context = context;
//        this.times = times;
//    }

    @Override
    public int getCount() {
        //return times.length;
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
        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = inflater.inflate(R.layout.apointment_grid_button, null);
        }

        Button btn = convertView.findViewById(R.id.grid_button);
        btn.setText(apointmentList.get(position).getTime());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "clicked on" + apointmentList.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    public void setApointmentList(List<BloodApointment> apointmentList) {
        this.apointmentList = apointmentList;
    }
}
