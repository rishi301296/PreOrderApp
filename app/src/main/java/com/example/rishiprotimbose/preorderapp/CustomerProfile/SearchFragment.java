package com.example.rishiprotimbose.preorderapp.CustomerProfile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.Adapter.CustomAdapter;
import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
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
    private static ImageButton bbook, border, bsort, binfo;
    private static ProgressBar progress;
    private static TextView tv;
    private static ListView listView;
    private static RelativeLayout layoutlistview;
    private static LatLng latLng;
    private static ArrayAdapter adapter;
    private static RecyclerView recyclerView;
    private static CustomAdapter customAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);

        return mView;
    }

    private void init() {
        bsearch = (Button) mView.findViewById(R.id.bsearch2);
        bbook = (ImageButton) mView.findViewById(R.id.ibbook);
        border = (ImageButton) mView.findViewById(R.id.iborder);
        bsort = (ImageButton) mView.findViewById(R.id.ibsort);
        binfo = (ImageButton) mView.findViewById(R.id.ibinfo);
        spinner = (Spinner) mView.findViewById(R.id.ssearch);
        progress = (ProgressBar) mView.findViewById(R.id.psearch);
        tv = (TextView) mView.findViewById(R.id.tvsearch);
        progress = (ProgressBar) mView.findViewById(R.id.psearch);
        recyclerView = (RecyclerView) mView.findViewById(R.id.lvsearch);
        layoutlistview = (RelativeLayout) mView.findViewById(R.id.layoutlistview);
        reference = CustomerProfileActivity.reference;
        latLng = CustomerProfileActivity.current_latlng;

        RecyclerView.LayoutManager LM = new LinearLayoutManager(mView.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(LM);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mView.getContext(), LinearLayoutManager.VERTICAL));

        if(customAdapter != null) {
            recyclerView.setAdapter(customAdapter);
        }

        adapter = ArrayAdapter.createFromResource(mView.getContext(), R.array.businesstypes, android.R.layout.simple_gallery_item);
        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

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
                    if(CustomerProfileActivity.restaurants.size() == 0) {
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
                }
                if(CustomerProfileActivity.businesstype[0].equals("Grocery Store")) {
                    // searchGrocery();
                    Toast.makeText((CustomerProfileActivity) getActivity(), "Grocery", Toast.LENGTH_SHORT).show();
                }
            }
        });

        border.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CustomerProfileActivity.businesstype[1] != null) {
                    Fragment fragment = new CustomerOrderFragment();
                    FragmentTransaction fragmentTransaction = CustomerProfileActivity.fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.mainfragment, fragment);
                    fragmentTransaction.commit();
                }
                else {
                    Toast.makeText(view.getContext(), "Select one restaurant to place an order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bsort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void setRestaurants(ArrayList<String> res) {
        customAdapter = new CustomAdapter(CustomerProfileActivity.restaurants, mView.getContext());

        for(final String key : res) {
            reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot result) {
                    final Users newr = (Users) result.child(key).getValue(Users.class);
                    CustomerProfileActivity.reference.child("Feedback")
                            .child(key)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final int rate = dataSnapshot.child("Total").getValue(Integer.class);
                                    if(rate > 0) {
                                        newr.setStars((int) (rate / (dataSnapshot.getChildrenCount() - 1)));
                                    }

                                    CustomerProfileActivity.ruk_u.put(key, newr);
                                    CustomerProfileActivity.ruu_k.put(newr, key);
                                    CustomerProfileActivity.restaurants.add(newr);

                                    customAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
        progress.setVisibility(View.INVISIBLE);
        recyclerView.setAdapter(customAdapter);
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
