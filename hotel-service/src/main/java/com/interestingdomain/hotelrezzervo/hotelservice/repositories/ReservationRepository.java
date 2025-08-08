package com.interestingdomain.hotelrezzervo.hotelservice.repositories;

import com.interestingdomain.hotelrezzervo.hotelservice.entity.Reservation;
import com.interestingdomain.hotelrezzervo.hotelservice.enums.ReservationStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    boolean existsByHotelIdAndRoomIdAndCheckInDateBeforeAndCheckOutDateAfterAndReservationStatusIs(Integer hotelId, Integer roomId, LocalDateTime checkOut, LocalDateTime checkIn, ReservationStatus reservationStatus);
}
