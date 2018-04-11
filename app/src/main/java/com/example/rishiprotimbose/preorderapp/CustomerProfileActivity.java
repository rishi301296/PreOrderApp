package com.example.rishiprotimbose.preorderapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerProfileActivity extends AppCompatActivity {

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1024;
    private boolean mLocationPermissionsGranted = false;

    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference reference;
    public static LatLng current_latlng;
    public static Location current_location;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static Marker marker;
    public static MarkerOptions options;
    private static android.support.v4.app.FragmentManager fragmentManager;
    public static TextView tvname;
    public static TextView tvphonenumber;
    public static TextView tvemail;
    private static ImageButton bviewmap;
    private static ImageButton bsearch;
    private static ImageButton bfeedback;
    private static ImageButton beditprofile;
    private static ImageButton bsignout;
    public static HashMap<String, Users> rusers, gusers;
    public static HashMap<String, LatLng> ruk_l;
    public static HashMap<LatLng, String> rul_k;
    public static Users customer;

    public static String businesstype;
    public static ArrayList<String> restaurants, grocery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        getLocationPermission();

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        rusers = new HashMap<>();
        gusers = new HashMap<>();
        ruk_l = new HashMap<>();
        rul_k = new HashMap<>();
        customer = new Users();
        customer.setName(getIntent().getExtras().getString("Name"));
        customer.setEmail(getIntent().getExtras().getString("Email"));
        customer.setPhoneNumber(getIntent().getExtras().getString("PhoneNumber"));
        customer.setAuth("Customer");
        tvemail = (TextView) findViewById(R.id.tvemail);
        tvname = (TextView) findViewById(R.id.tvname);
        tvphonenumber = (TextView) findViewById(R.id.tvphonenumber);
        bviewmap = (ImageButton) findViewById(R.id.bviewmap);
        bsearch = (ImageButton) findViewById(R.id.bsearch);
        bfeedback = (ImageButton) findViewById(R.id.bfeedback);
        beditprofile = (ImageButton) findViewById(R.id.beditprofile);
        bsignout = (ImageButton) findViewById(R.id.bsignout);
        tvname.setText(customer.getName());
        tvemail.setText(customer.getEmail());
        tvphonenumber.setText(customer.getPhoneNumber());

        restaurants = new ArrayList<>();
        grocery = new ArrayList<>();

        bsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(CustomerProfileActivity.this);
                mBuilder.setIcon(android.R.drawable.stat_notify_error);
                mBuilder.setTitle("SignOut");
                mBuilder.setMessage("Are you sure!!!");
                mBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                    }
                });
                mBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = mBuilder.create();
                alertDialog.show();
            }
        });
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
