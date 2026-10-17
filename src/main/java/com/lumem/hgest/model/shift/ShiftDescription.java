package com.lumem.hgest.model.shift;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

// ShiftDTO plus the OS number and title of its service; serialized flat (all ShiftDTO fields + serviceOs + serviceTitle)
public record ShiftDescription(
        @JsonUnwrapped ShiftDTO shift,
        long serviceOs,
        String serviceTitle
) {
    public ShiftDescription(Shift shift){
        this(
                new ShiftDTO(shift),
                shift.getService().getOs(),
                shift.getService().getTitle()
        );
    }
}
