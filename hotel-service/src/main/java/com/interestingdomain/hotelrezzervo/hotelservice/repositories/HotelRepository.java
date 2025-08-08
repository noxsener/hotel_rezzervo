package com.interestingdomain.hotelrezzervo.hotelservice.repositories;

import com.interestingdomain.hotelrezzervo.hotelservice.entity.Hotel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends CrudRepository<Hotel, Long> {
    List<Hotel> findAll(Pageable pageable);
}
