package com.example.rishiprotimbose.preorderapp.DealerProfile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.rishiprotimbose.preorderapp.Orders;
import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private static View view;
    private static ListView lvcustomers, lvorders;
    private static ConstraintLayout error, normal;
    private static List<String> customersList;
    private static List<Orders> ordersList;
    private static List<String> customerKeys;
    private static ArrayAdapter<String> customerAdapter;
    private static ArrayAdapter<String> ordersAdapter;
    private static int currentCustomer;
    private static List<String> currentOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentCustomer = -1;
        currentOrder = new ArrayList<>();
        lvcustomers = (ListView) view.findViewById(R.id.lvcustomers);
        lvorders = (ListView) view.findViewById(R.id.lvorders);
        customersList = new ArrayList<>();
        ordersList = new ArrayList<>();
        ordersAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_expandable_list_item_1, currentOrder);
        customerAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_expandable_list_item_1, customersList);
        lvcustomers.setAdapter(customerAdapter);
        lvorders.setAdapter(ordersAdapter);

        error = (ConstraintLayout) view.findViewById(R.id.error);
        normal = (ConstraintLayout) view.findViewById(R.id.normal);
        if(DealerProfileActivity.ordertypesdone) {
            normal.setVisibility(View.VISIBLE);
            error.setVisibility(View.INVISIBLE);
        }
        else {
            normal.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
        }

        DealerProfileActivity.reference.child("PlacedOrder").child(DealerProfileActivity.dealer_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot i : dataSnapshot.getChildren()) {
                    ordersList.add(i.child(i.getKey().substring(2,6)+DealerProfileActivity.dealer_key.substring(2,6)).getValue(Orders.class));

                    DealerProfileActivity.reference.child("Users").child(i.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot data) {
                            customersList.add(data.getValue(Users.class).getName());
                            customerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        lvcustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentOrder = new ArrayList<>();
                for(int j=0; j<ordersList.get(i).getFoodType().size(); j++) {
                    currentOrder.add(ordersList.get(i).foodType.get(j) + " : " + ordersList.get(i).foodQty.get(j));
                    ordersAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
