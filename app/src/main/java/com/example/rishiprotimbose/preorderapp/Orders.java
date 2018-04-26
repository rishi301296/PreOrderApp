package com.example.rishiprotimbose.preorderapp;

import java.util.List;

public class Orders {
    public  List<String> foodType, foodQty;

    public Orders() {}

    public Orders(List<String> FoodType, List<String> FoodQty) {
        setFoodType(FoodType);
        setFoodQty(FoodQty);
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
}
