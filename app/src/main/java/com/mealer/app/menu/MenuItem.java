package com.mealer.app.menu;

import com.google.firebase.database.Exclude;

import java.util.UUID;

public class MenuItem {

    protected final String itemId = UUID.randomUUID().toString();

    private String itemName;
    private String itemDescription;
    private String calories;
    private String mainIngredients;
    private boolean active;

    public MenuItem(String itemName, String itemDescription,
                    String calories, String mainIngredients, boolean active){
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.calories = calories;
        this.mainIngredients = mainIngredients;
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
    public void setActive(boolean active) {
        this.active = active;
    }

    @Exclude
    public boolean isActive() {
        return active;
    }
}
