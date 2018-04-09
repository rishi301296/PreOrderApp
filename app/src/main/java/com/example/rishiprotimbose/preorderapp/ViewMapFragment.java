package com.example.rishiprotimbose.preorderapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class ViewMapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    public static final float DEFAULT_ZOOM = 12f;
    private static LatLng latLng;
    private static ArrayList<String> restaurants;
    private static GoogleMap mMap;
    private static View mView;
    private static MapView mMapView;
    private static Marker marker;
    private static MarkerOptions Poptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_view_map, container, false);

        Bundle bundle = getArguments();
        latLng = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));

        Poptions = new MarkerOptions();

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

        moveCamera(latLng, DEFAULT_ZOOM);
        marker = mMap.addMarker(Poptions.position(latLng).title("You are here"));
    }

    private void moveCamera(LatLng latlng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }
}
