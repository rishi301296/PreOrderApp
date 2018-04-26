package com.example.rishiprotimbose.preorderapp.DealerProfile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.rishiprotimbose.preorderapp.DealerProfile.DealerProfileActivity.dealer_key;
import static com.example.rishiprotimbose.preorderapp.DealerProfile.DealerProfileActivity.reference;

public class FoodCategoryFragment extends Fragment {

    private static View view;
    private static CheckBox cbnorthindian;
    private static CheckBox cbsouthindian;
    private static CheckBox cbcontinental;
    private static CheckBox cbchinese;
    private static CheckBox cbpizza;
    private static CheckBox cbburger;
    private static Button bupdate;
    private static List<String> availablefoods, allfoods;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_food_category, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        bupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpdatedAvailableFoods();
            }
        });
    }

    private void init() {
        cbnorthindian = (CheckBox) view.findViewById(R.id.cbnorthindian);
        cbsouthindian = (CheckBox) view.findViewById(R.id.cbsouthindian);
        cbcontinental = (CheckBox) view.findViewById(R.id.cbcontinental);
        cbchinese = (CheckBox) view.findViewById(R.id.cbchinese);
        cbpizza = (CheckBox) view.findViewById(R.id.cbpizza);
        cbburger = (CheckBox) view.findViewById(R.id.cbburger);
        bupdate = (Button) view.findViewById(R.id.bupdate);
        availablefoods = new ArrayList<>();
        allfoods = new ArrayList<String>();
        allfoods.add("NorthIndian");
        allfoods.add("SouthIndian");
        allfoods.add("Continental");
        allfoods.add("Chinese");
        allfoods.add("Pizza");
        allfoods.add("Burger");

        DealerProfileActivity.reference.child("OrderTypes").child(DealerProfileActivity.dealer_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot result) {
                for(DataSnapshot i : result.getChildren()) {
                    if(i.getKey().equals("NorthIndian") && i.getValue(String.class).equals("true")) {
                        cbnorthindian.setChecked(true);
                        availablefoods.add(i.getKey());
                    }
                    if(i.getKey().equals("SouthIndian") && i.getValue(String.class).equals("true")) {
                        cbsouthindian.setChecked(true);
                    }
                    if(i.getKey().equals("Continental") && i.getValue(String.class).equals("true")) {
                        cbcontinental.setChecked(true);
                    }
                    if(i.getKey().equals("Chinese") && i.getValue(String.class).equals("true")) {
                        cbchinese.setChecked(true);
                    }
                    if(i.getKey().equals("Pizza") && i.getValue(String.class).equals("true")) {
                        cbpizza.setChecked(true);
                    }
                    if(i.getKey().equals("Burger") && i.getValue(String.class).equals("true")) {
                        cbburger.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void setUpdatedAvailableFoods() {
        availablefoods = new ArrayList<>();

        if(cbnorthindian.isChecked()) { availablefoods.add("NorthIndian"); }
        if(cbsouthindian.isChecked()) { availablefoods.add("SouthIndian"); }
        if(cbcontinental.isChecked()) { availablefoods.add("Continental"); }
        if(cbchinese.isChecked()) { availablefoods.add("Chinese"); }
        if(cbpizza.isChecked()) { availablefoods.add("Pizza"); }
        if(cbburger.isChecked()) { availablefoods.add("Burger"); }

        for(String i : allfoods) {
            if(availablefoods.contains(i)) {
                reference.child("OrderTypes").child(dealer_key).child(i).setValue("true");
                reference.child("OrderTypes").child(i).child(dealer_key).setValue("true");
            }
            else {
                reference.child("OrderTypes").child(dealer_key).child(i).setValue("false");
                reference.child("OrderTypes").child(i).child(dealer_key).setValue(null);
            }
        }

        Toast.makeText(view.getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
        DealerProfileActivity.ordertypesdone = true;
    }
}
