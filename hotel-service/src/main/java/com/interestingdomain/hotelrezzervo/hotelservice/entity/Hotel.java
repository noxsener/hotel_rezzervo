package com.interestingdomain.hotelrezzervo.hotelservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "HOTEL", indexes = {
        @Index(columnList = "STAR_RATING")
})
@Data
@EqualsAndHashCode(of = {"id"})
@ToString
public class Hotel {

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

    @Column(name = "STAR_RATING")
    private Integer starRating;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ADDRESS")
    private String address;
}
