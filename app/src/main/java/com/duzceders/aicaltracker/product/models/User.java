package com.duzceders.aicaltracker.product.models;

import com.duzceders.aicaltracker.product.models.enums.ActivityLevel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private String surname;
    private String email;

    private int height_cm;
    private int weight_kg;
    private int target_weight_kg;
    private double body_fat_percent;

    private ActivityLevel activity_level;

    private double bmr;
    private double daily_calorie_needs;
    private double daily_calorie_needs_left;
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

        private double maintain;
        private double weight_gain;
        private double weight_loss;

        public CalorieGoals() {
        }
    }

    /// DailyMacros: Günlük makro besin değerlerini tutar.
    @Getter
    @Setter
    public static class DailyMacros {
        private double daily_carbs_need_g;
        private double daily_fats_need_g;
        private double daily_proteins_need_g;

        private double daily_carbs_left_g;
        private double daily_fats_left_g;
        private double daily_proteins_left_g;

        public DailyMacros() {
        }
    }
}

