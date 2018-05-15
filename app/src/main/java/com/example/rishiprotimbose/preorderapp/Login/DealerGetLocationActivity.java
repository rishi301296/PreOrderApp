package com.example.rishiprotimbose.preorderapp.Login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealerGetLocationActivity extends FragmentActivity implements OnMapReadyCallback, PlaceFinderListener {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1024;
    private boolean mLocationPermissionsGranted = false;
    private static final float DEFAULT_ZOOM = 13f;
    private static final Integer PROXIMITY_RADIUS = 12000;
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
    public static Marker marker;
    private static MarkerOptions options;
    private static String BusinessType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_get_location);

        getLocationPermission();
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

            mMap.getUiSettings().setCompassEnabled(true);
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
                public void onMarkerDrag(Marker marker) {}

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    if( !geoLocatePlace(null, new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))) {
                        Toast.makeText(getApplicationContext(), "Location not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference();
        registeredRestaurants = new HashMap<>();
        ibgo = (ImageButton) findViewById(R.id.ibsearch);
        ibcur = (ImageButton) findViewById(R.id.ibcur);
        etsearch = (EditText) findViewById(R.id.etsearch);
        BusinessType = getIntent().getExtras().getString("BUSINESS_TYPE");

        options = new MarkerOptions()
                .draggable(true)
                .anchor(0.5f, 0.8f);

        etsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                    etsearch.setSelected(false);
                }
                return false;
            }
        });

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

    private boolean geoLocatePlace(String search, LatLng latLng) {
        Geocoder gc = new Geocoder(getApplicationContext());
        List <Address> list;
        try {
            if(latLng != null) {
                list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
            }
            else {
                list = gc.getFromLocationName(search, 1);
            }
            if(list.size() > 0) {
                Address add = list.get(0);
                marker.setTitle(add.getLocality());

                moveCamera(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), DEFAULT_ZOOM, "Is this your business location?");
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private void geoLocate() {
        String search = etsearch.getText().toString().trim();
        if(!search.equals("")) {
            if (!geoLocatePlace(search, null)) {
                try {
                    new PlaceFinder(getApplicationContext(), this, current_latlng, search, "" + PROXIMITY_RADIUS).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Search field is empty!", Toast.LENGTH_SHORT).show();
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

    private void moveCamera(LatLng latlng, float zoom, String title) {
        current_latlng = latlng;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latlng.latitude, latlng.longitude), zoom));
        if(marker != null) {
            mMap.clear();
            marker.remove();
        }
        marker = mMap.addMarker(options
                .position(latlng)
                .title(title));

        getRegAndNonRegRestaurants();
    }

    private void getRegAndNonRegRestaurants() {
        synchronized (this) {
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("restaurant_marker", "drawable", getPackageName()));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 64, 84, false);
            BitmapDescriptor restaurant_marker = BitmapDescriptorFactory.fromBitmap(resizedBitmap);

            Object data[] = new Object[3];
            data[0]=current_latlng;
            data[1]=mMap;
            data[2]=restaurant_marker;

            TaskGetRegisteredRestaurants getRegisteredRestaurants = new TaskGetRegisteredRestaurants();
            getRegisteredRestaurants.execute(data);

            String restaurant = "restaurant";
            String url = getUrl(marker.getPosition().latitude, marker.getPosition().longitude, restaurant);
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;

            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(dataTransfer);

//            getRegisteredRestaurants();
//            getUnregisteredRestaurants();
        }
    }

    private void getRegisteredRestaurants() {

    }

    private void getUnregisteredRestaurants() {

    }

    private String getUrl(double latitude, double longitude, String nearByPlace) {
        return  "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        +"location="+latitude+","+longitude
        +"&radius="+PROXIMITY_RADIUS
        +"&type="+nearByPlace
        +"&keyword=cruise"
        +"&key="+getResources().getString(R.string.google_maps_key);
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

    @Override
    public void onPlaceFinderSuccess(LatLng latLng, String name) {
        if(latLng != null) {
            moveCamera(latLng, DEFAULT_ZOOM, name);
        }
        else {
            Toast.makeText(getApplicationContext(), "No location found", Toast.LENGTH_SHORT).show();
        }
    }
}
