package com.duzceders.aicaltracker.features.calorie_tracker;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;
import com.duzceders.aicaltracker.product.service.api.GeminiAPIService;
import com.duzceders.aicaltracker.product.service.manager.CloudinaryServiceManager;
import com.duzceders.aicaltracker.product.utils.SingleLiveEvent;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import lombok.Getter;

public class CalorieTrackerViewModel extends AndroidViewModel {
    private final FirebaseRepository repository = new FirebaseRepository();

    @Getter
    private final SingleLiveEvent<String> imageUrlLiveData = new SingleLiveEvent<>();
    @Getter
    private final SingleLiveEvent<String> geminiResponseLiveData = new SingleLiveEvent<>();
    @Getter
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    private final CloudinaryServiceManager cloudinaryServiceManager;

    private final GeminiAPIService geminiService;

    public CalorieTrackerViewModel(Application application) {
        super(application);
        this.cloudinaryServiceManager = new CloudinaryServiceManager(application);
        this.geminiService = new GeminiAPIService();
    }


    public LiveData<User> getUserData() {
        return repository.getUserByUID();
    }

    public void analyzeImageFromCamera(Bitmap image, Context context) {
        if (image == null) return;
        changeLoading();

        String mealId = UUID.randomUUID().toString();
        byte[] imageBytes = bitmapToByteArray(image);


        cloudinaryServiceManager.uploadImageFromCamera(image, new CloudinaryServiceManager.CloudinaryUploadCallback() {

            @Override
            public void onSuccess(String imageUrl) {
                imageUrlLiveData.postValue(imageUrl);
                geminiService.analyzeImage(context, imageBytes, new GeminiAPIService.GeminiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        geminiResponseLiveData.postValue(result);
                        changeLoading();
                    }

                    @Override
                    public void onError(Exception e) {
                        changeLoading();

                    }
                });

            }

            @Override
            public void onError(String errorMessage) {
                changeLoading();

            }
        }, mealId);

    }

    public void analyzeImageFromGallery(Uri imageUri, byte[] imageBytes, Context context) {
        changeLoading();
        String mealId = UUID.randomUUID().toString();

        cloudinaryServiceManager.uploadImageFromGallery(imageUri, new CloudinaryServiceManager.CloudinaryUploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                imageUrlLiveData.postValue(imageUrl);
                geminiService.analyzeImage(context, imageBytes, new GeminiAPIService.GeminiCallback() {
                    @Override
                    public void onSuccess(String result) {
                        geminiResponseLiveData.postValue(result);
                        changeLoading();
                    }

                    @Override
                    public void onError(Exception e) {
                        changeLoading();
                    }
                });

            }

            @Override
            public void onError(String errorMessage) {
                changeLoading();
            }
        }, mealId);
    }


    private void changeLoading() {
        isLoading.postValue(!isLoading.getValue());
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
