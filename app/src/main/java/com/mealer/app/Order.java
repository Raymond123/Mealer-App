package com.mealer.app;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.mealer.app.menu.MenuItem;

import java.util.HashMap;
import java.util.UUID;

public class Order implements Parcelable {

    private DatabaseReference dbRef;

    private MenuItem orderItem;
    private String orderId;
    private String orderStatus;
    private String orderFrom;
    private int notifications;

    public Order(){}

    public Order(MenuItem item, String status, String from){
        this.orderId = UUID.randomUUID().toString();
        this.orderStatus = status;
        this.orderItem = item;
        this.orderFrom = from;
        this.notifications = 0;
    }

    public MenuItem getOrderItem() {
        return orderItem;
    }

    @Exclude
    public String getOrderId() {
        return orderId;
    }

    public int getNotifications() {
        return notifications;
    }

    public String getOrderFrom() {
        return orderFrom;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void notified(DatabaseReference ref){
        notifications = 1;
        if(ref!=null){
            HashMap<String, Object> update = new HashMap<>();
            update.put("notifications", getNotifications());
            ref.updateChildren(update);
        }
    }

    public void accepted(String user){
        this.orderStatus = "accepted";
        if(user!=null) {
            updateStatus(user);
        }
    }

    public void completed(String user){
        this.orderStatus = "completed";
        if(user!=null) {
            updateStatus(user);
        }
    }

    public void rejected(String user){
        this.orderStatus = "rejected";
        if(user!=null) {
            updateStatus(user);
        }
    }

    public void deleted(String user){
        this.orderStatus = "deleted";
        if(user!=null) {
            updateStatus(user);
        }
    }

    private void updateStatus(String user){
        dbRef = FirebaseDatabase
                .getInstance("https://mealer-app-58f99-default-rtdb.firebaseio.com/")
                .getReference("users");

        HashMap<String, Object> update = new HashMap<>();
        update.put("orderStatus", getOrderStatus());
        dbRef.child(user).child("orders").child(orderId).updateChildren(update);
    }

    protected Order(Parcel in) {
        orderItem = in.readParcelable(MenuItem.class.getClassLoader());
        orderId = in.readString();
        orderStatus = in.readString();
        orderFrom = in.readString();
        notifications = in.readInt();
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
        parcel.writeString(orderFrom);
        parcel.writeInt(notifications);
    }
}
