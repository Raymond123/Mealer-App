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

    public CookUser(String firstName, String lastName, String email, String phoneNumber, String address, String password, String description) {
        super(firstName, lastName, email, phoneNumber, address, password);
        this.description = description;

        DatabaseReference databaseReference = getReference("users/cook");
        databaseReference.child(getId()).setValue(this);

        /*
        databaseReference.child(getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    generateNewId();
                }
                databaseReference.child(getId()).setValue(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

    }

}
