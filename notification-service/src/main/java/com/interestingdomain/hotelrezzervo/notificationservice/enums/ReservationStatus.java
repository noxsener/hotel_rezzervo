package com.interestingdomain.hotelrezzervo.notificationservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.StringUtils;

public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    FAILED;

    @JsonCreator
    public static ReservationStatus getReservationStatus(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        for (ReservationStatus status : ReservationStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return name();
    }
}