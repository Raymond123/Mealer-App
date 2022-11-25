package com.mealer.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mealer.app.menu.MenuItem;

public class MenuItemTest {

    @Test
    public void checkMenuItemNameTest(){

        MenuItem newMeal =  new MenuItem("Cheesy Bread", "Cheese in Bread", "790", "cheese; bread", 20.00,false);

        assertEquals("Check the id of the product", "Cheesy Bread" , newMeal.getItemName());
    }
    @Test
    public void checkMenuItemDescriptionTest(){

        MenuItem newMeal =  new MenuItem("Cheesy Bread", "Cheese in Bread", "790", "cheese; bread", 20.00,false);

        assertEquals("Check the id of the product", "Cheese in Bread" , newMeal.getItemDescription());
    }
    @Test
    public void checkMenuItemCaloriesTest(){

        MenuItem newMeal =  new MenuItem("Cheesy Bread", "Cheese in Bread", "790", "cheese; bread", 20.00, false);

        assertEquals("Check the id of the product", "790" , newMeal.getCalories());
    }
    @Test
    public void checkMenuItemIngredientsTest(){

        MenuItem newMeal =  new MenuItem("Cheesy Bread", "Cheese in Bread", "790", "cheese; bread", 20.00, false);

        assertEquals("Check the id of the product", "cheese; bread" , newMeal.getMainIngredients());
    }
    @Test
    public void checkMenuItemActiveStatusTest(){

        MenuItem newMeal =  new MenuItem("Cheesy Bread", "Cheese in Bread", "790", "cheese; bread", 20.00, false);

        assertEquals("Check the id of the product", false , newMeal.isActive());
    }
}
