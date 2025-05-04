package com.duzceders.aicaltracker.product.models;

import com.duzceders.aicaltracker.product.models.enums.MealType;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meal implements Serializable {

    private String meal_name;
    private MealType meal_type;
    private String image_url;
    private String user_note;
    private String recommendations;

    private double protein_g;
    private double fat_g;
    private double carbs_g;
    private double calorie_kcal;

//    private long meal_time;


    public Meal() {
    }

    public Meal(String meal_name, MealType meal_type, String image_url, String user_note, double protein_g, double fat_g, double carbs_g, double calorie_kcal) {
        this.meal_name = meal_name;
        this.meal_type = meal_type;
        this.image_url = image_url;
        this.user_note = user_note;
        this.protein_g = protein_g;
        this.fat_g = fat_g;
        this.carbs_g = carbs_g;
        this.calorie_kcal = calorie_kcal;
//        this.meal_time = meal_time;
    }

//    public Timestamp getMealTimeAsTimestamp() {
//        return new Timestamp(Instant.ofEpochSecond(meal_time));
//
//    }
}