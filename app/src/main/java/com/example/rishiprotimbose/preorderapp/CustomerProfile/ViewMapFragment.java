package com.example.rishiprotimbose.preorderapp.CustomerProfile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
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

    public static final float DEFAULT_ZOOM = 10f, FOCUS_ZOOM = 15f;

    private static LatLng latLng;
    private static GoogleMap mMap;
    private static View mView;
    private static MapView mMapView;
    private static Marker marker;
    private static MarkerOptions Poptions, Roptions;
    private static ImageButton border, bbook;

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

        bbook = (ImageButton) mView.findViewById(R.id.ibbook);
        border = (ImageButton) mView.findViewById(R.id.iborder);
        Poptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker));
        Roptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker));

        moveCamera(latLng, DEFAULT_ZOOM);
        marker = mMap.addMarker(Poptions.position(latLng).title("You are here"));

        addOtherMarkers();

        if(CustomerProfileActivity.businesstype[1] != null) {
            moveCamera(CustomerProfileActivity.ruk_m.get(CustomerProfileActivity.businesstype[1]).getPosition(), FOCUS_ZOOM);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {
                if(m != marker) {
                    CustomerProfileActivity.businesstype[1] = CustomerProfileActivity.rum_k.get(m);
                    return true;
                }
                return false;
            }
        });

        bbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        border.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void moveCamera(LatLng latlng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

    private void addOtherMarkers() {
        if(CustomerProfileActivity.businesstype[0] != null &&
                ((CustomerProfileActivity.businesstype[0].equals("Restaurants")) && (CustomerProfileActivity.ruk_u.size() > 0) )) {
            for(String key : CustomerProfileActivity.ruk_u.keySet()) {
                Users new_user = CustomerProfileActivity.ruk_u.get(key);
                LatLng la = new LatLng(Double.valueOf(new_user.getLatitude()), Double.valueOf(new_user.getLongitude()));
                final Marker m = mMap.addMarker(Roptions.
                        position(la).
                        title(new_user.getName()));

                CustomerProfileActivity.rum_k.put(m, key);
                CustomerProfileActivity.ruk_m.put(key, m);
            }
        }
        if(CustomerProfileActivity.businesstype[0] != null &&
            ((CustomerProfileActivity.businesstype[0].equals("Grocery Store")) && (CustomerProfileActivity.grocery.size() > 0) )) {
            Toast.makeText(mView.getContext(), "App in progress!!!", Toast.LENGTH_SHORT).show();
        }
    }
}
