package com.example.rishiprotimbose.preorderapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class DealerGetLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1024;
    private boolean mLocationPermissionsGranted = false;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private GoogleMap mMap;
    private static LatLng current_latlng;
    private static Location current_location;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ImageButton go;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_get_location);

        go = (ImageButton) findViewById(R.id.ibsearch);
        search = (EditText) findViewById(R.id.etsearch);

        getLocationPermission();

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(LATITUDE, String.valueOf(current_latlng.latitude));
                intent.putExtra(LONGITUDE, String.valueOf(current_latlng.longitude));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(DealerGetLocationActivity.this);

        Log.d("1st", "initMap()");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("2nd", "onMapReady()");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // Add a marker in current location and move the camera
            mMap.setMyLocationEnabled(false);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //mMap.getUiSettings().setCompassEnabled(false);
            //Toast.makeText(getApplicationContext(), current_latlng.latitude+", "+current_latlng.longitude, Toast.LENGTH_SHORT).show();
            //mMap.addMarker(new MarkerOptions().position(current_latlng).title("You are here"));
            //moveCamera(current_latlng, DEFAULT_ZOOM);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        try {
            if (mLocationPermissionsGranted) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            current_location = (Location) task.getResult();
                            current_latlng = new LatLng(current_location.getLatitude(), current_location.getLongitude());

                            moveCamera(current_latlng, DEFAULT_ZOOM);
                            mMap.addMarker(new MarkerOptions().position(current_latlng).title("You are here!"));

                            Log.d("LatLng", current_location.getLatitude()+", "+current_location.getLongitude());
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Unable To Find Location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(), "mLocationPermissionsNotGranted", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latlng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, DealerGetLocationActivity.class);
    }

    public static String getLatitude(Intent intent) {
        return intent.getStringExtra(LATITUDE);
    }

    public static String getLongitude(Intent intent) {
        return intent.getStringExtra(LONGITUDE);
    }
}
