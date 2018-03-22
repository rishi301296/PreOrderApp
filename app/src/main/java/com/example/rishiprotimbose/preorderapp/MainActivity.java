package com.example.rishiprotimbose.preorderapp;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progress;
    private Button login;
    private TextView signup;
    private static Boolean logging;
    private DatabaseReference reference;
    private Query query;

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

                    Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();

                    Query query = reference.child("Users").orderByChild("Email").equalTo(Email);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                for(DataSnapshot result : dataSnapshot.getChildren()) {


                                    Users new_user = dataSnapshot.child("Users").child(result.getKey()).child("Auth").getValue(Users.class);
                                    Toast.makeText(getApplicationContext(), new_user.getAuth(), Toast.LENGTH_SHORT).show();

                                    /*
                                    if(dataSnapshot.child("Users").child(result.getKey()).child("Auth").getValue().equals("Customer")) {
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
                                    } */
                                    break;
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
