package com.interestingdomain.hotelrezzervo.notificationservice.consumer;

import com.interestingdomain.hotelrezzervo.notificationservice.dto.ReservationDto;

public interface InformService {
    void sendEmail(ReservationDto reservation);

    void sendNotification(ReservationDto reservation);
}
