package com.mealer.app;

import android.os.Parcelable;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class ClientUser extends User implements Parcelable {

    public ClientUser(){

    }

    private String cardNumber;
    private String cardExpiry;
    private String cardSecurity;

    public ClientUser(HashMap<String, String> attributes){
        super(attributes.get("firstName"), attributes.get("lastName"), attributes.get("email"),
                attributes.get("address"), attributes.get("userType"));

        this.cardNumber = attributes.get("cardNumber");
        this.cardExpiry = attributes.get("cardExpiry");
        this.cardSecurity = attributes.get("cardSecurity");
    }

    public ClientUser(String firstName, String lastName, String email, String address,
                      String cardNumber, String cardExpiry, String cardSecurity, String uID, String userType){
        super(firstName, lastName, email, address, userType);
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardSecurity = cardSecurity;

        DatabaseReference databaseReference = getReference("users");
        databaseReference.child(uID).setValue(this);
    }

    public String getCardNumber(){
        return this.cardNumber;
    }

    public String getCardExpiry(){
        return this.cardExpiry;
    }

    public String getCardSecurity(){
        return this.cardSecurity;
    }

}
