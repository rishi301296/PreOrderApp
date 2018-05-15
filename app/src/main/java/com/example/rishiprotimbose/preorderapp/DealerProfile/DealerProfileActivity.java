package com.example.rishiprotimbose.preorderapp.DealerProfile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rishiprotimbose.preorderapp.CustomerProfile.BookFragment;
import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DealerProfileActivity extends AppCompatActivity {

    public static boolean ordertypesdone = false;

    public static FirebaseAuth firebaseAuth;
    public static DatabaseReference reference;
    public static android.support.v4.app.FragmentManager fragmentManager;
    public static TextView tvname;
    private static ImageButton border, bbook, bfoodcategory, bfeedback, beditprofile;
    public static Users dealer;
    public static String dealer_key;
    private static Typeface comfortaa, colourbars_bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_profile);

        init();

        addListener();

        if(!DealerProfileActivity.ordertypesdone) {
            Fragment fragment = new FoodCategoryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();

        comfortaa = Typeface.createFromAsset(getAssets(), "fonts/comfortaa.ttf");
        colourbars_bd = Typeface.createFromAsset(getAssets(), "fonts/colourbars_bd.ttf");

        dealer = new Users();
        dealer.setName(getIntent().getExtras().getString("Name"));
        dealer.setEmail(getIntent().getExtras().getString("Email"));
        dealer.setPhoneNumber(getIntent().getExtras().getString("PhoneNumber"));
        dealer.setBusinessType(getIntent().getExtras().getString("BusinessType"));
        dealer.setLatitude(getIntent().getExtras().getString("Latitude"));
        dealer.setLongitude(getIntent().getExtras().getString("Longitude"));
        dealer.setAuth("Dealer");
        dealer_key = getIntent().getExtras().getString("Key");

        tvname = (TextView) findViewById(R.id.tvname);
        tvname.setTypeface(colourbars_bd);
        border = (ImageButton) findViewById(R.id.border);
        bbook = (ImageButton) findViewById(R.id.bbook);
        bfoodcategory = (ImageButton) findViewById(R.id.bfoodcategory);
        bfeedback = (ImageButton) findViewById(R.id.bfeedback);
        beditprofile = (ImageButton) findViewById(R.id.beditprofile);
        tvname.setText(dealer.getName());

        reference.child("OrderTypes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(dealer_key)) {
                    ordertypesdone = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void addListener() {
        bbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
        border.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
        beditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
        bfoodcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
            }
        });
    }

    public void changeFragment(View view) {
        android.support.v4.app.Fragment fragment;

        if(view == findViewById(R.id.bbook)) {
            fragment = new BookFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
        if(view == findViewById(R.id.border)) {
            fragment = new OrderFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
        if(view == findViewById(R.id.bfeedback)) {
            fragment = new Feedback2Fragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
//        if(view == findViewById(R.id.beditprofile)) {
//            fragment = new EditProfileFragment();
//            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.mainfragment, fragment);
//            fragmentTransaction.commit();
//        }
        if(view == findViewById(R.id.bfoodcategory)) {
            fragment = new FoodCategoryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment, fragment);
            fragmentTransaction.commit();
        }
    }
}