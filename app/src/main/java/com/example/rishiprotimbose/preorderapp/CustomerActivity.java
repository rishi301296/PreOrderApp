package com.example.rishiprotimbose.preorderapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CustomerActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1024;
    private static boolean mLocationPermissionsGranted = false;
    private static final float DEFAULT_ZOOM = 10f;

    private static FirebaseUser firebaseUser;
    private static DatabaseReference reference;
    private GoogleMap mMap;
    private static LatLng cur_latlng;
    private static Location cur_loc;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static Marker marker, m;
    private static MarkerOptions Poptions, Roptions, Soptions;
    private static Spinner sbusinesstype;
    private static ArrayAdapter adapter;

    private static List restaurants, grocery_store;
    private static Map<String, Marker> mapping_KM;
    private static Map<Marker, String> mapping_MK;
    private static HashSet<String> res1, res2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        restaurants = new ArrayList<String>();
        grocery_store = new ArrayList<String>();
        mapping_KM = new HashMap<String, Marker>();
        mapping_MK = new HashMap<Marker, String>();
        res1 = new HashSet<String>();
        res2 = new HashSet<String>();

        getLocationPermission();

        reference = FirebaseDatabase.getInstance().getReference();
        Poptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.person_marker)))
                .anchor(0.5f, 0.8f);
        Roptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.restaurant_marker)))
                .anchor(0.5f, 0.8f);
        Soptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.store_marker)))
                .anchor(0.5f, 0.8f);

        sbusinesstype = (Spinner) findViewById(R.id.sbusinesstype);

        adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.businesstype, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sbusinesstype.setAdapter(adapter);
        
        sbusinesstype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    Log.d("pos", String.valueOf(position));
//                    getRestaurants();
                }
                else if(position == 1) {
                    Toast.makeText(getApplicationContext(), "Gros", Toast.LENGTH_SHORT).show();
                //    showGroceryStore();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getRestaurants() {
        int lat, lng;
        lat = (int) cur_latlng.latitude;
        lng = (int) cur_latlng.longitude;

        lat = ((lat / 10) * 10);
        lng = ((lng / 10) * 10);

        Log.d("string", String.valueOf(lat) + " " + String.valueOf(lat + 10));

        GetDataTask getDataTask1 = new GetDataTask("Restaurants", "Latitudes", res1, reference, cur_latlng, getApplicationContext());
        getDataTask1.execute();

        GetDataTask getDataTask2 = new GetDataTask("Restaurants", "Longitudes", res2, reference, cur_latlng, getApplicationContext());
        getDataTask2.execute();

        Log.d("result", res1.size()+", "+res2.size());

//            reference.child("Dealers").child("Restaurants").child("Longitudes").child(String.valueOf(lng) + " " + String.valueOf(lng + 10)).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot result : dataSnapshot.getChildren()) {
//                        final String key = result.getKey();
//                        Log.d("res2 key", key);
//                        res2.add(key);
//                    }
//                    Log.d("res2 size", String.valueOf(res2.size()));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

        Log.d("size", res1.size() + ", " + res2.size());
        res1.retainAll(res2);
        restaurants = new ArrayList<String>(res1);
        Log.d("restaurants array size", String.valueOf(restaurants.size()));
    }

    private void showRestaurants() {
        Log.d("showRestaurants", "showRes");
        Log.d("Test", String.valueOf(restaurants.size()));


//        for(int i = 0; i < restaurants.size(); i++) {
//        reference.child("Users").child(restaurants.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot result : dataSnapshot.getChildren()) {
//                    double la = (double) Double.valueOf(result.child("Latitude").getValue().toString());
//                    double lo = (double) Double.valueOf(result.child("Longitude").getValue().toString());
//                    m = mMap.addMarker(Roptions.position(new LatLng(la, lo)).title(result.child(result.getKey()).child("Name").getValue().toString()));
//                    mapping_KM.put(result.getKey(), m);
//                    mapping_MK.put(m, result.getKey());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        }

        // display in ListView
    }

//    private void showGroceryStore() {
//
//    }

    private void getLocationPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
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
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.custmap);
        mapFragment.getMapAsync(CustomerActivity.this);

        Log.d("1st", "initMap()");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("2nd", "onMapReady()");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            Log.d("onMapReady", "got dev loc");

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }
        else {
            Toast.makeText(getApplicationContext(), "LocPerNotGrant", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceLocation() {
        if(cur_latlng != null) {
            moveCamera(cur_latlng, DEFAULT_ZOOM);
            marker = mMap.addMarker(Poptions.position(cur_latlng).title("You are here"));
        }
        else {
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
                                cur_loc = (Location) task.getResult();
                                cur_latlng = new LatLng(cur_loc.getLatitude(), cur_loc.getLongitude());

                                getRestaurants();
//                                GetDataTask getDataTask = new GetDataTask(reference, cur_latlng, getApplicationContext(), restaurants);
//                                getDataTask.execute();

                                moveCamera(cur_latlng, DEFAULT_ZOOM);
                                marker = mMap.addMarker(Poptions.position(cur_latlng).title("You are here"));

                                Log.d("getDeviceLocation", cur_loc.getLatitude() + ", " + cur_loc.getLongitude());
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
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveCamera(LatLng latlng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }
}
