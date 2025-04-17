package com.duzceders.aicaltracker.product.models;
import com.google.firebase.Timestamp;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Meal {
    private String meal_name;
    private String meal_type; // breakfast, lunch, dinner
    private String image_url;
    private String user_note;

    private double protein_g;
    private double fat_g;
    private double carbs_g;
    private double calorie_g;

    private Timestamp meal_time;

    public Meal() {}

}
