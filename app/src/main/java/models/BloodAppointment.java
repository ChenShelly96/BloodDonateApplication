package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BloodAppointment implements Serializable {
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

    public Map<String, Object> asMap(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("Id", id);
        data.put("date", date);
        data.put("time", time);
        data.put("location", location);

        return data;
    }
}
