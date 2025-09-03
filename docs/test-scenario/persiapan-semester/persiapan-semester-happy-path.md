# Skenario Pengujian: Persiapan Semester - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Akademik
- **Modul**: Academic Planning & Class Preparation
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario utama (Academic Admin, Management, Instructor)

---

## PS-HP-001: Academic Admin - Assessment Foundation Review

### Informasi Skenario
- **ID Skenario**: PS-HP-001 (Persiapan Semester - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Admin staff account tersedia: `academic.admin1` / `Welcome@YSQ2024`
- Database memiliki data siswa baru dengan placement tests completed
- Database memiliki data siswa lama dengan exam results dari semester sebelumnya
- Minimal 50+ siswa untuk realistic class distribution

### Data Test
```
Academic Admin Login:
Username: academic.admin1
Password: Welcome@YSQ2024

Assessment Data Expected:
New Students: 45 registrations
- Placement Tests Completed: 38/45 (84%)
- Tests Scheduled: 4/45 (9%)
- Tests Not Scheduled: 3/45 (7%)

Existing Students: 113 continuing students
- Exam Results Submitted: 95/113 (84%)
- Partial Results: 8/113 (7%)
- Results Missing: 10/113 (9%)

Overall Readiness: 133/158 students (84%) ready for class assignment
```

### Langkah Pengujian

#### Bagian 1: Login dan Access Assessment Foundation
1. **Login sebagai Academic Admin**
   - Aksi: Login dengan credentials academic admin
   - Verifikasi:
     - Login berhasil
     - Dashboard academic admin muncul
     - Menu academic planning tersedia

2. **Navigate ke Assessment Foundation**
   - Aksi: Akses `/academic/assessment-foundation`
   - Verifikasi:
     - Halaman assessment foundation terbuka
     - Dashboard statistics muncul
     - Real-time data displayed

#### Bagian 2: Review New Students Assessment
3. **Review New Students section**
   - Verifikasi:
     - Total new registrations: 45 students
     - Placement tests completed: 38/45 (84%)
     - Progress bars accurate
     - Status breakdown clear
     - Export functionality available

4. **View detailed placement test status**
   - Aksi: Klik "View Details" pada new students section
   - Verifikasi:
     - List siswa baru dengan status placement test
     - Filter by status working
     - Search functionality
     - Individual student progress visible

5. **Handle pending placement tests**
   - Aksi: Klik "Schedule Missing Tests" untuk 3 siswa yang belum scheduled
   - Verifikasi:
     - Form scheduling muncul
     - Batch scheduling available
     - Notification system active

#### Bagian 3: Review Existing Students Assessment
6. **Review Existing Students section**
   - Verifikasi:
     - Total continuing students: 113
     - Exam results submitted: 95/113 (84%)
     - Partial results: 8/113
     - Missing results: 10/113
     - Charts dan graphs accurate

7. **Follow up missing exam results**
   - Aksi: Klik "Send Reminders" untuk missing results
   - Verifikasi:
     - Reminder system activated
     - Teacher notifications sent
     - Tracking system updated
     - Follow-up schedule created

#### Bagian 4: Overall Readiness Assessment
8. **Review overall statistics**
   - Verifikasi:
     - Overall readiness: 133/158 (84%)
     - Trend analysis displayed
     - Comparison dengan semester sebelumnya
     - Readiness threshold indicators

9. **Generate assessment reports**
   - Aksi: Klik "Generate Report" dan "Export Data"
   - Verifikasi:
     - PDF report generated
     - Excel export successful
     - Email notification sent
     - Report contains comprehensive data

10. **Move to next phase**
    - Aksi: Klik "Proceed to Level Distribution"
    - Verifikasi:
      - Validation check passed (80%+ readiness)
      - Redirect ke level distribution page
      - Phase progression tracked

### Hasil Diharapkan
- Admin staff berhasil review assessment foundation
- Data assessment accuracy verified (84% completion)
- Pending items identified dan action taken
- Ready untuk proceed ke level distribution phase
- Complete audit trail dan reporting

### Kriteria Sukses
- [ ] Login dan akses assessment foundation berhasil
- [ ] New students placement test data accurate
- [ ] Existing students exam results tracked
- [ ] Overall readiness statistics correct
- [ ] Pending items handling functional
- [ ] Export dan reporting working
- [ ] Phase progression allowed

---

## PS-HP-002: Academic Admin - Level Distribution Analysis

### Informasi Skenario
- **ID Skenario**: PS-HP-002
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Assessment foundation completed (dari PS-HP-001)
- Student level assignments ready
- Level capacity configuration available

### Data Test
```
Expected Level Distribution:
Tahsin 1 Foundation: 25 students (new beginners)
Tahsin 1 Standard: 30 students (mixed)
Tahsin 1 Advanced: 20 students (basic plus)
Tahsin 2: 35 students (intermediate level)
Tahsin 3: 23 students (advanced level)
Tahfidz Pemula: 10 students (advanced quran readers)

Total Classes Required: 
Tahsin 1: 9-10 classes (8-12 students per class)
Tahsin 2: 4-5 classes (7-10 students per class)
Tahsin 3: 3 classes (7-8 students per class)
Tahfidz: 2-3 classes (4-5 students per class)
```

### Langkah Pengujian

#### Bagian 1: Access Level Distribution
1. **Navigate ke level distribution**
   - Aksi: Dari assessment foundation, lanjut ke `/academic/level-distribution`
   - Verifikasi:
     - Level distribution dashboard muncul
     - Charts dan graphs visible
     - Interactive filters available

#### Bagian 2: Analyze Student Distribution
2. **Review level assignment results**
   - Verifikasi:
     - Student distribution by level accurate
     - New vs existing breakdown clear
     - Demographic analysis available
     - Capacity planning indicators

3. **Interactive chart analysis**
   - Aksi: Klik pada chart segments untuk drill down
   - Verifikasi:
     - Detailed student lists per level
     - Filter by category (new/existing)
     - Student detail accessible
     - Export functionality

#### Bagian 3: Class Requirement Calculation
4. **Review class requirements**
   - Verifikasi:
     - Automatic class calculation based on level
     - Class size constraints applied
     - Configuration parameters visible
     - Optimal distribution suggested

5. **Adjust class size parameters**
   - Aksi: Modify class size for Tahsin 1 (8-12 students)
   - Verifikasi:
     - Parameter update reflected immediately
     - Recalculation automatic
     - Impact analysis shown
     - Validation rules enforced

#### Bagian 4: Capacity Planning
6. **Check teacher capacity requirements**
   - Verifikasi:
     - Teacher requirement calculation
     - Specialization needs identified
     - Workload distribution planned
     - Resource allocation summary

7. **Generate distribution report**
   - Aksi: Export level distribution analysis
   - Verifikasi:
     - Comprehensive report generated
     - Class planning recommendations
     - Teacher assignment guidelines
     - Ready untuk teacher availability phase

### Kriteria Sukses
- [ ] Level distribution data accurate
- [ ] Chart dan visualization working
- [ ] Class requirement calculation correct
- [ ] Capacity planning functional
- [ ] Parameter adjustment responsive
- [ ] Export dan reporting complete

---

## PS-HP-003: Academic Admin - Teacher Availability Collection

### Informasi Skenario
- **ID Skenario**: PS-HP-003
- **Prioritas**: Tinggi  
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Level distribution completed
- Teacher accounts active (minimal 20 teachers)
- Academic term defined

### Data Test
```
Academic Term: Semester Genap 2024
Teacher Availability Collection Period: 1 week
Expected Teachers: 20 active instructors
Availability Sessions:
- Pagi Awal (06:00-08:00)
- Pagi (08:00-10:00) 
- Siang (10:00-12:00)
- Sore (16:00-18:00)
- Malam (19:00-21:00)
```

### Langkah Pengujian

#### Bagian 1: Launch Availability Collection
1. **Access availability monitoring**
   - Aksi: Navigate ke `/academic/availability-monitoring`
   - Verifikasi:
     - Availability monitoring dashboard muncul
     - Teacher submission status visible
     - Deadline tracking active
     - Progress indicators accurate

2. **Launch collection process**
   - Aksi: Klik "Start Availability Collection"
   - Verifikasi:
     - Collection period activated
     - Teacher notifications sent
     - Deadline set dan visible
     - Tracking system initialized

#### Bagian 2: Monitor Submission Progress
3. **Track teacher submissions**
   - Verifikasi:
     - Real-time submission tracking
     - Progress by teacher visible
     - Completion percentage accurate
     - Pending reminders scheduled

4. **Send reminder notifications**
   - Aksi: Klik "Send Reminder" untuk teachers yang belum submit
   - Verifikasi:
     - Batch reminder functionality
     - Email notifications sent
     - Follow-up schedule updated
     - Tracking log maintained

#### Bagian 3: Review Availability Summary
5. **Analyze availability patterns**
   - Verifikasi:
     - Availability matrix visualization
     - Popular time slots identified
     - Teacher workload distribution
     - Conflict identification

6. **Handle special requests**
   - Aksi: Process teacher requests untuk schedule changes
   - Verifikasi:
     - Request handling workflow
     - Approval/rejection system
     - Communication with teachers
     - Audit trail maintained

### Kriteria Sukses
- [ ] Availability collection launched successfully
- [ ] Teacher notification system working
- [ ] Progress monitoring accurate
- [ ] Reminder system functional
- [ ] Summary analysis complete
- [ ] Ready untuk management assignment phase

---

## PS-HP-004: Management - Teacher Level Assignment

### Informasi Skenario
- **ID Skenario**: PS-HP-004
- **Prioritas**: Tinggi
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- Management account: `management.director` / `Welcome@YSQ2024`
- Teacher availability data complete
- Level requirements defined
- Teacher competency profiles available

### Data Test
```
Management Login:
Username: management.director
Password: Welcome@YSQ2024

Teacher Assignment Strategy:
Tahsin 1 Foundation: Senior teachers (new student specialists)
Tahsin 1 Standard: Mixed experience teachers
Tahsin 2-3: Experienced teachers with progression expertise
Tahfidz: Advanced qualification required

Target Distribution:
- Each teacher: 4-6 classes optimal
- Maximum: 8 classes per teacher
- Minimum: 2 classes per teacher
- Specialization balance maintained
```

### Langkah Pengujian

#### Bagian 1: Management Login dan Dashboard
1. **Login sebagai Management**
   - Aksi: Login dengan management credentials
   - Verifikasi:
     - Management dashboard accessible
     - Teacher level assignment menu available
     - Academic planning permission confirmed
     - Strategic overview displayed

2. **Access teacher level assignments**
   - Aksi: Navigate ke `/management/teacher-level-assignments`
   - Verifikasi:
     - Teacher assignment interface muncul
     - Drag-and-drop functionality available
     - Teacher competency data visible
     - Level requirements displayed

#### Bagian 2: Strategic Teacher Assignment
3. **Review teacher competencies**
   - Verifikasi:
     - Teacher profiles dengan experience level
     - Specialization indicators
     - Current workload data
     - Performance history available

4. **Assign teachers to Tahsin 1 Foundation**
   - Aksi: Drag senior teachers ke Tahsin 1 Foundation level
   - Verifikasi:
     - Drag-and-drop responsive
     - Assignment validation working
     - Workload calculation automatic
     - Competency matching verified

5. **Distribute remaining levels**
   - Aksi: Assign teachers ke Tahsin 1 Standard, Tahsin 2, Tahsin 3, dan Tahfidz
   - Verifikasi:
     - Balanced distribution achieved
     - Specialization requirements met
     - Workload targets maintained
     - Conflict resolution working

#### Bagian 3: Workload Optimization
6. **Review workload distribution**
   - Verifikasi:
     - Teacher workload visualization
     - Optimal range indicators (4-6 classes)
     - Overload warnings (>8 classes)
     - Underutilization alerts (<2 classes)

7. **Auto-assignment assistance**
   - Aksi: Klik "Auto-Assign Remaining" untuk optimization
   - Verifikasi:
     - Algorithm-based assignment
     - Workload balancing applied
     - Competency considerations included
     - Manual override capability

#### Bagian 4: Finalize Assignments
8. **Validate assignments**
   - Aksi: Run validation check untuk all assignments
   - Verifikasi:
     - Assignment completeness check
     - Conflict detection active
     - Quality metrics satisfied
     - Ready untuk class generation

9. **Submit assignments**
   - Aksi: Klik "Confirm Assignments"
   - Verifikasi:
     - Assignment locked dan confirmed
     - Teacher notifications sent
     - Audit trail recorded
     - Ready untuk next phase

### Kriteria Sukses
- [ ] Management login dan dashboard access
- [ ] Teacher competency data accurate
- [ ] Drag-and-drop assignment functional
- [ ] Workload distribution optimal
- [ ] Auto-assignment algorithm working
- [ ] Validation dan confirmation complete

---

## PS-HP-005: Academic Admin - Class Generation dan Refinement

### Informasi Skenario
- **ID Skenario**: PS-HP-005
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 15-20 menit

### Prasyarat
- Assessment foundation complete (80%+ readiness)
- Level distribution analyzed
- Teacher availability collected (100% submission)
- Management assignments confirmed
- System configuration validated

### Data Test
```
Class Generation Parameters:
- Default class size: 7-10 students
- Tahsin 1 class size: 8-12 students
- Tahfidz class size: 4-8 students
- Student integration ratio: 40% new, 60% existing (mixed classes)
- Teacher workload: 4-6 classes optimal
- Schedule conflict minimization active
```

### Langkah Pengujian

#### Bagian 1: Prerequisites Validation
1. **Access class generation interface**
   - Aksi: Navigate ke `/academic/class-generation`
   - Verifikasi:
     - Generation readiness dashboard muncul
     - Prerequisites checklist displayed
     - Data completeness indicators
     - Configuration parameters visible

2. **Validate generation readiness**
   - Verifikasi:
     - Student level assignments: >80% completion ✅
     - Teacher availability: 100% submission ✅
     - Management assignments: Complete ✅
     - Room availability: Confirmed ✅
     - System configuration: Validated ✅

#### Bagian 2: Run Class Generation Algorithm
3. **Configure generation parameters**
   - Aksi: Review dan adjust class size parameters if needed
   - Verifikasi:
     - Parameter adjustment interface
     - Impact calculation immediate
     - Validation rules enforced
     - Default values appropriate

4. **Execute class generation**
   - Aksi: Klik "Generate Classes"
   - Verifikasi:
     - Algorithm processing indicator
     - Progress tracking visible
     - Generation completion successful
     - Results summary displayed

5. **Review generation results**
   - Verifikasi:
     - Generated classes list dengan details
     - Class composition (new vs existing students)
     - Teacher assignments automatic
     - Schedule optimization achieved
     - Conflict identification

#### Bagian 3: Manual Refinement
6. **Access class refinement interface**
   - Aksi: Navigate ke `/academic/class-refinement`
   - Verifikasi:
     - Drag-and-drop refinement tools
     - Class composition analysis
     - Schedule conflict visualization
     - Size constraint indicators

7. **Refine class compositions**
   - Aksi: Drag students between classes untuk optimization
   - Verifikasi:
     - Student transfer functionality
     - Real-time validation
     - Class size monitoring
     - Balance maintenance

8. **Resolve schedule conflicts**
   - Aksi: Adjust time slots dengan drag-and-drop schedule editor
   - Verifikasi:
     - Schedule editor responsive
     - Conflict detection real-time
     - Alternative suggestions
     - Teacher availability respected

#### Bagian 4: Size Override Management
9. **Handle size exceptions**
   - Aksi: Approve oversized class dengan justification
   - Verifikasi:
     - Override request workflow
     - Justification requirements
     - Approval authority check
     - Exception tracking

10. **What-if scenario testing**
    - Aksi: Test alternative configurations
    - Verifikasi:
      - Scenario simulation working
      - Impact analysis accurate
      - Comparison tools available
      - Rollback capability

#### Bagian 5: Finalize Class Structure
11. **Quality validation**
    - Verifikasi:
      - Average class size within target range
      - Teacher workload balance achieved
      - Student category integration optimized
      - Schedule conflicts resolved
      - Special needs accommodated

12. **Save final configuration**
    - Aksi: Klik "Save Final Classes"
    - Verifikasi:
      - Configuration saved successfully
      - Backup created
      - Ready untuk final review
      - Audit trail complete

### Kriteria Sukses
- [ ] Prerequisites validation passed
- [ ] Class generation algorithm successful
- [ ] Manual refinement tools functional
- [ ] Schedule optimization achieved
- [ ] Size constraint management working
- [ ] Quality metrics satisfied
- [ ] Ready untuk final review phase

---

## PS-HP-006: Academic Admin - Final Review dan System Go-Live

### Informasi Skenario
- **ID Skenario**: PS-HP-006
- **Prioritas**: Kritical
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Class generation completed dan refined
- All stakeholder reviews conducted
- System integration verified
- Infrastructure ready

### Data Test
```
Final Review Checklist:
✅ Student assessments processed: 133/158 (84%)
✅ Classes created dan optimized: 18 classes total
✅ Teachers assigned dengan balanced workload
✅ Schedule conflicts resolved: 0 conflicts
✅ System integration operational
✅ Infrastructure ready
✅ Communications complete
✅ Support systems activated
```

### Langkah Pengujian

#### Bagian 1: Complete Schedule Review
1. **Access final schedule review**
   - Aksi: Navigate ke `/academic/final-schedule-review`
   - Verifikasi:
     - Complete schedule grid visualization
     - All classes displayed dengan details
     - Teacher assignments visible
     - Student distributions accurate

2. **Quality metrics validation**
   - Verifikasi:
     - Average class size: 8.7 students (within 7-10 range)
     - Teacher workload: 4.5 classes average (within 4-6 optimal)
     - Student integration: 42% new, 58% existing
     - Zero schedule conflicts
     - 100% special needs accommodated

#### Bagian 2: Stakeholder Review Process
3. **Academic staff self-review**
   - Verifikasi:
     - Self-validation checklist complete
     - Quality standards met
     - Ready untuk stakeholder review

4. **Management review dan approval**
   - Aksi: Send untuk management approval
   - Verifikasi:
     - Approval workflow triggered
     - Management notification sent
     - Review timeline established
     - Approval status tracking

5. **Teacher confirmation process**
   - Verifikasi:
     - Teacher notifications dengan assignment details
     - Confirmation requests sent
     - Response tracking active
     - Feedback collection system

#### Bagian 3: System Go-Live Preparation
6. **Access system go-live interface**
   - Aksi: Navigate ke `/academic/system-golive`
   - Verifikasi:
     - Go-live readiness dashboard
     - Final verification checklist
     - Success metrics establishment
     - Monitoring system ready

7. **Infrastructure verification**
   - Verifikasi:
     - Database records creation ready
     - Attendance system configuration
     - Assessment system grade book setup
     - Communication system class groups
     - Financial system billing updates

#### Bagian 4: Execute System Go-Live
8. **Final verification checklist**
   - Verifikasi semua items:
     - ✅ Student assessments processed
     - ✅ Classes created dan optimized
     - ✅ Teachers assigned dengan balanced workload
     - ✅ Schedule conflicts resolved
     - ✅ System integration operational
     - ✅ Infrastructure ready
     - ✅ Communications complete
     - ✅ Support systems activated

9. **Execute go-live**
   - Aksi: Klik "Execute Go-Live" dengan final confirmation
   - Verifikasi:
     - **WARNING**: This action is irreversible
     - Final confirmation dialog
     - Go-live execution successful
     - System status: LIVE

#### Bagian 5: Post Go-Live Verification
10. **Verify system activation**
    - Verifikasi:
      - Class records created dengan unique IDs
      - Student enrollment records active
      - Teacher assignment records confirmed
      - Schedule entries dan room bookings created
      - System integration verified

11. **Notification system verification**
    - Verifikasi:
      - Teacher notifications sent dengan assignment details
      - Student notifications dengan class information
      - Parent notifications dengan contact details
      - Administrative alerts dan monitoring active

12. **Success metrics establishment**
    - Verifikasi:
      - Student attendance targets set
      - Teacher satisfaction monitoring active
      - System performance tracking enabled
      - Academic quality assessment ready
      - Stakeholder feedback collection activated

### Hasil Diharapkan
- Complete semester preparation workflow finished successfully
- 18 classes created dengan optimal distribution
- 133 students placed appropriately dengan level assignments
- 20 teachers assigned dengan balanced workload (4.5 classes average)
- Zero schedule conflicts
- System fully operational dan ready untuk academic delivery
- Complete audit trail dan monitoring activated

### Kriteria Sukses
- [ ] Final schedule review comprehensive
- [ ] Quality metrics all satisfied
- [ ] Stakeholder approval process complete
- [ ] Infrastructure verification passed
- [ ] System go-live executed successfully
- [ ] Database records created correctly
- [ ] Notification system operational
- [ ] Success metrics established
- [ ] Monitoring system active

---

## Workflow Summary

### Complete Semester Preparation Flow:
1. **Assessment Foundation** → Data collection dan validation (84% readiness)
2. **Level Distribution** → Student placement dan class requirements
3. **Teacher Availability** → Availability collection (100% submission)  
4. **Management Assignment** → Strategic teacher-level assignments
5. **Class Generation** → Algorithm-based class creation + manual refinement
6. **Final Review** → Quality validation + stakeholder approval + system go-live

### Role Permissions:
- **Academic Admin**: Assessment, availability monitoring, class generation, final review
- **Management**: Teacher level assignments, strategic decisions, final approval
- **Instructors**: Availability submission, assignment confirmation

### Key Success Metrics:
- Student assessment completion: >80%
- Teacher availability submission: 100%
- Class size optimization: 7-10 students average
- Teacher workload balance: 4-6 classes optimal
- Schedule conflicts: Zero tolerance
- System integration: Complete operational status

---

## Catatan untuk Tester

### Implementation Reference
- **Controllers**: `AcademicPlanningController`, `ManagementController`
- **Services**: `AcademicPlanningService`, `ClassGenerationService`
- **Templates**: 14 responsive templates dengan Bootstrap 5
- **Security**: Role-based access control dengan `@PreAuthorize`

### Focus Areas
- **Data Integrity**: Assessment completeness validation
- **Algorithm Performance**: Class generation optimization
- **UI Responsiveness**: Drag-and-drop interfaces
- **Workflow Integrity**: Phase progression logic
- **Quality Assurance**: Metrics validation
- **System Integration**: Database dan notification systems

### Performance Considerations
- Load testing dengan 150+ students
- Concurrent teacher availability submissions
- Class generation algorithm performance
- Real-time UI updates dan responsiveness
- Report generation dengan large datasets

### Edge Cases
- Insufficient teacher availability
- Oversized/undersized class scenarios
- Schedule conflict resolution
- Teacher workload balancing challenges
- System failure recovery procedures