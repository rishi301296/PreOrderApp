package com.example.rishiprotimbose.preorderapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FeedbackFragment extends Fragment {

    private static View mView;
    private static TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_feedback, container, false);
        tv = (TextView) mView.findViewById(R.id.tv);
        tv.setText(String.valueOf(CustomerProfileActivity.restaurants.size()));
        return mView;
    }
}
