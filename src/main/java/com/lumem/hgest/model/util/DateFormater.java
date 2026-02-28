package com.lumem.hgest.model.util;

import java.time.LocalDate;

public class DateFormater {

    public static String brazilianFormat(LocalDate localDate){
        return localDate.getDayOfMonth() +
                "/" +
                localDate.getMonthValue() +
                "/" +
                localDate.getYear();
    }
}
