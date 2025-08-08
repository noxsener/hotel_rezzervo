package com.interestingdomain.hotelrezzervo.notificationservice.consumer;

import com.interestingdomain.hotelrezzervo.notificationservice.dto.ReservationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmptyInformService implements InformService {
    @Override
    public void sendEmail(ReservationDto reservation) {
        log.warn("InformService sendEmail didn't implemented yet");
    }

    @Override
    public void sendNotification(ReservationDto reservation) {
        log.warn("InformService sendNotification didn't implemented yet");
    }
}
