package com.interestingdomain.hotelrezzervo.reservationservice.repositories;

import java.time.LocalDateTime;

public interface ReservationRepositoryCustom {
    boolean doesOverlappingReservationExist(Integer hotelId, Integer roomId, LocalDateTime checkOut, LocalDateTime checkIn);
}
