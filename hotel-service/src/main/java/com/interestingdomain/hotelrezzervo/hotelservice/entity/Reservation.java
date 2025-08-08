package com.interestingdomain.hotelrezzervo.hotelservice.entity;

import com.interestingdomain.hotelrezzervo.hotelservice.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESERVATION ", indexes = {
        @Index(columnList = "HOTEL_ID,ROOM_ID"),
        @Index(columnList = "CHECK_IN_DATE"),
        @Index(columnList = "CHECK_OUT_DATE"),
})
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "hotelId", "roomId"})
public class Reservation {

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;
    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "HOTEL_ID")
    private Integer hotelId;

    @Column(name = "ROOM_ID")
    private Integer roomId;

    @Column(name = "GUEST_NAME")
    private String guestName;

    @Column(name = "CHECK_IN_DATE")
    private LocalDateTime checkInDate;

    @Column(name = "CHECK_OUT_DATE")
    private LocalDateTime checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "RESERVATION_STATUS", nullable = false)
    private ReservationStatus reservationStatus;

    @Column(name = "RESERVATION_STATUS_DESCRIPTION")
    private String reservationStatusDescription;
}
