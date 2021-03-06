package com.example.rishiprotimbose.preorderapp.Locations;

import android.os.AsyncTask;

import com.example.rishiprotimbose.preorderapp.Login.DealerGetLocationActivity;
import com.example.rishiprotimbose.preorderapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlacesData extends AsyncTask<Object, String , String> {
    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List< HashMap<String, String> > nearByPlacesList = null;
        DataParser dataParser = new DataParser();
        nearByPlacesList = dataParser.parse(s);
        showNearByPlaces(nearByPlacesList);
    }

    private void showNearByPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for(int i=0; i<nearbyPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            String placeName = googlePlace.get("place_name");
            double lat = Double.parseDouble(googlePlace.get("lat").trim());
            double lng = Double.parseDouble(googlePlace.get("lng").trim());

            LatLng latLng = new LatLng(lat, lng);

            markerOptions.position(latLng)
                    .title(placeName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_restaurants));

            if(!DealerGetLocationActivity.registeredRestaurants.containsKey(String.valueOf(latLng.latitude)+String.valueOf(latLng.longitude))) {
                mMap.addMarker(markerOptions);
            }
        }
    }
}
