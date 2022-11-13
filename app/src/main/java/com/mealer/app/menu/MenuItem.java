package com.mealer.app.menu;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.UUID;

public class MenuItem implements Parcelable {

    protected String itemId;
    private String itemName;
    private String itemDescription;
    private String calories;
    private String mainIngredients;
    private boolean active;

    public MenuItem(){

    }

    public MenuItem(String itemName, String itemDescription,
                    String calories, String mainIngredients, boolean active){
        this.itemId = UUID.randomUUID().toString();
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.calories = calories;
        this.mainIngredients = mainIngredients;
        this.active = active;
    }

    protected MenuItem(Parcel in) {
        this.itemId = in.readString();
        this.itemName = in.readString();
        this.itemDescription = in.readString();
        this.calories = in.readString();
        this.mainIngredients = in.readString();
        this.active = in.readInt()==1;
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    // get/set methods for MenuItem attributes
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setMainIngredients(String mainIngredients) {
        this.mainIngredients = mainIngredients;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Exclude
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCalories() {
        return calories;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemName() {
        return itemName;
    }

    public String getMainIngredients() {
        return mainIngredients;
    }

    @Exclude
    public boolean isActive() {
        return active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(itemId);
        parcel.writeString(itemName);
        parcel.writeString(itemDescription);
        parcel.writeString(calories);
        parcel.writeString(mainIngredients);
        parcel.writeInt(active?1:0);
    }
}
