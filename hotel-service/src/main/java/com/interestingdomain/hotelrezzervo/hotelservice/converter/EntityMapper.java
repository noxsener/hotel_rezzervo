package com.interestingdomain.hotelrezzervo.hotelservice.converter;

import com.interestingdomain.hotelrezzervo.hotelservice.dto.HotelDto;
import com.interestingdomain.hotelrezzervo.hotelservice.dto.ReservationDto;
import com.interestingdomain.hotelrezzervo.hotelservice.dto.RoomDto;
import com.interestingdomain.hotelrezzervo.hotelservice.entity.Hotel;
import com.interestingdomain.hotelrezzervo.hotelservice.entity.Reservation;
import com.interestingdomain.hotelrezzervo.hotelservice.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("squid:S1214")
@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EntityMapper {
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    RoomDto map(Room value);

    HotelDto map(Hotel value);

    Room map(RoomDto value);

    Hotel map(HotelDto value);

    Reservation map(ReservationDto value);

    ReservationDto map(Reservation value);
}
