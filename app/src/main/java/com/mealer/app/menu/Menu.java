package com.mealer.app.menu;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Menu {

    private HashMap<String, Object> menu;
    private ArrayList<MenuItem> activeMenu;
    private ArrayList<MenuItem> inactiveMenu;
    private DatabaseReference dbRef;

    public Menu(DatabaseReference dbRef){
        this.menu = new HashMap<>();
        this.activeMenu = new ArrayList<>();
        this.inactiveMenu = new ArrayList<>();

        menu.put("active", this.activeMenu);
        menu.put("inactive", this.inactiveMenu);

        this.dbRef = dbRef;
    }

    private void swapMenu(List<MenuItem> prev, List<MenuItem> next, MenuItem item){
        prev.remove(item);
        next.add(item);
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
        if(item.isActive()) activeMenu.add(item);
        else inactiveMenu.add(item);
        dbRef.updateChildren(menu);
    }

    public boolean removeMenuItem(MenuItem item){
        if(item.isActive()) return false;
        else{
            inactiveMenu.remove(item);
            dbRef.updateChildren(menu);
            return true;
        }
    }

    public HashMap<String, Object> getMenu() {
        return menu;
    }

    public ArrayList<MenuItem> getActiveMenu() {
        return activeMenu;
    }

    public ArrayList<MenuItem> getInactiveMenu() {
        return inactiveMenu;
    }
}
