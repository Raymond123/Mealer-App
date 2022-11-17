package com.mealer.app.menu;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
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

    public Menu(Map<String, Object> menu){
        this.menu = (HashMap<String, Object>) menu;
        this.activeMenu = (HashMap<String, MenuItem>) menu.get("active");
        this.inactiveMenu = (HashMap<String, MenuItem>) menu.get("inactive");
        //this.dbRef = dbRef;
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

    // if active menu null, create new one (will be null if empty in the db)
    private void createActiveMenu(){
        this.activeMenu = new HashMap<>();
        this.menu.put("active", this.activeMenu);
    }

    // if inactive menu null, create new one (will be null if empty in the db)
    private void createInactiveMenu(){
        this.inactiveMenu = new HashMap<>();
        this.menu.put("inactive", this.inactiveMenu);
    }

    /**
     * Moves a MenuItem from prev Map to next Map
     * @param prev previous Map to remove item from
     * @param next new Map to add item to
     * @param item the item to switch between maps
     * @throws NullPointerException throws if either prev or next is null
     */
    private void swapMenu(HashMap<String, MenuItem> prev,
                          HashMap<String, MenuItem> next,
                          MenuItem item) throws NullPointerException{
        prev.remove(item.itemId);
        next.put(item.itemId, item);
        item.setActive((!item.isActive()));
    }

    /**
     * public method to swap the map that item is in
     * @param item the item to swap maps
     */
    public void moveItem(MenuItem item){
        try {
            if (item.isActive()) {
                // if item is active then it is in the activeMenu, so swap to inactiveMenu
                swapMenu(activeMenu, inactiveMenu, item);
            } else {
                // else item is in the inactiveMenu, so swap to activeMenu
                swapMenu(inactiveMenu, activeMenu, item);
            }
        } catch(NullPointerException npe){
            // catches the NullPointerException thrown by swapMenu,
            // then checks which Map is null and creates it
            if(this.inactiveMenu==null)
                createInactiveMenu();
            if(this.activeMenu==null)
                createActiveMenu();
            moveItem(item);
        }
    }

    /**
     * private method to insert an item into a MAp
     * @param item item to insert
     * @param map Map to insert item into
     * @throws NullPointerException throws if the map is null
     */
    private void insertItemToMap(MenuItem item,
                                 HashMap<String, MenuItem> map) throws NullPointerException{
        map.put(item.itemId, item);
        updateMenu();
    }

    /**
     * add the item to the Menu, checks whether the item is active or not and
     * adds it to the correct Map
     * @param item the item to add to the menu
     */
    public void addNewMenuItem(MenuItem item){
        if(item.isActive()) {
            try {
                insertItemToMap(item, this.activeMenu);
            }catch(NullPointerException npe){
                // if activeMenu null create and insert
                createActiveMenu();
                insertItemToMap(item, this.activeMenu);
            }
        }
        else {
            try {
                insertItemToMap(item, this.inactiveMenu);
            }catch(NullPointerException npe){
                // if inactiveMenu null create and insert
                createInactiveMenu();
                insertItemToMap(item, this.inactiveMenu);
            }
        }
    }

    /**
     * checks if item is inactive and removes only true.
     * app doesn't allow click of delete button if item is active,
     * so this is just kind of a failsafe
     * @param item item to remove
     */
    public void removeMenuItem(MenuItem item){
        if(!item.isActive()) {
            try {
                inactiveMenu.remove(item.itemId);
            }catch(NullPointerException ignore){} // if inactiveMenu empty ignore
            updateMenu();
        }
    }

    /**
     * update the menu in the db
     */
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
        System.out.println(dbRef);
        parcel.writeSerializable(this.menu);
        parcel.writeSerializable(this.activeMenu);
        parcel.writeSerializable(this.inactiveMenu);
//        parcel.writeValue(this.dbRef);
    }
}
