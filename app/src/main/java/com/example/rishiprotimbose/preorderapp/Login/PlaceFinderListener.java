package com.example.rishiprotimbose.preorderapp.Login;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface PlaceFinderListener {
    void onPlaceFinderSuccess(LatLng latLng, String name);
}
