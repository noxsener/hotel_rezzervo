package com.interestingdomain.hotelrezzervo.reservationservice.repositories;


import com.interestingdomain.hotelrezzervo.reservationservice.entity.Reservation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long>, ReservationRepositoryCustom {
    List<Reservation> findAll(Pageable pageable);
}
