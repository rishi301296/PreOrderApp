package com.example.rishiprotimbose.preorderapp.CustomerProfile;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rishiprotimbose.preorderapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FeedbackFragment extends Fragment {

    private static View mView;
    private static TextView tvbusinesstype, tvname;
    private static EditText etdesc;
    private static RatingBar ratingBar;
    private static LayerDrawable stars;
    private static Button bsubmit;
    private static ConstraintLayout error, normal;

    private static float rate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_feedback, container, false);

        init();

        Log.d("tag", "init done");

        return mView;
    }

    public void init() {
        error = (ConstraintLayout) mView.findViewById(R.id.error);
        normal = (ConstraintLayout) mView.findViewById(R.id.normal);
        tvbusinesstype = (TextView) mView.findViewById(R.id.tvbusinesstype);
        tvname = (TextView) mView.findViewById(R.id.tvname);
        etdesc = (EditText) mView.findViewById(R.id.etdesc);
        ratingBar = (RatingBar) mView.findViewById(R.id.ratingBar);
        bsubmit = (Button) mView.findViewById(R.id.bsubmit);
        stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorOrange), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(CustomerProfileActivity.businesstype[1] == null) {
            error.setVisibility(View.VISIBLE);
            normal.setVisibility(View.INVISIBLE);
        }
        else {
            error.setVisibility(View.INVISIBLE);
            normal.setVisibility(View.VISIBLE);

            tvbusinesstype.setText(CustomerProfileActivity.businesstype[0]);
            tvname.setText(CustomerProfileActivity.ruk_u.get(CustomerProfileActivity.businesstype[1]).getName());

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    rate = rating;
                }
            });

            bsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bsubmit.isClickable()) {
                        bsubmit.setClickable(false);
                        submitData();
                        bsubmit.setClickable(true);
                    }
                }
            });
        }
    }

    private void submitData() {
        final String Desc = etdesc.getEditableText().toString();

        Log.d("rate", ""+rate);

        if(Desc.equals("") || rate == 0) {
            Toast.makeText(mView.getContext(), "Incomplete Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomerProfileActivity.reference.child("Feedback")
                .child(CustomerProfileActivity.businesstype[1])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(CustomerProfileActivity.customer_key)) {
                            Toast.makeText(mView.getContext(), "Already given review to this dealer!", Toast.LENGTH_SHORT).show();
                            etdesc.setText("");
                            ratingBar.setRating(0f);
                        }
                        else {
                            CustomerProfileActivity.reference.child("Feedback")
                                    .child(CustomerProfileActivity.businesstype[1])
                                    .child(CustomerProfileActivity.customer_key)
                                    .child("Stars").setValue(rate);
                            CustomerProfileActivity.reference.child("Feedback")
                                    .child(CustomerProfileActivity.businesstype[1])
                                    .child(CustomerProfileActivity.customer_key)
                                    .child("Description").setValue(Desc);

                            CustomerProfileActivity.reference.child("Feedback")
                                    .child(CustomerProfileActivity.businesstype[1])
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            rate += dataSnapshot.child("Total").getValue(Integer.class);

                                            CustomerProfileActivity.reference.child("Feedback")
                                                    .child(CustomerProfileActivity.businesstype[1])
                                                    .child("Total").setValue(rate);

                                            etdesc.setText("");
                                            ratingBar.setRating(0f);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });

                            Toast.makeText(mView.getContext(), "Thank You for your feedback!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }
}
