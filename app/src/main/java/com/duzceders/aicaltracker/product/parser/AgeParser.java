package com.duzceders.aicaltracker.product.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AgeParser {

    public int calculateAge(String birthdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
        try {
            Date birthDate = sdf.parse(birthdate);

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);


            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
