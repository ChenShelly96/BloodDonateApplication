package com.androidapp.blooddonate;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocationMaps implements Parcelable {


    public static final Creator<LocationMaps> CREATOR = new Creator<LocationMaps>() {
        @Override
        public LocationMaps createFromParcel( Parcel in) {
            return new LocationMaps(in);
        }

        @Override
        public LocationMaps[] newArray(int size) {
            return new LocationMaps[size];
        }
    };
    public static int id = -1;
    private String Latitude;
    private String Longitude;

    public LocationMaps ( String x, String y) {

        Latitude = x;
        Longitude = y;
        id++;

    }
    public LocationMaps ( String x, String y, int i) {

        Latitude = x;
        Longitude = y;
        id = i+1;

    }

    public LocationMaps ( String x, String y, boolean bool){
        Latitude = x;
        Longitude = y;
    }

    public LocationMaps () {
        Latitude = null;
        Longitude = null;
    }
    public Map<String, Object> getLocationMaps( String city, String street, String number){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("city", city);
        data.put("street", street);
        data.put("number", number);
        return data;
    }
    protected LocationMaps ( Parcel in) {
        Latitude = in.readString();
        Longitude = in.readString();
    }

    public boolean locEquals( LocationMaps loc) {
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
