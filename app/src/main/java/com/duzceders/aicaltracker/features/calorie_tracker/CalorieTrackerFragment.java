package com.duzceders.aicaltracker.features.calorie_tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.FragmentCalorieTrackerBinding;
import com.duzceders.aicaltracker.features.calorie_tracker.adapter.MealAdapter;
import com.duzceders.aicaltracker.features.calorie_tracker.factory.CalorieTrackerViewModelFactory;
import com.duzceders.aicaltracker.features.food_view.FoodViewActivity;
import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;
import com.duzceders.aicaltracker.product.widgets.LoadingDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CalorieTrackerFragment extends Fragment {

    private FragmentCalorieTrackerBinding binding;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_INTENT_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 102;
    private static final int GALLERY_INTENT_REQUEST_CODE = 103;

    private static final String TAG = "CalorieTrackerFragment";

    private FirebaseRepository firebaseRepository;
    private CalorieTrackerViewModel viewModel;

    private LoadingDialog loadingDialog;
    private MealAdapter mealAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext());
        firebaseRepository = new FirebaseRepository();
        viewModel = new ViewModelProvider(this, new CalorieTrackerViewModelFactory(requireActivity().getApplication())).get(CalorieTrackerViewModel.class);


        loadingDialog = new LoadingDialog(requireContext());
        Intent intent = new Intent(getContext(), FoodViewActivity.class);

        viewModel.getImageUrlLiveData().observe(this, imageUrl -> {
            if (imageUrl != null) {
                intent.putExtra("imageUrl", imageUrl);
            }
        });

        viewModel.getFoodInfoLiveData().observe(this, foodInfo -> {
            if (foodInfo != null) {
                intent.putExtra("foodInfo", foodInfo);
                loadingDialog.dismiss();
                startActivity(intent);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalorieTrackerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUiWithUserData(user);
            } else {
                Toast.makeText(requireContext(), "Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            }
        });

        setClickListeners();
        setupRecyclerView();

        viewModel.refreshMeals();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refreshMeals();
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        mealAdapter = new MealAdapter(null, requireContext());
        binding.recyclerView.setAdapter(mealAdapter);


        viewModel.getMeals().observe(getViewLifecycleOwner(), meals -> {
            if (meals != null && !meals.isEmpty()) {
                binding.startTrackingLabel.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                mealAdapter.updateMeals(meals);
            } else {
                binding.startTrackingLabel.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void updateUiWithUserData(User user) {


        binding.totalCaloriesValue.setText(String.valueOf(Math.max(user.getDaily_calorie_needs_left(), 0)));
        binding.totalProteinValue.setText(String.valueOf(Math.max(user.getDaily_macros().getDaily_proteins_left_g(), 0)));
        binding.totalCarbsValue.setText(String.valueOf(Math.max(user.getDaily_macros().getDaily_carbs_left_g(), 0)));
        binding.totalFatsValue.setText(String.valueOf(Math.max(user.getDaily_macros().getDaily_fats_left_g(), 0)));

        int progressCalories = calculateProgress(user.getDaily_calorie_needs_left(), user.getDaily_calorie_needs());
        int progressProtein = calculateProgress(user.getDaily_macros().getDaily_proteins_left_g(), user.getDaily_macros().getDaily_proteins_need_g());
        int progressCarbs = calculateProgress(user.getDaily_macros().getDaily_carbs_left_g(), user.getDaily_macros().getDaily_carbs_need_g());
        int progressFats = calculateProgress(user.getDaily_macros().getDaily_fats_left_g(), user.getDaily_macros().getDaily_fats_need_g());

        binding.circularProgressCalories.setProgress(progressCalories);
        binding.circularProgressProtein.setProgress(progressProtein);
        binding.circularProgressCarbs.setProgress(progressCarbs);
        binding.circularProgressFats.setProgress(progressFats);
    }

    private int calculateProgress(double left, double target) {
        return (int) (((float) (target - left) / target) * 100);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_INTENT_REQUEST_CODE);
        }
    }

    @SuppressLint("IntentReset")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT_REQUEST_CODE);
    }

    private void setClickListeners() {
        FloatingActionButton fabMain = binding.fabMain;
        FloatingActionButton fabCamera = binding.fabCamera;
        FloatingActionButton fabGallery = binding.fabGallery;

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
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != requireActivity().RESULT_OK) return;
        if (requestCode == CAMERA_INTENT_REQUEST_CODE && data != null) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            if (photo == null) return;
            try {
                loadingDialog.show();
                viewModel.analyzeImageFromCamera(photo, requireContext());
            } catch (Exception e) {
                loadingDialog.dismiss();
                throw new RuntimeException(e);
            }
        }

        if (requestCode == GALLERY_INTENT_REQUEST_CODE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri == null) return;

            try {
                loadingDialog.show();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.requireActivity().getContentResolver(), selectedImageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bitmap.recycle();
                viewModel.analyzeImageFromGallery(selectedImageUri, byteArray, requireContext());
            } catch (IOException e) {
                loadingDialog.dismiss();
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            showToast("İzin reddedildi");
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

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}