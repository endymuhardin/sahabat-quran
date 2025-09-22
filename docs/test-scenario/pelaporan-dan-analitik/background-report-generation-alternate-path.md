# Skenario Pengujian: Simplified Report Generation - Alternate Path

## Informasi Umum
- **Kategori**: Pelaporan dan Analitik - Simplified Report Generation
- **Modul**: Unified Report Processing Error Handling
- **Tipe Skenario**: Alternate Path (Jalur Alternatif & Edge Cases)
- **Total Skenario**: 4 skenario untuk edge cases dan error handling
- **Playwright Test**: `reporting.StudentReportAlternateTest`

---

## SRG-AP-001: Report Generation with Incomplete Student Data

### Informasi Skenario
- **ID Skenario**: SRG-AP-001 (Simplified Report Generation - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Students dengan incomplete assessment data dalam system
- Missing attendance records untuk some students
- Partial grade information available
- System configured untuk graceful handling of missing data

### Data Test
```
Incomplete Data Scenarios:
Student: Sarah Abdullah
- Missing: Final assessment scores
- Available: Placement dan midterm only
- Attendance: Partial records (60% coverage)
- Teacher Feedback: Missing

Expected Behavior:
- Generate all reports including incomplete ones
- Include disclaimers for missing data
- Graceful handling dalam batch processing
- Clear indication of data quality issues
```

### Langkah Pengujian

#### Bagian 1: Execute Generation with Mixed Data Quality
1. **Setup Term with Mixed Data Quality**
   - Aksi: Select academic term yang contains students dengan varying data completeness
   - Verifikasi:
     - Term selected successfully
     - System prepared untuk mixed data processing

2. **Execute Generate All Reports**
   - Aksi: Klik "Generate All Reports" untuk term dengan incomplete data
   - Verifikasi:
     - Generation request accepted
     - System doesn't reject due to incomplete data
     - Redirect to status dashboard successful
     - Batch created untuk all students including incomplete ones

3. **Monitor Mixed Quality Processing**
   - Aksi: Monitor status dashboard untuk processing progress
   - Verifikasi:
     - Processing continues despite data quality issues
     - No batch failures due to missing data
     - Individual reports marked appropriately

#### Bagian 2: Verify Incomplete Data Handling
4. **Review Processing Results**
   - Aksi: Check final batch status
   - Verifikasi:
     - Batch completes successfully
     - Students dengan incomplete data still get reports
     - Appropriate warnings/disclaimers included
     - No system errors during processing

5. **Verify Incomplete Reports Content**
   - Aksi: Download reports untuk students dengan missing data
   - Verifikasi:
     - PDF generated successfully
     - Missing sections marked as "Data Not Available"
     - Disclaimer notice prominent
     - Report structure maintained despite missing data

6. **Compare Complete vs Incomplete Reports**
   - Aksi: Compare reports dari students dengan complete vs incomplete data
   - Verifikasi:
     - Quality difference clearly indicated
     - Complete reports have full information
     - Incomplete reports have appropriate disclaimers
     - Visual indicators for data quality

---

## SRG-AP-002: System Error During Processing

### Informasi Skenario
- **ID Skenario**: SRG-AP-002 (Simplified Report Generation - Alternate Path - 002)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- System dalam normal operating state
- Error simulation available (simulated database issues, file system errors)
- Error recovery mechanisms active

### Data Test
```
Error Scenarios:
- Processing Interruption: Service restart during generation
- File System Issues: Unable to write PDF files
- Database Connection: Temporary database unavailability
- Resource Constraints: System under high load
```

### Langkah Pengujian

#### Bagian 1: Trigger Error Conditions
1. **Start Normal Generation Process**
   - Aksi: Initiate report generation untuk multiple students
   - Verifikasi: Processing starts normally

2. **Simulate Processing Error**
   - Aksi: Trigger simulated error during processing
   - Verifikasi:
     - Error occurs during batch processing
     - System detects error condition
     - Batch status updated appropriately

#### Bagian 2: Verify Error Handling
3. **Monitor Error Response**
   - Aksi: Observe system behavior during error
   - Verifikasi:
     - Batch status changes to FAILED
     - Error message recorded
     - System remains stable
     - No data corruption occurs

4. **Review Error Information**
   - Aksi: Check error details dalam status dashboard
   - Verifikasi:
     - Error message descriptive dan actionable
     - Timestamp of error recorded
     - Partial results preserved if applicable
     - Clear indication of what succeeded/failed

#### Bagian 3: Test Recovery Process
5. **Attempt Recovery/Retry**
   - Aksi: Try to regenerate failed reports
   - Verifikasi:
     - System allows retry attempts
     - Failed items can be reprocessed
     - Previous successful items not affected
     - Recovery process works as expected

---

## SRG-AP-003: No Students Available for Generation

### Informasi Skenario
- **ID Skenario**: SRG-AP-003 (Simplified Report Generation - Alternate Path - 003)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Academic term dengan no enrolled students atau no active enrollments
- Empty term scenarios

### Data Test
```
Empty Term Scenarios:
- Term: Test Term dengan no enrollments
- Expected Result: Graceful handling, appropriate user feedback
- No System Errors: System should handle empty scenarios gracefully
```

### Langkah Pengujian

#### Bagian 1: Select Empty Term
1. **Attempt Generation untuk Empty Term**
   - Aksi: Select term dengan no enrolled students
   - Verifikasi:
     - Term selectable dari dropdown
     - No validation errors initially

2. **Execute Generation untuk Empty Term**
   - Aksi: Klik "Generate All Reports"
   - Verifikasi:
     - System handles empty term gracefully
     - Appropriate message displayed
     - No processing errors occur

#### Bagian 2: Verify Empty Term Handling
3. **Review Empty Batch Results**
   - Aksi: Check status dashboard untuk empty batch
   - Verifikasi:
     - Batch created dengan zero students
     - Status indicates no students to process
     - Completion immediate (no work to do)
     - User feedback clear dan informative

4. **Verify User Experience**
   - Aksi: Review overall user experience untuk empty scenarios
   - Verifikasi:
     - No confusing error messages
     - Clear explanation of why no reports generated
     - Appropriate guidance provided
     - System remains stable

---

## SRG-AP-004: Regeneration of Non-existent Report

### Informasi Skenario
- **ID Skenario**: SRG-AP-004 (Simplified Report Generation - Alternate Path - 004)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Student dan term combinations yang don't have existing reports
- Clean state dengan no pre-generated files

### Data Test
```
Non-existent Report Scenarios:
- Student: Test Student dengan no existing report
- Term: Valid term but no report previously generated
- Expected: First-time generation rather than regeneration
```

### Langkah Pengujian

#### Bagian 1: Attempt Regeneration of Non-existent Report
1. **Select Student/Term dengan No Existing Report**
   - Aksi: Select combination yang has no existing report
   - Verifikasi:
     - Selection successful
     - System allows regeneration attempt

2. **Execute Regeneration Process**
   - Aksi: Klik "Regenerate Report"
   - Verifikasi:
     - System handles non-existent report gracefully
     - Process treats as first-time generation
     - No errors about missing file to delete

#### Bagian 2: Verify First-time Generation Behavior
3. **Monitor Processing untuk Non-existent Report**
   - Aksi: Monitor regeneration process
   - Verifikasi:
     - Processing completes successfully
     - New report generated (not regenerated)
     - Status indicates successful creation
     - No file deletion errors

4. **Verify Generated Report**
   - Aksi: Download newly created report
   - Verifikasi:
     - Report generated successfully
     - Content accurate untuk selected student/term
     - Quality equivalent to normal generation
     - File properly stored for future access

#### Bagian 3: Test Subsequent Regeneration
5. **Test True Regeneration**
   - Aksi: Regenerate the same report that now exists
   - Verifikasi:
     - System detects existing report
     - Old report deleted successfully
     - New report generated
     - Replace process works correctly

### Expected Outcomes (Semua Skenario)

#### Error Handling Outcomes
- System handles incomplete data gracefully
- Processing errors don't crash system
- Empty terms handled appropriately
- Non-existent report regeneration works smoothly
- User feedback clear untuk all error conditions

#### Recovery Outcomes
- Failed processing can be retried
- Partial results preserved when appropriate
- System state remains consistent during errors
- Recovery mechanisms reliable

#### User Experience Outcomes
- Error messages informative dan actionable
- No confusing system behaviors
- Graceful degradation when data incomplete
- Clear feedback for edge cases
- Consistent interface behavior regardless of data quality

### Notes untuk Manual Tester
- Test dengan various data quality scenarios
- Verify error messages are user-friendly
- Document system behavior under stress
- Test recovery mechanisms thoroughly
- Verify consistency of error handling across scenarios
- Note any unexpected system behaviors
- Test dengan different error conditions

### Automated Test Reference
- File: `src/test/java/com/sahabatquran/webapp/functional/scenarios/operationworkflow/StudentReportAlternateTest.java`
- Covers: All alternate path scenarios outlined dalam dokumen ini
- Execution: `./mvnw test -Dtest="StudentReportAlternateTest"`