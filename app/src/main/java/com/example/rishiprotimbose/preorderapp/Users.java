package com.example.rishiprotimbose.preorderapp;

import java.lang.ref.SoftReference;

/**
 * Created by Rishi Protim Bose on 19/03/2018.
 */

public class Users {
    public String Auth;
    public String Name;
    public String Email;
    public String PhoneNumber;
    public String BusinessType;
    public String Latitude;
    public String Longitude;

    public Users() {

    }

    public Users(String auth, String name, String email, String phoneNumber, String businessType, String latitude, String longitude) {
        Auth = auth;
        Name = name;
        Email = email;
        PhoneNumber = phoneNumber;
        BusinessType = businessType;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getAuth() {
        return Auth;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getBusinessType() {
        return BusinessType;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }
}
