package com.duzceders.aicaltracker.product.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageHelper {
    private static final String SELECTED_LANGUAGE = "selected_language";

    // Dil ayarını kaydet
    public static void setLanguage(Context context, String languageCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply();
        updateLanguage(context, languageCode);
    }

    // Kaydedilmiş dil ayarını getir
    public static String getLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, Locale.getDefault().getLanguage());
    }

    // Kaydedilmiş dil ayarını uygula
    public static void applyLanguage(Context context) {
        String languageCode = getLanguage(context);
        updateLanguage(context, languageCode);
    }

    // Dil değişimini gerçekleştir
    private static void updateLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}