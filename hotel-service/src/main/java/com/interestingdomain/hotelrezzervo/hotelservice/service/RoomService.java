package com.interestingdomain.hotelrezzervo.hotelservice.service;

import com.interestingdomain.hotelrezzervo.hotelservice.entity.Room;
import com.interestingdomain.hotelrezzervo.hotelservice.exception.NotFound;
import com.interestingdomain.hotelrezzervo.hotelservice.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public Room update(Long id, Room room) throws NotFound {
        Optional<Room> currentEntityOptinal = roomRepository.findById(id);
        if (currentEntityOptinal.isEmpty()) {
            throw new NotFound();
        }
        Room currentEntity = currentEntityOptinal.get();
        BeanUtils.copyProperties(room, currentEntity, "id");
        return roomRepository.save(currentEntity);
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public Boolean deleteById(Long id) throws NotFound {
        boolean isExists = roomRepository.existsById(id);
        if (!isExists) {
            throw new NotFound();
        }
        roomRepository.deleteById(id);
        return !roomRepository.existsById(id);
    }

    public List<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }
}
