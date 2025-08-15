package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ClassSessionRepositoryTest {

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindClassSession() {
        User user = new User();
        user.setUsername("ust.fulan");
        user.setPassword("password");
        user.setRole(UserRole.TEACHER);
        user.setEmail("ust.fulan@sahabatquran.id");
        userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacherRepository.saveAndFlush(teacher);

        Classroom classroom = new Classroom();
        classroom.setName("Kelas Sore");
        classroomRepository.saveAndFlush(classroom);

        ClassSchedule schedule = new ClassSchedule();
        schedule.setTeacher(teacher);
        schedule.setClassroom(classroom);
        classScheduleRepository.saveAndFlush(schedule);

        ClassSession session = new ClassSession();
        session.setClassSchedule(schedule);
        session.setSessionDate(LocalDate.now());
        session.setStatus("Selesai");
        classSessionRepository.saveAndFlush(session);

        ClassSession found = classSessionRepository.findById(session.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getStatus()).isEqualTo("Selesai");
    }
}
