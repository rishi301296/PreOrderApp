package com.example.rishiprotimbose.preorderapp.Login;

import android.content.Context;
import android.os.AsyncTask;

import com.example.rishiprotimbose.preorderapp.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class PlaceFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private String GOOGLE_API_KEY;
    private LatLng origin;
    private PlaceFinderListener listener;
    private String destination;
    private String radius;

    public PlaceFinder(Context context, PlaceFinderListener listener, LatLng origin, String destination, String radius) {
        this.origin = origin;
        this.destination = destination;
        this.listener = listener;
        this.GOOGLE_API_KEY = context.getResources().getString(R.string.google_maps_key);
        this.radius = radius;
    }

    public void execute() throws UnsupportedEncodingException {
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin.latitude + "," + origin.longitude, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        return DIRECTION_URL_API + "key=" + GOOGLE_API_KEY + "&sensor=false&location=" + urlOrigin + "&radius=" + radius + "&keyword=" + urlDestination;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        LatLng latLng = null;
        String name = "";
        JSONObject jsonData = new JSONObject(data);
        if (jsonData.getJSONArray("results").length() > 0) {

            JSONObject jsonPlace = jsonData.getJSONArray("results").getJSONObject(0);
            JSONObject jsonGeometry = jsonPlace.getJSONObject("geometry");
            JSONObject jsonLocation = jsonGeometry.getJSONObject("location");

            latLng = new LatLng(jsonLocation.getDouble("lat"), jsonLocation.getDouble("lng"));
            name = jsonPlace.getString("name");
        }

        listener.onPlaceFinderSuccess(latLng, name);
    }
}