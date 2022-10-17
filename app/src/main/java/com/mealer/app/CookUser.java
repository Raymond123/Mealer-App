package com.mealer.app;

import android.os.Parcelable;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CookUser extends User implements Parcelable {

    private String description;
    // picture???

    public CookUser(HashMap<String, String> attributes){
        super(attributes.get("firstName"), attributes.get("lastName"), attributes.get("email"),
                attributes.get("address"), attributes.get("userType"));

        this.description = attributes.get("description");
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
