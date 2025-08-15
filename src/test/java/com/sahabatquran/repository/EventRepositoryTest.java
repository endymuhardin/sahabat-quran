package com.sahabatquran.repository;

import com.sahabatquran.domain.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void testSaveAndFindEvent() {
        Event event = new Event();
        event.setName("Tabligh Akbar");
        event.setDescription("Event besar tahunan");
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().plusHours(3));
        eventRepository.saveAndFlush(event);

        Event found = eventRepository.findById(event.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Tabligh Akbar");
    }
}
