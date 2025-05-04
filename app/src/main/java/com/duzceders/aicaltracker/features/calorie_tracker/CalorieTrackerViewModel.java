package com.duzceders.aicaltracker.features.calorie_tracker;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.duzceders.aicaltracker.product.models.FoodInfo;
import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.models.enums.UserField;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;
import com.duzceders.aicaltracker.product.service.api.GeminiAPIService;
import com.duzceders.aicaltracker.product.service.manager.CloudinaryServiceManager;
import com.duzceders.aicaltracker.product.utils.SingleLiveEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

public class CalorieTrackerViewModel extends AndroidViewModel {
    private final FirebaseRepository repository = new FirebaseRepository();

    @Getter
    private final SingleLiveEvent<String> imageUrlLiveData = new SingleLiveEvent<>();
    @Getter
    private final SingleLiveEvent<FoodInfo> foodInfoLiveData = new SingleLiveEvent<>();
    @Getter
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    @Getter
    private final MutableLiveData<Integer> waterGlassesFilled = new MutableLiveData<>(0);

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

    public LiveData<List<Meal>> getMeals() {
        return repository.getUserMealsLiveData();
    }

    public void refreshMeals() {
        repository.fetchUserMeals();
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
                        FoodInfo foodInfo = processGeminiResponse(result);
                        foodInfoLiveData.postValue(foodInfo);
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
                        FoodInfo foodInfo = processGeminiResponse(result);
                        foodInfoLiveData.postValue(foodInfo);
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

    public void setWaterGlassesFilled(int count) {
        waterGlassesFilled.setValue(count);
    }

    public void saveWaterIntake(User user, int glassCount, double glassSize) {
        if (user == null || glassCount <= 0) return;

        double totalWaterConsumed = glassCount * glassSize;
        double waterLeft = Math.max(0, user.getDaily_water_needs_left_liters() - totalWaterConsumed);
        
        repository.updateUser(UserField.DAILY_WATER_NEEDS_LEFT_LITERS, waterLeft);
    }

    private void changeLoading() {
        isLoading.postValue(!isLoading.getValue());
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private FoodInfo processGeminiResponse(String result) {
        try {

            JSONObject fullResponse = new JSONObject(result);
            String textContent = fullResponse.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");

            String cleanedJson = textContent.replace("```json", "").replace("```", "").trim();

            JSONObject responseJson = new JSONObject(cleanedJson);

            String foodName;
            double calories, protein, fat, carbs;
            String recommendations;

            foodName = responseJson.optString("food_name", "Name not found");
            calories = responseJson.optDouble("calories", 0);
            protein = responseJson.optDouble("protein", 0);
            fat = responseJson.optDouble("fat", 0);
            carbs = responseJson.optDouble("carbs", 0);
            recommendations = responseJson.optString("recommendations", "No recommendations found");
            FoodInfo foodInfo = new FoodInfo();
            foodInfo.setFoodName(foodName);
            foodInfo.setCalories((int) calories);
            foodInfo.setProtein((int) protein);
            foodInfo.setFat((int) fat);
            foodInfo.setCarbs((int) carbs);
            foodInfo.setRecommendations(recommendations);
            return foodInfo;

        } catch (JSONException e) {
            Log.e("GeminiAPI", "JSON parse error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.removeUserListener();
        repository.removeMealsListener();
    }
}
