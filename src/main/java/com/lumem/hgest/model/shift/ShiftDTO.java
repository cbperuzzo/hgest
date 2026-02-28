package com.lumem.hgest.model.shift;

import com.lumem.hgest.model.service.Service;
import com.lumem.hgest.model.user.StoredUser;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftDTO (
        Long id,
        Long userId,
        Long serviceId,
        String segment,
        LocalTime openTime,
        LocalDate openDate,
        LocalTime closeTime,
        String description,
        String imageSource,
        LocalDate closeDate,
        boolean closed,
        Long totalMinutes
) {
    public ShiftDTO(Shift shift){
        this(
                shift.getId(),
                shift.getStoredUser().getId(),
                shift.getService().getId(),
                shift.getSegment(),
                shift.getOpenTime(),
                shift.getOpenDate(),
                shift.getCloseTime(),
                shift.getDescription(),
                shift.getImageSource(),
                shift.getCloseDate(),
                shift.isClosed(),
                shift.getTotalMinutes()
                );
    }

}
