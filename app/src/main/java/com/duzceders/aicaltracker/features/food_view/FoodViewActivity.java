package com.duzceders.aicaltracker.features.food_view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.product.models.FoodInfo;
import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.service.api.GeminiAPIService;
import com.duzceders.aicaltracker.product.utils.LanguageHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class FoodViewActivity extends AppCompatActivity {

    private ImageView mealImageView;
    private TextView foodNameTextView, totalCaloriesText, proteinTextView, carbTextView, fatTextView;
    private EditText portionEditText, aiAdviceText, userNoteEditText;
    private Button saveButton;
    private ProgressBar analyzeProgressBar;
    private GeminiAPIService geminiAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Önce dil ayarını uygula
        LanguageHelper.applyLanguage(this);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_food_view);

        initializeViews();
        setListeners();
        String imageUrl = getIntent().getStringExtra("imageUrl");
        FoodInfo foodInfo = (FoodInfo) getIntent().getSerializableExtra("foodInfo");
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(mealImageView);
        } else {
            mealImageView.setImageResource(R.drawable.test_food);
        }

        if (foodInfo != null){
            foodNameTextView.setText(foodInfo.getFoodName());
            totalCaloriesText.setText(getString(R.string.total_calories_format, foodInfo.getCalories()));
            aiAdviceText.setText(foodInfo.getRecommendations());
            proteinTextView.setText(getString(R.string.protein_format, foodInfo.getProtein()));
            carbTextView.setText(getString(R.string.carbs_format, foodInfo.getCarbs()));
            fatTextView.setText(getString(R.string.fat_format, foodInfo.getFat()));
        } else {

            Log.e("FoodViewActivity", "FoodInfo is null");
        }
    }

    private void initializeViews() {
        mealImageView = findViewById(R.id.mealImageView);
        /// intent ile gelen veriyi buraya ekle
        foodNameTextView = findViewById(R.id.foodNameTextView);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);
        aiAdviceText = findViewById(R.id.aiAdviceText);
        userNoteEditText = findViewById(R.id.userNoteEditText);
        saveButton = findViewById(R.id.saveButton);

        proteinTextView = findViewById(R.id.proteinTextView);
        carbTextView = findViewById(R.id.carbTextView);
        fatTextView = findViewById(R.id.fatTextView);
    }

    private void setListeners() {
        saveButton.setOnClickListener(v -> {
            String foodName = foodNameTextView.getText().toString();
            String userNote = userNoteEditText.getText().toString();
        });
    }


    private void showToast(String message) {
        Toast.makeText(FoodViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}