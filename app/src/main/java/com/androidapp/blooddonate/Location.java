package com.androidapp.blooddonate;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Location implements Parcelable {


    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    public static int id = -1;
    private String Latitude;
    private String Longitude;

    public Location ( String x, String y) {

        Latitude = x;
        Longitude = y;
        id++;

    }
    public Location ( String x, String y, int i) {

        Latitude = x;
        Longitude = y;
        id = i+1;

    }

    public Location ( String x, String y, boolean bool){
        Latitude = x;
        Longitude = y;
    }

    public Location () {
        Latitude = null;
        Longitude = null;
    }

    protected Location ( Parcel in) {
        Latitude = in.readString();
        Longitude = in.readString();
    }

    public boolean locEquals(Location loc) {
        if(this.get_Latitude().equals(loc.get_Latitude()) && this.get_Longitude().equals(loc.get_Longitude()))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Latitude, Longitude);
    }

    public String get_Latitude() {
        return Latitude;
    }

    public void set_Latitude(String x) {
        Latitude = x;
    }

    public int get_id() {
        return id;
    }

    public String get_Longitude() {
        return Longitude;
    }

    public void set_Longitude(String y) {
        Longitude = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Latitude);
        parcel.writeString(Longitude);
    }
}
