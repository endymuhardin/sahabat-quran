package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ClassScheduleRepositoryTest {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindClassSchedule() {
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
        classroom.setName("Kelas Pagi");
        classroomRepository.saveAndFlush(classroom);

        ClassSchedule schedule = new ClassSchedule();
        schedule.setTeacher(teacher);
        schedule.setClassroom(classroom);
        schedule.setDayOfWeek("Senin");
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(10, 0));
        classScheduleRepository.saveAndFlush(schedule);

        ClassSchedule found = classScheduleRepository.findById(schedule.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getDayOfWeek()).isEqualTo("Senin");
    }
}
