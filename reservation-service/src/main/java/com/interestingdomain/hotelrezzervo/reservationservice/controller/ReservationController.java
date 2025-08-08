package com.interestingdomain.hotelrezzervo.reservationservice.controller;

import com.interestingdomain.hotelrezzervo.reservationservice.converter.EntityMapper;
import com.interestingdomain.hotelrezzervo.reservationservice.dto.ReservationDto;
import com.interestingdomain.hotelrezzervo.reservationservice.entity.Reservation;
import com.interestingdomain.hotelrezzervo.reservationservice.exception.BadRequest;
import com.interestingdomain.hotelrezzervo.reservationservice.exception.NotFound;
import com.interestingdomain.hotelrezzervo.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reservation")
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;

    @GetMapping
    public ResponseEntity<List<Reservation>> getReservations(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        return service.findById(id).map(a -> ResponseEntity.ok().body(a)).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reservation> saveReservation(@RequestBody ReservationDto hotel) throws BadRequest {
        Reservation entity = EntityMapper.INSTANCE.map(hotel);
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReservation(@PathVariable Long id) throws NotFound {
        return new ResponseEntity<>(service.deleteById(id), HttpStatus.NO_CONTENT);
    }
}
