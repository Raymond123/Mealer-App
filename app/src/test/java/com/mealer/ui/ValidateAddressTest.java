package com.mealer.ui;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValidateAddressTest {

    @Test
    public void validateAddress(){

        boolean test1 = SignUpPage.validateAddress("", null);
        boolean test2 = SignUpPage.validateAddress("Address", null);

        assertFalse(test1);
        assertTrue(test2);

    }

}
