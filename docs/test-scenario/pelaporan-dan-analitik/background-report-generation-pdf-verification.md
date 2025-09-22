# Skenario Pengujian: Background Report Generation - PDF Content Verification

## Informasi Umum
- **Kategori**: Pelaporan dan Analitik - Background Report Generation
- **Modul**: PDF Content Verification and Data Consistency Testing
- **Tipe Skenario**: Content Verification (Verifikasi Konten PDF)
- **Total Skenario**: 1 skenario komprehensif untuk verifikasi konten PDF
- **Playwright Test**: `reporting.BackgroundReportGenerationTest#shouldGenerateReportWithCorrectPDFContent`

---

## BRG-HP-007: PDF Content Verification Test

### Informasi Skenario
- **ID Skenario**: BRG-HP-007 (Background Report Generation - Happy Path - 007)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Student data sudah disetup dengan assessment lengkap
- Sistem background processing aktif dan operational
- PDF generation service berfungsi dengan baik
- iText PDF library tersedia untuk ekstraksi konten
- Test data Ali Rahman dengan assessment scores:
  - Placement: 85.0 (B+)
  - Midterm: 88.5 (A-)
  - Final: 87.5 (A-)

### Data Test
```
Student Information:
- ID: 30000000-0000-0000-0000-000000000001
- Name: Ali Rahman
- Academic Term: Semester 1 2024/2025
- Class: Tahfidz 2 - Kelas Pagi

Expected Assessment Data:
- Placement Test: 85.0 (Grade: B+)
- Midterm Assessment: 88.5 (Grade: A-)
- Final Assessment: 87.5 (Grade: A-)
- Assessment Notes: "Strong foundation", "Excellent progress", "Consistent performance"

Expected PDF Content:
- Student name and identification
- Academic term information
- All assessment scores and grades
- Calculated overall performance
- Attendance information
- Assessment notes and feedback
- Professional formatting and structure
```

### Langkah Pengujian

#### Bagian 1: Verify Test Data Before Processing
1. **Database Data Verification**
   - Aksi: Query database untuk confirm student Ali Rahman exists
   - Verifikasi:
     - Student ID 30000000-0000-0000-0000-000000000001 exists
     - Full name matches "Ali Rahman"
     - Assessment data sesuai dengan expected values
     - Data integrity sebelum processing

2. **Test Setup Validation**
   - Aksi: Validate test SQL has been executed properly
   - Verifikasi:
     - Assessment records created correctly
     - Attendance data generated
     - Class enrollments in place
     - Academic term configuration correct

#### Bagian 2: Generate Background Report
3. **Initiate Background Report Generation**
   - Aksi: Login sebagai academic admin dan start individual report
   - Verifikasi:
     - Dashboard accessible
     - Form submission successful
     - Batch processing initiated
     - Status monitoring active

4. **Monitor Processing Completion**
   - Aksi: Wait untuk background processing completion
   - Verifikasi:
     - Status progression: INITIATED → VALIDATING → IN_PROGRESS → COMPLETED
     - Processing completes within 2 minutes
     - No errors during generation
     - Batch status shows COMPLETED

#### Bagian 3: Locate and Verify Generated PDF
5. **PDF File Discovery**
   - Aksi: Search /tmp/reports directory untuk generated PDF
   - Verifikasi:
     - Reports directory exists
     - PDF file dengan naming pattern student_report_[student_id]_[timestamp].pdf
     - File is the most recent for the test student
     - File size reasonable (1KB - 10MB)

6. **PDF File Integrity**
   - Aksi: Validate PDF sebagai valid document
   - Verifikasi:
     - File dapat dibuka sebagai PDF document
     - Contains at least 1 page
     - Not corrupted atau truncated
     - Reasonable page count (1-10 pages)

#### Bagian 4: Extract and Verify PDF Content
7. **Content Extraction**
   - Aksi: Extract text content dari PDF menggunakan iText
   - Verifikasi:
     - Text extraction successful
     - Content length substantial (>100 characters)
     - No extraction errors
     - Content readable dan properly formatted

8. **Student Information Verification**
   - Aksi: Search extracted content untuk student details
   - Verifikasi:
     - Contains "Ali Rahman" student name
     - Contains "Semester 1 2024/2025" academic term
     - Student identification information present
     - Academic level information included

9. **Assessment Scores Verification**
   - Aksi: Verify all assessment scores dalam PDF content
   - Verifikasi:
     - Placement score: 85 atau 85.0 present
     - Placement grade: "B+" present
     - Midterm score: 88.5 atau 88 present
     - Midterm grade: "A-" present
     - Final score: 87.5 atau 87 present
     - Final grade information included

10. **Assessment Types Verification**
    - Aksi: Confirm assessment type labels dalam PDF
    - Verifikasi:
      - "PLACEMENT" atau "Placement" assessment mentioned
      - "MIDTERM" atau "Midterm" assessment mentioned
      - "FINAL" atau "Final" assessment mentioned
      - Assessment structure clear dan organized

