package com.interestingdomain.hotelrezzervo.hotelservice.service;

import com.interestingdomain.hotelrezzervo.hotelservice.entity.Hotel;
import com.interestingdomain.hotelrezzervo.hotelservice.exception.NotFound;
import com.interestingdomain.hotelrezzervo.hotelservice.repositories.HotelRepository;
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
public class HotelService {

    private final HotelRepository hotelRepository;

    public Hotel save(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Hotel update(Long id, Hotel hotel) throws NotFound {
        Optional<Hotel> currentEntityOptinal = hotelRepository.findById(id);
        if (currentEntityOptinal.isEmpty()) {
            throw new NotFound();
        }
        Hotel currentEntity = currentEntityOptinal.get();
        BeanUtils.copyProperties(hotel, currentEntity, "id");
        return hotelRepository.save(currentEntity);
    }

    public Optional<Hotel> findById(Long id) {
        return hotelRepository.findById(id);
    }

    public Boolean deleteById(Long id) throws NotFound {
        boolean isExists = hotelRepository.existsById(id);
        if (!isExists) {
            throw new NotFound();
        }
        hotelRepository.deleteById(id);
        return !hotelRepository.existsById(id);
    }

    public List<Hotel> findAll(Pageable pageable) {
        return hotelRepository.findAll(pageable);
    }
}
