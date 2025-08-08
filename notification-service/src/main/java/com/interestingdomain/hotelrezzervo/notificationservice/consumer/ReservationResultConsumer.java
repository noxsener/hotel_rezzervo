package com.interestingdomain.hotelrezzervo.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interestingdomain.hotelrezzervo.notificationservice.dto.ReservationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationResultConsumer {

    private final static String RESERVATION_SUCCESS = "reservation.successful";
    private final static String RESERVATION_FAIL = "reservation.failed";

    private final InformService informService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = RESERVATION_SUCCESS)
    public void handleReservationSuccessRequest(String eventJson) throws JsonProcessingException {
        ReservationDto event = objectMapper.readValue(eventJson, ReservationDto.class);
        informService.sendEmail(event);
    }

    @KafkaListener(topics = RESERVATION_FAIL)
    public void handleReservationFailRequest(String eventJson) throws JsonProcessingException {
        ReservationDto event = objectMapper.readValue(eventJson, ReservationDto.class);
        informService.sendEmail(event);
    }
}
