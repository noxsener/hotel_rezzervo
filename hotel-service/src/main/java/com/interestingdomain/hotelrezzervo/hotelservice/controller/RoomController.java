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
    public ResponseEntity<List<Room>> getRooms(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("hotel/{hotelId}")
    public ResponseEntity<List<Room>> getHotelRooms(@PathVariable Long hotelId, Pageable pageable) {
        return ResponseEntity.ok(service.findByHotel(hotelId,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        return service.findById(id).map(a -> ResponseEntity.ok().body(a)).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Room> saveRoom(@RequestBody RoomDto room) {
        Room entity = EntityMapper.INSTANCE.map(room);
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody RoomDto room) throws NotFound {
        Room entity = EntityMapper.INSTANCE.map(room);
        return new ResponseEntity<>(service.update(id, entity), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteRoom(@PathVariable Long id) throws NotFound {
        return new ResponseEntity<>(service.deleteById(id), HttpStatus.NO_CONTENT);
    }
}
