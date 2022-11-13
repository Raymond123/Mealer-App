package com.mealer.app.menu;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu implements Parcelable {

    private HashMap<String, Object> menu;
    private HashMap<String, MenuItem> activeMenu;
    private HashMap<String, MenuItem> inactiveMenu;
    private DatabaseReference dbRef;
    private String dbRefString;

    public Menu(){

    }

    public Menu(Map<String, Object> menu, DatabaseReference dbRef){
        this.menu = (HashMap<String, Object>) menu;
        this.activeMenu = (HashMap<String, MenuItem>) menu.get("active");
        this.inactiveMenu = (HashMap<String, MenuItem>) menu.get("inactive");
        this.dbRef = dbRef;
    }

    protected Menu(Parcel in) {
        this.menu = (HashMap<String, Object>) in.readSerializable();
        this.activeMenu = (HashMap<String, MenuItem>) in.readSerializable();
        this.inactiveMenu = (HashMap<String, MenuItem>) in.readSerializable();
        this.dbRef = (DatabaseReference) in.readValue(ClassLoader.getSystemClassLoader());
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    private void createActiveMenu(){
        this.activeMenu = new HashMap<>();
        this.menu.put("active", this.activeMenu);
    }

    private void createInactiveMenu(){
        this.inactiveMenu = new HashMap<>();
        this.menu.put("inactive", this.inactiveMenu);
    }

    private void swapMenu(HashMap<String, MenuItem> prev,
                          HashMap<String, MenuItem> next,
                          MenuItem item) throws NullPointerException{
        prev.remove(item.itemId);
        next.put(item.itemId, item);
        item.setActive((!item.isActive()));
    }

    public void moveItem(MenuItem item){
        try {
            if (item.isActive()) {
                swapMenu(activeMenu, inactiveMenu, item);
            } else {
                swapMenu(inactiveMenu, activeMenu, item);
            }
        } catch(NullPointerException npe){
            if(this.inactiveMenu==null)
                createInactiveMenu();
            if(this.activeMenu==null)
                createActiveMenu();
            moveItem(item);
        }
    }

    private void insertItemToMap(MenuItem item,
                                 HashMap<String, MenuItem> map) throws NullPointerException{
        map.put(item.itemId, item);
        updateMenu();
    }

    public void addNewMenuItem(MenuItem item){
        if(item.isActive()) {
            try {
                insertItemToMap(item, this.activeMenu);
            }catch(NullPointerException npe){
                createActiveMenu();
                insertItemToMap(item, this.activeMenu);
            }
        }
        else {
            try {
                insertItemToMap(item, this.inactiveMenu);
            }catch(NullPointerException npe){
                createInactiveMenu();
                insertItemToMap(item, this.inactiveMenu);
            }
        }
    }

    public boolean removeMenuItem(MenuItem item){
        if(item.isActive()) return false;
        else{
            try {
                inactiveMenu.remove(item.itemId);
            }catch(NullPointerException ignore){}
            updateMenu();
            return true;
        }
    }

    public void updateMenu(){
        dbRef.updateChildren(menu);
    }

    @Exclude
    public HashMap<String, Object> getMenu() {
        return menu;
    }

    public HashMap<String, MenuItem> getActiveMenu() {
        return activeMenu;
    }

    public HashMap<String, MenuItem> getInactiveMenu() {
        return inactiveMenu;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menu=" + menu +
                ", activeMenu=" + activeMenu +
                ", inactiveMenu=" + inactiveMenu +
                ", dbRef=" + dbRef +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeSerializable(this.menu);
        parcel.writeSerializable(this.activeMenu);
        parcel.writeSerializable(this.inactiveMenu);
        parcel.writeValue(this.dbRef);
    }
}
