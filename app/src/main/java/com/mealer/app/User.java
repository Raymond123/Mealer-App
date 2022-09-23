package com.mealer.app;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String password; //TODO: secure passwords
    private String id;

    public User(String firstName, String lastName, String email, String address, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.password = password;
        this.id = newId();
    }

    protected DatabaseReference getReference(String path){
        return FirebaseDatabase.getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/").getReference(path);
    }

    private String newId(){
        String uId = Integer.toString(new Random().nextInt(999));
        String base = "000";

        if(base.length() > uId.length()) {
            char[] baseArr = base.toCharArray();
            for (int i = 0; i < uId.length(); i++) {
                baseArr[i] = uId.charAt(i);
            }
            uId = new String(baseArr);
        }

        return uId; //TODO: check id is unique, if not create new id, also make id larger (more digits)
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
        return id;
    }
}
