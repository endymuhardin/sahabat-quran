package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EventAttendanceRepositoryTest {

    @Autowired
    private EventAttendanceRepository eventAttendanceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveAndFindEventAttendance() {
        Event event = new Event();
        event.setName("Kajian Subuh");
        eventRepository.saveAndFlush(event);

        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        User user = new User();
        user.setUsername("admin");
        user.setRole(role);
        user.setEmail("admin@sahabatquran.id");

        UserPassword userPassword = new UserPassword();
        userPassword.setPassword("password");
        userPassword.setUser(user);
        user.setPassword(userPassword);

        userRepository.save(user);

        EventAttendance eventAttendance = new EventAttendance();
        eventAttendance.setEvent(event);
        eventAttendance.setUser(user);
        eventAttendance.setPresent(true);
        eventAttendanceRepository.saveAndFlush(eventAttendance);

        EventAttendance found = eventAttendanceRepository.findById(eventAttendance.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.isPresent()).isTrue();
    }
}
