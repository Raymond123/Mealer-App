package com.mealer.ui.ui.menu;

import android.content.Context;
import android.widget.Toast;

import com.mealer.app.menu.MenuItem;

public class ValidateMenu {

    //TODO: validate inputs
    // inputs cant be empty,
    // all fields except calories must only contain a-z
    // everything should be lower case
    // ingredients should all be separated by semicolon (;)
    // description can contain these characters: ()-;,.![]:
     private MenuItem menuItem;

    public ValidateMenu(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    protected boolean validateItemName(Context context) {

        if (menuItem.getItemName().equals("")) {
            if (context != null) {
                Toast.makeText(context, "Item Name cannot be empty!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;

    }

    protected boolean validateCalories(Context context) {
        if (menuItem.getCalories().equals("")) {
            if (context != null) {
                Toast.makeText(context, "Calories cannot be empty!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    protected boolean validateDescription(Context context) {
        if (menuItem.getItemDescription().equals("")) {
            if (context != null) {
                Toast.makeText(context, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

     protected boolean validateIngredients(Context context) {
        if (menuItem.getMainIngredients().equals("")) {
            if(context != null) {
                Toast.makeText(context, "Ingredients cannot be empty!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    public boolean validateAll(Context context){
        if(validateItemName(context) && validateIngredients(context) && validateCalories(context) && validateDescription(context)){
            return true;
        }
        return false;
    }

}
