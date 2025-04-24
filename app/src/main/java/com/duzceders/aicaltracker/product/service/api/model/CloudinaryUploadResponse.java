package com.duzceders.aicaltracker.product.service.api.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class CloudinaryUploadResponse {
    @SerializedName("public_id")
    private String publicId;

    @SerializedName("url")
    private String url;

    @SerializedName("secure_url")
    private String secureUrl;

    @SerializedName("original_filename")
    private String originalFilename;

}