package com.example.rishiprotimbose.preorderapp;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progress;
    private Button login;
    private TextView signup;
    private static Boolean logging;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        progress = findViewById(R.id.progressbarlogin);

        logging = false;
        email = (EditText) findViewById(R.id.etemail);
        password = (EditText) findViewById(R.id.etpassword);
        signup = (TextView) findViewById(R.id.tvsignup);
        login = (Button) findViewById(R.id.blogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!logging) {
                    logging = true;
                    userLogin();
                    logging = false;
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }

    private void userLogin() {
        final String Email = email.getText().toString().trim();
        final String Password = password.getText().toString().trim();

        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
            Toast.makeText(MainActivity.this, "Missing Parameters", Toast.LENGTH_SHORT).show();
            clear_all();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()) {

                //    Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();

                    reference.child("Users").orderByChild("Email").startAt(Email).endAt(Email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                for(DataSnapshot result : dataSnapshot.getChildren()) {
                                //    Log.d("value", result.getKey());
                                //    Log.d("Auth", result.child("Auth").getValue(String.class));

                                    if(result.child("Auth").getValue(String.class).equals("Customer")) {
                                        Intent intent = new Intent(getApplicationContext(), CustomerActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        clear_all();
                                        startActivity(intent);
                                    }
                                    else {
                                        Intent intent = new Intent(getApplicationContext(), DealerActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        clear_all();
                                        startActivity(intent);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Invalid Email or Password!", Toast.LENGTH_LONG).show();
                    clear_all();
                }
            }
        });
    }

    private void clear_all()
    {
        password.setText("");
        email.setText("");
    }
}
