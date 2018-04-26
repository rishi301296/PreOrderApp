package com.example.rishiprotimbose.preorderapp.Login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.Locations.GetNearbyPlacesData;
import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealerGetLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1024;
    private boolean mLocationPermissionsGranted = false;
    private static final float DEFAULT_ZOOM = 13f;
    private static final int PROXIMITY_RADIUS = 20000;
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";

    private GoogleMap mMap;
    public static Map<String, Users> registeredRestaurants;
    public static DatabaseReference reference;
    private static LatLng current_latlng;
    private static Location current_location;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ImageButton ibgo, ibcur;
    private EditText etsearch;
    private static Marker marker;
    private static MarkerOptions options;
    private static String BusinessType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_get_location);

        reference = FirebaseDatabase.getInstance().getReference();
        registeredRestaurants = new HashMap<>();
        ibgo = (ImageButton) findViewById(R.id.ibsearch);
        ibcur = (ImageButton) findViewById(R.id.ibcur);
        etsearch = (EditText) findViewById(R.id.etsearch);
        BusinessType = getIntent().getExtras().getString("BUSINESS_TYPE");

        options = new MarkerOptions()
        .draggable(true)
        .anchor(0.5f, 0.8f);

        getLocationPermission();
        init();

        ibgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DealerGetLocationActivity.registeredRestaurants.containsKey(String.valueOf(marker.getPosition().latitude) + String.valueOf(marker.getPosition().longitude))) {
                    Intent intent = new Intent();
                    intent.putExtra(LATITUDE, String.valueOf(marker.getPosition().latitude));
                    intent.putExtra(LONGITUDE, String.valueOf(marker.getPosition().longitude));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(view.getContext(), "The dealer is already registered!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibcur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            init();
        }
        if(mMap != null) {
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker m) {
                    marker = m;
                    m.showInfoWindow();
                    return true;
                }
            });

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    marker.hideInfoWindow();
                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(getApplicationContext());
                    LatLng ll = marker.getPosition();
                    List <Address> list;
                    double lat = ll.latitude, lng = ll.longitude;

                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                        if(list.size() > 0) {
                            Address add = list.get(0);
                            marker.setTitle(add.getLocality());

                            moveCamera(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), DEFAULT_ZOOM, "Is this your business location?");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "No result found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void init() {
        etsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                }
                return false;
            }
        });
    }

    private void geoLocate() {
        String search = etsearch.getText().toString().trim();
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List <Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(search, 1);
        }
        catch (IOException e) {
            Log.d("Exception:", e.getMessage());
        }
        if(list.size() > 0) {
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getLocality());
        }
        else {
            Toast.makeText(getApplicationContext(), "No location found", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDeviceLocation() {
        if(current_latlng != null) {
            moveCamera(current_latlng, DEFAULT_ZOOM, "You are here");
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
                                current_location = (Location) task.getResult();
                                if(current_location != null) {
                                    current_latlng = new LatLng(current_location.getLatitude(), current_location.getLongitude());

                                    moveCamera(current_latlng, DEFAULT_ZOOM, "You are here");
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "We cannot find you!", Toast.LENGTH_SHORT).show();
                                }
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

    private void moveCamera(LatLng latlng, float zoom, String title) {
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latlng.latitude, latlng.longitude), zoom));
        if(marker != null) {
            mMap.clear();
            marker.remove();
        }
        marker = mMap.addMarker(options.position(latlng).title(title));

        getRegAndNonRegRestaurants();
    }

    private void getRegAndNonRegRestaurants() {
        Object data[] = new Object[2];
        data[0]=current_latlng;
        data[1]=mMap;

        TaskGetRegisteredRestaurants getRegisteredRestaurants = new TaskGetRegisteredRestaurants();
        getRegisteredRestaurants.execute(data);

        String restaurant = "restaurant";
        String url = getUrl(marker.getPosition().latitude, marker.getPosition().longitude, restaurant);
        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(dataTransfer);
    }

    private String getUrl(double latitude, double longitude, String nearByPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearByPlace);
        googlePlaceUrl.append("&keyword=cruise");
        googlePlaceUrl.append("&key="+getResources().getString(R.string.google_places_key));

        return googlePlaceUrl.toString();
    }

//    private void searchRestaurants() {
//
//    }


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
