package com.duzceders.aicaltracker.product.service.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.duzceders.aicaltracker.BuildConfig;
import com.duzceders.aicaltracker.product.utils.LanguageHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiAPIService {
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String PROJECT_ID = BuildConfig.GEMINI_PROJECT_ID;
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-04-17:generateContent?key=" + API_KEY;
    private static final OkHttpClient client = new OkHttpClient();

    // Callback interface
    public interface GeminiCallback {
        void onSuccess(String result);

        void onError(Exception e);
    }

    public void analyzeImage(Context context, byte[] imageBytes, GeminiCallback callback) {
        try {
            String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            // Görsel kısmı
            JsonObject inlineData = new JsonObject();
            inlineData.addProperty("mimeType", "image/jpeg");
            inlineData.addProperty("data", base64Image);

            JsonObject imagePart = new JsonObject();
            imagePart.add("inlineData", inlineData);

            // Prompt kısmı
            JsonObject textPart = new JsonObject();
            String promptText;
            String currentLanguage = LanguageHelper.getLanguage(context);
            if ("tr".equals(currentLanguage)) {
                promptText = "Bu bir yemek fotoğrafı. Lütfen sadece aşağıdaki formatta JSON döndür:\n" + "{\n" + "\"food_name\": (string cinsinden),\n" + "\"calories\": (tam sayı kcal cinsinden),\n" + "\"protein\": (tam sayı gram cinsinden),\n" + "\"fat\": (tam sayı gram cinsinden),\n" + "\"carbs\": (tam sayı gram cinsinden),\n" + "\"recommendations\": (yemeği nasıl daha sağlıklı hale getirebilirim, yemeğin özelliklerine bağlı kalarak analiz et? kısa açıklama)\n" + "}\n" + "Hiçbir açıklama yazmadan SADECE JSON ver. Görseli analiz ederek bu bilgileri tahmin et.Türkçe cevap ver. Makro besinleri tahmin ederken, yemeğin içeriğini ve miktarını göz önünde bulundur, tutarlı bir tahmin yap.";
            } else {
                promptText = "This is a food image. Please return JSON in the following format only:\n" + "{\n" + "\"food_name\": (as string),\n" + "\"calories\": (as integer in kcal),\n" + "\"protein\": (as integer in grams),\n" + "\"fat\": (as integer in grams),\n" + "\"carbs\": (as integer in grams),\n" + "\"recommendations\": (short explanation of how to make this meal healthier based on its characteristics)\n" + "}\n" + "Return ONLY JSON without any explanation. Use the image analysis to estimate this information. Respond in English. When estimating macronutrients, consider the content and quantity of the meal for a consistent estimate.";
            }
            textPart.addProperty("text", promptText);

            // Parts listesi
            JsonArray partsArray = new JsonArray();
            partsArray.add(imagePart);
            partsArray.add(textPart);

            // İçerik
            JsonObject content = new JsonObject();
            content.add("parts", partsArray);

            // Contents listesi
            JsonArray contentsArray = new JsonArray();
            contentsArray.add(content);

            // Ana payload
            JsonObject payload = new JsonObject();
            payload.add("contents", contentsArray);

            String jsonString = payload.toString();
            Log.d("GeminiAPIService", "JSON Request: " + jsonString);

            RequestBody body = RequestBody.create(jsonString, MediaType.parse("application/json"));

            Request request = new Request.Builder().url(ENDPOINT).post(body).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError(new IOException("Unexpected code " + response));
                        return;
                    }
                    String responseBody = response.body().string();
                    Log.d("GeminiAPIService", "Response Body: " + responseBody);

                    String extractedJson = extractJson(responseBody);
                    callback.onSuccess(extractedJson);
                }
            });

        } catch (Exception e) {
            callback.onError(e);
        }
    }

    private static String extractJson(String responseBody) {
        // Gemini cevabından sadece JSON kısmını ayıklamak için basit bir yöntem
        int start = responseBody.indexOf('{');
        int end = responseBody.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return responseBody.substring(start, end + 1);
        } else {
            return "{}";  // Hata durumunda boş bir JSON dön
        }
    }
}