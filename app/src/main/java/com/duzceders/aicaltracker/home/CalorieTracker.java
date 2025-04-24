package com.duzceders.aicaltracker.home;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.ai.AiAnalysisActivity;
import com.duzceders.aicaltracker.product.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.FirebaseApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CalorieTracker extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_INTENT_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 102;
    private static final int GALLERY_INTENT_REQUEST_CODE = 103;


    private TextView totalCaloriesValue;
    private TextView totalProteinValue;
    private TextView totalCarbsValue;
    private TextView totalFatsValue;
    private TextView welcomeText;

    private CircularProgressIndicator circularProgressCalories;
    private CircularProgressIndicator circularProgressProtein;
    private CircularProgressIndicator circularProgressCarbs;
    private CircularProgressIndicator circularProgressFats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_calorie_tracker);

        CalorieTrackerViewModel viewModel = new ViewModelProvider(this).get(CalorieTrackerViewModel.class);

        initializeViews();

        viewModel.getUserData().observe(this, user -> {
            if (user != null) {
                updateUiWithUserData(user);
            } else {
                Toast.makeText(this, "Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            }
        });


        setClickListeners();

    }

    private void updateUiWithUserData(User user) {
        String welcomeMessage = getResources().getString(R.string.welcome, user.getName() + " " + user.getSurname());
        welcomeText.setText(welcomeMessage);

        totalCaloriesValue.setText(String.valueOf(user.getDaily_calorie_needs_left()));
        totalProteinValue.setText(String.valueOf(user.getDaily_macros().getDaily_proteins_left_g()));
        totalCarbsValue.setText(String.valueOf(user.getDaily_macros().getDaily_carbs_left_g()));
        totalFatsValue.setText(String.valueOf(user.getDaily_macros().getDaily_fats_left_g()));

        int progressCalories = calculateProgress(user.getDaily_calorie_needs_left(), user.getDaily_calorie_needs());
        int progressProtein = calculateProgress(user.getDaily_macros().getDaily_proteins_left_g(), user.getDaily_macros().getDaily_proteins_need_g());
        int progressCarbs = calculateProgress(user.getDaily_macros().getDaily_carbs_left_g(), user.getDaily_macros().getDaily_carbs_need_g());
        int progressFats = calculateProgress(user.getDaily_macros().getDaily_fats_left_g(), user.getDaily_macros().getDaily_fats_need_g());


        circularProgressCalories.setProgress(progressCalories);
        circularProgressProtein.setProgress(progressProtein);
        circularProgressCarbs.setProgress(progressCarbs);
        circularProgressFats.setProgress(progressFats);
    }

    private void initializeViews() {

        totalCaloriesValue = findViewById(R.id.totalCaloriesValue);
        totalProteinValue = findViewById(R.id.totalProteinValue);
        totalCarbsValue = findViewById(R.id.totalCarbsValue);
        totalFatsValue = findViewById(R.id.totalFatsValue);
        welcomeText = findViewById(R.id.welcomeText);

        circularProgressCalories = findViewById(R.id.circularProgressCalories);
        circularProgressProtein = findViewById(R.id.circularProgressProtein);
        circularProgressCarbs = findViewById(R.id.circularProgressCarbs);
        circularProgressFats = findViewById(R.id.circularProgressFats);
    }


    private int calculateProgress(double left, double target) {
        return (int) (((float) (target - left) / target) * 100);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_INTENT_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT_REQUEST_CODE);
    }


    private void setClickListeners() {
        FloatingActionButton fabMain = findViewById(R.id.fabMain);
        FloatingActionButton fabCamera = findViewById(R.id.fabCamera);
        FloatingActionButton fabGallery = findViewById(R.id.fabGallery);

        final boolean[] isFabOpen = {false};

        fabMain.setOnClickListener(v -> {
            int visibility = isFabOpen[0] ? View.GONE : View.VISIBLE;
            fabCamera.setVisibility(visibility);
            fabGallery.setVisibility(visibility);
            isFabOpen[0] = !isFabOpen[0];
        });

        fabCamera.setOnClickListener(v -> {
            if (checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST_CODE)) {
                openCamera();
            }
        });

        fabGallery.setOnClickListener(v -> {
            String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

            if (checkPermission(permission, GALLERY_PERMISSION_REQUEST_CODE)) {
                openGallery();
            }
        });
    }

    private boolean checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        Bitmap photo = null;

        if (requestCode == CAMERA_INTENT_REQUEST_CODE) {
            photo = (Bitmap) data.getExtras().get("data");
        }

        if (requestCode == GALLERY_INTENT_REQUEST_CODE) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri == null) return;

            try {
                photo = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                        ? ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(this.getContentResolver(), selectedImageUri))
                        : MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Görsel okunurken hata oluştu", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        if (photo != null) {
            Uri photoUri = saveBitmapToCache(photo);
            if (photoUri != null) {
                Intent intent = new Intent(this, AiAnalysisActivity.class);
                intent.putExtra("imageUri", photoUri.toString());
                startActivity(intent);
            }
        }
    }

    private Uri saveBitmapToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File imageFile = new File(cachePath, "captured.jpg");
            FileOutputStream stream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

            return FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "İzin reddedildi", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                openCamera();
                break;
            case GALLERY_PERMISSION_REQUEST_CODE:
                openGallery();
                break;
        }
    }

    private void goToAiAnalysisActivity(Uri imageUri) {
        Intent intent = new Intent(this, AiAnalysisActivity.class);
        intent.putExtra("imageUri", imageUri.toString()); // URI'yi string olarak gönderiyoruz
        startActivity(intent);
    }

}



