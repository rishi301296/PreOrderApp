package com.example.rishiprotimbose.preorderapp.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.Adapter.CustomSpinnerAdapter;
import com.example.rishiprotimbose.preorderapp.CustomerProfile.CustomerProfileActivity;
import com.example.rishiprotimbose.preorderapp.DealerProfile.DealerProfileActivity;
import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignupActivity extends Activity {

    public static final int REQUEST_CODE_GETLOCATION = 404;

    private static Typeface amarante, comfortaa;
    private EditText name, email, password, phonenumber;
    private Button bsignup;
    private ProgressBar progress;
    private RadioButton customer, dealer;
    private Spinner businesstype;
    private CheckBox getlocation;
    private ArrayAdapter adapter;
    private static String latitude, longitude;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();

        customer.setChecked(true);
        customerOn();

        bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsignup.setClickable(false);
                register();
                bsignup.setClickable(true);
            }
        });

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerOn();
            }
        });

        dealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dealerOn();
            }
        });

        getlocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b) {
                    latitude = "0";
                    longitude = "0";
                }
                else {
                    Intent intent = DealerGetLocationActivity.makeIntent(getApplicationContext());
                    intent.putExtra("BUSINESS_TYPE", businesstype.getSelectedItem().toString());
                    startActivityForResult(intent, REQUEST_CODE_GETLOCATION);
                }
            }
        });
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        amarante = Typeface.createFromAsset(getAssets(),"fonts/amarante.ttf");
        comfortaa = Typeface.createFromAsset(getAssets(),"fonts/comfortaa.ttf");

        ((TextView) findViewById(R.id.textView2)).setTypeface(amarante);
        progress = (ProgressBar) findViewById(R.id.progressbarsignup);
        name = (EditText) findViewById(R.id.etname);
        name.setTypeface(comfortaa);
        password = (EditText) findViewById(R.id.etpassword);
        password.setTypeface(comfortaa);
        email = (EditText) findViewById(R.id.etemail);
        email.setTypeface(comfortaa);
        phonenumber = (EditText) findViewById(R.id.etphonenumber);
        phonenumber.setTypeface(comfortaa);
        bsignup = (Button) findViewById(R.id.bsignup);
        customer = (RadioButton) findViewById(R.id.rbcustomer);
        customer.setTypeface(amarante);
        dealer = (RadioButton) findViewById(R.id.rbdealer);
        dealer.setTypeface(amarante);
        businesstype = (Spinner) findViewById(R.id.sbusinesstype);
        getlocation = (CheckBox) findViewById(R.id.cbgetlocation);

        adapter = new CustomSpinnerAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.businesstypes), comfortaa);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        businesstype.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_GETLOCATION && resultCode == Activity.RESULT_OK) {
            // Got Latitude and Longitude
            latitude = DealerGetLocationActivity.getLatitude(data);
            longitude = DealerGetLocationActivity.getLongitude(data);
        }
        else {
            reset_latlon();
        }
    }

    private void register() {
        final String Email = email.getText().toString().trim();
        final String Password = password.getText().toString().trim();
        final String Name = name.getText().toString().trim();
        final String PhoneNumber = phonenumber.getText().toString().trim();
        final String Auth;
        final String BusinessType = businesstype.getSelectedItem().toString();

        if(customer.isChecked()) {
            Auth = "Customer";
        }
        else {
            Auth = "Dealer";
        }

        if(!checkValidity(Auth, Email, Password, PhoneNumber, Name, getlocation)) {
            return;
        }

        progress.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()) {
                    DatabaseReference reference = database.getReference();
                    Users new_user;
                    final String Id = reference.push().getKey();
                    if(Auth.equals("Customer")) {
                        new_user = new Users(Auth, Name, Email, PhoneNumber);
                    }
                    else {
                        new_user = new Users(Auth, Name, Email, PhoneNumber, BusinessType, latitude, longitude);
                    }

                    reference.child("Users").child(Id).setValue(new_user);

                    if((new_user.getBusinessType()+new_user.getAuth()).equals("RestaurantsDealer")) {
                        setLngLat(reference.child("Dealers").child("Restaurants"), latitude, longitude, Id);
                        reference.child("Feedback").child(Id).child("Total").setValue(0);
                    }

                    clear_all();
                    Toast.makeText(getApplicationContext(), "User Successfully Registered.", Toast.LENGTH_SHORT).show();
                    Intent intent;
                    if(new_user.getAuth().equals("Customer")) {
                        intent = new Intent(getApplicationContext(), CustomerProfileActivity.class);
                    }
                    else {
                        intent = new Intent(getApplicationContext(), DealerProfileActivity.class);
                    }
                    intent.putExtra("Name", new_user.getName());
                    intent.putExtra("Email", new_user.getEmail());
                    intent.putExtra("PhoneNumber", new_user.getPhoneNumber());
                    intent.putExtra("Key", Id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, REQUEST_CODE_GETLOCATION);
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), "Email already exists!", Toast.LENGTH_SHORT).show();
                    email.setText("");
                }
                else {
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(getApplicationContext(), "SignUp Unsuccessful!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                }
            }
        });
    }

    private void setLngLat(DatabaseReference reference, String latitude, String longitude, String Id) {
        double ll = Double.valueOf(longitude), la = Double.valueOf(latitude);
        int lng = (int) ll, lat = (int) la;
        lng = (lng/10)*10;
        lat = (lat/10)*10;
        reference.child("Longitudes").child(String.valueOf(lng)+" "+String.valueOf(lng+10)).child(Id).setValue("true");
        reference.child("Latitudes").child(String.valueOf(lat)+" "+String.valueOf(lat+10)).child(Id).setValue("true");
    }

    private void customerOn() {
        clear_all();
        ((LinearLayout) findViewById(R.id.sbusiness)).setVisibility(View.INVISIBLE);
        getlocation.setVisibility(View.INVISIBLE);
    }

    private void dealerOn() {
        clear_all();
        ((LinearLayout) findViewById(R.id.sbusiness)).setVisibility(View.VISIBLE);
        getlocation.setVisibility(View.VISIBLE);
    }

    private void clear_all()
    {
        name.setText("");
        password.setText("");
        email.setText("");
        phonenumber.setText("");
        reset_latlon();
    }

    private void reset_latlon() {
        getlocation.setChecked(false);
        latitude = null;
        longitude = null;
    }

    private boolean checkValidity(String Auth, String Email, String Password, String PhoneNumber, String Name, CheckBox getlocation) {
        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(getApplicationContext(), "Name is missing!", Toast.LENGTH_SHORT).show();
            name.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(getApplicationContext(), "Email is missing!", Toast.LENGTH_SHORT).show();
            email.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(getApplicationContext(), "Password is missing!", Toast.LENGTH_SHORT).show();
            password.setFocusable(true);
            return false;
        }
        if (TextUtils.isEmpty(PhoneNumber)) {
            Toast.makeText(getApplicationContext(), "Phone Number is missing!", Toast.LENGTH_SHORT).show();
            phonenumber.setFocusable(true);
            return false;
        }
        if (Auth.equals("Dealer") && (!getlocation.isChecked())) {
            Toast.makeText(getApplicationContext(), "Location is not set!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
