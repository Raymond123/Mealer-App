package com.mealer.ui;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValidateNameTest {

    @Test
    public void validateName(){
        boolean test1 = SignUpPage.validateName("", "lastName", null);
        boolean test2 = SignUpPage.validateName("firstName", "", null);
        boolean test3 = SignUpPage.validateName("", "", null);
        boolean test4 = SignUpPage.validateName("firstName", "lastName", null);

        assertFalse(test1);
        assertFalse(test2);
        assertFalse(test3);
        assertTrue(test4);
    }

}
