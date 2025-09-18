package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.StudentSessionPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentSessionPreferenceRepository extends JpaRepository<StudentSessionPreference, UUID> {
    
    List<StudentSessionPreference> findByRegistrationIdOrderByPreferencePriority(UUID registrationId);
    // TimeSlot-based lookup, replacing session-based
    List<StudentSessionPreference> findByTimeSlotIdOrderByPreferencePriority(UUID timeSlotId);
    
    @Query("SELECT ssp FROM StudentSessionPreference ssp WHERE ssp.registration.id = :registrationId AND ssp.preferencePriority = :priority")
    StudentSessionPreference findByRegistrationIdAndPriority(@Param("registrationId") UUID registrationId, @Param("priority") Integer priority);
    
    @Query("SELECT COUNT(ssp) FROM StudentSessionPreference ssp WHERE ssp.timeSlot.id = :timeSlotId AND ssp.preferencePriority = 1")
    long countFirstPreferenceByTimeSlot(@Param("timeSlotId") UUID timeSlotId);
    
    @Query("SELECT COUNT(ssp) FROM StudentSessionPreference ssp WHERE ssp.timeSlot.id = :timeSlotId")
    long countAllPreferencesByTimeSlot(@Param("timeSlotId") UUID timeSlotId);
    
    void deleteByRegistrationId(UUID registrationId);
}