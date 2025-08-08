package com.interestingdomain.hotelrezzervo.reservationservice.converter;

import com.interestingdomain.hotelrezzervo.reservationservice.dto.ReservationDto;
import com.interestingdomain.hotelrezzervo.reservationservice.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface EntityMapper {
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    Reservation map(ReservationDto value);

    ReservationDto map(Reservation value);
}
