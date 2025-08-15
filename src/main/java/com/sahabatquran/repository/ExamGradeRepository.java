package com.sahabatquran.repository;

import com.sahabatquran.domain.ExamGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamGradeRepository extends JpaRepository<ExamGrade, UUID> {
}
