package com.mealer.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mealer.app.menu.MenuItem;
import com.mealer.ui.ui.menu.ValidateMenu;

import org.junit.Test;

public class ValidateMenuTest {

    @Test
    public void validateAllTest(){
        MenuItem activeItem = new MenuItem("test", "test", "test", "test", 20.00, true);
        MenuItem inactiveItem = new MenuItem("test", "test", "test", "test", 20.00, false);
        MenuItem emptyItem = new MenuItem("", "", "", "", 0, false);

        ValidateMenu validateActiveMenu = new ValidateMenu(activeItem, null);
        ValidateMenu validateInactiveMenu = new ValidateMenu(inactiveItem, null);
        ValidateMenu validateEmptyMenu = new ValidateMenu(emptyItem, null);

        boolean active = validateActiveMenu.validateAll();
        boolean inactive = validateInactiveMenu.validateAll();
        boolean empty = validateEmptyMenu.validateAll();

        assertTrue(active);
        assertTrue(inactive);
        assertFalse(empty);
    }
}
