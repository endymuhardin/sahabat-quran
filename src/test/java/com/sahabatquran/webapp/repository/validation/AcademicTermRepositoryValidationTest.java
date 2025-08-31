package com.sahabatquran.webapp.repository.validation;

import com.sahabatquran.webapp.entity.AcademicTerm;
import com.sahabatquran.webapp.integration.BaseIntegrationTest;
import com.sahabatquran.webapp.repository.AcademicTermRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql(scripts = "/test-data/class-preparation-setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup-class-preparation-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AcademicTermRepositoryValidationTest extends BaseIntegrationTest {
    
    @Autowired
    private AcademicTermRepository academicTermRepository;
    
    @ParameterizedTest
    @CsvSource({
        "'', 'Empty term name should fail'",
        "'                                                   ', 'Term name too long should fail'", // 51+ chars
        "'Valid Term', 'This should actually pass - control case'"
    })
    void save_ShouldValidateTermName(String termName, String description) {
        // Given
        AcademicTerm term = createBasicTerm();
        term.setTermName(termName.trim().isEmpty() ? null : termName);
        
        if (termName.trim().isEmpty() || termName.length() > 50) {
            // When & Then - Should fail validation
            assertThatThrownBy(() -> academicTermRepository.saveAndFlush(term))
                .satisfiesAnyOf(
                    ex -> assertThat(ex).isInstanceOf(ConstraintViolationException.class),
                    ex -> assertThat(ex).isInstanceOf(DataIntegrityViolationException.class)
                );
        } else {
            // Control case - should pass
            // When & Then - Should succeed
            AcademicTerm savedTerm = academicTermRepository.saveAndFlush(term);
            assertThat(savedTerm.getId()).isNotNull();
        }
    }
    
    @ParameterizedTest
    @CsvSource({
        "PLANNING, 'Valid PLANNING status should pass'",
        "ACTIVE, 'Valid ACTIVE status should pass'",
        "COMPLETED, 'Valid COMPLETED status should pass'"
    })
    void save_ShouldValidateStatus(String statusString, String description) {
        // Given
        AcademicTerm term = createBasicTerm();
        AcademicTerm.TermStatus status = AcademicTerm.TermStatus.valueOf(statusString);
        term.setStatus(status);
        
        // When & Then - Should succeed
        AcademicTerm savedTerm = academicTermRepository.saveAndFlush(term);
        assertThat(savedTerm.getStatus()).isEqualTo(status);
    }
    
    @ParameterizedTest
    @CsvSource({
        "2024-06-01, 2024-05-31, 'End date before start date should fail'",
        "2024-06-01, 2024-06-01, 'Start date equals end date should fail'",
        "2024-06-01, 2024-06-02, 'Valid date range should pass'"
    })
    void save_ShouldValidateDateRange(String startDateStr, String endDateStr, String description) {
        // Given
        AcademicTerm term = createBasicTerm();
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        term.setStartDate(startDate);
        term.setEndDate(endDate);
        
        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            // When & Then - Should fail due to check constraint
            assertThatThrownBy(() -> academicTermRepository.saveAndFlush(term))
                .isInstanceOf(DataIntegrityViolationException.class);
        } else {
            // When & Then - Should succeed
            AcademicTerm savedTerm = academicTermRepository.saveAndFlush(term);
            assertThat(savedTerm.getStartDate()).isEqualTo(startDate);
            assertThat(savedTerm.getEndDate()).isEqualTo(endDate);
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "TEST_TERM_2024" // This term name already exists from setup script
    })
    void save_ShouldAllowDuplicateTermName(String duplicateTermName) {
        // Given - No unique constraint exists on term_name in the current schema
        AcademicTerm duplicateTerm = createBasicTerm();
        duplicateTerm.setTermName(duplicateTermName);
        
        // When & Then - Should succeed as no unique constraint is enforced
        AcademicTerm savedTerm = academicTermRepository.saveAndFlush(duplicateTerm);
        assertThat(savedTerm.getTermName()).isEqualTo(duplicateTermName);
        assertThat(savedTerm.getId()).isNotNull();
    }
    
    @ParameterizedTest
    @CsvSource({
        "'', 'Null start date should fail'",
        "'', 'Null end date should fail'"
    })
    void save_ShouldValidateRequiredDates(String unused, String description) {
        // Given
        AcademicTerm term = createBasicTerm();
        
        if (description.contains("start date")) {
            term.setStartDate(null);
        } else if (description.contains("end date")) {
            term.setEndDate(null);
        }
        
        // When & Then - Should fail validation
        assertThatThrownBy(() -> academicTermRepository.saveAndFlush(term))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
    
    private AcademicTerm createBasicTerm() {
        AcademicTerm term = new AcademicTerm();
        term.setTermName("VALIDATION_TEST_TERM_" + System.currentTimeMillis());
        term.setStartDate(LocalDate.now().plusDays(1));
        term.setEndDate(LocalDate.now().plusDays(90));
        term.setStatus(AcademicTerm.TermStatus.PLANNING);
        term.setPreparationDeadline(LocalDate.now().plusDays(30));
        return term;
    }
}