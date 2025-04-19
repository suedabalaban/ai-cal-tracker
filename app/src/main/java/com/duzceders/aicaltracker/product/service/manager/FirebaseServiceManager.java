package com.duzceders.aicaltracker.product.service.manager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import lombok.Getter;

@Getter
public class FirebaseServiceManager {
    private static FirebaseServiceManager instance;

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firebaseFirestore;


    private FirebaseServiceManager(){
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseServiceManager getInstance(){
        if (instance==null){
            instance=new FirebaseServiceManager();
        }
        return instance;
    }

}
