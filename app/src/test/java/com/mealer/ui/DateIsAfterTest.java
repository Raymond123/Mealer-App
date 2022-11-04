package com.mealer.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateIsAfterTest {

    @Test
    public void dateIsAfter() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Date localDate = new java.sql.Date(System.currentTimeMillis());

        String strDateBefore = "2021-11-04";
        String strDateAfter = "2023-01-10";

        Date dateBefore = simpleDateFormat.parse(strDateBefore);
        Date dateAfter = simpleDateFormat.parse(strDateAfter);

        assertTrue(localDate.before(dateAfter));
        assertTrue(localDate.after(dateBefore));
        assertFalse(localDate.after(dateAfter));
        assertFalse(localDate.before(dateBefore));
    }

}
