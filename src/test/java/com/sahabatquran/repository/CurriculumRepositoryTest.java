package com.sahabatquran.repository;

import com.sahabatquran.domain.Curriculum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CurriculumRepositoryTest {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Test
    public void testSaveAndFindCurriculum() {
        Curriculum curriculum = new Curriculum();
        curriculum.setName("Tahsin");
        curriculum.setLevel("Dasar");
        curriculumRepository.saveAndFlush(curriculum);

        Curriculum found = curriculumRepository.findById(curriculum.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Tahsin");
    }
}
