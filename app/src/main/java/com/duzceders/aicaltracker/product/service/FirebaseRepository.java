package com.duzceders.aicaltracker.product.service;

import android.net.Uri;
import android.util.Log;
import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.service.manager.FirebaseServiceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;
import java.util.function.Consumer;

public class FirebaseRepository {

    private static final String TAG = "FirebaseRepository";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public FirebaseRepository() {
        FirebaseServiceManager manager = FirebaseServiceManager.getInstance();
        db = manager.getFirebaseFirestore();
        auth = manager.getFirebaseAuth();
    }

    public void getUsersFirebase(Consumer<QuerySnapshot> onSuccess, Consumer<Exception> onFailure) {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        onSuccess.accept(task.getResult());
                    } else {
                        onFailure.accept(task.getException());
                    }
                });
    }

    private String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void addUser(User user) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added"))
                .addOnFailureListener(e -> Log.e(TAG, "User add failed", e));
    }

    public void addMeal(Meal meal, Uri imageUri) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }
        String mealId = UUID.randomUUID().toString();
        db.collection("users")
                .document(userId)
                .collection("meals")
                .document(mealId)
                .set(meal)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Meal added"))
                .addOnFailureListener(e -> Log.e(TAG, "Meal save failed", e));
    }
}
