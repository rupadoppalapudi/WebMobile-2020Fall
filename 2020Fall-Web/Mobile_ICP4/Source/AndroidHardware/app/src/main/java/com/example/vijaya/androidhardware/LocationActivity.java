package com.example.vijaya.androidhardware;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Declaring the variables
    private GoogleMap mMap;
    private static final String TAG = "MainActivity";
    private boolean mLocationPermission = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        checkLocationPermission();
    }

    /*
     * This method will call after Map is ready.
     * In this we will call the current location of the device.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermission) {
            getCurrentLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    /*
     * This method will call once the user select the permission either allow or deny.
     * permission is allowed in the emulator, thus we are initializing the map.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermission = false;
        switch (requestCode) {
            case 2345:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermission = true;
                    initMap();
                }
        }
    }

    /*
     * initializing the map settings.
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /*
     * checking the location permission.
     */
    private void checkLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        System.out.println("in Permissions check method");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("in Permissions check method first If");
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("in Permissions check method second if");
                mLocationPermission = true;
                initMap();
            } else {
                System.out.println("in Permissions check method first else");
                ActivityCompat.requestPermissions(this, permissions, 2345);
            }
        } else {
            System.out.println("in Permissions check method second else");
            ActivityCompat.requestPermissions(this, permissions, 2345);
        }
    }

    /*
     * getting the current location details.
     */
    private void getCurrentLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermission){
                final Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Log.i(TAG,"Location found");
                            Location currentLocation = (Location) task.getResult();
                            updateLocation(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),15f);
                        } else{
                            Log.e(TAG,"Location not found");
                        }
                    }
                });
            }
        }catch (SecurityException e)
        {
            Log.e(TAG,e.getMessage());
        }
    }

    /*
     * updating the marker image and latitude and longitude details.
     */
    private void updateLocation(LatLng latlng, float zoom){
        System.out.println("latlng is");
        System.out.println(latlng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(latlng).title("Latitude : "+latlng.latitude+" "+"Longitude : "+latlng.
                longitude).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_maps)));
    }
}