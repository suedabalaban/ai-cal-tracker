package com.duzceders.aicaltracker.product.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import lombok.Getter;

@Getter
public class FirebaseServiceManager {
    private static FirebaseServiceManager instance;

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseStorage firebaseStorage;


    private FirebaseServiceManager(){
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseServiceManager getInstance(){
        if (instance==null){
            instance=new FirebaseServiceManager();
        }
        return instance;
    }

}
