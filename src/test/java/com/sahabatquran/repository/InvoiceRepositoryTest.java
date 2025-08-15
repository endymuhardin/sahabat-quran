package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByStudentAndStatus() {
        User user = new User();
        user.setUsername("fulan.student");
        user.setPassword("password");
        user.setRole(UserRole.STUDENT);
        user.setEmail("fulan.student@sahabatquran.id");
        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        studentRepository.save(student);

        Invoice invoice1 = new Invoice();
        invoice1.setStudent(student);
        invoice1.setAmount(new BigDecimal("100.00"));
        invoice1.setStatus(InvoiceStatus.PENDING);
        invoice1.setType(InvoiceType.TUITION);
        invoiceRepository.save(invoice1);

        Invoice invoice2 = new Invoice();
        invoice2.setStudent(student);
        invoice2.setAmount(new BigDecimal("200.00"));
        invoice2.setStatus(InvoiceStatus.PAID);
        invoice2.setType(InvoiceType.EVENT);
        invoiceRepository.save(invoice2);

        List<Invoice> found = invoiceRepository.findByStudentAndStatus(student, InvoiceStatus.PENDING);
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getStatus()).isEqualTo(InvoiceStatus.PENDING);
    }
}
