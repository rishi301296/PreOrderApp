package com.example.rishiprotimbose.preorderapp.Login;

import android.os.AsyncTask;

import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TaskGetRegisteredRestaurants extends AsyncTask<Object, String, LatLng> {
    LatLng latLng;
    GoogleMap mMap;

    @Override
    protected LatLng doInBackground(Object... objects) {
        latLng = (LatLng) objects[0];
        mMap = (GoogleMap) objects[1];

        return latLng;
    }

    @Override
    protected void onPostExecute(LatLng latLng) {
        int lat = ((int)(latLng.latitude)/10)*10, lng = ((int)(latLng.longitude)/10)*10;
        final ArrayList<String> res1 = new ArrayList<>(), res2 = new ArrayList<>();

        synchronized (this) {
            DealerGetLocationActivity.reference
                    .child("Dealers")
                    .child("Restaurants")
                    .child("Latitudes")
                    .child(String.valueOf(lat) + " " + String.valueOf(lat + 10))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        final String key = result.getKey();
                        if (result.getValue(String.class).equals("true")) {
                            res1.add(key);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            DealerGetLocationActivity.reference
                    .child("Dealers")
                    .child("Restaurants")
                    .child("Longitudes")
                    .child(String.valueOf(lng) + " " + String.valueOf(lng + 10))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        final String key = result.getKey();
                        if (result.getValue(String.class).equals("true")) {
                            res2.add(key);
                        }
                    }
                    res1.retainAll(res2);
                    if(res1.size() == 0) {  }
                    setRestaurants(res1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void setRestaurants(ArrayList<String> res) {
        for(final String key : res) {
            DealerGetLocationActivity.reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot result) {
                    Users newr = new Users();
                    newr = result.child(key).getValue(Users.class);
                    LatLng latLng = new LatLng(Double.valueOf(newr.getLatitude()), Double.valueOf(newr.getLongitude()));
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_marker))
                            .position(latLng)
                            .title(newr.getName()));
                    DealerGetLocationActivity.registeredRestaurants.put(String.valueOf(latLng.latitude)+String.valueOf(latLng.longitude), newr);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
}
