package com.duzceders.aicaltracker.features.food_view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.ActivityFoodViewBinding;
import com.duzceders.aicaltracker.product.models.FoodInfo;
import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.parser.MealIDParser;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;
import com.duzceders.aicaltracker.product.service.api.GeminiAPIService;
import com.duzceders.aicaltracker.product.utils.LanguageHelper;

public class FoodViewActivity extends AppCompatActivity {

    private ActivityFoodViewBinding binding;


    private GeminiAPIService geminiAPIService;
    private FirebaseRepository firebaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.applyLanguage(this);
        super.onCreate(savedInstanceState);
        binding = ActivityFoodViewBinding.inflate(getLayoutInflater());
        firebaseRepository = new FirebaseRepository();
        setContentView(binding.getRoot());
        String imageUrl = getIntent().getStringExtra("imageUrl");
        FoodInfo foodInfo = (FoodInfo) getIntent().getSerializableExtra("foodInfo");

        setListeners(foodInfo);

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.mealImageView);
        } else {
            binding.mealImageView.setImageResource(R.drawable.test_food);
        }

        setFoodInfo(foodInfo);
    }

    private void setFoodInfo(FoodInfo foodInfo) {
        if (foodInfo == null) Log.e("FoodViewActivity", "FoodInfo is null");
        else {
            binding.foodNameTextView.setText(foodInfo.getFoodName());
            binding.totalCaloriesText.setText(getString(R.string.total_calories_format, foodInfo.getCalories()));
            binding.aiAdviceText.setText(foodInfo.getRecommendations());
            binding.proteinTextView.setText(getString(R.string.protein_format, foodInfo.getProtein()));
            binding.carbTextView.setText(getString(R.string.carbs_format, foodInfo.getCarbs()));
            binding.fatTextView.setText(getString(R.string.fat_format, foodInfo.getFat()));
        }
    }


    private void setListeners(FoodInfo foodInfo) {
        binding.saveButton.setOnClickListener(v -> {
            Meal meal = new Meal();
            meal.setMeal_name(binding.foodNameTextView.getText().toString());
            ///add meal type logic
            meal.setImage_url(getIntent().getStringExtra("imageUrl"));
            meal.setUser_note(binding.userNoteEditText.getText().toString());
            meal.setProtein_g(foodInfo.getProtein());
            meal.setFat_g(foodInfo.getFat());
            meal.setCarbs_g(foodInfo.getCarbs());
            meal.setCalorie_g(foodInfo.getCalories());
            meal.setMeal_time(new com.google.firebase.Timestamp(new java.util.Date()));
            meal.setRecommendations(foodInfo.getRecommendations());

            String mealID = MealIDParser.extractMealIdWithoutRegex(meal.getImage_url());
            firebaseRepository.addMeal(meal, mealID);
            /// reduce the macros of user
            finish();
        });
    }

    private void showToast(String message) {
        Toast.makeText(FoodViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}