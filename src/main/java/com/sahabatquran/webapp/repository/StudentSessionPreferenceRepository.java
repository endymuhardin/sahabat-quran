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
    
    List<StudentSessionPreference> findBySessionIdOrderByPreferencePriority(UUID sessionId);
    
    @Query("SELECT ssp FROM StudentSessionPreference ssp WHERE ssp.registration.id = :registrationId AND ssp.preferencePriority = :priority")
    StudentSessionPreference findByRegistrationIdAndPriority(@Param("registrationId") UUID registrationId, @Param("priority") Integer priority);
    
    @Query("SELECT COUNT(ssp) FROM StudentSessionPreference ssp WHERE ssp.session.id = :sessionId AND ssp.preferencePriority = 1")
    long countFirstPreferenceBySession(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT COUNT(ssp) FROM StudentSessionPreference ssp WHERE ssp.session.id = :sessionId")
    long countAllPreferencesBySession(@Param("sessionId") UUID sessionId);
    
    void deleteByRegistrationId(UUID registrationId);
}