# Skenario Pengujian: Background Report Generation - Happy Path

## Informasi Umum
- **Kategori**: Pelaporan dan Analitik - Background Report Generation
- **Modul**: Background Report Processing and Academic Analytics
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario utama untuk background report generation
- **Playwright Test**: `reporting.BackgroundReportGenerationTest`

---

## BRG-HP-001: Individual Student Background Report Generation

### Informasi Skenario
- **ID Skenario**: BRG-HP-001 (Background Report Generation - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- Semester dalam status ACTIVE atau COMPLETED
- Siswa sudah memiliki data nilai lengkap dalam semester
- Data kehadiran siswa tercatat dengan baik
- Assessment data sudah divalidasi dan complete
- Academic term sudah memiliki data grade calculation
- Background processing service aktif dan ready

### Data Test
```
Student Data:
- Nama: Ali Rahman
- Level: Tahfidz 2
- Academic Term: Semester 1 2024/2025
- Final Grade: A- (87.5)
- Attendance Rate: 92%
- Teacher Feedback: "Menunjukkan kemajuan konsisten dalam hafalan"

Assessment Data:
- Placement Test: 85.0 (B+)
- Midterm Assessment: 88.5 (A-)
- Final Assessment: 87.5 (A-)
- Teacher Evaluation: Consistent performance
- Attendance Score: 92%

Expected Processing:
- Report Type: INDIVIDUAL_REPORT_CARD
- Estimated Duration: 30 seconds
- File Format: PDF
- File Size: ~1-2 MB
```

### Langkah Pengujian

#### Bagian 1: Akses Background Generation Dashboard
1. **Login sebagai Academic Admin**
   - Aksi: Login dengan `academic.admin1` / `Welcome@YSQ2024`
   - Verifikasi: Dashboard academic admin muncul dengan menu reports

2. **Navigate ke Background Report Generation**
   - Aksi: Klik menu "Reports" → "Student Report Cards" → "Background Generation"
   - Verifikasi:
     - Background reports dashboard terbuka
     - Quick actions tersedia (Individual, Bulk, Status)
     - Modern UI dengan progress monitoring features

3. **Verify Dashboard Components**
   - Aksi: Review dashboard interface
   - Verifikasi:
     - Individual Student Report card tersedia
     - Bulk Class Reports card tersedia
     - Processing Status card tersedia
     - Navigation back to main reports working

#### Bagian 2: Initiate Individual Report Generation
4. **Start Individual Report Generation**
   - Aksi: Klik "Start Generation" pada Individual Student Report card
   - Verifikasi:
     - Individual report form muncul
     - Student dropdown populated dengan data students
     - Term dropdown populated dengan academic terms
     - Report type dropdown memiliki opsi standard

5. **Configure Individual Report Parameters**
   - Aksi:
     - Select "Ali Rahman" dari student dropdown
     - Select "Semester 1 2024/2025" dari term dropdown
     - Pilih "INDIVIDUAL_REPORT_CARD" sebagai report type
   - Verifikasi:
     - Semua form fields terisi dengan benar
     - No validation errors ditampilkan
     - Submit button aktif dan ready

6. **Submit Background Generation Request**
   - Aksi: Klik "Start Background Generation"
   - Verifikasi:
     - Success notification muncul dengan message "Background report generation initiated"
     - Response JSON contains batchId
     - Status URL provided untuk monitoring
     - Form otomatis tertutup setelah submit

#### Bagian 3: Monitor Background Processing
7. **Automatic Status Monitoring**
   - Aksi: System otomatis redirect ke status monitor
   - Verifikasi:
     - Status monitor interface terbuka
     - Progress bar initialization ditampilkan
     - Batch ID dan processing information visible
     - Real-time polling dimulai (setiap 2 detik)

8. **Real-time Progress Updates**
   - Aksi: Observe automatic status updates
   - Verifikasi:
     - Status progression: INITIATED → VALIDATING → IN_PROGRESS → COMPLETED
     - Progress bar percentage updates (0% → 100%)
     - Processing metrics updated (completed, pending, failed items)
     - Estimated completion time displayed

9. **Processing Completion Verification**
   - Aksi: Wait for processing completion (maximum 2 minutes)
   - Verifikasi:
     - Status changes to "COMPLETED"
     - Progress bar reaches 100%
     - Success message "Generation Completed Successfully!" displayed
     - File path dan file size information available

#### Bagian 4: Result Verification and Access
10. **Verify Generation Results**
    - Aksi: Review completed batch information
    - Verifikasi:
      - Total reports generated: 1
      - Successful reports: 1
      - Failed reports: 0
      - Actual duration within expected range (30-60 seconds)
      - File generated dengan proper naming convention

11. **Access Generated Report**
    - Aksi: Klik download/access link if available
    - Verifikasi:
      - PDF file dapat didownload
      - File size dalam expected range (1-2 MB)
      - Report content accurate dengan data Ali Rahman
      - Student level "Tahfidz 2" ditampilkan dengan benar

12. **Verify Report Content Quality**
    - Aksi: Open dan review PDF content
    - Verifikasi:
      - Student name: Ali Rahman displayed correctly
      - Academic term: Semester 1 2024/2025 shown
      - Grade components visible dan accurate
      - Final grade A- (87.5) calculated correctly
      - Attendance rate 92% displayed
      - Teacher feedback included dan formatted properly

---

## BRG-HP-002: Bulk Class Background Report Generation

### Informasi Skenario
- **ID Skenario**: BRG-HP-002 (Background Report Generation - Happy Path - 002)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 15-20 menit

### Prasyarat
- Multiple students dengan complete assessment data
- Class group assignments configured dengan proper enrollments
- Academic term active dengan multiple enrolled students
- Background processing service dengan sufficient resources
- PDF generation service operational

### Data Test
```
Bulk Generation Parameters:
- Academic Term: Semester 1 2024/2025
- Filter by Class: Tahsin 1 - Kelas Pagi A (optional)
- Filter by Level: Tahfidz 2 (optional)
- Include Student Reports: Yes
- Include Class Summaries: Yes

Expected Results:
- Total Students: 5-8 students
- Estimated Reports: 6-10 reports
- Estimated Duration: 3-5 minutes
- File Format: PDF for each report
- Total Size: 10-20 MB
```

### Langkah Pengujian

#### Bagian 1: Initiate Bulk Generation
1. **Access Bulk Generation Form**
   - Aksi: Dari background dashboard, klik "Start Bulk Generation"
   - Verifikasi:
     - Bulk report form terbuka
     - Term, class, dan level dropdowns populated
     - Report options checkboxes available

2. **Configure Bulk Parameters**
   - Aksi:
     - Select "Semester 1 2024/2025" untuk term
     - Optional: Select class filter "Tahsin 1 - Kelas Pagi A"
     - Check "Include Student Reports"
     - Check "Include Class Summaries"
   - Verifikasi:
     - All form fields configured correctly
     - Configuration preview shows expected scope
     - Submit button enabled

3. **Submit Bulk Generation Request**
   - Aksi: Klik "Start Bulk Generation"
   - Verifikasi:
     - Success notification: "Background bulk generation initiated"
     - BatchId returned untuk tracking
     - Estimated report count displayed (6-10 reports)
     - Estimated duration provided (3-5 minutes)

#### Bagian 2: Monitor Bulk Processing
4. **Processing Validation Phase**
   - Aksi: Monitor initial validation phase
   - Verifikasi:
     - Status: VALIDATING
     - Progress: 0-10%
     - Validation checks for data completeness running
     - No critical errors dalam validation

5. **Processing Generation Phase**
   - Aksi: Observe generation phase progression
   - Verifikasi:
     - Status changes to: IN_PROGRESS
     - Progress incrementally increases (10% → 90%)
     - Individual report items processed sequentially
     - Processing time per report reasonable (30-60 seconds each)

6. **Completion Monitoring**
   - Aksi: Wait for bulk processing completion
   - Verifikasi:
     - Status changes to: COMPLETED
     - Progress reaches: 100%
     - All report items marked as COMPLETED
     - No items marked as FAILED
     - Actual duration within estimated range

#### Bagian 3: Results Verification
7. **Verify Bulk Results Summary**
   - Aksi: Review completion summary
   - Verifikasi:
     - Total reports generated matches expected count
     - All student reports generated successfully
     - Class summary reports included if configured
     - File paths dan sizes reported correctly

8. **Quality Check Sample Reports**
   - Aksi: Download dan review sample generated reports
   - Verifikasi:
     - PDF quality consistent across all reports
     - Student data accurate dalam each report
     - Class summary data comprehensive
     - No data inconsistencies atau corruption

---

## BRG-HP-003: Background Processing Status Monitoring

### Informasi Skenario
- **ID Skenario**: BRG-HP-003 (Background Report Generation - Happy Path - 003)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Active background processing batch running
- Multiple report items dalam various stages
- Real-time polling system operational

### Data Test
```
Monitoring Scenarios:
- In-Progress Batch: Multiple items being processed
- Status Updates: Real-time progress tracking
- Completion Notifications: Success/failure alerts
- Error Handling: Graceful error display
```

### Langkah Pengujian

#### Bagian 1: Status Dashboard Access
1. **Access Status Monitor**
   - Aksi: Klik "View Status" dari dashboard atau access ongoing batch
   - Verifikasi:
     - Status monitor interface opens
     - Current batch information displayed
     - Real-time polling started automatically

2. **Verify Status Components**
   - Aksi: Review status display elements
   - Verifikasi:
     - Progress bar dengan accurate percentage
     - Item counts (completed, pending, failed)
     - Batch status clearly indicated
     - Time information (started, estimated completion)

#### Bagian 2: Real-time Updates
3. **Monitor Progress Updates**
   - Aksi: Observe automatic status refreshes
   - Verifikasi:
     - Status updates every 2 seconds
     - Progress bar animates smoothly
     - Counters increment accurately
     - No UI freezing atau unresponsive behavior

4. **Verify Update Accuracy**
   - Aksi: Compare UI updates dengan actual processing
   - Verifikasi:
     - Progress percentage matches actual completion
     - Item status changes reflected immediately
     - Time estimates reasonably accurate
     - Status transitions logical dan expected

#### Bagian 3: Completion Handling
5. **Completion Detection**
   - Aksi: Wait for processing completion
   - Verifikasi:
     - Status automatically updates to COMPLETED
     - Polling stops when processing finished
     - Success notification displayed
     - Completion actions available

6. **Post-Completion Actions**
   - Aksi: Review available post-completion options
   - Verifikasi:
     - Download links available for completed reports
     - Results summary comprehensive
     - File access working correctly
     - Return navigation options provided

---

## BRG-HP-004: Background Processing Cancellation

### Informasi Skenario
- **ID Skenario**: BRG-HP-004 (Background Report Generation - Happy Path - 004)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 5-7 menit

### Prasyarat
- Active background processing batch
- Batch dalam cancellable state (IN_PROGRESS atau VALIDATING)
- User has appropriate permissions untuk cancellation

### Data Test
```
Cancellation Scenarios:
- Batch Status: IN_PROGRESS
- Items Status: Mix of PENDING, GENERATING, COMPLETED
- Expected Behavior: Graceful shutdown dengan cleanup
```

### Langkah Pengujian

#### Bagian 1: Initiate Cancellation
1. **Start Processing untuk Cancellation Test**
   - Aksi: Start a bulk generation dengan multiple items
   - Verifikasi: Batch processing started dan in cancellable state

2. **Access Cancellation Option**
   - Aksi: Klik "Cancel Generation" button dari status monitor
   - Verifikasi:
     - Cancellation confirmation dialog appears
     - Warning message about stopping current process
     - Confirm/Cancel options available

3. **Confirm Cancellation**
   - Aksi: Klik "Confirm" pada cancellation dialog
   - Verifikasi:
     - Cancellation request submitted successfully
     - Status immediately changes to "CANCELLING"
     - Processing stops gracefully

#### Bagian 2: Verify Cancellation Results
4. **Monitor Cancellation Process**
   - Aksi: Observe cancellation process
   - Verifikasi:
     - Status changes to: CANCELLED
     - Pending items marked as CANCELLED
     - Completed items remain as COMPLETED
     - In-progress items stopped cleanly

5. **Verify Post-Cancellation State**
   - Aksi: Review final batch state
   - Verifikasi:
     - Batch marked as cancelled dengan timestamp
     - Partial results available untuk completed items
     - No system errors atau inconsistent state
     - Resources properly cleaned up

---

## BRG-HP-005: Error Handling and Recovery

### Informasi Skenario
- **ID Skenario**: BRG-HP-005 (Background Report Generation - Happy Path - 005)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- System dalam normal operating state
- Error simulation capabilities available
- Monitoring systems active

### Data Test
```
Error Scenarios:
- Network Interruption: Temporary connection loss
- Service Restart: Background service restart during processing
- Data Issues: Invalid data handling
- Resource Constraints: High load conditions
```

### Langkah Pengujian

#### Bagian 1: Network Interruption Handling
1. **Simulate Network Interruption**
   - Aksi: Start processing, then simulate network loss
   - Verifikasi:
     - System gracefully handles connection loss
     - Status polling attempts retry mechanism
     - Error notification displayed appropriately

2. **Network Recovery**
   - Aksi: Restore network connection
   - Verifikasi:
     - Status polling resumes automatically
     - Processing state accurately recovered
     - No data loss atau corruption

#### Bagian 2: System Error Recovery
3. **Verify Error Display**
   - Aksi: Observe error handling dalam various scenarios
   - Verifikasi:
     - Error messages clear dan actionable
     - System remains stable during errors
     - Recovery options provided where applicable

4. **Test Recovery Mechanisms**
   - Aksi: Execute recovery actions
   - Verifikasi:
     - Recovery actions work as expected
     - System returns to stable state
     - No persistent issues remain

---

## BRG-HP-006: Integration with Main Reports System

### Informasi Skenario
- **ID Skenario**: BRG-HP-006 (Background Report Generation - Happy Path - 006)
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, MANAGEMENT
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Main reports system operational
- Background generation integrated
- Navigation links configured

### Data Test
```
Integration Points:
- Navigation: Main reports → Background generation
- Return Navigation: Background → Main reports
- Data Consistency: Same data between systems
- Access Control: Consistent permissions
```

### Langkah Pengujian

#### Bagian 1: Navigation Integration
1. **Access dari Main Reports**
   - Aksi: Navigate dari Student Reports page ke Background Generation
   - Verifikasi:
     - Navigation link clearly visible
     - Navigation transitions smoothly
     - Context preserved during navigation

2. **Return Navigation**
   - Aksi: Navigate back ke main reports dari background dashboard
   - Verifikasi:
     - Return navigation available
     - Previous context restored
     - No data loss during navigation

#### Bagian 2: Data Consistency
3. **Verify Data Consistency**
   - Aksi: Compare data between main reports dan background generation
   - Verifikasi:
     - Student lists identical
     - Term information consistent
     - Assessment data matches
     - No discrepancies dalam data presentation

4. **Test Access Control Integration**
   - Aksi: Verify permissions work consistently
   - Verifikasi:
     - Same permission requirements
     - Access control enforced properly
     - Role-based access working

### Expected Outcomes (Semua Skenario)

#### Functional Outcomes
- Background report generation berfungsi dengan reliable
- Processing times reasonable untuk individual dan bulk operations
- Status monitoring accurate dan real-time
- Error handling robust dan user-friendly
- Integration seamless dengan existing reports system

#### Performance Outcomes
- Individual reports generated dalam 30-60 seconds
- Bulk reports processed efficiently dengan parallel processing
- System remains responsive during background processing
- Resource usage optimized dan sustainable

#### User Experience Outcomes
- Intuitive interface untuk initiating background processing
- Clear feedback dan progress indication
- Smooth navigation between related features
- Comprehensive error messages dan recovery guidance

### Notes untuk Manual Tester
- Test dengan different data volumes untuk performance verification
- Verify browser compatibility (Chrome, Firefox, Safari)
- Test dengan different user roles untuk access control verification
- Document any performance issues atau unexpected behaviors
- Verify mobile responsiveness jika applicable

### Automated Test Reference
- File: `src/test/java/com/sahabatquran/webapp/functional/scenarios/reporting/BackgroundReportGenerationTest.java`
- Covers: All scenarios outlined dalam dokumen ini
- Execution: `./mvnw test -Dtest="BackgroundReportGenerationTest"`