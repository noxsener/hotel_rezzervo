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
    public ResponseEntity<List<HotelDto>> getHotels(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable).stream().map(EntityMapper.INSTANCE::map).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotel(@PathVariable Long id) {
        return service.findById(id).map(hotel -> ResponseEntity.ok().body(EntityMapper.INSTANCE.map(hotel))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HotelDto> saveHotel(@RequestBody HotelDto hotel) {
        Hotel entity = EntityMapper.INSTANCE.map(hotel);
        return new ResponseEntity<>(EntityMapper.INSTANCE.map(service.save(entity)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long id, @RequestBody HotelDto hotel) throws NotFound {
        Hotel entity = EntityMapper.INSTANCE.map(hotel);
        return new ResponseEntity<>(EntityMapper.INSTANCE.map(service.update(id, entity)), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteHotel(@PathVariable Long id) throws NotFound {
        return new ResponseEntity<>(service.deleteById(id), HttpStatus.NO_CONTENT);
    }
}
