package com.example.rishiprotimbose.preorderapp.CustomerProfile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.Orders;
import com.example.rishiprotimbose.preorderapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class CustomerOrderFragment extends Fragment {

    private static View view;
    private static GridLayout gridLayout;
    private static FloatingActionButton fplus, fminus;
    private static Button bsubmit;
    private static List<String> foodtype, foodqty, foodavailable;
    private static int currentfood;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_order, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridLayout = (GridLayout) view.findViewById(R.id.gridLayout);
        fplus = (FloatingActionButton) view.findViewById(R.id.fplus);
        fminus = (FloatingActionButton) view.findViewById(R.id.fminus);
        bsubmit = (Button) view.findViewById(R.id.bsubmit);
        foodqty = new ArrayList<>();
        foodtype = new ArrayList<>();
        foodavailable = new ArrayList<>();
        currentfood = -1;

        CustomerProfileActivity.reference.child("OrderTypes").child(CustomerProfileActivity.businesstype[1]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot i : dataSnapshot.getChildren()) {
                    if(i.getValue(String.class).equals("true")) {
                        foodavailable.add(i.getKey());
                    }
                }

                setGreen();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        setToggleEvent();

        fplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentfood != -1) {
                    TextView tv = getTextViewFoodQty(currentfood);
                    int qty = Integer.parseInt(tv.getText().toString());
                    if(foodavailable.contains((getTextViewFoodType(currentfood)).getText().toString())) {
                        tv.setText(String.valueOf(qty+1));
                    }
                }
            }
        });

        fminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentfood != -1) {
                    TextView tv = getTextViewFoodQty(currentfood);
                    int qty = Integer.parseInt(tv.getText().toString());
                    if(qty != 0 && foodavailable.contains((getTextViewFoodType(currentfood)).getText().toString())) {
                        tv.setText(String.valueOf(qty-1));
                    }
                }
            }
        });

        bsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    private void setGreen() {
        if(foodavailable.contains("NorthIndian")) {
            TextView tvnorth = getTextViewFoodType(0);
            tvnorth.setTextColor(Color.GREEN);
        }
        if(foodavailable.contains("SouthIndian")) {
            TextView tvsouth = getTextViewFoodType(1);
            tvsouth.setTextColor(Color.GREEN);
        }
        if(foodavailable.contains("Continental")) {
            TextView tvcontinental = getTextViewFoodType(2);
            tvcontinental.setTextColor(Color.GREEN);
        }
        if(foodavailable.contains("Chinese")) {
            TextView tvchinese = getTextViewFoodType(3);
            tvchinese.setTextColor(Color.GREEN);
        }
        if(foodavailable.contains("Pizza")) {
            TextView tvpizza = getTextViewFoodType(4);
            tvpizza.setTextColor(Color.GREEN);
        }
        if(foodavailable.contains("Burger")) {
            TextView tvburger = getTextViewFoodType(5);
            tvburger.setTextColor(Color.GREEN);
        }
    }

    private void setToggleEvent() {
        for(int i=0; i<gridLayout.getChildCount(); i++) {
            final int fi = i;
            final LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(i);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentfood == fi) {
                        linearLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        currentfood = -1;
                    }
                    else {
                        linearLayout.setBackgroundColor(Color.parseColor("#88A8A8AA"));
                        if (currentfood > -1) {
                            LinearLayout l = (LinearLayout) gridLayout.getChildAt(currentfood);
                            l.setBackgroundColor(Color.parseColor("#00000000"));
                        }
                        currentfood = fi;
                    }
                }
            });
        }
    }

    private TextView getTextViewFoodType(int i) {
        LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(i);
        if(i == 0) { return (TextView) linearLayout.findViewById(R.id.tvnorth); }
        if(i == 1) { return (TextView) linearLayout.findViewById(R.id.tvsouth); }
        if(i == 2) { return (TextView) linearLayout.findViewById(R.id.tvcontinental); }
        if(i == 3) { return (TextView) linearLayout.findViewById(R.id.tvchinese); }
        if(i == 4) { return (TextView) linearLayout.findViewById(R.id.tvpizza); }
        if(i == 5) { return (TextView) linearLayout.findViewById(R.id.tvburger); }

        return null;
    }

    private TextView getTextViewFoodQty(int i) {
        LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(i);
        if(i == 0) { return (TextView) linearLayout.findViewById(R.id.tvnorthqty); }
        if(i == 1) { return (TextView) linearLayout.findViewById(R.id.tvsouthqty); }
        if(i == 2) { return (TextView) linearLayout.findViewById(R.id.tvcontinentalqty); }
        if(i == 3) { return (TextView) linearLayout.findViewById(R.id.tvchineseqty); }
        if(i == 4) { return (TextView) linearLayout.findViewById(R.id.tvpizzaqty); }
        if(i == 5) { return (TextView) linearLayout.findViewById(R.id.tvburgerqty); }

        return null;
    }

    private void placeOrder() {
        List<String> FoodTypes = new ArrayList<>(), FoodQty = new ArrayList<>();
        for(int i=0; i<gridLayout.getChildCount(); i++) {
            TextView tvFood = getTextViewFoodType(i), tvQty = getTextViewFoodQty(i);
            String qty = tvQty.getText().toString();
            if(!qty.equals("0")) {
                FoodTypes.add(tvFood.getText().toString());
                FoodQty.add(qty);
            }
        }
        if(FoodTypes.size() == 0) {
            Toast.makeText(view.getContext(), "Please add items to your cart", Toast.LENGTH_SHORT).show();
            return;
        }

        Orders orders = new Orders(FoodTypes, FoodQty, CustomerProfileActivity.current_latlng, Time.);

        String serial_key = CustomerProfileActivity.customer_key.substring(2, 6)+CustomerProfileActivity.businesstype[1].substring(2,6);
        CustomerProfileActivity.reference.child("PlacedOrder")
                .child("Restaurants")
                .child(CustomerProfileActivity.customer_key)
                .child(CustomerProfileActivity.businesstype[1])
                .child(serial_key).setValue(orders);
        CustomerProfileActivity.reference.child("PlacedOrder")
                .child("Restaurants")
                .child(CustomerProfileActivity.businesstype[1])
                .child(CustomerProfileActivity.customer_key)
                .child(serial_key).setValue(orders);
        Toast.makeText(view.getContext(), "Order Placed!", Toast.LENGTH_SHORT).show();

        resetAll();
    }

    private void resetAll() {
        for(int i=0; i<gridLayout.getChildCount(); i++) {
            TextView tvQty = getTextViewFoodQty(i);
            tvQty.setText("0");
        }
        currentfood = -1;
        for(int i=0; i<gridLayout.getChildCount(); i++) {
            final LinearLayout linearLayout = (LinearLayout) gridLayout.getChildAt(i);
            linearLayout.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }
}
