package com.interestingdomain.hotelrezzervo.reservationservice.repositories;

import com.interestingdomain.hotelrezzervo.reservationservice.entity.Reservation;
import com.interestingdomain.hotelrezzervo.reservationservice.enums.ReservationStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean doesOverlappingReservationExist(Integer hotelId, Integer roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Reservation> reservationRoot = cq.from(Reservation.class);
        cq.select(reservationRoot.get("id"));
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(reservationRoot.get("hotelId"), hotelId));
        predicates.add(cb.equal(reservationRoot.get("roomId"), roomId));
        predicates.add(cb.equal(reservationRoot.get("reservationStatus"), ReservationStatus.CONFIRMED));
        predicates.add(cb.lessThan(reservationRoot.get("checkInDate"), checkOut));
        predicates.add(cb.greaterThan(reservationRoot.get("checkOutDate"), checkIn));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Long> query = entityManager.createQuery(cq);
        query.setMaxResults(1);
        List<Long> row = query.getResultList();
        return !row.isEmpty();
    }
}
