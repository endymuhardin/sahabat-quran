package com.sahabatquran.repository;

import com.sahabatquran.domain.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    public void testSaveAndFindRoom() {
        Room room = new Room();
        room.setName("Room 1");
        room.setCapacity(20);
        roomRepository.saveAndFlush(room);

        Room found = roomRepository.findById(room.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Room 1");
    }
}
