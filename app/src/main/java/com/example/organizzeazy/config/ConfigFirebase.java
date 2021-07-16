package com.example.organizzeazy.config;

import com.google.firebase.auth.FirebaseAuth;

public class ConfigFirebase {

    private static FirebaseAuth mAuth;

    //retorna a instancia do firebaseauth
    public static FirebaseAuth getFirebaseAuth() {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

}

