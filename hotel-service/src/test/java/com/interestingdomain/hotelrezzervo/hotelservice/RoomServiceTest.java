package com.interestingdomain.hotelrezzervo.hotelservice;

import com.interestingdomain.hotelrezzervo.hotelservice.entity.Hotel;
import com.interestingdomain.hotelrezzervo.hotelservice.entity.Room;
import com.interestingdomain.hotelrezzervo.hotelservice.exception.NotFound;
import com.interestingdomain.hotelrezzervo.hotelservice.repositories.HotelRepository;
import com.interestingdomain.hotelrezzervo.hotelservice.repositories.RoomRepository;
import com.interestingdomain.hotelrezzervo.hotelservice.service.HotelService;
import com.interestingdomain.hotelrezzervo.hotelservice.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");

        room = new Room();
        room.setId(101L);
        room.setRoomNumber(101);
        room.setHotel(hotel);
        room.setCapacity(2);
        room.setPricePerNight(new BigDecimal("150.00"));
    }

    @Test
    void testSave() {
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room savedRoom = roomService.save(new Room());

        assertNotNull(savedRoom);
        assertEquals(101, savedRoom.getRoomNumber());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testUpdate_Success() throws NotFound {
        Room updatedDetails = new Room();
        updatedDetails.setCapacity(3);
        updatedDetails.setPricePerNight(new BigDecimal("200.00"));
        updatedDetails.setRoomNumber(101);

        when(roomRepository.findById(101L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Room result = roomService.update(101L, updatedDetails);

        assertNotNull(result);
        assertEquals(3, result.getCapacity());
        assertEquals(new BigDecimal("200.00"), result.getPricePerNight());
        assertEquals(101, result.getRoomNumber()); // Unchanged property
        verify(roomRepository, times(1)).findById(101L);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testUpdate_NotFound() {
        Room updatedDetails = new Room();
        updatedDetails.setCapacity(3);

        when(roomRepository.findById(102L)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> roomService.update(102L, updatedDetails));

        verify(roomRepository, times(1)).findById(102L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void testFindById_Found() {
        when(roomRepository.findById(101L)).thenReturn(Optional.of(room));

        Optional<Room> result = roomService.findById(101L);

        assertTrue(result.isPresent());
        assertEquals(101, result.get().getRoomNumber());
        verify(roomRepository, times(1)).findById(101L);
    }

    @Test
    void testFindById_NotFound() {
        when(roomRepository.findById(102L)).thenReturn(Optional.empty());

        Optional<Room> result = roomService.findById(102L);

        assertFalse(result.isPresent());
        verify(roomRepository, times(1)).findById(102L);
    }

    @Test
    void testDeleteById_Success() throws NotFound {
        when(roomRepository.existsById(101L)).thenReturn(true).thenReturn(false);
        doNothing().when(roomRepository).deleteById(101L);

        Boolean result = roomService.deleteById(101L);

        assertTrue(result);
        verify(roomRepository, times(2)).existsById(101L);
        verify(roomRepository, times(1)).deleteById(101L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(roomRepository.existsById(102L)).thenReturn(false);

        assertThrows(NotFound.class, () -> roomService.deleteById(102L));

        verify(roomRepository, times(1)).existsById(102L);
        verify(roomRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 5);
        when(roomRepository.findAll(pageable)).thenReturn(Collections.singletonList(room));

        List<Room> result = roomService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101, result.get(0).getRoomNumber());
        verify(roomRepository, times(1)).findAll(pageable);
    }
}
