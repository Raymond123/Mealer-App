package com.mealer.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;


public class User extends Admin implements Parcelable {

    private int mData;

    // initializing user attributes
    private String firstName;
    private String lastName;
    private String email;
    private String userType;

    // address strings
    private String city;
    private String houseNumber;
    private String street;

    // user constructor
    public User(String firstName, String lastName, String email, String userType,
                String city, String houseNumber, String street){
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
        this.city = city;
        this.houseNumber = houseNumber;
        this.street = street;
    }

    public static String[] parseAddress(String address){
        String[] addressMap = address.replace(",", "").split(" ");
        if(addressMap.length > 3){
            // combine all "arrays" in case that street name had spaces
        }
        return addressMap;
    }

    // empty user constructor required for taking firebase snapshot
    public User() {

    }

    // user constructor required for implementing parcelable interface
    protected User(Parcel in) {
        mData = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        city = in.readString();
        houseNumber = in.readString();
        street = in.readString();
        userType = in.readString();
    }

    // required for implementing parcelable
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * method for getting reference to firebase realtime database from user subclasses
     * @param path the path to store the database information
     * @return returns a database reference to the projects firebase realtime database
     */
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

    public String getCity(){ return city; }

    public String getHouseNumber() { return houseNumber; }

    public String getStreet() { return street; }

    public String getUserType(){ return userType;}


    // more methods required for implementing parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(mData);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(city);
        parcel.writeString(houseNumber);
        parcel.writeString(street);
        parcel.writeString(userType);
    }
}
