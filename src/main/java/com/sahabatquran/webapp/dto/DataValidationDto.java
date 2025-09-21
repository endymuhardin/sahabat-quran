package com.sahabatquran.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for data validation and quality assessment in cross-term analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataValidationDto {

    // Data completeness flags
    private boolean missingData;
    private boolean partialData;
    private boolean insufficientData;
    private boolean missingTeacherData;
    private boolean incompleteStudentRecords;
    private boolean missingFinancialData;

    // Access and restriction flags
    private boolean restrictedAccess;
    private boolean limitedAccess;
    private boolean archivedTermsIncluded;

    // Analysis capability flags
    private boolean trendAnalysisAvailable;
    private boolean comparisonPossible;
    private boolean partialAnalysisOnly;
    private boolean limitedHistoricalContext;
    private boolean shortTermTrendsOnly;

    // Data quality metrics
    private BigDecimal dataQualityScore;
    private BigDecimal confidenceLevel;

    // Warning and suggestion lists
    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Builder.Default
    private List<String> limitations = new ArrayList<>();

    @Builder.Default
    private List<String> suggestions = new ArrayList<>();

    // Helper methods
    public void addWarning(String warning) {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        warnings.add(warning);
    }

    public void addLimitation(String limitation) {
        if (limitations == null) {
            limitations = new ArrayList<>();
        }
        limitations.add(limitation);
    }

    public void addSuggestion(String suggestion) {
        if (suggestions == null) {
            suggestions = new ArrayList<>();
        }
        suggestions.add(suggestion);
    }

    /**
     * Calculate overall data quality score based on flags
     */
    public void calculateDataQualityScore() {
        int totalFlags = 0;
        int negativeFlags = 0;

        // Count negative data quality indicators
        if (missingData) negativeFlags++;
        if (partialData) negativeFlags++;
        if (insufficientData) negativeFlags++;
        if (missingTeacherData) negativeFlags++;
        if (incompleteStudentRecords) negativeFlags++;
        if (missingFinancialData) negativeFlags++;
        if (restrictedAccess) negativeFlags++;
        if (limitedAccess) negativeFlags++;
        if (archivedTermsIncluded) negativeFlags++;

        totalFlags = 9; // Total number of quality indicators

        // Calculate score as percentage
        BigDecimal score = BigDecimal.valueOf((totalFlags - negativeFlags) * 100.0 / totalFlags);
        this.dataQualityScore = score.setScale(1, BigDecimal.ROUND_HALF_UP);

        // Set confidence level based on score
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) {
            this.confidenceLevel = BigDecimal.valueOf(90);
        } else if (score.compareTo(BigDecimal.valueOf(60)) >= 0) {
            this.confidenceLevel = BigDecimal.valueOf(70);
        } else if (score.compareTo(BigDecimal.valueOf(40)) >= 0) {
            this.confidenceLevel = BigDecimal.valueOf(50);
        } else {
            this.confidenceLevel = BigDecimal.valueOf(30);
        }
    }

    /**
     * Check if any data issues exist
     */
    public boolean hasDataIssues() {
        return missingData || partialData || insufficientData ||
               missingTeacherData || incompleteStudentRecords || missingFinancialData;
    }

    /**
     * Check if access restrictions exist
     */
    public boolean hasAccessRestrictions() {
        return restrictedAccess || limitedAccess;
    }
}