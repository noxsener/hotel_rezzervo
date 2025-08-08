package com.interestingdomain.hotelrezzervo.hotelservice.controller;

import com.interestingdomain.hotelrezzervo.hotelservice.converter.EntityMapper;
import com.interestingdomain.hotelrezzervo.hotelservice.dto.HotelDto;
import com.interestingdomain.hotelrezzervo.hotelservice.entity.Hotel;
import com.interestingdomain.hotelrezzervo.hotelservice.exception.NotFound;
import com.interestingdomain.hotelrezzervo.hotelservice.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hotel")
@Slf4j
@RequiredArgsConstructor
public class HotelController {

    private final HotelService service;

    @GetMapping
    public ResponseEntity<List<Hotel>> getHotels(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotel(@PathVariable Long id) {
        return service.findById(id).map(a -> ResponseEntity.ok().body(a)).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Hotel> saveHotel(@RequestBody HotelDto hotel) {
        Hotel entity = EntityMapper.INSTANCE.map(hotel);
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody HotelDto hotel) throws NotFound {
        Hotel entity = EntityMapper.INSTANCE.map(hotel);
        return new ResponseEntity<>(service.update(id, entity), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long id) throws NotFound {
        return new ResponseEntity<>(service.deleteById(id), HttpStatus.NO_CONTENT);
    }
}
