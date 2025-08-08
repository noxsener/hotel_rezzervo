package com.interestingdomain.hotelrezzervo.reservationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interestingdomain.hotelrezzervo.reservationservice.converter.EntityMapper;
import com.interestingdomain.hotelrezzervo.reservationservice.dto.ReservationDto;
import com.interestingdomain.hotelrezzervo.reservationservice.entity.Reservation;
import com.interestingdomain.hotelrezzervo.reservationservice.enums.ReservationStatus;
import com.interestingdomain.hotelrezzervo.reservationservice.exception.BadRequest;
import com.interestingdomain.hotelrezzervo.reservationservice.exception.CommonException;
import com.interestingdomain.hotelrezzervo.reservationservice.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {

    private final static String RESERVATION_INIT = "reservation.initiated";
    private final static String RESERVATION_SUCCESS = "reservation.successful";
    private final static String RESERVATION_FAIL = "reservation.failed";
    private final static String RESERVATION_ERROR = "reservation.error";

    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisLockService redisLockService;
    private final ObjectMapper objectMapper;

    public Reservation save(Reservation reservation) throws BadRequest {
        reservation.setReservationStatus(ReservationStatus.PENDING);
        Reservation entity = reservationRepository.save(reservation);
        ReservationDto dto = EntityMapper.INSTANCE.map(entity);
        try {
            kafkaTemplate.send(RESERVATION_INIT, objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new BadRequest(e);
        }
        return entity;
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public Boolean deleteById(Long id) {
        boolean isExists = reservationRepository.existsById(id);
        if (isExists) {
            reservationRepository.deleteById(id);
            return !reservationRepository.existsById(id);
        }
        return Boolean.FALSE;
    }

    @KafkaListener(topics = RESERVATION_INIT)
    public void handleReservationInitRequest(String eventJson) throws JsonProcessingException {
        ReservationDto event = objectMapper.readValue(eventJson, ReservationDto.class);
        String lockKey = String.format("lock:hotel:room:%d:%d:%s_%s",
                event.getHotelId(),
                event.getRoomId(),
                event.getCheckInDate(),
                event.getCheckOutDate());
        Long lockValue = event.getId();
        long lockTimeout = 30000;
        boolean isLocked = false;
        try {
            isLocked = redisLockService.tryLock(lockKey, lockValue, lockTimeout);
            if (!isLocked) {
                event = updateStatus(event, ReservationStatus.ERROR, "Please try again later.");
                kafkaTemplate.send(RESERVATION_ERROR, objectMapper.writeValueAsString(event));
                return;
            }
            boolean isOverlapping = doesOverlappingReservationExist(event.getHotelId(), event.getRoomId(), event.getCheckInDate(), event.getCheckOutDate());
            if (!isOverlapping) {
                event = updateStatus(event, ReservationStatus.CONFIRMED, "Successfully reserved room.");
                kafkaTemplate.send(RESERVATION_SUCCESS, objectMapper.writeValueAsString(event));
            } else {
                event = updateStatus(event, ReservationStatus.FAILED, "Room has already been reserved.");
                kafkaTemplate.send(RESERVATION_FAIL, objectMapper.writeValueAsString(event));
            }
        } catch (Exception e) {
            try {
                kafkaTemplate.send(RESERVATION_ERROR, objectMapper.writeValueAsString(event));
            } catch (JsonProcessingException ex) {
                log.error("handleReservationInitRequest JsonProcessing failed", ex);
            }
        } finally {
            if (isLocked) {
                redisLockService.unlock(lockKey);
            }
        }
    }

    public ReservationDto updateStatus(ReservationDto reservation, ReservationStatus reservationStatus, String statusDescription) throws CommonException {
        if (reservationStatus == null) {
            throw new CommonException("Invalid reservation status");
        }
        Reservation entity = EntityMapper.INSTANCE.map(reservation);
        entity.setReservationStatus(reservationStatus);
        entity.setReservationStatusDescription(statusDescription);
        entity = reservationRepository.save(entity);
        return EntityMapper.INSTANCE.map(entity);
    }

    private boolean doesOverlappingReservationExist(Integer hotelId, Integer roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        return reservationRepository.doesOverlappingReservationExist(hotelId, roomId, checkIn, checkOut);
    }

    public List<Reservation> findAll(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }
}
