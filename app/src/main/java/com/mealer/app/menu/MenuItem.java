package com.mealer.app.menu;

public class MenuItem {

    private String itemName;
    private String itemDescription;
    private String calories;
    private String[] mainIngredients;
    private boolean active;

    public MenuItem(String itemName, String itemDescription,
                    String calories, String[] mainIngredients, boolean active){
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

    public String[] getMainIngredients() {
        return mainIngredients;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
