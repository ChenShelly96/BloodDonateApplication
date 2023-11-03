package com.androidapp.blooddonate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.androidapp.blooddonate.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, LocationListener , DialogInterface{
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
  //  private static final int REQUEST_CODE = 101;
    public int locNumber;
    public int counterLocsFromDB ;

    ArrayList<android.location.Location> locsToMap; // Create an ArrayList object
    ArrayList<Marker> markerArrayList;
    //database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locNumberRef = database.getReference("LocationMaps number");
    DatabaseReference locationsRef = database.getReference("Locations");
    DatabaseReference publicRef = database.getReference();

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView LocationField;
    private LocationManager locationManager;
    private String provider;
    private FirebaseAuth mAuth;
    //LocationMaps currentLocation;
    //FusedLocationProviderClient fusedLocationProviderClient;
   // private static final int REQUEST_CODE = 101;
    public MapsActivity2 ( String provider ) {
        this.provider = provider;
    }

    public MapsActivity2 ( ) {

    }

    public MapsActivity2 ( int contentLayoutId, int locNumber ) {
        super(contentLayoutId);
        this.locNumber = locNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locsToMap = new ArrayList<>(); // Create an ArrayList object
        markerArrayList = new ArrayList<>();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map, mapFragment)
                .commit();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
             //   .findFragmentById(R.id.map);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity2.this);
       // fetchLocation();
       // if (mapFragment == null) throw new AssertionError();
        //mapFragment.getMapAsync(this);
        // Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);

      /*  LocationField = (TextView) findViewById(R.id.placeName);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (provider != null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MapsActivity2.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                android.location.Location location = locationManager.getLastKnownLocation(provider);

                // Initialize the location fields
                if (location != null) {
                    onLocationChanged(location);
                } else {
                    Log.d("result", "LocationMaps not available");
                }
            }*/
       // }

        locNumberRef.setValue(0);

    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }
      //  android.location.Location loc = locationManager.getLastKnownLocation(provider);
        //onLocationChanged(loc);
        Task< android.location.Location> task = fusedLocationProviderClient.getLastLocation();
       task.addOnSuccessListener(new OnSuccessListener<Location>() {
           @Override
           public void onSuccess ( Location location ) {

                if (location != null) {
                   currentLocation = location;
                   Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                   SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                           .findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                   supportMapFragment.getMapAsync(MapsActivity2.this);
               }
           }
        });
    }

    public void onClickBtn(View v) {
        Intent i = getIntent();
        LocationMaps addLoc = (LocationMaps) i.getParcelableExtra("key");
        writeLoc(addLoc.get_Latitude(), addLoc.get_Longitude());
    }


    private void get_full(double latitude, double longitude) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        LocationField.setText(address + " " + city + " ");
    }


    private String get_full_for_table(double latitude, double longitude) throws IOException {
        StringBuffer fullstring = new StringBuffer();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        fullstring.append(address).append(" ");
        return fullstring.toString();
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Marker marker;
        mMap = googleMap;
        mMap.clear();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        android.location.Location location= new android.location.Location("tel aviv");



        LatLng currentLoc = new LatLng(location.getLatitude()
                , location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLoc));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((location.getLatitude()), (location.getLongitude())), 17.0f));
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Your current location"));


        LatLng sec = new LatLng(32.073698, 34.781924); // this is a test
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Your current location"));
        mMap.addMarker(new MarkerOptions().position(sec).title("Your sec location"));

        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.custom_map_style)
        );


        MarkerOptions userIndicator = new MarkerOptions()
                .position(new LatLng((location.getLatitude()), (location.getLongitude())))
                .title("You are here")
                .snippet("lat:" + (location.getLatitude()) + ", lng:" +  (location.getLongitude()));


        addLocationsToMap(mMap); //read from db and marker them on map
        //mMap.setMyLocationEnabled(true);
        markerToast(mMap,currentLoc);
    }


    public void addLocationsToMap(GoogleMap googleMap) {
        int locNum = 0;


           //hard codded:
        LatLng sec = new LatLng(32.073698, 34.781924); // this is a test
        mMap.addMarker(new MarkerOptions().position(sec).title("Your sec location"));

        //LatLng third = new LatLng(32.095280, 34.871420); // this is a test
        //mMap.addMarker(new MarkerOptions().position(third).title("Your third location"));


        locNumberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int locNum = (int) snapshot.getValue(int.class);
                locNumber = locNum;
                //readLocsFromDB(); // read from db and add them to arrayList
                locateLocs();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("result:", "Database error!");
            }
        });

    }

    public void locateLocs() {
        mMap.clear();
        //place the locations from the db on the map:
        Log.d("result:", "locateLocs func ");

        for (int i = 0; i < locNumber; i++) {
            LatLng thisLoc = new LatLng((locsToMap.get(i).getLatitude()), (locsToMap.get(i).getLongitude()));
            Marker marker = mMap.addMarker(
                    new MarkerOptions()
                            .position(thisLoc)
                            .draggable(true));
            this.mMap.addMarker(new MarkerOptions().position(thisLoc).title("loc from db"));
            Log.d("result:", "i is: " + i + "- location added.");

        }
    }


    public void readLocsFromDB() {
        removeAlllocsFromLocal();
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    counter++;
                    android.location.Location loc = snap.getValue(android.location.Location.class);
                    assert loc != null;
                    LatLng newLoc = new LatLng((loc.getLatitude()), (loc.getLongitude()));
                    Marker newMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(newLoc)
                                    .draggable(true));
                    locsToMap.add(loc);
                    //TODO:
                    //ADD MARKER TO THE MARKER ARRAY
                    markerArrayList.add(newMarker);



                }
                counterLocsFromDB=counter;
                locNumberRef.setValue(counterLocsFromDB);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("result:", "Database error!");

            }
        });

    }


    public void removeAlllocsFromLocal(){
        for(int i=0; i<locsToMap.size(); i++){
            locsToMap.remove(i);
        }
    }

    public void markerToast(GoogleMap googleMap,  LatLng thisLoc){
        Intent i = getIntent();
       // LocationMaps location = (LocationMaps) i.getParcelableExtra("key");
       // LatLng thisLoc = new LatLng(parseDouble(location.get_Latitude()), parseDouble(location.get_Longitude()));

        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                double distance = distance(thisLoc.latitude, thisLoc.longitude,marker.getPosition().latitude,marker.getPosition().longitude);
                double dist = (int)(Math.round(distance * 1000))/1000.0;
                Toast.makeText(MapsActivity2.this, "This location is " + dist + " km from you.", Toast.LENGTH_LONG).show();
                popupMessage(marker);
                return true;
            }
        });

    }

    public void popupMessage(Marker marker) {
        String address = "";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to navigate to this parking?");
        try {
            address = get_full_for_table(marker.getPosition().latitude, marker.getPosition().longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }

        alertDialogBuilder.setTitle(address+ " ");
        alertDialogBuilder.setPositiveButton("Navigate", new OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.waze.com/ul?ll=" +marker.getPosition().latitude+"%2C"+marker.getPosition().longitude+"&navigate=yes&zoom=17"));
                startActivity(navigation);
            }
        });
        alertDialogBuilder.setNegativeButton("Delete parking" , new OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //TODO:
                //delete marker + location from the db
                deleteLoc(marker);



            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void deleteLoc(Marker maker){
       // android.location.Location locToDelete = new android.location.Location(maker.getPosition().latitude+"", maker.getPosition().longitude+"", false);
        android.location.Location locToDelete = new android.location.Location(maker.getPosition().latitude+" "+ maker.getPosition().longitude+"");
        LatLng latLng = new LatLng((locToDelete.getLatitude()), (locToDelete.getLongitude()));
        //Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        //int id=54353;

        Log.d("to be remove", maker.getPosition().latitude + ", " + maker.getPosition().longitude);
        for(int i=0; i<locsToMap.size(); i++){
            if(locsToMap.get(i).equals(locToDelete)){
               // id = locsToMap.get(i).get_id();
                Log.d("removing", locsToMap.get(i).getLatitude() + ", " + locsToMap.get(i).getLongitude());
                locsToMap.remove(i);
                locNumber=locNumber-1;
                break;
            }
        }
        Log.d("marker to be remove", maker.getPosition().latitude + ", " + maker.getPosition().longitude);
        for(int j=0; j<markerArrayList.size(); j++){
            if((markerArrayList.get(j).getPosition().latitude+"").equals(locToDelete.getLatitude())&&(markerArrayList.get(j).getPosition().longitude+"").equals(locToDelete.getLongitude())){
                Log.d("removing marker ", markerArrayList.get(j).getPosition().latitude + ", " + markerArrayList.get(j).getPosition().longitude);
                markerArrayList.remove(j);
                break;
            }
        }


      //  Query locationQuery = locationsRef.child(id+"");
       // Log.d("id to delete ", id +"");

       /* locationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("result ", "data change!");
                for (DataSnapshot locationSnapshot: snapshot.getChildren()) {
                    Log.d("result ", "found!");
                    locationSnapshot.getRef().removeValue();
                }
            }
*/
     /*       @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("result", "onCancelled", error.toException());

            }
        });*/

        locNumberRef.setValue(locNumber); //is working
        addLocationsToMap(mMap);
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        Intent intent = new Intent(this, MapsActivity.class);
        String lat = location.getLatitude() + "";
        String lng = location.getLongitude() + "";
        LocationMaps location_xy = new LocationMaps(lat, lng);

        try {
            get_full(location.getLatitude(), location.getLongitude());
            intent.putExtra("key", (Parcelable) location_xy);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull List<android.location.Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Turn on location services! ",
                Toast.LENGTH_SHORT).show();
    }

    public void writeLoc(String lat, String longlat) {
        mAuth = FirebaseAuth.getInstance();
        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //get all data from the layout text
        LocationMaps l = new LocationMaps(lat, longlat);
        DatabaseReference myRef = database.getReference("User").child("User id: " + id).child("LocationMaps " + (locNumber+1));

        DatabaseReference publicRef = database.getReference("Locations").child("" + (locNumber+1));
        DatabaseReference locNumberRef = database.getReference("LocationMaps number");


        publicRef.setValue(l);
        myRef.setValue(l);
        Toast.makeText(this, "location " + (locNumber+1) + " added.",
                Toast.LENGTH_SHORT).show();
        locNumberRef.setValue(l.get_id());
        //addToView();

        Toast.makeText(this, "LocationMaps added!", Toast.LENGTH_LONG).show();

    }


    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return (dist);
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void cancel() {

    }

    @Override
    public void dismiss() {

    }
}