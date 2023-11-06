package models;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BloodAppointment implements Serializable {
    public final static int APPOINTMENT_TIME_MINUTES = 20;//in minutes
    String id;
    String date;
    String time;
    String location;
    String occupied;

    public BloodAppointment(String id, String date, String time, String location, String occupied) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.location = location;
        this.occupied = occupied;
    }

    public String getId(){
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getOccupied() {
        return occupied;
    }

    public void setOccupied(String occupied) {
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        return location + " " + date + " " + time;
    }

    public Date getDateFromString() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = formatter.parse(this.date + " " + this.time);
            return date;
        } catch (ParseException e) {
            Log.e("date time", "cant make time from string");
        }
        return null;
    }

    public Map<String, Object> asMap(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("Id", id);
        data.put("date", date);
        data.put("time", time);
        data.put("location", location);

        return data;
    }
}
