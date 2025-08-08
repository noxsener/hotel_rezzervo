package com.interestingdomain.hotelrezzervo.hotelservice;

import com.interestingdomain.hotelrezzervo.hotelservice.entity.Hotel;
import com.interestingdomain.hotelrezzervo.hotelservice.exception.NotFound;
import com.interestingdomain.hotelrezzervo.hotelservice.repositories.HotelRepository;
import com.interestingdomain.hotelrezzervo.hotelservice.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAddress("123 Test St");
        hotel.setStarRating(5);
    }

    @Test
    void testSave() {
        when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel);

        Hotel savedHotel = hotelService.save(new Hotel());

        assertNotNull(savedHotel);
        assertEquals("Test Hotel", savedHotel.getName());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testUpdate_Success() throws NotFound {
        Hotel updatedDetails = new Hotel();
        updatedDetails.setName("Updated Hotel Name");

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        updatedDetails.setStarRating(5);
        Hotel result = hotelService.update(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Hotel Name", result.getName());
        assertEquals(5, result.getStarRating()); // Unchanged property
        verify(hotelRepository, times(1)).findById(1L);
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void testUpdate_NotFound() {
        Hotel updatedDetails = new Hotel();
        updatedDetails.setName("Updated Hotel Name");

        when(hotelRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFound.class, () -> hotelService.update(2L, updatedDetails));

        verify(hotelRepository, times(1)).findById(2L);
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void testFindById_Found() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        Optional<Hotel> result = hotelService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Hotel", result.get().getName());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(hotelRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Hotel> result = hotelService.findById(2L);

        assertFalse(result.isPresent());
        verify(hotelRepository, times(1)).findById(2L);
    }

    @Test
    void testDeleteById_Success() throws NotFound {
        when(hotelRepository.existsById(1L)).thenReturn(true).thenReturn(false);
        doNothing().when(hotelRepository).deleteById(1L);

        Boolean result = hotelService.deleteById(1L);

        assertTrue(result);
        verify(hotelRepository, times(2)).existsById(1L);
        verify(hotelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(hotelRepository.existsById(2L)).thenReturn(false);

        assertThrows(NotFound.class, () -> hotelService.deleteById(2L));

        verify(hotelRepository, times(1)).existsById(2L);
        verify(hotelRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        when(hotelRepository.findAll(pageable)).thenReturn(Collections.singletonList(hotel));

        List<Hotel> result = hotelService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getName());
        verify(hotelRepository, times(1)).findAll(pageable);
    }
}