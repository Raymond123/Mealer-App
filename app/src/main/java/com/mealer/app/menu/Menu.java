package com.mealer.app.menu;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu {

    private HashMap<String, Object> menu;
    private HashMap<String, MenuItem> activeMenu;
    private HashMap<String, MenuItem> inactiveMenu;
    private DatabaseReference dbRef;

    public Menu(DatabaseReference dbRef){
        this.menu = new HashMap<>();
        this.activeMenu = new HashMap<>();
        this.inactiveMenu = new HashMap<>();

        menu.put("active", this.activeMenu);
        menu.put("inactive", this.inactiveMenu);

        this.dbRef = dbRef;
    }

    private void swapMenu(HashMap<String, MenuItem> prev,
                          HashMap<String, MenuItem> next,
                          MenuItem item){
        prev.remove(item.itemId);
        next.put(item.itemId, item);
        item.setActive((!item.isActive()));
    }

    public void moveItem(MenuItem item){
        if(item.isActive()){
            swapMenu(activeMenu, inactiveMenu, item);
        }else{
            swapMenu(inactiveMenu, activeMenu, item);
        }
    }

    public void addNewMenuItem(MenuItem item){
        if(item.isActive()) activeMenu.put(item.itemId, item);
        else inactiveMenu.put(item.itemId, item);
        updateMenu();
    }

    public boolean removeMenuItem(MenuItem item){
        if(item.isActive()) return false;
        else{
            inactiveMenu.remove(item.itemId);
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
}
