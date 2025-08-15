package com.sahabatquran.repository;

import com.sahabatquran.domain.Classroom;
import com.sahabatquran.domain.Curriculum;
import com.sahabatquran.domain.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ClassroomRepositoryTest {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    public void testSaveAndFindClassroom() {
        Curriculum curriculum = new Curriculum();
        curriculum.setName("Tahsin Lanjutan");
        curriculum.setLevel("Menengah");
        curriculumRepository.saveAndFlush(curriculum);

        Room room = new Room();
        room.setName("Room 2");
        room.setCapacity(15);
        roomRepository.saveAndFlush(room);

        Classroom classroom = new Classroom();
        classroom.setName("Kelas Tahsin A");
        classroom.setCurriculum(curriculum);
        classroom.setRoom(room);
        classroomRepository.saveAndFlush(classroom);

        Classroom found = classroomRepository.findById(classroom.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Kelas Tahsin A");
    }
}
