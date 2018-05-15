package com.example.rishiprotimbose.preorderapp;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class Orders {
    public  List<String> foodType, foodQty;
    public LatLng coordinates;
    public Timer time;
    public Date date;

    public Orders() {}

    public Orders(List<String> FoodType, List<String> FoodQty, LatLng coordinates, Timer time, Date date) {
        setFoodType(FoodType);
        setFoodQty(FoodQty);
        setCoordinates(coordinates);
        setTime(time);
        setDate(date);
    }

    public List<String> getFoodType() {
        return foodType;
    }

    public void setFoodType(List<String> foodType) {
        this.foodType = foodType;
    }

    public List<String> getFoodQty() {
        return foodQty;
    }

    public void setFoodQty(List<String> foodQty) {
        this.foodQty = foodQty;
    }

    public LatLng getCoordinates() { return coordinates; }

    public void setCoordinates(LatLng coordinates) { this.coordinates = coordinates; }

    public Timer getTime() { return time; }

    public void setTime(Timer time) { this.time = time; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }
}
