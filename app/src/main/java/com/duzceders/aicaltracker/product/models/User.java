package com.duzceders.aicaltracker.product.models;

import com.duzceders.aicaltracker.product.models.enums.ActivityLevel;
import com.google.firebase.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private String surname;
    private String email;
    private String birthday;
    private String gender;
    private Timestamp last_login;

    private int height_cm;
    private int weight_kg;
    private int target_weight_kg;
    private int body_fat_percent;

    private ActivityLevel activity_level;

    private int bmr;
    private int daily_calorie_needs;
    private int daily_calorie_needs_left;
    private double daily_water_needs_liters;
    private double daily_water_needs_left_liters;

    private CalorieGoals calorie_goals;
    private DailyMacros daily_macros;

    public User() {
    }

    /// CalorieGoals: Kilo koruma- kilo alma- kilo verme için gerekli kalori değerlerini tutar.
    @Getter
    @Setter
    public static class CalorieGoals {

        private int maintain;
        private int weight_gain;
        private int weight_loss;

        public CalorieGoals() {
        }
    }

    /// DailyMacros: Günlük makro besin değerlerini tutar.
    @Getter
    @Setter
    public static class DailyMacros {
        private int daily_carbs_need_g;
        private int daily_fats_need_g;
        private int daily_proteins_need_g;

        private int daily_carbs_left_g;
        private int daily_fats_left_g;
        private int daily_proteins_left_g;

        public DailyMacros() {
        }
    }
}

