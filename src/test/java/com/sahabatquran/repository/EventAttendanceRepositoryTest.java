package com.sahabatquran.repository;

import com.sahabatquran.domain.Event;
import com.sahabatquran.domain.EventAttendance;
import com.sahabatquran.domain.User;
import com.sahabatquran.domain.UserRole;
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

    @Test
    void testSaveAndFindEventAttendance() {
        Event event = new Event();
        event.setName("Kajian Subuh");
        eventRepository.saveAndFlush(event);

        User user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);
        user.setEmail("admin@sahabatquran.id");
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
