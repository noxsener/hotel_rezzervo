package com.interestingdomain.hotelrezzervo.reservationservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interestingdomain.hotelrezzervo.reservationservice.converter.EntityMapper;
import com.interestingdomain.hotelrezzervo.reservationservice.dto.ReservationDto;
import com.interestingdomain.hotelrezzervo.reservationservice.entity.Reservation;
import com.interestingdomain.hotelrezzervo.reservationservice.enums.ReservationStatus;
import com.interestingdomain.hotelrezzervo.reservationservice.exception.BadRequest;
import com.interestingdomain.hotelrezzervo.reservationservice.exception.CommonException;
import com.interestingdomain.hotelrezzervo.reservationservice.repositories.ReservationRepository;
import com.interestingdomain.hotelrezzervo.reservationservice.service.RedisLockService;
import com.interestingdomain.hotelrezzervo.reservationservice.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private RedisLockService redisLockService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private ReservationDto reservationDto;
    private String reservationDtoJson;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setHotelId(1);
        reservation.setRoomId(101);
        reservation.setGuestName("John Doe");
        reservation.setCheckInDate(LocalDateTime.now().plusDays(1));
        reservation.setCheckOutDate(LocalDateTime.now().plusDays(3));
        reservation.setReservationStatus(ReservationStatus.PENDING);

        reservationDto = EntityMapper.INSTANCE.map(reservation);
        reservationDtoJson = objectMapper.writeValueAsString(reservationDto);
    }

    @Test
    void testSave_Success() throws BadRequest, JsonProcessingException {
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result = reservationService.save(new Reservation());

        assertEquals(ReservationStatus.PENDING, result.getReservationStatus());
        assertNotNull(result);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(kafkaTemplate, times(1)).send(eq("reservation.initiated"), anyString());
    }

    @Test
    void testSave_KafkaThrowsException() throws JsonProcessingException {
        Reservation newReservation = new Reservation();
        when(reservationRepository.save(newReservation)).thenReturn(reservation);

        // Force ObjectMapper to throw an exception
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());

        assertThrows(BadRequest.class, () -> reservationService.save(newReservation));

        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void testFindById_Found() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        Optional<Reservation> result = reservationService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(reservationRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Reservation> result = reservationService.findById(2L);
        assertFalse(result.isPresent());
        verify(reservationRepository, times(1)).findById(2L);
    }

    @Test
    void testDeleteById_Success() {
        when(reservationRepository.existsById(1L)).thenReturn(true).thenReturn(false);
        doNothing().when(reservationRepository).deleteById(1L);

        Boolean result = reservationService.deleteById(1L);

        assertTrue(result);
        verify(reservationRepository, times(2)).existsById(1L);
        verify(reservationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(reservationRepository.existsById(2L)).thenReturn(false);
        Boolean result = reservationService.deleteById(2L);
        assertFalse(result);
        verify(reservationRepository, times(1)).existsById(2L);
        verify(reservationRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(reservationRepository.findAll(pageable)).thenReturn(Collections.singletonList(reservation));

        List<Reservation> result = reservationService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(reservationRepository, times(1)).findAll(pageable);
    }

    @Test
    void handleReservationInitRequest_LockFails() throws Exception {
        when(redisLockService.tryLock(anyString(), anyLong(), anyLong())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        reservationService.handleReservationInitRequest(reservationDtoJson);

        verify(redisLockService, times(1)).tryLock(anyString(), anyLong(), anyLong());
        verify(kafkaTemplate, times(1)).send(eq("reservation.error"), contains("\"reservationStatus\":\"ERROR\""));
        verify(reservationRepository, never()).doesOverlappingReservationExist(anyInt(), anyInt(), any(), any());
    }

    @Test
    void handleReservationInitRequest_RoomAvailable() throws Exception {
        when(redisLockService.tryLock(anyString(), anyLong(), anyLong())).thenReturn(true);
        when(reservationRepository.doesOverlappingReservationExist(anyInt(), anyInt(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        reservationService.handleReservationInitRequest(reservationDtoJson);

        verify(redisLockService, times(1)).tryLock(anyString(), anyLong(), anyLong());
        verify(reservationRepository, times(1)).doesOverlappingReservationExist(anyInt(), anyInt(), any(), any());
        verify(kafkaTemplate, times(1)).send(eq("reservation.successful"), contains("\"reservationStatus\":\"CONFIRMED\""));
        verify(redisLockService, times(1)).unlock(anyString());
    }

    @Test
    void handleReservationInitRequest_RoomNotAvailable() throws Exception {
        when(redisLockService.tryLock(anyString(), anyLong(), anyLong())).thenReturn(true);
        when(reservationRepository.doesOverlappingReservationExist(anyInt(), anyInt(), any(), any())).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        reservationService.handleReservationInitRequest(reservationDtoJson);

        verify(redisLockService, times(1)).tryLock(anyString(), anyLong(), anyLong());
        verify(reservationRepository, times(1)).doesOverlappingReservationExist(anyInt(), anyInt(), any(), any());
        verify(kafkaTemplate, times(1)).send(eq("reservation.failed"), contains("\"reservationStatus\":\"FAILED\""));
        verify(redisLockService, times(1)).unlock(anyString());
    }

    @Test
    void updateStatus_Success() throws CommonException {
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> {
            Reservation saved = inv.getArgument(0);
            assertEquals(ReservationStatus.CONFIRMED, saved.getReservationStatus());
            assertEquals("Test Description", saved.getReservationStatusDescription());
            return saved;
        });

        ReservationDto result = reservationService.updateStatus(reservationDto, ReservationStatus.CONFIRMED, "Test Description");

        assertEquals(ReservationStatus.CONFIRMED, result.getReservationStatus());
        assertEquals("Test Description", result.getReservationStatusDescription());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void updateStatus_NullStatus() {
        assertThrows(CommonException.class, () -> reservationService.updateStatus(reservationDto, null, "Test"));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}