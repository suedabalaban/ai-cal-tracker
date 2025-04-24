package com.duzceders.aicaltracker.product.service.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.duzceders.aicaltracker.BuildConfig;
import com.duzceders.aicaltracker.product.config.CloudinaryConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CloudinaryServiceManager {
    private static final String TAG = "CloudinaryServiceMgr";
    private Context context;

    public interface CloudinaryUploadCallback {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    public CloudinaryServiceManager(Context context) {
        this.context = context;
        if (!CloudinaryConfig.isInitialized()) {
            CloudinaryConfig.initialize(context);
        }
    }


    public void uploadImageFromCamera(Bitmap bitmap, CloudinaryUploadCallback callback) {
        try {
            // Bitmap'i geçici bir dosyaya dönüştür
            String filename = "image_" + UUID.randomUUID().toString() + ".jpg";
            File outputDir = context.getCacheDir();
            File outputFile = new File(outputDir, filename);

            FileOutputStream out = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            // Dosyayı Cloudinary'e yükle
            uploadFile(outputFile, callback);
        } catch (IOException e) {
            Log.e(TAG, "Error saving camera image", e);
            callback.onError("Kamera görüntüsü kaydedilemedi: " + e.getMessage());
        }
    }

    // Galeriden seçilen bir resmi Cloudinary'e yükler
    public void uploadImageFromGallery(Uri imageUri, CloudinaryUploadCallback callback) {
        uploadUri(imageUri, callback);
    }

    private void uploadFile(File file, final CloudinaryUploadCallback callback) {
        // 1. Yöntem: Unsigned upload (Upload preset gerektirir)
        try {
            String requestId = MediaManager.get().upload(file.getAbsolutePath())
                    .unsigned("my_preset") // Cloudinary konsolundan oluşturduğunuz preset adını buraya yazın
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progress = (double) bytes / totalBytes;
                            Log.d(TAG, "Upload progress: " + (int) (progress * 100) + "%");
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String secureUrl = (String) resultData.get("secure_url");
                            Log.d(TAG, "Upload successful: " + secureUrl);
                            callback.onSuccess(secureUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Upload error: " + error.getDescription());
                            
                            // Unsigned upload başarısız olursa signed upload deneyelim
                            uploadFileSigned(file, callback);
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.d(TAG, "Upload rescheduled");
                        }
                    })
                    .dispatch();
        } catch (Exception e) {
            Log.e(TAG, "Unsigned upload exception", e);
            // Hata durumunda signed upload deneyelim
            uploadFileSigned(file, callback);
        }
    }

    // İmzalı (signed) yükleme metodu - Upload preset gerektirmez
    private void uploadFileSigned(File file, final CloudinaryUploadCallback callback) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("public_id", "app_upload_" + UUID.randomUUID().toString());
            
            String requestId = MediaManager.get().upload(file.getAbsolutePath())
                    .option("api_key", BuildConfig.CLOUDINARY_API_KEY)
                    .options(options)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Signed upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progress = (double) bytes / totalBytes;
                            Log.d(TAG, "Signed upload progress: " + (int) (progress * 100) + "%");
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String secureUrl = (String) resultData.get("secure_url");
                            Log.d(TAG, "Signed upload successful: " + secureUrl);
                            callback.onSuccess(secureUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Signed upload error: " + error.getDescription());
                            callback.onError("Yükleme başarısız: " + error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.d(TAG, "Signed upload rescheduled");
                        }
                    })
                    .dispatch();
        } catch (Exception e) {
            Log.e(TAG, "Signed upload exception", e);
            callback.onError("Yükleme başarısız: " + e.getMessage());
        }
    }

    private void uploadUri(Uri uri, final CloudinaryUploadCallback callback) {
        // 1. Yöntem: Unsigned upload (Upload preset gerektirir)
        try {
            String requestId = MediaManager.get().upload(uri)
                    .unsigned("my_preset") // Cloudinary konsolundan oluşturduğunuz preset adını buraya yazın
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progress = (double) bytes / totalBytes;
                            Log.d(TAG, "Upload progress: " + (int) (progress * 100) + "%");
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String secureUrl = (String) resultData.get("secure_url");
                            Log.d(TAG, "Upload successful: " + secureUrl);
                            callback.onSuccess(secureUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Upload error: " + error.getDescription());
                            
                            // Urisigned upload başarısız olursa signed upload deneyelim
                            uploadUriSigned(uri, callback);
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.d(TAG, "Upload rescheduled");
                        }
                    })
                    .dispatch();
        } catch (Exception e) {
            Log.e(TAG, "Unsigned upload exception", e);
            // Hata durumunda signed upload deneyelim
            uploadUriSigned(uri, callback);
        }
    }

    // İmzalı (signed) URI yükleme metodu - Upload preset gerektirmez
    private void uploadUriSigned(Uri uri, final CloudinaryUploadCallback callback) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("public_id", "app_upload_" + UUID.randomUUID().toString());
            
            String requestId = MediaManager.get().upload(uri)
                    .option("api_key", BuildConfig.CLOUDINARY_API_KEY)
                    .options(options)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "Signed upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progress = (double) bytes / totalBytes;
                            Log.d(TAG, "Signed upload progress: " + (int) (progress * 100) + "%");
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String secureUrl = (String) resultData.get("secure_url");
                            Log.d(TAG, "Signed upload successful: " + secureUrl);
                            callback.onSuccess(secureUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "Signed upload error: " + error.getDescription());
                            callback.onError("Yükleme başarısız: " + error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            Log.d(TAG, "Signed upload rescheduled");
                        }
                    })
                    .dispatch();
        } catch (Exception e) {
            Log.e(TAG, "Signed upload exception", e);
            callback.onError("Yükleme başarısız: " + e.getMessage());
        }
    }
}