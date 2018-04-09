package com.example.rishiprotimbose.preorderapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends Activity {

    public static final int REQUEST_CODE_GETLOCATION = 404;
    public static final String BUSINESS_TYPE = "BusinessType";

    private EditText name, email, password, phonenumber;
    private Button signup;
    private ProgressBar progress;
    private RadioButton customer, dealer;
    private Spinner businesstype;
    private CheckBox getlocation;
    private ArrayAdapter adapter;
    private static boolean logging;
    private static String latitude, longitude;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        logging = false;
        progress = (ProgressBar) findViewById(R.id.progressbarsignup);
        name = (EditText) findViewById(R.id.etname);
        password = (EditText) findViewById(R.id.etpassword);
        email = (EditText) findViewById(R.id.etemail);
        phonenumber = (EditText) findViewById(R.id.etphonenumber);
        signup = (Button) findViewById(R.id.bsignup);
        customer = (RadioButton) findViewById(R.id.rbcustomer);
        dealer = (RadioButton) findViewById(R.id.rbdealer);
        businesstype = (Spinner) findViewById(R.id.sbusinesstype);
        getlocation = (CheckBox) findViewById(R.id.cbgetlocation);
        latitude = "0";
        longitude = "0";

        customerOn();

        adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.businesstype, android.R.layout.simple_gallery_item);
        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        businesstype.setAdapter(adapter);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!logging) {
                    logging = true;
                //    Toast.makeText(getApplicationContext(), latitude+", "+longitude, Toast.LENGTH_SHORT).show();
                    register();
                    logging = false;
                }
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

                    // Save to database
                    // {if Owner = Customer    ->   (Email, Name, PhoneNumber))
                    // or
                    // (if Owner = Dealer    ->    (Email, Name, PhoneNumber, Location, BusinessType))

                    DatabaseReference reference = database.getReference();
                    Users new_user;
                    final String Id = reference.push().getKey();
                    if(Auth.equals("Customer")) {
                        new_user = new Users(Auth, Name, Email, PhoneNumber);
                    }
                    else {
                        new_user = new Users(Auth, Name, Email, PhoneNumber, BusinessType, latitude, longitude);
                    }

                    reference.child("Users").child(Id).child("Auth").setValue(new_user.getAuth());
                    reference.child("Users").child(Id).child("Name").setValue(new_user.getName());
                    reference.child("Users").child(Id).child("Email").setValue(new_user.getEmail());
                    reference.child("Users").child(Id).child("PhoneNumber").setValue(new_user.getPhoneNumber());
                    reference.child("Users").child(Id).child("Latitude").setValue(new_user.getLatitude());
                    reference.child("Users").child(Id).child("Longitude").setValue(new_user.getLongitude());

                    if(BusinessType.equals("Restaurants") && Auth.equals("Dealer")) {
                        setLngLat(reference.child("Dealers").child("Restaurants"), latitude, longitude, Id);
                    }

                    clear_all();
                    Toast.makeText(getApplicationContext(), "User Successfully Registered.", Toast.LENGTH_SHORT).show();
                    Intent intent;
                    if(Auth.equals("Customer")) {
                        intent = new Intent(getApplicationContext(), CustomerProfileActivity.class);
                    }
                    else {
                        intent = new Intent(getApplicationContext(), DealerActivity.class);
                    }
                    intent.putExtra("Name", new_user.getName());
                    intent.putExtra("Email", new_user.getEmail());
                    intent.putExtra("PhoneNumber", new_user.getPhoneNumber());
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
        reference.child("Longitudes").child(String.valueOf(lng)+" "+String.valueOf(lng+10)).child(Id).setValue("Restaurants");
        reference.child("Latitudes").child(String.valueOf(lat)+" "+String.valueOf(lat+10)).child(Id).setValue("Restaurants");
    }

    private void customerOn() {
        clear_all();
        businesstype.setVisibility(View.INVISIBLE);
        getlocation.setVisibility(View.INVISIBLE);
    }

    private void dealerOn() {
        clear_all();
        businesstype.setVisibility(View.VISIBLE);
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
        latitude = "0";
        longitude = "0";
    }

    private boolean checkValidity(String Auth, String Email, String Password, String PhoneNumber, String Name, CheckBox getlocation) {
        if (TextUtils.isEmpty(Email)
                || TextUtils.isEmpty(Password)
                || TextUtils.isEmpty(Name)
                || TextUtils.isEmpty(PhoneNumber)
                || (Auth.equals("Dealer") && (!getlocation.isChecked()))) {
            Toast.makeText(getApplicationContext(), "Missing Parameters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
