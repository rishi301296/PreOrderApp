package com.example.rishiprotimbose.preorderapp.Login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.CustomerProfile.CustomerProfileActivity;
import com.example.rishiprotimbose.preorderapp.DealerProfile.DealerProfileActivity;
import com.example.rishiprotimbose.preorderapp.R;
import com.example.rishiprotimbose.preorderapp.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    final private static int REQ_CODE = 1024;

    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progress;
    private Button login;
    private TextView signup;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setClickable(false);
                userLogin();
                login.setClickable(true);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        progress = findViewById(R.id.progressbarlogin);

        email = (EditText) findViewById(R.id.etemail);
        password = (EditText) findViewById(R.id.etpassword);
        signup = (TextView) findViewById(R.id.tvsignup);
        login = (Button) findViewById(R.id.blogin);
    }

    private void userLogin() {
        final String Email = email.getText().toString().trim();
        final String Password = password.getText().toString().trim();

        if(!checkValidity(Email, Password)) {
            return;
        }

        progress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()) {
                    reference.child("Users").orderByChild("email").startAt(Email).endAt(Email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                for(DataSnapshot result : dataSnapshot.getChildren()) {
                                    Users new_user = new Users();
                                    new_user = result.getValue(Users.class);
                                    if(new_user.getAuth().equals("Customer")) {
                                        Intent intent = new Intent(getApplicationContext(), CustomerProfileActivity.class);
                                        intent.putExtra("Email", new_user.getEmail());
                                        intent.putExtra("Name", new_user.getName());
                                        intent.putExtra("PhoneNumber", new_user.getPhoneNumber());
                                        intent.putExtra("Key", result.getKey());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        clear_all();
                                        startActivityForResult(intent, REQ_CODE);
                                    }
                                    else if(new_user.getAuth().equals("Dealer")) {
                                        Intent intent = new Intent(getApplicationContext(), DealerProfileActivity.class);
                                        intent.putExtra("Email", new_user.getEmail());
                                        intent.putExtra("Name", new_user.getName());
                                        intent.putExtra("PhoneNumber", new_user.getPhoneNumber());
                                        intent.putExtra("Key", result.getKey());
                                        intent.putExtra("Businesstype", new_user.getBusinessType());
                                        intent.putExtra("Latitude", new_user.getLatitude());
                                        intent.putExtra("Longitude", new_user.getLongitude());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        clear_all();
                                        startActivityForResult(intent, REQ_CODE);
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Login Denied", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "No data exist! SignIn again", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    clear_all();
                }
            }
        });
    }

    private boolean checkValidity(String Email, String Password) {
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
        return true;
    }

    private void clear_all()
    {
        password.setText("");
        email.setText("");
    }
}
