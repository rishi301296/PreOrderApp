package com.example.rishiprotimbose.preorderapp;

/**
 * Created by Rishi Protim Bose on 19/03/2018.
 */

public class Users {
    public String auth;
    public String name;
    public String email;
    public String phoneNumber;
    public String businessType;
    public String latitude;
    public String longitude;
    public int stars;

    public Users() {}

    public Users(String auth, String name, String email, String phoneNumber) {
        this.setAuth(auth);
        this.setName(name);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setBusinessType("Na");
        this.setLatitude("0");
        this.setLongitude("0");
        stars = 0;
    }

    public Users(String auth, String name, String email, String phoneNumber, String businessType, String latitude, String longitude) {
        this.setAuth(auth);
        this.setName(name);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setBusinessType(businessType);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        stars = 0;
    }

    public String getAuth() { return auth; }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBusinessType() { return businessType; }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getLatitude() { return latitude; }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() { return longitude; }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getStars() { return stars; }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
