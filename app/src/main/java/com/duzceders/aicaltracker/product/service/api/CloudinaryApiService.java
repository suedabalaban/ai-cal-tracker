package com.duzceders.aicaltracker.product.service.api;

import com.duzceders.aicaltracker.product.service.api.model.CloudinaryUploadResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CloudinaryApiService {
    @Multipart
    @POST("v1_1/{cloudName}/image/upload")
    Call<CloudinaryUploadResponse> uploadImage(
            @Part MultipartBody.Part file,
            @Query("api_key") String apiKey,
            @Query("timestamp") long timestamp,
            @Query("signature") String signature
    );
}