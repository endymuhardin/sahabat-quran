package com.sahabatquran.repository;

import com.sahabatquran.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AttendanceRepositoryTest {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

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

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSaveAndFindAttendance() {
        Role studentRole = new Role();
        studentRole.setName("STUDENT");
        roleRepository.save(studentRole);

        User studentUser = new User();
        studentUser.setUsername("fulan.student");
        studentUser.setRole(studentRole);
        studentUser.setEmail("fulan.student@sahabatquran.id");

        UserPassword studentPassword = new UserPassword();
        studentPassword.setPassword("password");
        studentPassword.setUser(studentUser);
        studentUser.setPassword(studentPassword);

        userRepository.save(studentUser);

        Student student = new Student();
        student.setUser(studentUser);
        studentRepository.saveAndFlush(student);

        Role teacherRole = new Role();
        teacherRole.setName("TEACHER");
        roleRepository.save(teacherRole);

        User teacherUser = new User();
        teacherUser.setUsername("ust.fulan");
        teacherUser.setRole(teacherRole);
        teacherUser.setEmail("ust.fulan@sahabatquran.id");

        UserPassword teacherPassword = new UserPassword();
        teacherPassword.setPassword("password");
        teacherPassword.setUser(teacherUser);
        teacherUser.setPassword(teacherPassword);

        userRepository.save(teacherUser);

        Teacher teacher = new Teacher();
        teacher.setUser(teacherUser);
        teacherRepository.saveAndFlush(teacher);

        Classroom classroom = new Classroom();
        classroom.setName("Kelas Malam");
        classroomRepository.saveAndFlush(classroom);

        ClassSchedule schedule = new ClassSchedule();
        schedule.setTeacher(teacher);
        schedule.setClassroom(classroom);
        classScheduleRepository.saveAndFlush(schedule);

        ClassSession session = new ClassSession();
        session.setClassSchedule(schedule);
        session.setSessionDate(LocalDate.now());
        classSessionRepository.saveAndFlush(session);

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setClassSession(session);
        attendance.setPresent(true);
        attendanceRepository.saveAndFlush(attendance);

        Attendance found = attendanceRepository.findById(attendance.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.isPresent()).isTrue();
    }
}
