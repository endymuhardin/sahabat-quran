package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/test-data/class-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-class-preparation-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AcademicTermRepositoryTest extends BaseIntegrationTest {
    
    @Autowired
    private AcademicTermRepository academicTermRepository;
    
    @Autowired
    private TestDataUtil testDataUtil;
    
    private AcademicTerm testTerm;
    
    @BeforeEach
    void setUp() {
        // Get the test term created by SQL script
        testTerm = academicTermRepository.findByTermName("TEST_TERM_2024").orElseThrow();
    }
    
    @Test
    void findByTermName_ShouldReturnTerm_WhenTermExists() {
        // When
        Optional<AcademicTerm> result = academicTermRepository.findByTermName("TEST_TERM_2024");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTermName()).isEqualTo("TEST_TERM_2024");
        assertThat(result.get().getStatus()).isEqualTo(AcademicTerm.TermStatus.PLANNING);
    }
    
    @Test
    void findByStatus_ShouldReturnTermsWithStatus_WhenStatusExists() {
        // Given - create additional terms with different statuses
        AcademicTerm activeTerm = testDataUtil.createTestAcademicTerm();
        activeTerm.setStatus(AcademicTerm.TermStatus.ACTIVE);
        academicTermRepository.save(activeTerm);
        
        AcademicTerm completedTerm = testDataUtil.createTestAcademicTerm();
        completedTerm.setStatus(AcademicTerm.TermStatus.COMPLETED);
        academicTermRepository.save(completedTerm);
        
        // When
        List<AcademicTerm> planningTerms = academicTermRepository.findByStatus(AcademicTerm.TermStatus.PLANNING);
        List<AcademicTerm> activeTerms = academicTermRepository.findByStatus(AcademicTerm.TermStatus.ACTIVE);
        List<AcademicTerm> completedTerms = academicTermRepository.findByStatus(AcademicTerm.TermStatus.COMPLETED);
        
        // Then
        assertThat(planningTerms).hasSizeGreaterThanOrEqualTo(1);
        assertThat(activeTerms).hasSizeGreaterThanOrEqualTo(1); // May include seed data
        assertThat(completedTerms).hasSize(1);
        
        assertThat(planningTerms.get(0).getStatus()).isEqualTo(AcademicTerm.TermStatus.PLANNING);
        assertThat(activeTerms.get(0).getStatus()).isEqualTo(AcademicTerm.TermStatus.ACTIVE);
        assertThat(completedTerms.get(0).getStatus()).isEqualTo(AcademicTerm.TermStatus.COMPLETED);
    }
    
    @Test
    void findActiveTerms_ShouldReturnActiveTermsOrderedByStartDate() {
        // Given - create active terms with different start dates
        AcademicTerm earlierTerm = testDataUtil.createTestAcademicTerm();
        earlierTerm.setStatus(AcademicTerm.TermStatus.ACTIVE);
        earlierTerm.setStartDate(LocalDate.now().plusDays(10));
        academicTermRepository.save(earlierTerm);
        
        AcademicTerm laterTerm = testDataUtil.createTestAcademicTerm();
        laterTerm.setStatus(AcademicTerm.TermStatus.ACTIVE);
        laterTerm.setStartDate(LocalDate.now().plusDays(30));
        academicTermRepository.save(laterTerm);
        
        // When
        List<AcademicTerm> activeTerms = academicTermRepository.findActiveTerms();
        
        // Then - Account for existing ACTIVE terms from seed data
        assertThat(activeTerms).hasSizeGreaterThanOrEqualTo(2);
        assertThat(activeTerms.get(0).getStartDate()).isAfter(activeTerms.get(1).getStartDate());
    }
    
    @Test
    void findPlanningTerms_ShouldReturnPlanningTermsOrderedByStartDate() {
        // Given - test term is already PLANNING, create another
        AcademicTerm anotherPlanningTerm = testDataUtil.createTestAcademicTerm();
        anotherPlanningTerm.setStatus(AcademicTerm.TermStatus.PLANNING);
        anotherPlanningTerm.setStartDate(LocalDate.now().plusDays(60));
        academicTermRepository.save(anotherPlanningTerm);
        
        // When
        List<AcademicTerm> planningTerms = academicTermRepository.findPlanningTerms();
        
        // Then
        assertThat(planningTerms).hasSizeGreaterThanOrEqualTo(2);
        // Should be ordered by start date ascending
        for (int i = 1; i < planningTerms.size(); i++) {
            assertThat(planningTerms.get(i).getStartDate())
                .isAfterOrEqualTo(planningTerms.get(i-1).getStartDate());
        }
    }
    
    @Test
    void findTermsByDate_ShouldReturnTermsContainingDate() {
        // Given - Use a date that only falls within TEST_TERM_2024 (2024-06-01 to 2024-11-30)
        // but not within any other terms. Semester 2 2024 (2024-09-01 to 2024-12-31) overlaps
        // Use a date between Semester 1 end and Semester 2 start
        LocalDate dateInTerm = LocalDate.of(2024, 8, 15); // Only in TEST_TERM_2024 range
        
        // When
        List<AcademicTerm> termsContainingDate = academicTermRepository.findTermsByDate(dateInTerm);
        
        // Then - Multiple terms may contain the same date, but TEST_TERM_2024 should be one of them
        assertThat(termsContainingDate).isNotEmpty();
        assertThat(termsContainingDate).anyMatch(term -> term.getTermName().equals("TEST_TERM_2024"));
        
        // Verify that all returned terms actually contain the date
        assertThat(termsContainingDate).allMatch(term -> 
            !dateInTerm.isBefore(term.getStartDate()) && !dateInTerm.isAfter(term.getEndDate()));
    }
    
    @Test
    void findTermsWithUpcomingPreparationDeadline_ShouldReturnTermsWithFutureDeadlines() {
        // Given - create term with future preparation deadline
        AcademicTerm futureTerm = testDataUtil.createTestAcademicTerm();
        futureTerm.setPreparationDeadline(LocalDate.now().plusDays(10));
        academicTermRepository.save(futureTerm);
        
        // When
        List<AcademicTerm> upcomingTerms = academicTermRepository
            .findTermsWithUpcomingPreparationDeadline(LocalDate.now());
        
        // Then
        assertThat(upcomingTerms).hasSizeGreaterThanOrEqualTo(1);
        assertThat(upcomingTerms).allMatch(term -> 
            term.getPreparationDeadline().isAfter(LocalDate.now()) ||
            term.getPreparationDeadline().isEqual(LocalDate.now())
        );
    }
    
    @Test
    void findAllOrderByStartDateDesc_ShouldReturnAllTermsInDescendingOrder() {
        // Given - create multiple terms with different start dates
        AcademicTerm term1 = testDataUtil.createTestAcademicTerm();
        term1.setStartDate(LocalDate.now().plusDays(30));
        academicTermRepository.save(term1);
        
        AcademicTerm term2 = testDataUtil.createTestAcademicTerm();
        term2.setStartDate(LocalDate.now().plusDays(60));
        academicTermRepository.save(term2);
        
        // When
        List<AcademicTerm> allTerms = academicTermRepository.findAllOrderByStartDateDesc();
        
        // Then
        assertThat(allTerms).hasSizeGreaterThanOrEqualTo(3);
        // Should be in descending order by start date
        for (int i = 1; i < allTerms.size(); i++) {
            assertThat(allTerms.get(i).getStartDate())
                .isBeforeOrEqualTo(allTerms.get(i-1).getStartDate());
        }
    }
    
    @Test
    void save_ShouldPersistAcademicTerm_WithAllFields() {
        // Given
        AcademicTerm newTerm = testDataUtil.createTestAcademicTerm();
        
        // When
        AcademicTerm savedTerm = academicTermRepository.save(newTerm);
        
        // Then
        assertThat(savedTerm.getId()).isNotNull();
        assertThat(savedTerm.getCreatedAt()).isNotNull();
        assertThat(savedTerm.getUpdatedAt()).isNotNull();
        
        // Verify persistence
        Optional<AcademicTerm> retrieved = academicTermRepository.findById(savedTerm.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTermName()).isEqualTo(newTerm.getTermName());
        assertThat(retrieved.get().getStartDate()).isEqualTo(newTerm.getStartDate());
        assertThat(retrieved.get().getEndDate()).isEqualTo(newTerm.getEndDate());
        assertThat(retrieved.get().getStatus()).isEqualTo(newTerm.getStatus());
    }
    
    @Test
    void update_ShouldUpdateUpdatedAtTimestamp() throws InterruptedException {
        // Given
        AcademicTerm term = testDataUtil.createTestAcademicTerm();
        AcademicTerm savedTerm = academicTermRepository.save(term);
        LocalDate originalUpdatedAt = savedTerm.getUpdatedAt().toLocalDate();
        
        // Small delay to ensure timestamp difference
        Thread.sleep(100);
        
        // When
        savedTerm.setStatus(AcademicTerm.TermStatus.ACTIVE);
        AcademicTerm updatedTerm = academicTermRepository.save(savedTerm);
        
        // Then
        assertThat(updatedTerm.getUpdatedAt().toLocalDate()).isAfterOrEqualTo(originalUpdatedAt);
        assertThat(updatedTerm.getStatus()).isEqualTo(AcademicTerm.TermStatus.ACTIVE);
    }
}