11. **Calculated Grades Verification**
    - Aksi: Verify calculated overall performance
    - Verifikasi:
      - Overall grade calculation present (around 87.0 average)
      - Grade displays "A-" atau "A" overall performance
      - Grade calculation logic appears correct
      - Performance summary included

12. **Attendance Information Verification**
    - Aksi: Check attendance data dalam PDF
    - Verifikasi:
      - Attendance percentage mentioned (around 92%)
      - Attendance information atau "kehadiran" present
      - Attendance summary accurate
      - Attendance impact on overall assessment

13. **Assessment Notes Verification**
    - Aksi: Verify teacher feedback dan notes
    - Verifikasi:
      - Contains "Strong foundation" comment
      - Contains "Excellent progress" comment
      - Contains "Consistent performance" comment
      - Assessment notes properly formatted
      - Teacher feedback comprehensive

#### Bagian 5: Verify PDF File Properties
14. **File System Verification**
    - Aksi: Check PDF file properties pada filesystem
    - Verifikasi:
      - File exists dan accessible
      - File size reasonable (>1KB, <10MB)
      - File permissions correct
      - File modification time recent

15. **PDF Document Structure**
    - Aksi: Analyze PDF document structure
    - Verifikasi:
      - Valid PDF document format
      - Proper page structure
      - No corruption dalam document
      - Professional appearance dan formatting

#### Bagian 6: Verify Data Consistency (Before/After Processing)
16. **Database Integrity Check**
    - Aksi: Re-query database after processing
    - Verifikasi:
      - Student data unchanged
      - Assessment data preserved
      - No data corruption during processing
      - Database state consistent

17. **Data Consistency Validation**
    - Aksi: Compare pre/post processing data
    - Verifikasi:
      - Student name consistent
      - Student email unchanged
      - Assessment scores match
      - No data loss atau modification

### Expected Outcomes

#### Content Verification Outcomes
- PDF contains all required student information accurately
- All assessment scores dan grades properly displayed
- Calculated overall performance matches expected values
- Attendance information correctly represented
- Teacher feedback dan notes included completely

#### Data Integrity Outcomes
- Source data remains unchanged after processing
- PDF content matches database records exactly
- No data corruption atau loss during PDF generation
- Assessment calculations accurate dan verifiable

#### File Quality Outcomes
- PDF file properly formatted dan professional
- File size appropriate untuk content volume
- PDF structure valid dan accessible
- Text extraction successful untuk verification

#### System Reliability Outcomes
- Background processing completes successfully
- PDF generation reliable dan consistent
- No errors during content verification process
- Processing performance acceptable

### Technical Implementation Details

#### PDF Content Extraction
```java
// Extract text content from PDF using iText
private String extractPdfContent(Path pdfPath) {
    try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath.toString()))) {
        StringBuilder content = new StringBuilder();
        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
            String pageText = PdfTextExtractor.extractFromPage(pdfDoc.getPage(i));
            content.append(pageText).append("\n");
        }
        return content.toString();
    } catch (IOException e) {
        throw new RuntimeException("Failed to extract PDF content", e);
    }
}
```

#### File Discovery Pattern
```java
// Find generated PDF files with specific pattern
List<Path> pdfFiles = Files.list(reportDir)
    .filter(path -> path.toString().endsWith(".pdf"))
    .filter(path -> path.toString().contains("student_report_" + studentId))
    .sorted((p1, p2) -> Files.getLastModifiedTime(p2).compareTo(Files.getLastModifiedTime(p1)))
    .toList();
```

#### Content Verification Assertions
```java
// Comprehensive content verification
assertTrue(pdfContent.contains("Ali Rahman"), "PDF should contain student name");
assertTrue(pdfContent.contains("85") || pdfContent.contains("85.0"), "PDF should contain placement score");
assertTrue(pdfContent.contains("B+"), "PDF should contain placement grade");
assertTrue(pdfContent.contains("Strong foundation"), "PDF should contain assessment notes");
```

### Notes untuk Manual Tester
- Test harus dijalankan after database schema fixes
- Ensure /tmp/reports directory has proper permissions
- Monitor PDF file generation dalam real-time
- Verify content extraction accuracy dengan manual PDF review
- Test dengan different student data untuk comprehensive coverage
- Document any content formatting issues atau missing information

### Dependencies dan Prerequisites
- iText PDF library (com.itextpdf) untuk content extraction
- Filesystem access ke /tmp/reports directory
- Working background processing service
- Complete test data setup dengan student assessments
- Valid academic term dan class configurations

### Automated Test Reference
- File: `src/test/java/com/sahabatquran/webapp/functional/scenarios/reporting/BackgroundReportGenerationTest.java`
- Method: `shouldGenerateReportWithCorrectPDFContent()`
- Execution: `./mvnw test -Dtest="BackgroundReportGenerationTest#shouldGenerateReportWithCorrectPDFContent"`

### Related Documentation
- Background Report Generation Happy Path: `background-report-generation-happy-path.md`
- Background Report Generation Alternate Path: `background-report-generation-alternate-path.md`
- PDF Generation Service Implementation: Review backend service code
- Test Data Setup: `/sql/student-report-test-setup.sql`