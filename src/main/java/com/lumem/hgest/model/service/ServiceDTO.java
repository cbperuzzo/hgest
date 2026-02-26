package com.lumem.hgest.model.service;

import java.time.LocalDate;
import java.time.LocalTime;

public record ServiceDTO(
        long id,
        long os,
        String title,
        String description,
        LocalDate openDate,
        LocalTime openTime,
        LocalDate closeDate,
        LocalTime closeTime,
        boolean closed
) {

    public ServiceDTO(Service service) {
        this(
                service.getId(),
                service.getOs(),
                service.getTitle(),
                service.getDescription(),
                service.getOpenDate(),
                service.getOpenTime(),
                service.getCloseDate(),
                service.getCloseTime(),
                service.isClosed()
        );
    }
}
