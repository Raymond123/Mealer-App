package com.mealer.ui;

import org.junit.Test;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddDaysTest {

    @Test
    public void addDaysTest(){
        Date localDate = new java.sql.Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Calendar baseCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        int addOneWeek = 7;
        int addOneMonth = 30;
        int addOneYear = 365;

        try{
            baseCalendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(localDate.toString())));
            calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(localDate.toString())));
        }catch (ParseException parseException){
            parseException.printStackTrace();
        }

        baseCalendar.add(Calendar.WEEK_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, addOneWeek);
        String oneWeekbase = simpleDateFormat.format(baseCalendar.getTime());
        String newDateOneWeek = simpleDateFormat.format(calendar.getTime());

        baseCalendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, addOneMonth);
        String oneMonthbase = simpleDateFormat.format(baseCalendar.getTime());
        String newDateOneMonth = simpleDateFormat.format(calendar.getTime());

        baseCalendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_MONTH, addOneYear);
        String oneYearBase = simpleDateFormat.format(baseCalendar.getTime());
        String newDateOneYear = simpleDateFormat.format(calendar.getTime());

        assertEquals(oneWeekbase, newDateOneWeek);
        assertEquals(oneMonthbase, newDateOneMonth);
        assertEquals(oneYearBase, newDateOneYear);
    }

}
