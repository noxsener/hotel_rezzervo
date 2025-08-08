package com.interestingdomain.hotelrezzervo.hotelservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ROOM ", indexes = {
        @Index(columnList = "CAPACITY"),
        @Index(columnList = "PRICE_PER_NIGHT")
})
@Data
@EqualsAndHashCode(of = {"id"})
@ToString
public class Room {

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;
    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    ;
    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOTEL_ID", nullable = false)
    private Hotel hotel;

    @Column(name = "ROOM_NUMBER")
    private Integer roomNumber;

    @Column(name = "CAPACITY")
    private Integer capacity;

    @Column(name = "PRICE_PER_NIGHT")
    private BigDecimal pricePerNight;
    ;
}
