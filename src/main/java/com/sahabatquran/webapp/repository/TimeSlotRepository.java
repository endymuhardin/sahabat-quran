package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.TimeSlot;
import com.sahabatquran.webapp.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {
    List<TimeSlot> findBySessionAndDayOfWeekIn(Session session, List<TimeSlot.DayOfWeek> days);
    Optional<TimeSlot> findBySessionAndDayOfWeek(Session session, TimeSlot.DayOfWeek day);
    List<TimeSlot> findBySession(Session session);
}
