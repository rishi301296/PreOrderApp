package com.example.rishiprotimbose.preorderapp;

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

    public Users(String auth, String name, String email, String phoneNumber) {
        Auth = auth;
        Name = name;
        Email = email;
        PhoneNumber = phoneNumber;
        BusinessType = "NA";
        Latitude = "0";
        Longitude = "0";
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

    public void setAuth(String auth) {
        Auth = auth;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setBusinessType(String businessType) {
        BusinessType = businessType;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
