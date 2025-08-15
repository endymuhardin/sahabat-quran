package com.sahabatquran.repository;

import com.sahabatquran.domain.Invoice;
import com.sahabatquran.domain.InvoiceStatus;
import com.sahabatquran.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByStudentAndStatus(Student student, InvoiceStatus status);
}
