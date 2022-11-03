package com.mealer.app;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Complaint implements Parcelable {

    private String subject;
    private String cookID;
    private String description;

    public Complaint(){

    }

    public Complaint(String subject, String description, String cookID){
        this.subject = subject;
        this.description = description;
        this.cookID = cookID;
    }

    protected Complaint(Parcel in) {
        subject = in.readString();
        cookID = in.readString();
        description = in.readString();
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

    public String getCookID() {
        return cookID;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(subject);
        parcel.writeString(cookID);
        parcel.writeString(description);
    }
}
