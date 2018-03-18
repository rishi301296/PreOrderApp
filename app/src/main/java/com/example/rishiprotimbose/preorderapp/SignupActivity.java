package com.example.rishiprotimbose.preorderapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.acl.Owner;

public class SignupActivity extends Activity {

    private EditText name, email, password, phonenumber;
    private Button signup;
    private ProgressBar progress;
    private RadioButton customer, dealer;
    private Spinner spinner;
    private CheckBox getlocation;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        progress = findViewById(R.id.progressbarsignup);
        name = findViewById(R.id.etname);
        password = findViewById(R.id.etpassword);
        email = findViewById(R.id.etemail);
        phonenumber = findViewById(R.id.etphonenumber);
        signup = findViewById(R.id.bsignup);
        customer = findViewById(R.id.rbcustomer);
        dealer = findViewById(R.id.rbdealer);
        spinner = findViewById(R.id.sbusinesstype);
        getlocation = findViewById(R.id.cbgetlocation);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.INVISIBLE);
                getlocation.setVisibility(View.INVISIBLE);
            }
        });

        dealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                getlocation.setVisibility(View.VISIBLE);
            }
        });

    }

    private void register() {

        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        String Name = name.getText().toString().trim();
        String PhoneNumber = phonenumber.getText().toString().trim();
        String Location;
    //    String BusinessType = spinner.getSelectedItem().toString();
        String Owner;
        if(customer.isChecked()) {
            Owner = "Customer";
        }
        else {
            Owner = "Dealer";
        }

        Toast.makeText(getApplicationContext(), Owner, Toast.LENGTH_SHORT).show();

        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(SignupActivity.this, "User Successfully Registered.", Toast.LENGTH_SHORT).show();
                    clear_all();
                    Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(SignupActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    clear_all();
                }
                else {
                    Toast.makeText(SignupActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                }
            }
        });
    }

    private void clear_all()
    {
        name.setText("");
        password.setText("");
        email.setText("");
        phonenumber.setText("");
    }

    private class Customer {
        private String Name, Email, Phone_Number;

        public void setCustomerValues(String Name, String Email, String Phone_Number) {
            this.Name = Name;
            this.Email = Email;
            this.Phone_Number = Phone_Number;
        }

        public String getCustomerName() {
            return this.Name;
        }

        public String getCustomerEmail() {
            return this.Email;
        }

        public String getCustomerPhone_Number() {
            return this.Phone_Number;
        }
    }
}
