package com.mealer.app;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

public class CookUser extends User{

    private String description;
    // picture???

    public CookUser(String firstName, String lastName, String email, String address, String password, String description) {
        super(firstName, lastName, email, address, password);
        this.description = description;

        DatabaseReference databaseReference = getReference("users/cook");
        databaseReference.child(getId()).setValue(this);

    }

}
