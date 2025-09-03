# Skenario Pengujian: Laporan Siswa dan Transkrip Nilai - Happy Path

## Informasi Umum
- **Kategori**: Aktivitas Semester - Laporan dan Transkrip
- **Modul**: Sistem Pelaporan Siswa dan Transkrip Nilai
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario untuk laporan siswa dan transkrip nilai
- **Playwright Test**: `semester-activities.StudentReportTest`

---

## LS-HP-001: Generate Laporan Kartu Nilai Siswa Individual

### Informasi Skenario
- **ID Skenario**: LS-HP-001 (Laporan Siswa - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF, FINANCE_STAFF
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Semester dalam status ACTIVE atau COMPLETED
- Siswa sudah memiliki data nilai lengkap dalam semester
- Data kehadiran siswa tercatat dengan baik
- Feedback evaluasi teacher sudah tersedia
- Academic term sudah memiliki data grade calculation

### Data Test
```
Student Data:
- Nama: Ali Rahman
- Level: Tahfidz 2
- Academic Term: Semester 1 2024/2025
- Final Grade: A- (87.5)
- Attendance Rate: 92%
- Teacher Feedback: "Menunjukkan kemajuan konsisten dalam hafalan"

Grade Components:
- Placement Test: 85/100
- Midterm Assessment: 90/100  
- Final Assessment: 87/100
- Teacher Evaluation: A-
- Attendance Score: 92%
```

### Langkah Pengujian

#### Bagian 1: Akses dan Navigasi Laporan
1. **Login sebagai Admin Staff**
   - Aksi: Login dengan `staff.admin1` / `Welcome@YSQ2024`
   - Verifikasi: Dashboard admin staff muncul dengan menu reports

2. **Navigate ke Student Reports**
   - Aksi: Klik menu "Reports" → "Student Report Cards"
   - Verifikasi: 
     - Halaman student reports terbuka
     - List semua siswa dalam term aktif ditampilkan
     - Filter options tersedia (term, level, status)

3. **Select Academic Term**
   - Aksi: Pilih academic term dari dropdown
   - Verifikasi:
     - Student list filtered sesuai term yang dipilih
     - Academic term information clearly displayed
     - Term status (ACTIVE/COMPLETED) shown

#### Bagian 2: Generate Individual Report Card
4. **Search dan Select Student**
   - Aksi: Search "Ali Rahman" dan klik "Generate Report"
   - Verifikasi:
     - Student profile information displayed
     - Academic performance summary shown
     - Report generation options available

5. **Configure Report Parameters**
   - Aksi: Select report components (grades, attendance, feedback, progression)
   - Verifikasi:
     - Report customization options working
     - Preview information accurate
     - Generated date automatically set

6. **Generate Report Card**
   - Aksi: Klik "Generate Report Card"
   - Verifikasi:
     - PDF report generated successfully
     - Report contains all student information:
       * Student basic info (name, level, term)
       * Grade breakdown with component scores
       * Attendance summary with percentage
       * Teacher feedback and evaluation
       * Academic progression status
       * School/institution branding
     - Report download link available
     - Generation timestamp recorded

#### Bagian 3: Verifikasi Report Content
7. **Validate Report Data Accuracy**
   - Verifikasi Report PDF contains:
     - **Header**: School name, academic term, generation date
     - **Student Info**: Name, student ID, level, class
     - **Academic Performance**: 
       * Placement test score: 85/100
       * Midterm assessment: 90/100
       * Final assessment: 87/100
       * Overall grade: A- (87.5)
     - **Attendance**: 92% dengan breakdown per month
     - **Teacher Evaluation**: Qualitative feedback
     - **Academic Progress**: Level progression recommendation
     - **Footer**: Authorized signatures, school seal

8. **Multi-format Report Generation**
   - Aksi: Generate report dalam format PDF dan HTML
   - Verifikasi:
     - Both formats contain identical information
     - PDF optimized untuk print
     - HTML optimized untuk digital viewing
     - Formatting consistent across formats

### Kriteria Sukses
- [ ] Report generation process seamless dan user-friendly
- [ ] Generated report accurate dan comprehensive
- [ ] All grade components properly calculated dan displayed
- [ ] Student progression information clearly presented
- [ ] Report formatting professional dan consistent
- [ ] Multi-format generation working correctly

---

## LS-HP-002: Generate Transkrip Nilai Multi-Semester

### Informasi Skenario
- **ID Skenario**: LS-HP-002
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF, MANAGEMENT
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Student sudah menyelesaikan multiple semesters
- Historical grade data tersedia untuk multiple terms
- Academic progression history complete
- Cross-term data validation passed

### Data Test
```
Student Multi-Term History:
Nama: Fatima Zahra
Academic History:
- Semester 1 2023/2024: Tahsin 2 (Grade: A-, 88.5)
- Semester 2 2023/2024: Tahsin 3 (Grade: B+, 86.0) 
- Semester 1 2024/2025: Tahfidz 1 (Grade: A, 91.2)
- Current: Semester 2 2024/2025: Tahfidz 2 (In Progress)

Cumulative GPA: 3.52
Total Study Duration: 1.5 years
Academic Progression: Normal
```

### Langkah Pengujian

#### Bagian 1: Access Multi-Term Transcript
1. **Navigate ke Academic Transcripts**
   - Aksi: Access "Reports" → "Academic Transcripts"
   - Verifikasi: 
     - Multi-term transcript generation page terbuka
     - Student search functionality available
     - Historical data access enabled

2. **Search Multi-Term Student**
   - Aksi: Search "Fatima Zahra" by name atau student ID
   - Verifikasi:
     - Student profile dengan academic history displayed
     - All completed semesters listed
     - Academic progression timeline shown

#### Bagian 2: Configure Transcript Parameters
3. **Select Term Range**
   - Aksi: Select date range atau specific semesters untuk transcript
   - Verifikasi:
     - Flexible term selection options
     - Selected terms clearly highlighted
     - Grade data preview for selected terms

4. **Configure Transcript Content**
   - Aksi: Choose transcript components:
     * Academic grades by semester
     * Attendance records
     * Teacher evaluations
     * Progression milestones
     * Cumulative GPA calculation
   - Verifikasi: Content options properly configured

#### Bagian 3: Generate Official Transcript
5. **Generate Multi-Term Transcript**
   - Aksi: Klik "Generate Official Transcript"
   - Verifikasi:
     - PDF transcript generated successfully
     - Transcript contains comprehensive information:
       * Student identification dan personal info
       * Complete academic history by semester
       * Grade progression with GPA calculations
       * Attendance summary across all terms
       * Teacher evaluations dan recommendations
       * Academic milestones dan achievements
       * Official school certification

6. **Validate Cross-Term Data Consistency**
   - Verifikasi Transcript accuracy:
     - **Semester-by-semester breakdown**: Each term properly detailed
     - **GPA Calculations**: Cumulative GPA accurate (3.52)
     - **Academic Progression**: Level advancement properly tracked
     - **Date Consistency**: All dates accurate dan chronological
     - **Grade Validation**: No missing atau inconsistent grades
     - **Official Formatting**: Professional layout dengan watermark

### Kriteria Sukses
- [ ] Multi-term transcript generation successful
- [ ] Cross-term data properly consolidated
- [ ] Academic progression accurately represented
- [ ] GPA calculations mathematically correct
- [ ] Official transcript formatting professional
- [ ] Historical data integrity maintained

---

## LS-HP-003: Parent Access ke Student Reports

### Informasi Skenario
- **ID Skenario**: LS-HP-003
- **Prioritas**: Sedang
- **Role**: PARENT (Student Guardian)
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Parent account sudah terkait dengan student account
- Student sudah memiliki generated reports
- Parent notification system active
- Report sharing permissions configured

### Data Test
```
Parent Account:
- Username: parent.ali.rahman
- Password: Parent@YSQ2024
- Associated Student: Ali Rahman
- Email: parent.ali@example.com

Available Reports:
- Current Semester Report Card
- Progress Reports (3 available)
- Attendance Summary
- Teacher Feedback Reports
```

### Langkah Pengujian

#### Bagian 1: Parent Portal Access
1. **Login sebagai Parent**
   - Aksi: Login dengan parent credentials
   - Verifikasi:
     - Parent dashboard muncul
     - Student information clearly displayed
     - Reports menu accessible

2. **Navigate ke Student Reports**
   - Aksi: Klik "My Child's Reports" atau "Academic Reports"
   - Verifikasi:
     - List of available reports displayed
     - Report categories organized (grades, attendance, feedback)
     - Recent reports highlighted

#### Bagian 2: Access dan View Reports
3. **View Current Semester Report**
   - Aksi: Klik "Current Semester Report Card"
   - Verifikasi:
     - PDF report opens dalam browser
     - Report data matches staff-generated version
     - Download option available
     - Print-friendly format

4. **Access Historical Reports**
   - Aksi: Navigate ke "Historical Reports" atau "Past Semesters"
   - Verifikasi:
     - Previous semester reports available
     - Reports organized chronologically
     - Quick access buttons functioning

#### Bagian 3: Report Notifications dan Communication
5. **Check Report Notifications**
   - Verifikasi:
     - Email notifications received untuk new reports
     - In-app notifications working
     - Notification timestamps accurate
     - Read/unread status properly tracked

6. **Parent Feedback Submission**
   - Aksi: Submit feedback atau questions about report
   - Verifikasi:
     - Feedback form accessible
     - Message sent to appropriate staff
     - Confirmation received
     - Communication thread created

### Kriteria Sukses
- [ ] Parent portal access smooth dan intuitive
- [ ] Student reports accurately displayed
- [ ] Historical reports properly organized
- [ ] Notification system functioning correctly
- [ ] Parent-school communication enabled
- [ ] Report security dan privacy maintained

---

## LS-HP-004: Bulk Report Generation untuk Class/Level

### Informasi Skenario
- **ID Skenario**: LS-HP-004
- **Prioritas**: Sedang
- **Role**: ADMIN_STAFF, MANAGEMENT
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- Class atau level memiliki multiple students dengan complete grade data
- Semester dalam status untuk report generation
- System performance adequate untuk bulk operations
- Storage space sufficient untuk multiple reports

### Data Test
```
Class Data:
Level: Tahfidz 2
Total Students: 15
Academic Term: Semester 1 2024/2025
Teacher: Ustadz Ahmad

Students with Complete Data:
- Ali Rahman (A-, 87.5)
- Fatima Zahra (A, 91.2)
- Omar Hassan (B+, 85.8)
[... 12 more students]

Expected Output: 15 individual report cards
```

### Langkah Pengujian

#### Bagian 1: Bulk Generation Setup
1. **Access Bulk Report Generation**
   - Aksi: Navigate ke "Reports" → "Bulk Generation"
   - Verifikasi:
     - Bulk generation interface available
     - Class/level selection options displayed
     - Generation parameters configurable

2. **Select Target Class**
   - Aksi: Select "Tahfidz 2" class dari dropdown
   - Verifikasi:
     - Student list for selected class displayed (15 students)
     - Each student's data completion status shown
     - Missing data highlighted dengan warnings

#### Bagian 2: Configure Bulk Parameters
3. **Set Report Configuration**
   - Aksi: Configure bulk report parameters:
     * Report type: Semester report cards
     * Include components: Grades, attendance, feedback
     * Output format: PDF batch
     * Delivery method: Download zip file
   - Verifikasi: Configuration options properly set

4. **Validate Data Completeness**
   - Verifikasi:
     - All students have required data for report generation
     - Missing data cases flagged untuk review
     - Data quality checks passed
     - Estimated generation time displayed

#### Bagian 3: Execute Bulk Generation
5. **Start Bulk Report Generation**
   - Aksi: Klik "Generate All Reports"
   - Verifikasi:
     - Progress indicator shown (processing X of 15)
     - Individual report status updates
     - Estimated completion time accurate
     - System remains responsive during bulk operation

6. **Download dan Validate Results**
   - Aksi: Download generated report package
   - Verifikasi:
     - ZIP file contains 15 individual PDF reports
     - Each report properly named (StudentName_SemesterReport.pdf)
     - All reports contain accurate, unique student data
     - File sizes reasonable dan consistent
     - No corrupted atau incomplete files

### Kriteria Sukses
- [ ] Bulk generation process efficient dan reliable
- [ ] All student reports generated successfully
- [ ] Individual report quality maintained
- [ ] System performance acceptable during bulk operation
- [ ] File organization dan naming consistent
- [ ] Download package properly structured

---

## LS-HP-005: Semester Closure dengan Report Generation

### Informasi Skenario
- **ID Skenario**: LS-HP-005
- **Prioritas**: Tinggi
- **Role**: MANAGEMENT, ADMIN_STAFF
- **Estimasi Waktu**: 15-20 menit

### Prasyarat
- Semester mendekati completion
- All student grades finalized dan approved
- Attendance data complete untuk all students
- Teacher evaluations submitted untuk all classes
- Final grade calculations verified

### Data Test
```
Semester Closure Data:
Academic Term: Semester 1 2024/2025
Status: ACTIVE → COMPLETED transition
Total Students: 45 across all levels
Total Classes: 6 classes
Final Grades Status: 100% completed
Report Generation Required: Yes

Closure Checklist:
- Grade calculations: ✓ Completed
- Attendance verification: ✓ Completed  
- Teacher evaluations: ✓ Completed
- Report generation: ⏳ In Progress
- Parent notifications: ⏳ Pending
- Data archival: ⏳ Pending
```

### Langkah Pengujian

#### Bagian 1: Pre-Closure Validation
1. **Access Semester Closure Interface**
   - Aksi: Navigate ke "Management" → "Semester Closure"
   - Verifikasi:
     - Closure checklist displayed
     - Current term status shown (ACTIVE)
     - Prerequisites validation results shown

2. **Validate Data Completeness**
   - Aksi: Run "Pre-Closure Validation Check"
   - Verifikasi:
     - All grades finalized: ✓
     - Attendance records complete: ✓
     - Teacher evaluations submitted: ✓
     - Any missing data flagged untuk completion
     - Validation summary accurate

#### Bagian 2: Mass Report Generation
3. **Initiate Semester-End Report Generation**
   - Aksi: Start "Generate All Semester Reports"
   - Verifikasi:
     - Mass report generation process launched
     - Progress tracking untuk each class
     - Individual report generation status
     - System performance monitoring active

4. **Monitor Bulk Generation Progress**
   - Verifikasi during generation:
     - Real-time progress updates (X of 45 reports completed)
     - Class-by-class completion status
     - Error handling untuk any failed generations
     - System remains stable under load
     - Estimated time remaining accurate

#### Bagian 3: Complete Semester Closure
5. **Finalize Report Distribution**
   - Aksi: Configure report distribution:
     * Generate parent notification emails
     * Prepare reports untuk download
     * Archive semester data
   - Verifikasi:
     - Parent notifications queued untuk sending
     - Report access permissions configured
     - Data archival process initiated

6. **Execute Semester Closure**
   - Aksi: Confirm "Close Semester"
   - Verifikasi:
     - Academic term status changed: ACTIVE → COMPLETED
     - All reports successfully generated (45/45)
     - Parent notifications sent
     - Historical data properly archived
     - System audit trail created
     - New term preparation can begin

### Kriteria Sukses
- [ ] Semester closure process comprehensive dan systematic
- [ ] All student reports generated without errors
- [ ] Data validation dan completeness checks passed
- [ ] Parent communication successfully executed
- [ ] Historical data properly preserved
- [ ] System transition smooth untuk next term preparation

---

## LS-HP-006: Laporan Analitik Performa Class dan Level

### Informasi Skenario
- **ID Skenario**: LS-HP-006
- **Prioritas**: Sedang
- **Role**: MANAGEMENT, INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Multiple classes dengan complete semester data
- Grade distribution data available
- Teacher performance metrics calculated
- Comparative analysis data ready

### Data Test
```
Analytic Report Data:
Academic Term: Semester 1 2024/2025
Classes Analyzed:
- Tahsin 1: 8 students, Avg Grade: 85.2
- Tahsin 2: 12 students, Avg Grade: 87.8
- Tahsin 3: 10 students, Avg Grade: 86.5
- Tahfidz 1: 9 students, Avg Grade: 89.1
- Tahfidz 2: 6 students, Avg Grade: 88.9

Performance Metrics:
- Overall semester average: 87.5
- Attendance rate: 91.2%
- Student satisfaction: 4.3/5.0
- Teacher effectiveness: 4.4/5.0
```

### Langkah Pengujian

#### Bagian 1: Generate Class Performance Reports
1. **Access Analytics Dashboard**
   - Aksi: Navigate ke "Reports" → "Analytics" → "Class Performance"
   - Verifikasi:
     - Analytics interface available
     - Term selection options displayed
     - Performance metrics overview shown

2. **Generate Class-Level Analytics**
   - Aksi: Select classes untuk analysis dan generate report
   - Verifikasi:
     - Individual class performance data displayed
     - Grade distribution charts generated
     - Attendance patterns analyzed
     - Comparative metrics calculated

#### Bagian 2: Level Progression Analysis
3. **Analyze Level Progression Patterns**
   - Aksi: Generate "Level Progression Report"
   - Verifikasi:
     - Student advancement rates by level
     - Success rates untuk each level transition
     - Identification of challenging progression points
     - Recommendations untuk curriculum improvement

#### Bagian 3: Teacher Performance Analytics
4. **Generate Teacher Effectiveness Reports**
   - Aksi: Create teacher performance analytics
   - Verifikasi:
     - Teaching effectiveness metrics per instructor
     - Student feedback aggregation
     - Class performance correlation dengan teacher assignment
     - Professional development recommendations

#### Bagian 4: Executive Summary Generation
5. **Create Management Summary Report**
   - Aksi: Generate comprehensive semester analytics summary
   - Verifikasi:
     - Executive dashboard dengan key metrics
     - Trend analysis dan insights
     - Actionable recommendations
     - Visual charts dan graphs untuk presentation

6. **Export Analytics Package**
   - Aksi: Export complete analytics package
   - Verifikasi:
     - PDF executive summary
     - Excel data sheets dengan raw metrics
     - PowerPoint presentation ready charts
     - Dashboard screenshots untuk documentation

### Kriteria Sukses
- [ ] Comprehensive analytics generation successful
- [ ] Data visualization clear dan informative
- [ ] Actionable insights provided
- [ ] Multiple export formats available
- [ ] Performance trends accurately identified
- [ ] Management reporting requirements met

---

## Integration dengan Automated Test

### Playwright Test Mapping

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|---------------|--------|-------------|
| Generate Report Card Individual | `semester-activities.StudentReportTest.testGenerateIndividualReportCard` | 🔄 Planned | `scenarios/semester-activities/StudentReportTest.java` |
| Multi-Semester Transcript | `semester-activities.StudentReportTest.testGenerateMultiTermTranscript` | 🔄 Planned | `scenarios/semester-activities/StudentReportTest.java` |
| Parent Report Access | `semester-activities.ParentPortalTest.testParentReportAccess` | 🔄 Planned | `scenarios/semester-activities/ParentPortalTest.java` |
| Bulk Report Generation | `semester-activities.AdminStaffTest.testBulkReportGeneration` | 🔄 Planned | `scenarios/semester-activities/AdminStaffTest.java` |
| Semester Closure Reports | `semester-activities.ManagementTest.testSemesterClosureReports` | 🔄 Planned | `scenarios/semester-activities/ManagementTest.java` |
| Class Analytics Reports | `semester-activities.ManagementTest.testClassAnalyticsReports` | 🔄 Planned | `scenarios/semester-activities/ManagementTest.java` |

### Test Data Requirements

#### Grade Calculation Test Data
```java
// Required for comprehensive report testing
TestGradeData placementTest = TestGradeData.builder()
    .score(85.0)
    .maxScore(100.0)
    .weightage(0.2)
    .build();

TestGradeData midtermAssessment = TestGradeData.builder()
    .score(90.0)  
    .maxScore(100.0)
    .weightage(0.3)
    .build();

TestGradeData finalAssessment = TestGradeData.builder()
    .score(87.0)
    .maxScore(100.0) 
    .weightage(0.4)
    .build();

TestAttendanceData attendance = TestAttendanceData.builder()
    .attendanceRate(92.0)
    .weightage(0.1)
    .build();
```

## Business Rules Validation

### Report Generation Rules
- [ ] Reports hanya dapat di-generate untuk semesters dengan complete grade data
- [ ] Multi-term transcripts require all historical data validation
- [ ] Parent access limited ke associated student reports only
- [ ] Bulk generation limited by system performance constraints
- [ ] Official transcripts require management approval untuk external use

### Data Privacy dan Security
- [ ] Student report data encrypted in transit dan at rest
- [ ] Parent access properly authenticated dan authorized
- [ ] Report sharing logs maintained untuk audit
- [ ] Sensitive information redacted dalam sample data
- [ ] GDPR compliance untuk report data handling

### Performance Requirements
- [ ] Individual report generation < 5 seconds
- [ ] Bulk generation progress updates every 2 seconds
- [ ] Multi-term transcript generation < 10 seconds
- [ ] Parent portal response time < 3 seconds
- [ ] Analytics report generation < 15 seconds

---

**Catatan Implementasi**:
- Semua skenario terintegrasi dengan existing semester activities workflow
- Report generation menggunakan existing grade calculation dan attendance systems
- Parent portal integration dengan current authentication system
- Analytics leverage existing performance tracking infrastructure
- Cross-term functionality consistent dengan multi-term management patterns