package models;

public class BloodApointment {
    String date;
    String time;
    String location;
    String occupied;

    public BloodApointment(String date, String time, String location, String occupied) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.occupied = occupied;
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

    @Override
    public String toString() {
        return location + " " + date + " " + time;
    }
}
