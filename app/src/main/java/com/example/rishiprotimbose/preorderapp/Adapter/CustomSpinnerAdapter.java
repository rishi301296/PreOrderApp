package com.example.rishiprotimbose.preorderapp.Adapter;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.rishiprotimbose.preorderapp.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Activity activity;
    private String[] data;
    private Typeface typeface;
    LayoutInflater inflater;

    public CustomSpinnerAdapter(Activity activitySpinner, int textViewResourceId, String[] objects, Typeface tf)
    {
        super(activitySpinner, textViewResourceId, objects);

        activity = activitySpinner;
        data     = objects;
        typeface = tf;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);

        TextView label = (TextView)row.findViewById(R.id.text1);
        label.setText(data[position]);
        label.setTypeface(typeface);

        return row;
    }
}