package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.PlacementTestVerse;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/placement-test-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PlacementTestVerseRepositoryTest extends BaseIntegrationTest {
    
    @Autowired
    private PlacementTestVerseRepository placementVerseRepository;
    
    private final Faker faker = new Faker();
    
    @BeforeEach
    void setUp() {
        // Test data is loaded via @Sql annotation
        // No manual setup needed
    }
    
    @Test
    void findByIsActiveTrueOrderByDifficultyLevelAscSurahNumberAscAyahStartAsc_ShouldReturnOrderedVerses() {
        // When
        List<PlacementTestVerse> result = placementVerseRepository
                .findByIsActiveTrueOrderByDifficultyLevelAscSurahNumberAscAyahStartAsc();
        
        // Then - should have migration data + setup script data
        assertThat(result).hasSizeGreaterThanOrEqualTo(3);
        
        // Verify ordering - should be ordered by difficulty level ASC, surah number ASC, ayah start ASC
        for (int i = 0; i < result.size() - 1; i++) {
            PlacementTestVerse current = result.get(i);
            PlacementTestVerse next = result.get(i + 1);
            
            if (current.getDifficultyLevel().equals(next.getDifficultyLevel())) {
                if (current.getSurahNumber().equals(next.getSurahNumber())) {
                    assertThat(current.getAyahStart()).isLessThanOrEqualTo(next.getAyahStart());
                } else {
                    assertThat(current.getSurahNumber()).isLessThan(next.getSurahNumber());
                }
            } else {
                assertThat(current.getDifficultyLevel()).isLessThan(next.getDifficultyLevel());
            }
        }
    }
    
    @Test
    void findByDifficultyLevelAndIsActiveTrueOrderBySurahNumberAscAyahStartAsc_ShouldReturnVersesByDifficulty() {
        // When
        List<PlacementTestVerse> level2Verses = placementVerseRepository
                .findByDifficultyLevelAndIsActiveTrueOrderBySurahNumberAscAyahStartAsc(2);
        
        // Then - should have migration data + our test data
        assertThat(level2Verses).hasSizeGreaterThanOrEqualTo(1);
        assertThat(level2Verses).allMatch(verse -> verse.getDifficultyLevel() == 2);
        
        // Verify ordering - should be ordered by surah number ASC, then ayah start ASC
        for (int i = 0; i < level2Verses.size() - 1; i++) {
            PlacementTestVerse current = level2Verses.get(i);
            PlacementTestVerse next = level2Verses.get(i + 1);
            
            if (current.getSurahNumber().equals(next.getSurahNumber())) {
                assertThat(current.getAyahStart()).isLessThanOrEqualTo(next.getAyahStart());
            } else {
                assertThat(current.getSurahNumber()).isLessThan(next.getSurahNumber());
            }
        }
    }
    
    @Test
    void findByDifficultyLevelRange_ShouldReturnVersesInRange() {
        // When
        List<PlacementTestVerse> rangeVerses = placementVerseRepository
                .findByDifficultyLevelRange(1, 2);
        
        // Then - should have migration data + setup script data
        assertThat(rangeVerses).hasSizeGreaterThanOrEqualTo(2);
        assertThat(rangeVerses)
                .extracting(PlacementTestVerse::getDifficultyLevel)
                .allMatch(level -> level >= 1 && level <= 2);
        
        // Verify ordering
        for (int i = 0; i < rangeVerses.size() - 1; i++) {
            PlacementTestVerse current = rangeVerses.get(i);
            PlacementTestVerse next = rangeVerses.get(i + 1);
            assertThat(current.getDifficultyLevel()).isLessThanOrEqualTo(next.getDifficultyLevel());
        }
    }
    
    @Test
    void findRandomByDifficultyLevel_ShouldReturnRandomVerseOfSpecifiedLevel() {
        // When - use existing level 1 verses from migration + setup data
        Optional<PlacementTestVerse> randomVerse = placementVerseRepository
                .findRandomByDifficultyLevel(1);
        
        // Then
        assertThat(randomVerse).isPresent();
        assertThat(randomVerse.get().getDifficultyLevel()).isEqualTo(1);
        assertThat(randomVerse.get().getIsActive()).isTrue();
    }
    
    @Test
    void findRandomVerse_ShouldReturnAnyRandomActiveVerse() {
        // When
        Optional<PlacementTestVerse> randomVerse = placementVerseRepository.findRandomVerse();
        
        // Then
        assertThat(randomVerse).isPresent();
        assertThat(randomVerse.get().getIsActive()).isTrue();
    }
    
    @Test
    void findBySurahNumberAndIsActiveTrueOrderByAyahStartAsc_ShouldReturnVersesBySurah() {
        // When - search for surah 2 (Al-Baqarah)
        List<PlacementTestVerse> baqarahVerses = placementVerseRepository
                .findBySurahNumberAndIsActiveTrueOrderByAyahStartAsc(2);
        
        // Then - should have migration data + setup script data for surah 2
        assertThat(baqarahVerses).hasSizeGreaterThanOrEqualTo(1);
        assertThat(baqarahVerses)
                .extracting(PlacementTestVerse::getSurahNumber)
                .containsOnly(2);
        assertThat(baqarahVerses)
                .extracting(PlacementTestVerse::getSurahName)
                .containsOnly("Al-Baqarah");
        
        // Verify ordering by ayah start
        for (int i = 0; i < baqarahVerses.size() - 1; i++) {
            assertThat(baqarahVerses.get(i).getAyahStart())
                    .isLessThanOrEqualTo(baqarahVerses.get(i + 1).getAyahStart());
        }
    }
    
    @Test
    void findDistinctDifficultyLevels_ShouldReturnAllActiveDifficultyLevels() {
        // When
        List<Integer> difficultyLevels = placementVerseRepository.findDistinctDifficultyLevels();
        
        // Then - should have migration data difficulty levels + setup script levels
        assertThat(difficultyLevels).hasSizeGreaterThanOrEqualTo(3);
        assertThat(difficultyLevels).contains(1, 2, 3);
        
        // Should be ordered
        for (int i = 0; i < difficultyLevels.size() - 1; i++) {
            assertThat(difficultyLevels.get(i)).isLessThan(difficultyLevels.get(i + 1));
        }
    }
    
    @Test
    void shouldNotReturnInactiveVerses() {
        // Given - create inactive verse
        PlacementTestVerse inactiveVerse = new PlacementTestVerse();
        inactiveVerse.setSurahNumber(114);
        inactiveVerse.setSurahName("Test Inactive Surah An-Nas");
        inactiveVerse.setAyahStart(1);
        inactiveVerse.setAyahEnd(1);
        inactiveVerse.setArabicText("Test Inactive Arabic");
        inactiveVerse.setDifficultyLevel(1);
        inactiveVerse.setIsActive(false);
        placementVerseRepository.save(inactiveVerse);
        
        // When
        List<PlacementTestVerse> activeVerses = placementVerseRepository
                .findByIsActiveTrueOrderByDifficultyLevelAscSurahNumberAscAyahStartAsc();
        
        // Then - should not include inactive verse
        assertThat(activeVerses).hasSizeGreaterThanOrEqualTo(3);
        assertThat(activeVerses)
                .extracting(PlacementTestVerse::getSurahName)
                .doesNotContain("Test Inactive Surah An-Nas");
        assertThat(activeVerses)
                .allMatch(verse -> verse.getIsActive());
    }
    
    @Test
    void findRandomByDifficultyLevel_ShouldReturnEmptyForNonExistentLevel() {
        // When
        Optional<PlacementTestVerse> result = placementVerseRepository
                .findRandomByDifficultyLevel(10);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    private PlacementTestVerse createRandomTestVerse(int difficultyLevel) {
        PlacementTestVerse verse = new PlacementTestVerse();
        verse.setSurahNumber(faker.number().numberBetween(1, 114));
        verse.setSurahName(faker.lorem().word());
        verse.setAyahStart(faker.number().numberBetween(1, 50));
        verse.setAyahEnd(faker.number().numberBetween(51, 100));
        verse.setArabicText(faker.lorem().sentence());
        verse.setDifficultyLevel(difficultyLevel);
        verse.setIsActive(true);
        return verse;
    }
}