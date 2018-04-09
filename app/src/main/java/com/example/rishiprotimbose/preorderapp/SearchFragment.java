package com.example.rishiprotimbose.preorderapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

public class SearchFragment extends Fragment {

    private static View mView;
    private static Button bsearch;
    private static Spinner spinner;
    private static ListView listView;
    private static ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        bsearch = (Button) mView.findViewById(R.id.bsearch);
        spinner = (Spinner) mView.findViewById(R.id.ssearch);
        listView = (ListView) mView.findViewById(R.id.lvsearch);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = ArrayAdapter.createFromResource(mView.getContext(), R.array.businesstype, android.R.layout.simple_gallery_item);
        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        spinner.setAdapter(adapter);

        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsearch.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                // search & display in listview
            }
        });
    }
}
