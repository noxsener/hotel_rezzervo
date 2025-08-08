package com.interestingdomain.hotelrezzervo.hotelservice.repositories;

import com.interestingdomain.hotelrezzervo.hotelservice.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    List<Room> findAll(Pageable pageable);
}
