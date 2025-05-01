package com.duzceders.aicaltracker.features.food_view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.ActivityFoodViewBinding;
import com.duzceders.aicaltracker.product.models.FoodInfo;
import com.duzceders.aicaltracker.product.service.api.GeminiAPIService;
import com.duzceders.aicaltracker.product.utils.LanguageHelper;

public class FoodViewActivity extends AppCompatActivity {

    private ActivityFoodViewBinding binding;


    private GeminiAPIService geminiAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.applyLanguage(this);
        super.onCreate(savedInstanceState);
        binding = ActivityFoodViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setListeners();
        String imageUrl = getIntent().getStringExtra("imageUrl");
        FoodInfo foodInfo = (FoodInfo) getIntent().getSerializableExtra("foodInfo");
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.mealImageView);
        } else {
            binding.mealImageView.setImageResource(R.drawable.test_food);
        }

        if (foodInfo != null) {
            binding.foodNameTextView.setText(foodInfo.getFoodName());
            binding.totalCaloriesText.setText(getString(R.string.total_calories_format, foodInfo.getCalories()));
            binding.aiAdviceText.setText(foodInfo.getRecommendations());
            binding.proteinTextView.setText(getString(R.string.protein_format, foodInfo.getProtein()));
            binding.carbTextView.setText(getString(R.string.carbs_format, foodInfo.getCarbs()));
            binding.fatTextView.setText(getString(R.string.fat_format, foodInfo.getFat()));
        } else {
            Log.e("FoodViewActivity", "FoodInfo is null");
        }
    }


    private void setListeners() {
        binding.saveButton.setOnClickListener(v -> {
            String foodName = binding.foodNameTextView.getText().toString();
            String userNote = binding.userNoteEditText.getText().toString();
        });
    }

    private void showToast(String message) {
        Toast.makeText(FoodViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}