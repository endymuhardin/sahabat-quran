package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.ClassPreparationChecklist;
import com.sahabatquran.webapp.entity.ClassSession;
import com.sahabatquran.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClassPreparationChecklistRepository extends JpaRepository<ClassPreparationChecklist, UUID> {
    
    List<ClassPreparationChecklist> findBySession(ClassSession session);
    
    List<ClassPreparationChecklist> findByItemCategory(ClassPreparationChecklist.ItemCategory itemCategory);
    
    List<ClassPreparationChecklist> findByIsCompleted(Boolean isCompleted);
    
    List<ClassPreparationChecklist> findByCompletedBy(User completedBy);
    
    @Query("SELECT cpc FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.id = :sessionId ORDER BY cpc.displayOrder, cpc.checklistItem")
    List<ClassPreparationChecklist> findBySessionOrderByDisplayOrder(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT cpc FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.id = :sessionId AND cpc.isCompleted = false " +
           "ORDER BY cpc.displayOrder")
    List<ClassPreparationChecklist> findIncompleteItemsBySession(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT cpc FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.id = :sessionId AND cpc.itemCategory = :category " +
           "ORDER BY cpc.displayOrder")
    List<ClassPreparationChecklist> findBySessionAndCategory(
            @Param("sessionId") UUID sessionId,
            @Param("category") ClassPreparationChecklist.ItemCategory category);
    
    @Query("SELECT COUNT(cpc) FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.id = :sessionId AND cpc.isCompleted = true")
    Long countCompletedItemsBySession(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT COUNT(cpc) FROM ClassPreparationChecklist cpc WHERE cpc.session.id = :sessionId")
    Long countTotalItemsBySession(@Param("sessionId") UUID sessionId);
    
    @Query("SELECT cpc FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.classGroup.id = :classId")
    List<ClassPreparationChecklist> findByClassId(@Param("classId") UUID classId);
    
    @Query("SELECT cpc FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.instructor.id = :instructorId AND cpc.isCompleted = false")
    List<ClassPreparationChecklist> findIncompleteItemsByInstructor(@Param("instructorId") UUID instructorId);
    
    @Query("SELECT cpc.itemCategory, COUNT(cpc) as totalItems, " +
           "SUM(CASE WHEN cpc.isCompleted = true THEN 1 ELSE 0 END) as completedItems " +
           "FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.classGroup.term.id = :termId " +
           "GROUP BY cpc.itemCategory")
    List<Object[]> findPreparationProgressByCategory(@Param("termId") UUID termId);
    
    @Query("SELECT cpc.session.instructor, " +
           "COUNT(cpc) as totalItems, " +
           "SUM(CASE WHEN cpc.isCompleted = true THEN 1 ELSE 0 END) as completedItems, " +
           "(SUM(CASE WHEN cpc.isCompleted = true THEN 1 ELSE 0 END) * 100.0 / COUNT(cpc)) as completionPercentage " +
           "FROM ClassPreparationChecklist cpc " +
           "WHERE cpc.session.classGroup.term.id = :termId " +
           "GROUP BY cpc.session.instructor")
    List<Object[]> findInstructorPreparationProgress(@Param("termId") UUID termId);
}