package com.example.rishiprotimbose.preorderapp.Login;

import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private static DatabaseReference reference;
    private static ListView request;
    private static ArrayAdapter<String> adapter;
    private static List<String> requestList;
    private static Button update;
    private static Typeface comfortaa, colourbars, amarante;
    private static String itemValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        init();
    }

    private void init() {
        reference = FirebaseDatabase.getInstance().getReference();
        colourbars = Typeface.createFromAsset(getAssets(), "fonts/colourbars.ttf");
        comfortaa = Typeface.createFromAsset(getAssets(), "fonts/comfortaa.ttf");
        amarante = Typeface.createFromAsset(getAssets(), "fonts/amarante.ttf");
        ((TextView) findViewById(R.id.tvadminhi)).setTypeface(colourbars);
        ((TextView) findViewById(R.id.tvadmindesc)).setTypeface(amarante);
        ((TextView) findViewById(R.id.tvrequest)).setTypeface(amarante);
        request = (ListView) findViewById(R.id.lvrequest);
        update = (Button) findViewById(R.id.bupdate);
        requestList = new ArrayList<>();
        itemValue = null;

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, requestList);

        request.setAdapter(adapter);

        request.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemValue = request.getItemAtPosition(position).toString();
            }
        });

        reference.child("AdminRequest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot result : dataSnapshot.getChildren()) {
                        requestList.add(result.getKey());
                    }
                }
                else {
                    requestList.add("No requests yet!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        reference.child("AdminRequest").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(requestList.get(0).equals("No requests yet!")) {
                    requestList = new ArrayList<>();
                }
                for(DataSnapshot result : dataSnapshot.getChildren()) {
                    requestList.add(result.getKey());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(DataSnapshot result : dataSnapshot.getChildren()) {
                    requestList.remove(result.getKey());
                    adapter.notifyDataSetChanged();
                }
                if(requestList.size() == 0) {
                    requestList.add("No requests yet!");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemValue == null) {
                    Toast.makeText(getApplicationContext(), "Please select any request", Toast.LENGTH_SHORT).show();
                }
                else if(itemValue.equals("No requests yet!")) {
                    Toast.makeText(getApplicationContext(), "No requests yet!", Toast.LENGTH_SHORT).show();
                }
                else {
                    reference.child("OrderTypes").child(itemValue).setValue("NA");
                    reference.child("AdminRequest").child(itemValue).setValue(null);
                }
            }
        });
    }
}
