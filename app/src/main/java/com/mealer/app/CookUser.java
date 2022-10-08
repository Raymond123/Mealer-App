package com.mealer.app;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CookUser extends User{

    private String description;
    // picture???

    public CookUser(Object attributes){
        super();
    }

    public CookUser(String firstName, String lastName, String email, String address, String description, String uID, String userType) {
        super(firstName, lastName, email, address, userType);
        this.description = description;

        DatabaseReference databaseReference = getReference("users");
        databaseReference.child(uID).setValue(this);

    }

    public String getDescription(){
        return this.description;
    }

}
