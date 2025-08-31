package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.SessionMaterial;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionMaterialRepository extends JpaRepository<SessionMaterial, UUID> {
    
    List<SessionMaterial> findBySession(ClassSession session);
    
    List<SessionMaterial> findByMaterialType(SessionMaterial.MaterialType materialType);
    
    List<SessionMaterial> findByUploadedBy(User uploadedBy);
    
    List<SessionMaterial> findByIsSharedWithStudents(Boolean isSharedWithStudents);
    
    @Query("SELECT sm FROM SessionMaterial sm WHERE sm.session.id = :sessionId ORDER BY sm.uploadDate DESC")
    List<SessionMaterial> findBySessionOrderByUploadDate(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT sm FROM SessionMaterial sm " +
           "WHERE sm.session.id = :sessionId AND sm.materialType = :materialType")
    List<SessionMaterial> findBySessionAndMaterialType(
            @Param("sessionId") UUID sessionId,
            @Param("materialType") SessionMaterial.MaterialType materialType);
    
    @Query("SELECT sm FROM SessionMaterial sm " +
           "WHERE sm.session.classGroup.id = :classId")
    List<SessionMaterial> findByClassId(@Param("classId") UUID classId);
    
    @Query("SELECT sm FROM SessionMaterial sm " +
           "WHERE sm.session.classGroup.id = :classId AND sm.isSharedWithStudents = true")
    List<SessionMaterial> findSharedMaterialsByClassId(@Param("classId") UUID classId);
    
    @Query("SELECT sm FROM SessionMaterial sm " +
           "WHERE sm.session.instructor.id = :instructorId")
    List<SessionMaterial> findByInstructorId(@Param("instructorId") UUID instructorId);
    
    @Query("SELECT sm FROM SessionMaterial sm " +
           "WHERE sm.uploadDate BETWEEN :startDate AND :endDate")
    List<SessionMaterial> findByUploadDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sm.materialType, COUNT(sm) FROM SessionMaterial sm " +
           "WHERE sm.session.classGroup.term.id = :termId " +
           "GROUP BY sm.materialType ORDER BY COUNT(sm) DESC")
    List<Object[]> findMaterialTypeCountsByTerm(@Param("termId") UUID termId);
    
    @Query("SELECT sm FROM SessionMaterial sm " +
           "WHERE sm.materialTitle LIKE %:title%")
    List<SessionMaterial> findByMaterialTitleContaining(@Param("title") String title);
    
    @Query("SELECT SUM(sm.fileSize) FROM SessionMaterial sm WHERE sm.session.classGroup.term.id = :termId")
    Long getTotalFileSizeByTerm(@Param("termId") UUID termId);
}