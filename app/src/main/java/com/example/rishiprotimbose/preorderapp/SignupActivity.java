package com.example.rishiprotimbose.preorderapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import java.security.acl.Owner;

public class SignupActivity extends AppCompatActivity {

    private EditText name, email, password, phonenumber;
    private Button signup;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progress;
    private RadioButton customer, dealer;
    private Spinner spinner;
    private TextView getlocation;
    private RadioGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        progress = (ProgressBar) findViewById(R.id.progressbarsignup);
        name = (EditText) findViewById(R.id.etname);
        password = (EditText) findViewById(R.id.etpassword);
        email = (EditText) findViewById(R.id.etemail);
        phonenumber = (EditText) findViewById(R.id.etphonenumber);
        signup = (Button) findViewById(R.id.bsignup);
        customer = (RadioButton) findViewById(R.id.rbcustomer);
        dealer = (RadioButton) findViewById(R.id.rbdealer);
        spinner = (Spinner) findViewById(R.id.sbusinesstype);
        getlocation = (TextView) findViewById(R.id.tvgetlocation);
        group = (RadioGroup) findViewById(R.id.rgownertype);

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

                    clear_all();

                    Toast.makeText(SignupActivity.this, "User Successfully Registered.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(SignupActivity.this, "Email already exists!", Toast.LENGTH_LONG).show();
                    clear_all();
                }
                else {
                    Toast.makeText(SignupActivity.this, "Error!", Toast.LENGTH_LONG).show();
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
}
