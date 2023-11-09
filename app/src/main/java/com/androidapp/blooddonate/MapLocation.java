package com.androidapp.blooddonate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

import CalendarView.CalendarViewActivity;

public class MapLocation extends FragmentActivity implements OnMapReadyCallback,
        LocationListener {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private android.location.Location myCurrentLocation;//location from GPS
    private Marker myLocationMarker, searchLocationMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SearchView mapSearchView;

    private String[] locs;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        locs = getIntent().getStringArrayExtra("locations");

        mapSearchView = findViewById(R.id.mapSearch);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        geocoder = new Geocoder(MapLocation.this);

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null){
                    try{
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(addressList != null && addressList.size() > 0) {
                        if(searchLocationMarker != null){
                            searchLocationMarker.remove();
                        }

                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        searchLocationMarker = mMap.addMarker(markerOptions);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                    else{
                        Toast.makeText(MapLocation.this, "Cant find the location " + location, Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        getLastLocation();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    myCurrentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapLocation.this);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FINE_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLastLocation();
                }
                else{
                    Toast.makeText(MapLocation.this, "location permission is denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void pinMyLocation(){
        if(myCurrentLocation == null){
            return;
        }

        if(myLocationMarker != null){
            myLocationMarker.remove();
        }

        LatLng userLocation = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        CameraUpdate point = CameraUpdateFactory.newLatLng(userLocation);//Start at location of Tel Aviv
        mMap.moveCamera(point);// moves camera to coordinates
        MarkerOptions myPos = new MarkerOptions().position(userLocation);
        myPos.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        myLocationMarker = mMap.addMarker(new MarkerOptions().position(userLocation));
    }

    private void pinPlaceLocation(String place){
        List<Address> addressList = null;

        try{
            addressList = geocoder.getFromLocationName(place, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(place);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(markerOptions);
        }
        else{
            Log.d("place", place + " not found");
            //Toast.makeText(MapLocation.this, "Cant find the location " + place, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(int i = 0; i < locs.length; i++){
                    if(locs[i].equals(marker.getTitle())){
                        Intent intent = new Intent(MapLocation.this, CalendarViewActivity.class);
                        intent.putExtra("result", i);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

                return false;
            }
        });

        //LatLng telAviv = new LatLng(32.08, 34.78);
        pinMyLocation();

        for (String place :locs) {
            pinPlaceLocation(place);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged ( android.location.Location location ) {

    }

    @Override
    public void onPointerCaptureChanged ( boolean hasCapture ) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
