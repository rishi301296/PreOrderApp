package com.example.rishiprotimbose.preorderapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class ViewMapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    public static final float DEFAULT_ZOOM = 12f;
    private static LatLng latLng;
    private static GoogleMap mMap;
    private static View mView;
    private static MapView mMapView;
    private static Marker marker;
    private static MarkerOptions Poptions, Roptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_view_map, container, false);

        Bundle bundle = getArguments();
        latLng = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));

        try {
            MapsInitializer.initialize(mView.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if(mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(ViewMapFragment.this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        Poptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.person_marker)));
        Roptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.restaurant_marker)));

        moveCamera(latLng, DEFAULT_ZOOM);
        marker = mMap.addMarker(Poptions.position(latLng).title("You are here"));

        addOtherMarkers();
    }

    private void moveCamera(LatLng latlng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

    private void addOtherMarkers() {
        if(CustomerProfileActivity.businesstype != null &&
                ((CustomerProfileActivity.businesstype.equals("Restaurants")) && (CustomerProfileActivity.restaurants.size() > 0) )) {
            for(String key : CustomerProfileActivity.rusers.keySet()) {
                Users new_user = CustomerProfileActivity.rusers.get(key);
                Log.d("Users", new_user.getName()+new_user.getLatitude());
                LatLng la = new LatLng(Double.valueOf(new_user.getLatitude()), Double.valueOf(new_user.getLongitude()));
                mMap.addMarker(Roptions.
                        position(la).
                        title(new_user.getName()));

                CustomerProfileActivity.rul_k.put(la, key);
                CustomerProfileActivity.ruk_l.put(key, la);
            }
        }
        if(CustomerProfileActivity.businesstype != null &&
            ((CustomerProfileActivity.businesstype.equals("Grocery Store")) && (CustomerProfileActivity.grocery.size() > 0) )) {
            Toast.makeText(mView.getContext(), "App in progress!!!", Toast.LENGTH_SHORT).show();
        }
    }
}
