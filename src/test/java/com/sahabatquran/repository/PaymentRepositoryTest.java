package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindPayment() {
        User studentUser = new User();
        studentUser.setUsername("fulan.student");
        studentUser.setPassword("password");
        studentUser.setRole(UserRole.STUDENT);
        studentUser.setEmail("fulan.student@sahabatquran.id");
        userRepository.save(studentUser);

        Student student = new Student();
        student.setUser(studentUser);
        studentRepository.saveAndFlush(student);

        Invoice invoice = new Invoice();
        invoice.setStudent(student);
        invoice.setAmount(new BigDecimal("150000.00"));
        invoice.setStatus(InvoiceStatus.PENDING);
        invoice.setType(InvoiceType.TUITION);
        invoiceRepository.saveAndFlush(invoice);

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(new BigDecimal("150000.00"));
        payment.setMethod(PaymentMethod.VA);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.saveAndFlush(payment);

        Payment found = paymentRepository.findById(payment.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getMethod()).isEqualTo(PaymentMethod.VA);
    }
}
