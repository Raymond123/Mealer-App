package com.mealer.app;

import com.google.firebase.database.DatabaseReference;

public class ClientUser extends User{

    public ClientUser(){

    }

    private String cardNumber;
    private String cardExpiry;
    private String cardSecurity;

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
