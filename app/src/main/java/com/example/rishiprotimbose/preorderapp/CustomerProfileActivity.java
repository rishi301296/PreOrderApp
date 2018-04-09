package com.example.rishiprotimbose.preorderapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerProfileActivity extends AppCompatActivity {

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1024;
    private boolean mLocationPermissionsGranted = false;

    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference reference;
    private static LatLng current_latlng;
    private static Location current_location;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static Marker marker;
    private static MarkerOptions options;
    private static android.support.v4.app.FragmentManager fragmentManager;
    private static TextView tvname;
    private static TextView tvphonenumber;
    private static TextView tvemail;
    private static Button bviewmap;
    private static Button bsearch;
    private static Button bfeedback;
    private static Button beditprofile;
    private static Users new_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        getLocationPermission();

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        new_user = new Users();
        new_user.setName(getIntent().getExtras().getString("Name"));
        new_user.setEmail(getIntent().getExtras().getString("Email"));
        new_user.setPhoneNumber(getIntent().getExtras().getString("PhoneNumber"));
        new_user.setAuth("Customer");
        tvemail = (TextView) findViewById(R.id.tvemail);
        tvname = (TextView) findViewById(R.id.tvname);
        tvphonenumber = (TextView) findViewById(R.id.tvphonenumber);
        bviewmap = (Button) findViewById(R.id.bviewmap);
        bsearch = (Button) findViewById(R.id.bsearch);
        bfeedback = (Button) findViewById(R.id.bfeedback);
        beditprofile = (Button) findViewById(R.id.beditprofile);
        tvname.setText(new_user.getName());
        tvemail.setText(new_user.getEmail());
        tvphonenumber.setText(new_user.getPhoneNumber());
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            getDeviceLocation();
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
                    getDeviceLocation();
                }
            }
        }
    }

    private void getDeviceLocation() {
        if(current_latlng == null) {
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
                            if (task.isSuccessful()) {
                                current_location = (Location) task.getResult();
                                current_latlng = new LatLng(current_location.getLatitude(), current_location.getLongitude());

                                addListener();

                                Log.d("LatLng", current_location.getLatitude() + ", " + current_location.getLongitude());
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable To Find Location!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "mLocationPermissionsNotGranted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addListener() {
        bviewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
        bfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
        beditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
    }

    public void changeFragment(View view) {
        android.support.v4.app.Fragment fragment;

        if(view == findViewById(R.id.bviewmap)) {
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", current_latlng.latitude);
            bundle.putDouble("longitude", current_latlng.longitude);
            Log.d("key", String.valueOf(current_latlng.latitude));
            fragment = new ViewMapFragment();
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
        if(view == findViewById(R.id.bsearch)) {
            fragment = new SearchFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
        if(view == findViewById(R.id.bfeedback)) {
            fragment = new FeedbackFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
        if(view == findViewById(R.id.beditprofile)) {
            fragment = new EditProfileFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
    }

}
