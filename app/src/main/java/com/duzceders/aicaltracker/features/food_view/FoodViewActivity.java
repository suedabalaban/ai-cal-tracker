package com.duzceders.aicaltracker.features.food_view;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.duzceders.aicaltracker.R;

public class FoodViewActivity extends AppCompatActivity {

    private ImageView mealImageView;
    private TextView foodNameTextView, totalCaloriesText;
    private EditText portionEditText, aiAdviceText, userNoteEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_view);

        initializeViews();
        loadImageFromIntent();
        setDummyData();
        setListeners();
    }

    private void loadImageFromIntent() {
        String uriString = getIntent().getStringExtra("imageUri");
        if (uriString != null) {
            Uri imageUri = Uri.parse(uriString);
            mealImageView.setImageURI(imageUri);
        }
    }


    private void initializeViews() {
        mealImageView = findViewById(R.id.mealImageView);
        foodNameTextView = findViewById(R.id.foodNameTextView);
        portionEditText = findViewById(R.id.portionEditText);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);
        aiAdviceText = findViewById(R.id.aiAdviceText);
        userNoteEditText = findViewById(R.id.userNoteEditText);
        saveButton = findViewById(R.id.saveButton);
    }


    private void setDummyData() {
        foodNameTextView.setText("İskender");
        portionEditText.setText("100");
        totalCaloriesText.setText("Toplam Kalori: 250 kcal");
        aiAdviceText.setText("Bu yemek protein açısından zengindir. Dengelemek için sebze tüketebilirsiniz.");
    }

    private void setListeners() {
        saveButton.setOnClickListener(v -> {
            String foodName = foodNameTextView.getText().toString();
            String userNote = userNoteEditText.getText().toString();

        });
    }
}