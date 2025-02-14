package com.lumem.hgest.model.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TimeDifference {
    public static Long calc(LocalTime opemTime,LocalDate openDate,
    LocalTime closeTime,LocalDate closeDate) {

        LocalDateTime dateTimeOpen = LocalDateTime.of(openDate, opemTime);
        LocalDateTime dateTimeClose = LocalDateTime.of(closeDate, closeTime);

        return ChronoUnit.MINUTES.between(dateTimeOpen, dateTimeClose);
    }
}
