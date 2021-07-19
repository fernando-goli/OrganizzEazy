package com.example.organizzeazy.config;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {

    private static FirebaseAuth mAuth;
    private static DatabaseReference firebaseDb;

    //retorna a instancia do firebaseDatabase
    public static DatabaseReference getFirebaseDatabase(){
        if( firebaseDb == null){
            firebaseDb = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseDb;
    }

    //retorna a instancia do firebaseauth
    public static FirebaseAuth getFirebaseAuth() {

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

}

