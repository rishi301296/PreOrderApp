package com.example.rishiprotimbose.preorderapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Map;

public class GetDataTask extends AsyncTask<Void, Void, Void> {

    private static Context context;
    private static DatabaseReference reference;
    private static LatLng cur_latlng;

    private static Map<String, Marker> mapping_KM;
    private static Map<Marker, String> mapping_MK;
    private static HashSet<String> res;

    private static int lat, lng;
    private static String bt, coord;

    public GetDataTask(String bt, String coord, HashSet<String> res, DatabaseReference reference, LatLng cur_latlng, Context context) {
        this.cur_latlng = cur_latlng;
        this.context = context;
        this.res = res;
        this.reference = reference;
        this.bt = bt;
        this.coord = coord;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        synchronized (context) {
            reference.child("Dealers").child(bt).child(coord).child(String.valueOf(lat) + " " + String.valueOf(lat + 10)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        Toast.makeText(context, result.getKey(), Toast.LENGTH_SHORT).show();
                        final String key = result.getKey();
                        if (result.getValue(String.class).equals(bt)) {
                            Log.d("res key", key);
                            res.add(key);
                        }
                    }
                    Log.d("res size", String.valueOf(res.size()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return null;
    }

    public HashSet<String> getRes() {
        return res;
    }
}
