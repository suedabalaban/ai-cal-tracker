package com.duzceders.aicaltracker.features.food_view;

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
import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.service.api.GeminiAPIService;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_view);

        initializeViews();
        //loadImageFromIntent();
        setListeners();
    }

//    private void loadImageFromIntent() {
//        String uriString = getIntent().getStringExtra("imageUri");
//        if (uriString != null) {
//            Uri imageUri = Uri.parse(uriString);
//            mealImageView.setImageURI(imageUri);
//        }
//    }


    private void initializeViews() {
        mealImageView = findViewById(R.id.mealImageView);
        //hazır veriyi image olarak ekle
        mealImageView.setImageResource(R.drawable.test_food);
        foodNameTextView = findViewById(R.id.foodNameTextView);
        portionEditText = findViewById(R.id.portionEditText);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);
        aiAdviceText = findViewById(R.id.aiAdviceText);
        userNoteEditText = findViewById(R.id.userNoteEditText);
        saveButton = findViewById(R.id.saveButton);
        analyzeButton = findViewById(R.id.analyzeButton);
        analyzeProgressBar = findViewById(R.id.analyzeProgressBar);

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
                Toast.makeText(FoodViewActivity.this, getString(R.string.image_load_error), Toast.LENGTH_SHORT).show();
                analyzeProgressBar.setVisibility(View.GONE);
                analyzeButton.setEnabled(true);
                return;
            }

            Log.d("FoodViewActivity", "Bitmap boyutu: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            byte[] byteArray = stream.toByteArray();


            Log.d("FoodViewActivity", "Resim byte array boyutu: " + byteArray.length + " bytes");

            Toast.makeText(FoodViewActivity.this, getString(R.string.analyzing_image), Toast.LENGTH_SHORT).show();
            GeminiAPIService.analyzeImage(byteArray, new GeminiAPIService.GeminiCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("GeminiAPI", "Response: " + result);
                    runOnUiThread(() -> {
                        analyzeProgressBar.setVisibility(View.GONE);
                        analyzeButton.setEnabled(true);
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

                            // Şimdi temizlenmiş JSON'u parse edebilirsin
                            JSONObject responseJson = new JSONObject(cleanedJson);

                            // JSON içinden verileri oku
                            String yemekIsmi = responseJson.optString("yemek_ismi", "İsim bulunamadı");
                            double kalori = responseJson.optDouble("kalori", 0);
                            double protein = responseJson.optDouble("protein", 0);
                            double yag = responseJson.optDouble("yağ", 0);
                            double karbonhidrat = responseJson.optDouble("karbonhidrat", 0);
                            String oneriler = responseJson.optString("oneriler", "Öneri bulunamadı");

                            // Meal nesnesini oluştur
                            Meal meal = new Meal();
                            meal.setMeal_name(yemekIsmi);
                            meal.setProtein_g((int) protein);
                            meal.setFat_g((int) yag);
                            meal.setCarbs_g((int) karbonhidrat);
                            meal.setCalorie_g((int) kalori);

                            // UI'ı güncelle
                            foodNameTextView.setText(yemekIsmi);
                            totalCaloriesText.setText(getString(R.string.total_calories_format, (int) kalori));
                            aiAdviceText.setText(oneriler);

                            proteinTextView.setText(getString(R.string.protein_format, (int) protein));
                            carbTextView.setText(getString(R.string.carbs_format, (int) karbonhidrat));
                            fatTextView.setText(getString(R.string.fat_format, (int) yag));

                            Toast.makeText(FoodViewActivity.this, getString(R.string.analysis_complete), Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            Log.e("GeminiAPI", "JSON parse error: " + e.getMessage());
                            e.printStackTrace();
                            Toast.makeText(FoodViewActivity.this, getString(R.string.data_processing_error), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                @Override
                public void onError(Exception e) {
                    Log.e("GeminiAPI", "Error: " + e.getMessage());
                    runOnUiThread(() -> {
                        analyzeProgressBar.setVisibility(View.GONE);
                        analyzeButton.setEnabled(true);
                        Toast.makeText(FoodViewActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } catch (Exception e) {
            analyzeProgressBar.setVisibility(View.GONE);
            analyzeButton.setEnabled(true);
            Log.e("FoodViewActivity", "Analiz hatası: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(FoodViewActivity.this, getString(R.string.image_processing_error), Toast.LENGTH_SHORT).show();

        }
    }
}