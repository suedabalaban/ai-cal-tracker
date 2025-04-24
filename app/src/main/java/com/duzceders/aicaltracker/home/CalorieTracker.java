package com.duzceders.aicaltracker.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;
import com.duzceders.aicaltracker.profile.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.FirebaseApp;

public class CalorieTracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_INTENT_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 102;
    private static final int GALLERY_INTENT_REQUEST_CODE = 103;

    private FirebaseRepository firebaseRepository;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

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
        firebaseRepository = new FirebaseRepository();
        setContentView(R.layout.activity_calorie_tracker);

        setupDrawer();

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

    private void setupDrawer() {
        // Initialize the drawer layout
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Setup toolbar
        setSupportActionBar(toolbar);

        // Remove default title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup drawer toggle
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class<?> currentActivity = CalorieTracker.this.getClass();

        if (id == R.id.nav_home && currentActivity != CalorieTracker.class) {
            Toast.makeText(this, "Ana sayfaya geçiş yapılıyor...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CalorieTracker.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings ) {
            Toast.makeText(this, "Ayarlar seçildi", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Çıkış yapılıyor...", Toast.LENGTH_SHORT).show();

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        if (resultCode != RESULT_OK) return;

        if (requestCode == CAMERA_INTENT_REQUEST_CODE) {
            Log.d("KAMERA", "Fotoğraf çekildi!");
            Toast.makeText(this, "Fotoğraf başarıyla çekildi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == GALLERY_INTENT_REQUEST_CODE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri == null) return;

            Log.d("GALERI", "Fotoğraf seçildi: " + selectedImageUri);
            Toast.makeText(this, "Galeri fotoğrafı seçildi", Toast.LENGTH_SHORT).show();
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
}