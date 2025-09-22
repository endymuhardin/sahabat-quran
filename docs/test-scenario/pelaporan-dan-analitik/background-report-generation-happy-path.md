# Skenario Pengujian: Simplified Report Generation - Happy Path

## Informasi Umum
- **Kategori**: Pelaporan dan Analitik - Simplified Report Generation
- **Modul**: Unified Report Processing System
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 4 skenario utama untuk simplified report generation
- **Playwright Test**: `reporting.StudentReportTest`

---

## SRG-HP-001: Single Button Report Generation for All Students

### Informasi Skenario
- **ID Skenario**: SRG-HP-001 (Simplified Report Generation - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Semester dalam status ACTIVE atau COMPLETED
- Multiple students sudah memiliki data nilai lengkap dalam semester
- Data kehadiran siswa tercatat dengan baik
- Assessment data sudah divalidasi dan complete
- Academic term sudah memiliki data grade calculation

### Data Test
```
Academic Term: Semester 1 2024/2025
Students: Multiple students dengan complete assessment data
Expected Processing:
- Report Type: All student reports in one batch
- Processing: Background one-by-one for all students
- File Format: PDF per student
- Status Monitoring: Server-side without real-time updates
```

### Langkah Pengujian

#### Bagian 1: Akses Report Generation Page
1. **Login sebagai Academic Admin**
   - Aksi: Login dengan `academic.admin1` / `Welcome@YSQ2024`
   - Verifikasi: Dashboard academic admin muncul dengan menu reports

2. **Navigate ke Student Reports**
   - Aksi: Klik menu "Reports" → "Student Report Cards"
   - Verifikasi:
     - Student Reports page terbuka di `/report-cards`
     - Form untuk term selection tersedia
     - Single "Generate All Reports" button visible
     - Simple interface tanpa bulk/individual options

3. **Verify Page Components**
   - Aksi: Review main reports interface
   - Verifikasi:
     - Term dropdown populated dengan academic terms
     - Students list display (for reference)
     - Single generate button approach
     - No complex bulk/individual selection UI

#### Bagian 2: Execute Single Generate All Process
4. **Select Academic Term**
   - Aksi: Select "Semester 1 2024/2025" dari term dropdown
   - Verifikasi:
     - Term selected successfully
     - Generate button becomes active
     - Form validation passes

5. **Execute Generate All Reports**
   - Aksi: Klik "Generate All Reports" button
   - Verifikasi:
     - Request submitted successfully
     - Redirect to status dashboard at `/report-cards/status`
     - Success message displayed: "Report generation started for all students"
     - Batch ID provided untuk tracking

6. **Verify Status Dashboard Access**
   - Aksi: Arrive at status dashboard
   - Verifikasi:
     - Status dashboard page loaded at `/report-cards/status`
     - Current batch information displayed
     - No real-time updates - static server-side display
     - Manual refresh required untuk status updates

#### Bagian 3: Monitor Processing via Status Dashboard
7. **Review Status Dashboard Layout**
   - Aksi: Examine status dashboard components
   - Verifikasi:
     - Batch status display (INITIATED/IN_PROGRESS/COMPLETED)
     - Total students count
     - Completed/failed reports count
     - Processing time information
     - Manual refresh functionality

8. **Monitor Progress via Manual Refresh**
   - Aksi: Manually refresh page untuk status updates
   - Verifikasi:
     - Status progression: INITIATED → IN_PROGRESS → COMPLETED
     - Report counts increment with each refresh
     - No automatic real-time updates
     - Server-side state correctly maintained

9. **Verify Completion Status**
   - Aksi: Refresh until processing completes
   - Verifikasi:
     - Final status shows "COMPLETED"
     - All students processed successfully
     - Total/completed counts match
     - Completion timestamp recorded

#### Bagian 4: Access Generated Reports
10. **Verify Download Functionality**
    - Aksi: Check available download options
    - Verifikasi:
      - Download links available untuk completed reports
      - Pre-generated PDF files accessible
      - File paths correctly structured
      - Individual student report downloads working

11. **Test PDF Download**
    - Aksi: Download sample student report
    - Verifikasi:
      - PDF download successful via `/download-pdf` endpoint
      - File contains correct student data
      - Report quality maintained
      - Proper filename dengan student/term information

---

## SRG-HP-002: Individual Student Report Regeneration

### Informasi Skenario
- **ID Skenario**: SRG-HP-002 (Simplified Report Generation - Happy Path - 002)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 5-7 menit

### Prasyarat
- Student sudah memiliki existing report
- Updated assessment data available
- User has regeneration permissions

### Data Test
```
Student: Ali Rahman
Academic Term: Semester 1 2024/2025
Scenario: Regenerate report after data updates
Expected Behavior: Old report deleted, new report generated
```

### Langkah Pengujian

#### Bagian 1: Setup for Regeneration
1. **Navigate to Student Reports**
   - Aksi: Access `/report-cards` page
   - Verifikasi: Main reports page accessible

2. **Select Student and Term for Regeneration**
   - Aksi:
     - Select student "Ali Rahman" dari student dropdown
     - Select "Semester 1 2024/2025" dari term dropdown
   - Verifikasi:
     - Selections successful
     - Regenerate option available

#### Bagian 2: Execute Regeneration
3. **Trigger Report Regeneration**
   - Aksi: Klik "Regenerate Report" button
   - Verifikasi:
     - Regeneration request submitted
     - Redirect to status dashboard
     - Success message: "Report regeneration started"

4. **Monitor Regeneration Process**
   - Aksi: Monitor via status dashboard
   - Verifikasi:
     - Single-item batch created untuk regeneration
     - Status progression normal
     - Old report deleted, new report generated

5. **Verify Regenerated Report**
   - Aksi: Download regenerated report
   - Verifikasi:
     - New report contains updated data
     - Previous report no longer accessible
     - Quality maintained

---

## SRG-HP-003: Status Dashboard Functionality

### Informasi Skenario
- **ID Skenario**: SRG-HP-003 (Simplified Report Generation - Happy Path - 003)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 5-7 menit

### Prasyarat
- Multiple report generation batches available
- Various batch states (completed, in-progress, failed)

### Data Test
```
Monitoring Scenarios:
- Multiple Batches: Different completion states
- Status Display: Server-side updates only
- Navigation: Easy access between batches
```

### Langkah Pengujian

#### Bagian 1: Access Status Dashboard
1. **Direct Status Dashboard Access**
   - Aksi: Navigate directly to `/report-cards/status`
   - Verifikasi:
     - Status dashboard accessible
     - All recent batches displayed
     - Chronological ordering (newest first)

2. **Review Batch Listings**
   - Aksi: Review available batch information
   - Verifikasi:
     - Batch names descriptive
     - Status clearly indicated
     - Progress information visible
     - Creation/completion timestamps

#### Bagian 2: Status Dashboard Navigation
3. **Test Manual Refresh Functionality**
   - Aksi: Manually refresh page
   - Verifikasi:
     - Page refreshes without issues
     - Status information updated
     - No automatic refresh mechanisms
     - State persistence maintained

4. **Verify Status Information Accuracy**
   - Aksi: Compare status dengan actual processing state
   - Verifikasi:
     - Status information accurate
     - Progress percentages correct
     - Time information reliable
     - No misleading indicators

#### Bagian 3: Batch Management
5. **Review Completed Batches**
   - Aksi: Examine completed batch details
   - Verifikasi:
     - Completion information comprehensive
     - Download links available
     - Success/failure counts accurate
     - Historical data preserved

6. **Test Navigation Back to Main Reports**
   - Aksi: Navigate back to main reports page
   - Verifikasi:
     - Navigation link available
     - Return to `/report-cards` successful
     - Context preserved

---

## SRG-HP-004: Pre-generated PDF Access System

### Informasi Skenario
- **ID Skenario**: SRG-HP-004 (Simplified Report Generation - Happy Path - 004)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, FINANCE_STAFF, INSTRUCTOR
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Reports already generated dan stored in file system
- Multiple students dengan completed reports
- Various user roles for access testing

### Data Test
```
Download Scenarios:
- Student: Ali Rahman
- Term: Semester 1 2024/2025
- File Location: Pre-generated PDF in file system
- Access: Via /download-pdf endpoint
```

### Langkah Pengujian

#### Bagian 1: Pre-generated Report Access
1. **Verify Report Existence**
   - Aksi: Check if reports exist via `/check-report` endpoint
   - Verifikasi:
     - Report existence check working
     - Accurate availability information
     - Proper response untuk existing/missing reports

2. **Test PDF Download Process**
   - Aksi: Download student report via `/download-pdf`
   - Verifikasi:
     - Download initiated successfully
     - PDF file served correctly
     - Proper content-type headers
     - Descriptive filename generated

#### Bagian 2: Download Functionality Verification
3. **Verify PDF Content Quality**
   - Aksi: Open downloaded PDF file
   - Verifikasi:
     - PDF opens without corruption
     - Student data accurate
     - Report formatting maintained
     - All sections properly rendered

4. **Test Multiple Student Downloads**
   - Aksi: Download reports untuk multiple students
   - Verifikasi:
     - All downloads successful
     - Unique filenames untuk each student
     - Consistent quality across reports
     - No file conflicts atau overwrites

#### Bagian 3: Access Control Verification
5. **Test Role-based Access**
   - Aksi: Test downloads dengan different user roles
   - Verifikasi:
     - ACADEMIC_ADMIN: Full access
     - FINANCE_STAFF: Appropriate access
     - INSTRUCTOR: Limited access as configured
     - Unauthorized users blocked

6. **Verify Error Handling**
   - Aksi: Test download untuk non-existent reports
   - Verifikasi:
     - Graceful error handling
     - Appropriate error messages
     - No system errors atau crashes
     - User-friendly feedback

### Expected Outcomes (Semua Skenario)

#### Functional Outcomes
- Simplified report generation berfungsi reliably
- Single generate button approach intuitive dan efficient
- Status dashboard provides clear information without complexity
- Pre-generated PDF access seamless dan fast
- Individual regeneration working as expected

#### Performance Outcomes
- Single generation process efficient untuk all students
- Status dashboard loads quickly without real-time overhead
- PDF downloads fast dari pre-generated files
- System remains responsive during background processing
- Resource usage optimized untuk server-side updates

#### User Experience Outcomes
- Simplified interface reduces user confusion
- Clear workflow: Generate → Monitor → Download
- No complex bulk/individual selection required
- Status information clear without overwhelming detail
- Intuitive navigation between related functions

### Notes untuk Manual Tester
- Focus on simplified workflow testing
- Verify no real-time update expectations
- Test manual refresh behavior thoroughly
- Validate pre-generated file access
- Document any performance differences from previous system
- Test dengan different data volumes
- Verify browser compatibility
- Test dengan different user roles

### Automated Test Reference
- File: `src/test/java/com/sahabatquran/webapp/functional/scenarios/operationworkflow/StudentReportTest.java`
- Covers: All scenarios outlined dalam dokumen ini
- Execution: `./mvnw test -Dtest="StudentReportTest"`