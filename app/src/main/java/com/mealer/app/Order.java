package com.mealer.app;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.mealer.app.menu.MenuItem;

import java.util.UUID;

public class Order implements Parcelable {

    private MenuItem orderItem;
    private String orderId;
    private String orderStatus;

    public Order(){}

    public Order(MenuItem item, String status){
        this.orderId = UUID.randomUUID().toString();
        this.orderStatus = status;
        this.orderItem = item;
    }

    public MenuItem getOrderItem() {
        return orderItem;
    }

    @Exclude
    public String getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void accepted(){
        this.orderStatus = "accepted";
    }

    public void rejected(){
        this.orderStatus = "rejected";
    }

    protected Order(Parcel in) {
        orderItem = in.readParcelable(MenuItem.class.getClassLoader());
        orderId = in.readString();
        orderStatus = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(orderItem, i);
        parcel.writeString(orderId);
        parcel.writeString(orderStatus);
    }
}
