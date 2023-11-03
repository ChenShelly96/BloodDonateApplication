package models;

import java.io.Serializable;

public class BloodApointment implements Serializable {
    String id;
    String date;
    String time;
    String location;
    String occupied;

    public BloodApointment(String id, String date, String time, String location, String occupied) {
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
}
