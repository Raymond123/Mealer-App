package com.mealer.app;

import android.os.Parcelable;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.mealer.app.menu.Menu;

import java.util.HashMap;

public class CookUser extends User implements Parcelable {

    // cook specific attributes
    private String description;
    private String accountStatus;
    private String suspensionEnd;
    private String rating;

    public CookUser(){

    }

    /**
     * cook user constructor specific for when initializing a new user object
     * for a user that already exists in the firebase authentication and realtime database
     * @param attributes a hashmap containing the users
     *                   attributes pulled from the firebase realtime database
     */
    public CookUser(HashMap<String, String> attributes){
        super(attributes.get("firstName"), attributes.get("lastName"),
                attributes.get("email"), attributes.get("userType"), attributes.get("city"),
                attributes.get("houseNumber"), attributes.get("street"));

        this.description = attributes.get("description");
        this.accountStatus = attributes.get("accountStatus");
        this.suspensionEnd = attributes.get("suspensionEnd");
        try {
            this.rating = attributes.get("rating");
        }catch(Exception ex){
            this.rating = "75";
        }
    }

    /**
     * cook user constructor for creating a new user and adding information to the firebase
     * realtime database
     * @param uID unique id generated by firebase authentication
     * @param userType this attribute is always "cook" for this class,
     *                but having specific attribute allows for displaying type
     */
    public CookUser(String firstName, String lastName, String email,
                    String city, String houseNumber, String street,
                    String description, String uID, String userType,
                    String accountStatus, String suspensionEnd) {
        super(firstName, lastName, email, userType, city, houseNumber, street);
        this.description = description;
        this.accountStatus = accountStatus;
        this.suspensionEnd = suspensionEnd;
        this.rating = "75";

        // get database reference to the "users" tree
        DatabaseReference databaseReference = getReference("users");
        // add user to database under uID
        databaseReference.child(uID).setValue(this);
    }

    // get method for cook user specific attributes
    public String getDescription(){
        return this.description;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getSuspensionEnd() {
        return suspensionEnd;
    }

    public String getRating() {
        return rating + "%";
    }

    @Exclude
    public int getIntRating() {return Integer.parseInt(rating); }

    @Exclude
    public void setRating(int rating) {
        this.rating = Integer.toString(rating);
    }
}
