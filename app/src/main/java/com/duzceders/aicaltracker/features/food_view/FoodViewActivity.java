package com.duzceders.aicaltracker.features.food_view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.features.auth.EmailPasswordActivity;
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
    private Button saveButton, analyzeButton;
    private ProgressBar analyzeProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Önce dil ayarını uygula
        LanguageHelper.applyLanguage(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_view);

        initializeViews();
        setListeners();
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
        analyzeButton.setOnClickListener(view -> {
            analyzeImage();
        });
    }

    private void analyzeImage() {
        try {
            Log.d("FoodViewActivity", "analyzeImage metodu çağrıldı");

            analyzeProgressBar.setVisibility(View.VISIBLE);
            analyzeButton.setEnabled(false);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_food);

            if (bitmap == null) {
                Log.e("FoodViewActivity", "Resim yüklenemedi");
                showToast(getString(R.string.image_load_error));
                analyzeProgressBar.setVisibility(View.GONE);
                analyzeButton.setEnabled(true);
                return;
            }

            Log.d("FoodViewActivity", "Bitmap boyutu: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] byteArray = stream.toByteArray();

            Log.d("FoodViewActivity", "Resim byte array boyutu: " + byteArray.length + " bytes");

            showToast(getString(R.string.analyzing_image));

            // Context parametresi eklenmiş GeminiAPIService çağrısı
            GeminiAPIService.analyzeImage(this, byteArray, new GeminiAPIService.GeminiCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("GeminiAPI", "Response: " + result);
                    runOnUiThread(() -> {
                        analyzeProgressBar.setVisibility(View.GONE);
                        analyzeButton.setEnabled(true);
                        processGeminiResponse(result);
                    });
                }
                @Override
                public void onError(Exception e) {
                    Log.e("GeminiAPI", "Error: " + e.getMessage());
                    runOnUiThread(() -> {
                        analyzeProgressBar.setVisibility(View.GONE);
                        analyzeButton.setEnabled(true);
                        showToast(getString(R.string.error_message));
                    });
                }
            });

        } catch (Exception e) {
            analyzeProgressBar.setVisibility(View.GONE);
            analyzeButton.setEnabled(true);
            Log.e("FoodViewActivity", "Analiz hatası: " + e.getMessage(), e);
            e.printStackTrace();
            showToast(getString(R.string.image_processing_error));
        }
    }

    // Gemini yanıtını işleme metodu
    private void processGeminiResponse(String result) {
        try {
            // İlk olarak ana response'u parse et
            JSONObject fullResponse = new JSONObject(result);
            String textContent = fullResponse
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // ```json ve ``` gibi code block işaretlerini temizle
            String cleanedJson = textContent
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Temizlenmiş JSON'u parse et
            JSONObject responseJson = new JSONObject(cleanedJson);

            // Dil kontrolü yaparak uygun alanları oku
            String currentLanguage = LanguageHelper.getLanguage(this);

            // JSON içinden verileri oku
            String foodName;
            double calories, protein, fat, carbs;
            String recommendations;

            if ("tr".equals(currentLanguage)) {
                foodName = responseJson.optString("yemek_ismi", "İsim bulunamadı");
                calories = responseJson.optDouble("kalori", 0);
                protein = responseJson.optDouble("protein", 0);
                fat = responseJson.optDouble("yağ", 0);
                carbs = responseJson.optDouble("karbonhidrat", 0);
                recommendations = responseJson.optString("oneriler", "Öneri bulunamadı");
            } else {
                foodName = responseJson.optString("food_name", "Name not found");
                calories = responseJson.optDouble("calories", 0);
                protein = responseJson.optDouble("protein", 0);
                fat = responseJson.optDouble("fat", 0);
                carbs = responseJson.optDouble("carbs", 0);
                recommendations = responseJson.optString("recommendations", "No recommendations found");
            }

            // Meal nesnesini oluştur
            Meal meal = new Meal();
            meal.setMeal_name(foodName);
            meal.setProtein_g((int) protein);
            meal.setFat_g((int) fat);
            meal.setCarbs_g((int) carbs);
            meal.setCalorie_g((int) calories);

            // UI'ı güncelle
            foodNameTextView.setText(foodName);
            totalCaloriesText.setText(getString(R.string.total_calories_format, (int) calories));
            aiAdviceText.setText(recommendations);

            proteinTextView.setText(getString(R.string.protein_format, (int) protein));
            carbTextView.setText(getString(R.string.carbs_format, (int) carbs));
            fatTextView.setText(getString(R.string.fat_format, (int) fat));

            showToast(getString(R.string.analysis_complete));
        } catch (JSONException e) {
            Log.e("GeminiAPI", "JSON parse error: " + e.getMessage());
            e.printStackTrace();
            showToast(getString(R.string.data_processing_error));
        }
    }
    private void showToast(String message) {
        Toast.makeText(FoodViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}