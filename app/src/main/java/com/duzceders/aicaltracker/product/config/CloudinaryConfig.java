package com.duzceders.aicaltracker.product.config;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cloudinary.android.MediaManager;
import com.duzceders.aicaltracker.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CloudinaryConfig {
    private static final String TAG = "CloudinaryConfig";
    private static boolean isInitialized = false;

    public static void initialize(Context context) {
        if (isInitialized) {
            Log.d(TAG, "CloudinaryConfig already initialized");
            return;
        }

        try {
            Map<String, String> config = getSecretProperties();

            MediaManager.init(context, config);
            isInitialized = true;
            Log.d(TAG, "CloudinaryConfig initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing CloudinaryConfig", e);
        }
    }

    @NonNull
    private static Map<String, String> getSecretProperties() {
        Properties properties = new Properties();

        String cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME;
        String apiKey = BuildConfig.CLOUDINARY_API_KEY;
        String apiSecret = BuildConfig.CLOUDINARY_API_SECRET;

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", "true");
        return config;
    }

    public static boolean isInitialized() {
        return isInitialized;
    }
}