package com.mealer.app;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Admin implements Parcelable {
    private String admin;

    public Admin(){

    }

    public Admin(boolean isAdmin){
        this.admin = "administrator";
    }

    protected Admin(Parcel in) {
        admin = in.readString();
    }

    public static final Creator<Admin> CREATOR = new Creator<Admin>() {
        @Override
        public Admin createFromParcel(Parcel in) {
            return new Admin(in);
        }

        @Override
        public Admin[] newArray(int size) {
            return new Admin[size];
        }
    };

    public String getUserType() {
        return this.admin;
    }

    public String getAdmin(){
        return  this.admin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(admin);
    }
}