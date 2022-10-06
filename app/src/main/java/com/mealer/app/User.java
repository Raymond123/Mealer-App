package com.mealer.app;

import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumer;
    private String address;
    private String password; //TODO: secure passwords

    public User(String firstName, String lastName, String email, String phoneNumer, String address, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumer = phoneNumer;
        this.address = address;
        this.password = password;
    }

    protected DatabaseReference getReference(String path){
        return FirebaseDatabase.getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/").getReference(path);
    }

    // getMethods()
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return UUID.randomUUID().toString();
    }
}
