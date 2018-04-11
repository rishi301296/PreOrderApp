package com.example.rishiprotimbose.preorderapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private static DatabaseReference reference;
    private static View mView;
    private static Spinner spinner;
    private static Button bsearch;
    private static TextView tv;
    private static ListView listView;
    private static LatLng latLng;
    private static ArrayAdapter adapter;
    private static ArrayAdapter<String> viewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        bsearch = (Button) mView.findViewById(R.id.bsearch2);
        spinner = (Spinner) mView.findViewById(R.id.ssearch);
        tv = (TextView) mView.findViewById(R.id.tvsearch);
        listView = (ListView) mView.findViewById(R.id.lvsearch);
        reference = CustomerProfileActivity.reference;
        latLng = CustomerProfileActivity.current_latlng;

        viewAdapter = new ArrayAdapter<String>(mView.getContext(), android.R.layout.simple_list_item_1, CustomerProfileActivity.restaurants);
        listView.setAdapter(viewAdapter);

        adapter = ArrayAdapter.createFromResource(mView.getContext(), R.array.businesstype, android.R.layout.simple_gallery_item);
        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        spinner.setAdapter(adapter);

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(CustomerProfileActivity.businesstype != null &&
                ((CustomerProfileActivity.businesstype.equals("Restaurants") && CustomerProfileActivity.restaurants.size() > 0) ||
                (CustomerProfileActivity.businesstype.equals("Grocery Store") && CustomerProfileActivity.grocery.size() > 0))) {
            bsearch.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            tv.setVisibility(View.VISIBLE);
        }
        else {
            bsearch.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    CustomerProfileActivity.businesstype = "Restaurants";
                    if(CustomerProfileActivity.restaurants.size() == 0) {
                        bsearch.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.INVISIBLE);
                    }
                    else {
                        bsearch.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                    }
                }
                if(position == 1) {
                    CustomerProfileActivity.businesstype = "Grocery Store";
                    if(CustomerProfileActivity.grocery.size() == 0) {
                        bsearch.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.INVISIBLE);
                    }
                    else {
                        bsearch.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomerProfileActivity.businesstype.equals("Restaurants")) {
                    searchRestaurants();
                    bsearch.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                }
                if(CustomerProfileActivity.businesstype.equals("Grocery Store")) {
                    // searchGrocery();
                    Toast.makeText((CustomerProfileActivity) getActivity(), "Grocery", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchRestaurants() {
        int lat = ((int)(latLng.latitude)/10)*10, lng = ((int)(latLng.longitude)/10)*10;

        final ArrayList<String> res1 = new ArrayList<String>(), res2 = new ArrayList<String>();

        synchronized (this) {
            reference.child("Dealers").child("Restaurants").child("Latitudes").child(String.valueOf(lat) + " " + String.valueOf(lat + 10)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        final String key = result.getKey();
                        if (result.getValue(String.class).equals("Restaurants")) {
                            res1.add(key);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            reference.child("Dealers").child("Restaurants").child("Longitudes").child(String.valueOf(lng) + " " + String.valueOf(lng + 10)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        final String key = result.getKey();
                        if (result.getValue(String.class).equals("Restaurants")) {
                            res2.add(key);
                        }
                    }
                    res1.retainAll(res2);
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
            reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot result) {
                        Users newr = new Users(result.child(key).child("Auth").getValue(String.class),
                                result.child(key).child("Name").getValue(String.class),
                                result.child(key).child("Email").getValue(String.class),
                                result.child(key).child("PhoneNumber").getValue(String.class),
                                result.child(key).child("BusinessType").getValue(String.class),
                                result.child(key).child("Latitude").getValue(String.class),
                                result.child(key).child("Longitude").getValue(String.class)
                                );
                        CustomerProfileActivity.rusers.put(key, newr);
                        CustomerProfileActivity.restaurants.add(newr.getName());
                        viewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}
