package com.interestingdomain.hotelrezzervo.hotelservice.controller;

import com.interestingdomain.hotelrezzervo.hotelservice.converter.EntityMapper;
import com.interestingdomain.hotelrezzervo.hotelservice.dto.RoomDto;
import com.interestingdomain.hotelrezzervo.hotelservice.entity.Room;
import com.interestingdomain.hotelrezzervo.hotelservice.exception.NotFound;
import com.interestingdomain.hotelrezzervo.hotelservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("room")
@Slf4j
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service;

    @GetMapping
    public ResponseEntity<List<RoomDto>> getRooms(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable).stream().map(EntityMapper.INSTANCE::map).toList());
    }

    @GetMapping("hotel/{hotelId}")
    public ResponseEntity<List<RoomDto>> getHotelRooms(@PathVariable Long hotelId, Pageable pageable) {
        return ResponseEntity.ok(service.findByHotel(hotelId,pageable).stream().map(EntityMapper.INSTANCE::map).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable Long id) {
        return service.findById(id).map(room -> ResponseEntity.ok().body(EntityMapper.INSTANCE.map(room))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoomDto> saveRoom(@RequestBody RoomDto room) {
        Room entity = EntityMapper.INSTANCE.map(room);
        return new ResponseEntity<>(EntityMapper.INSTANCE.map(service.save(entity)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody RoomDto room) throws NotFound {
        Room entity = EntityMapper.INSTANCE.map(room);
        return new ResponseEntity<>(EntityMapper.INSTANCE.map(service.update(id, entity)), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteRoom(@PathVariable Long id) throws NotFound {
        return new ResponseEntity<>(service.deleteById(id), HttpStatus.NO_CONTENT);
    }
}
