package com.example.rishiprotimbose.preorderapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.Adapter.CustomAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private static DatabaseReference reference;
    private static View mView;
    private static Spinner spinner;
    private static Button bsearch, bviewmap, bsort, bfeedback;
    private static ProgressBar progress;
    private static TextView tv;
    private static ListView listView;
    private static ConstraintLayout layoutlistview;
    private static LatLng latLng;
    private static List<Users> restaurants;
    private static ArrayAdapter adapter;
    private static RecyclerView recyclerView;
    private static CustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        init();

        return mView;
    }

    private void init() {
        bsearch = (Button) mView.findViewById(R.id.bsearch2);
        bviewmap = (Button) mView.findViewById(R.id.bbook);
        bsort = (Button) mView.findViewById(R.id.bsort);
        bfeedback = (Button) mView.findViewById(R.id.bfeedback);
        spinner = (Spinner) mView.findViewById(R.id.ssearch);
        progress = (ProgressBar) mView.findViewById(R.id.psearch);
        tv = (TextView) mView.findViewById(R.id.tvsearch);
        recyclerView = (RecyclerView) mView.findViewById(R.id.lvsearch);
        layoutlistview = (ConstraintLayout) mView.findViewById(R.id.layoutlistview);
        reference = CustomerProfileActivity.reference;
        latLng = CustomerProfileActivity.current_latlng;
        restaurants = new ArrayList<>();

        RecyclerView.LayoutManager LM = new LinearLayoutManager(mView.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(LM);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mView.getContext(), LinearLayoutManager.VERTICAL));

        if(customAdapter != null) {
            recyclerView.setAdapter(customAdapter);
        }

        adapter = ArrayAdapter.createFromResource(mView.getContext(), R.array.businesstype, android.R.layout.simple_gallery_item);
        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(CustomerProfileActivity.businesstype[0] != null &&
                ((CustomerProfileActivity.businesstype[0].equals("Restaurants") && CustomerProfileActivity.ruk_u.size() > 0) ||
                (CustomerProfileActivity.businesstype[0].equals("Grocery Store") && CustomerProfileActivity.grocery.size() > 0))) {
            listViewOn();
        }
        else {
            listViewOff();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    CustomerProfileActivity.businesstype[0] = "Restaurants";
                    if(CustomerProfileActivity.ruu_k.size() == 0) {
                        listViewOff();
                    }
                    else {
                        listViewOn();
                    }
                }
                if(position == 1) {
                    CustomerProfileActivity.businesstype[0] = "Grocery Store";
                    if(CustomerProfileActivity.grocery.size() == 0) {       // grocery chng hoga to guk_u
                        listViewOff();
                    }
                    else {
                        listViewOn();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomerProfileActivity.businesstype[0].equals("Restaurants")) {
                    progress.setVisibility(View.VISIBLE);
                    Toast.makeText(mView.getContext(), "Fetching Data...", Toast.LENGTH_SHORT).show();
                    searchRestaurants();
                    listViewOn();
                    progress.setVisibility(View.INVISIBLE);
                }
                if(CustomerProfileActivity.businesstype[0].equals("Grocery Store")) {
                    // searchGrocery();
                    Toast.makeText((CustomerProfileActivity) getActivity(), "Grocery", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bviewmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(detailAdapter.)
            }
        });
    }

    private void searchRestaurants() {
        int lat = ((int)(latLng.latitude)/10)*10, lng = ((int)(latLng.longitude)/10)*10;

        final ArrayList<String> res1 = new ArrayList<>(), res2 = new ArrayList<>();

        synchronized (this) {
            reference.child("Dealers").child("Restaurants").child("Latitudes").child(String.valueOf(lat) + " " + String.valueOf(lat + 10)).addListenerForSingleValueEvent(new ValueEventListener() {
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

            reference.child("Dealers").child("Restaurants").child("Longitudes").child(String.valueOf(lng) + " " + String.valueOf(lng + 10)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        final String key = result.getKey();
                        if (result.getValue(String.class).equals("true")) {
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
                        CustomerProfileActivity.ruk_u.put(key, newr);
                        CustomerProfileActivity.ruu_k.put(newr, key);

                    customAdapter = new CustomAdapter(CustomerProfileActivity.ruk_u.keySet(), mView.getContext());
                    recyclerView.setAdapter(customAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void listViewOn() {
        bsearch.setVisibility(View.INVISIBLE);
        layoutlistview.setVisibility(View.VISIBLE);
    }

    private void listViewOff() {
        bsearch.setVisibility(View.VISIBLE);
        layoutlistview.setVisibility(View.INVISIBLE);
    }
}
