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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends Activity {

    public static final int REQUEST_CODE_GETLOCATION = 404;
    private EditText name, email, password, phonenumber;
    private Button signup;
    private ProgressBar progress;
    private RadioButton customer, dealer;
    private Spinner businesstype;
    private CheckBox getlocation;
    private ArrayAdapter adapter;
    private static boolean logging;
    private String latitude, longitude;

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
        progress = findViewById(R.id.progressbarsignup);
        name = findViewById(R.id.etname);
        password = findViewById(R.id.etpassword);
        email = findViewById(R.id.etemail);
        phonenumber = findViewById(R.id.etphonenumber);
        signup = findViewById(R.id.bsignup);
        customer = findViewById(R.id.rbcustomer);
        dealer = findViewById(R.id.rbdealer);
        businesstype = findViewById(R.id.sbusinesstype);
        getlocation = findViewById(R.id.cbgetlocation);
        latitude = "0";
        longitude = "0";

        adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.businesstype, android.R.layout.simple_gallery_item);
        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        businesstype.setAdapter(adapter);

        customerOn();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!logging) {
                    logging = true;
                    Toast.makeText(getApplicationContext(), latitude + ", "+ longitude, Toast.LENGTH_SHORT).show();
                //    register();
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
                    startActivityForResult(intent, REQUEST_CODE_GETLOCATION);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_GETLOCATION) {
            if(resultCode == Activity.RESULT_OK) {
                // Got Latitude and Longitude
                latitude = DealerGetLocationActivity.getLatitude(data);
                longitude = DealerGetLocationActivity.getLongitude(data);
            }
            else {
                // Didn't get Latitude and Longitude
                Toast.makeText(getApplicationContext(), "Not Got", Toast.LENGTH_SHORT).show();
                reset_latlon();
            }
        }
        else {
            reset_latlon();
        }
    }

    private void reset_latlon() {
        getlocation.setChecked(false);
        latitude = "0";
        longitude = "0";
    }

    private void register() {

        final String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        final String Name = name.getText().toString().trim();
        final String PhoneNumber = phonenumber.getText().toString().trim();
        final String BusinessType = businesstype.getSelectedItem().toString();
        final String Auth;
        if(customer.isChecked()) {
            Auth = "Customer";
        }
        else {
            Auth = "Dealer";
        }

        if(!check_Validity(Auth, Email, Password, PhoneNumber, Name, getlocation)) {
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

                    DatabaseReference reference = database.getReference("Users");
                    Users new_user = new Users(Auth, Name, Email, PhoneNumber, BusinessType, latitude, longitude);
                    final String Id = reference.push().getKey();
                    reference.child(Id).child("Auth").setValue(Auth);
                    reference.child(Id).child("Name").setValue(Name);
                    reference.child(Id).child("Email").setValue(Email);
                    reference.child(Id).child("PhoneNumber").setValue(PhoneNumber);
                    reference.child(Id).child("Latitude").setValue("0");
                    reference.child(Id).child("Longitude").setValue("0");
                    if(Auth == "Customer") {
                        reference.child(Id).child("BusinessType").setValue("NA");
                    }
                    else {
                        reference.child(Id).child("BusinessType").setValue(BusinessType);
                    }

                    clear_all();
                    Toast.makeText(getApplicationContext(), "User Successfully Registered.", Toast.LENGTH_SHORT).show();
                    Intent intent;
                    if(Auth == "Customer") {
                        intent = new Intent(getApplicationContext(), CustomerActivity.class);
                    }
                    else {
                        intent = new Intent(getApplicationContext(), DealerActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(getApplicationContext(), "Email already exists!", Toast.LENGTH_SHORT).show();
                    email.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                }
            }
        });
    }

    private void customerOn() {
        clear_all();
        businesstype.setVisibility(View.INVISIBLE);
        getlocation.setVisibility(View.INVISIBLE);
        getlocation.setChecked(false);
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
        getlocation.setChecked(false);
        latitude = "0";
        longitude = "0";
    }

    private boolean check_Validity(String Auth, String Email, String Password, String PhoneNumber, String Name, CheckBox getlocation) {
        if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Name) || TextUtils.isEmpty(PhoneNumber) || (Auth == "Dealer" && getlocation.isChecked() == false)) {
            Toast.makeText(getApplicationContext(), "Missing Parameters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
