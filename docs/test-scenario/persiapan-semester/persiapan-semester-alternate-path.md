# Skenario Pengujian: Persiapan Semester - Alternate Path

## Informasi Umum
- **Kategori**: Validasi dan Error Handling
- **Modul**: Academic Planning & Class Preparation
- **Tipe Skenario**: Alternate Path (Jalur Alternatif)
- **Total Skenario**: 12 skenario validasi untuk 3 roles

---

## PS-AP-001: Academic Admin - Akses Tanpa Otentikasi

### Informasi Skenario
- **ID Skenario**: PS-AP-001 (Persiapan Semester - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: Unauthenticated User
- **Estimasi Waktu**: 3-4 menit

### Prasyarat
- Browser dalam kondisi logout (tidak ada session)
- Database dalam kondisi normal

### Langkah Pengujian

1. **Akses halaman academic planning tanpa login**
   - Aksi: Langsung akses URL `/academic/assessment-foundation`
   - Verifikasi:
     - Automatic redirect ke halaman login (`/login`)
     - Tidak dapat akses academic planning features
     - URL berubah ke login page

2. **Coba akses management functions**
   - Aksi: Langsung akses `/management/teacher-level-assignments`
   - Verifikasi:
     - Redirect ke login page
     - Security mechanism berfungsi
     - No system exposure

3. **Coba akses class generation**
   - Aksi: Langsung akses `/academic/class-generation`
   - Verifikasi:
     - Authentication required
     - Proper redirect behavior
     - No error or crash

### Kriteria Sukses
- [ ] Direct URL access ditolak tanpa authentication
- [ ] Redirect ke login page berhasil
- [ ] Tidak ada error server atau system exposure

---

## PS-AP-002: Role-Based Access Violation

### Informasi Skenario
- **ID Skenario**: PS-AP-002
- **Prioritas**: Tinggi
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Multiple user accounts dengan different roles

### Data Test
```
Student User: siswa.ali / Welcome@YSQ2024
Academic Admin User: academic.admin1 / Welcome@YSQ2024
Teacher User: ustadz.ahmad / Welcome@YSQ2024
Management User: management.director / Welcome@YSQ2024
```

### Langkah Pengujian

1. **Student coba akses academic planning**
   - Aksi: Login sebagai student, akses `/academic/assessment-foundation`
   - Verifikasi: 403 Forbidden atau access denied message

2. **Teacher coba akses management functions**
   - Aksi: Login sebagai teacher, akses `/management/teacher-level-assignments`
   - Verifikasi: Access denied, proper error message

3. **Staff coba akses restricted management areas**
   - Aksi: Login sebagai staff, akses management-only features
   - Verifikasi: Role separation enforced

4. **Verify proper role boundaries**
   - Verifikasi:
     - Each role only access sesuai permission
     - Clear error messages untuk violations
     - No privilege escalation possible

### Kriteria Sukses
- [ ] Role-based access control berfungsi ketat
- [ ] Setiap role hanya akses sesuai permission
- [ ] Error messages informatif dan secure

---

## PS-AP-003: Assessment Foundation - Insufficient Data

### Informasi Skenario
- **ID Skenario**: PS-AP-003
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Academic admin sudah login
- Database dengan incomplete assessment data (kurang dari 80% completion)

### Data Test
```
Insufficient Assessment Scenario:
New Students: 45 registrations
- Placement Tests Completed: 25/45 (55%) - INSUFFICIENT
- Tests Scheduled: 10/45 (22%)
- Tests Not Scheduled: 10/45 (22%)

Existing Students: 113 continuing students
- Exam Results Submitted: 68/113 (60%) - INSUFFICIENT
- Partial Results: 20/113 (18%)
- Results Missing: 25/113 (22%)

Overall Readiness: 93/158 students (59%) - BELOW THRESHOLD
```

### Langkah Pengujian

1. **Access assessment foundation dengan insufficient data**
   - Aksi: Navigate ke `/academic/assessment-foundation`
   - Verifikasi:
     - Dashboard menampilkan warning indicators
     - Red status untuk insufficient completion
     - Clear progress bars showing 59% overall

2. **Attempt to proceed dengan insufficient data**
   - Aksi: Klik "Proceed to Level Distribution"
   - Verifikasi:
     - Validation error: "Assessment completion must be ≥80%"
     - Process blocked dengan clear message
     - Recommended actions provided

3. **Handle insufficient placement tests**
   - Verifikasi:
     - Warning: Only 55% placement tests completed
     - Action items clearly listed
     - Bulk scheduling tools available
     - Estimated timeline untuk completion

4. **Address missing exam results**
   - Aksi: Use "Send Urgent Reminders" untuk missing results
   - Verifikasi:
     - Escalation workflow triggered
     - Teacher notifications sent dengan urgency
     - Follow-up schedule aggressive
     - Management notification sent

### Kriteria Sukses
- [ ] Insufficient data detection accurate
- [ ] Validation prevents progression
- [ ] Clear warning messages displayed
- [ ] Action items untuk resolution provided
- [ ] Escalation workflow functional

---

## PS-AP-004: Level Distribution - Extreme Imbalance

### Informasi Skenario
- **ID Skenario**: PS-AP-004
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Assessment data complete
- Extreme level distribution (heavily skewed)

### Data Test
```
Extreme Distribution Scenario:
Tahsin 1 Foundation: 80 students (heavily concentrated)
Tahsin 1 Standard: 35 students
Tahsin 1 Advanced: 25 students
Tahsin 2: 8 students (very few)
Tahsin 3: 3 students (insufficient)
Tahfidz Pemula: 1 student (cannot form class)

Total: 152 students
Class Formation Challenges:
- Tahsin 1: 17+ classes needed (resource strain)
- Tahfidz: Cannot form minimum class size
- Teacher specialization mismatch
```

### Langkah Pengujian

1. **Review extreme distribution**
   - Aksi: Navigate ke `/academic/level-distribution`
   - Verifikasi:
     - Charts show extreme imbalance
     - Warning indicators active
     - Resource strain alerts visible
     - Class formation warnings

2. **Attempt class formation dengan imbalance**
   - Verifikasi:
     - Tahfidz class cannot be formed (insufficient students)
     - Tahsin 1 requires excessive teachers
     - Teacher specialization warnings
     - Resource allocation alerts

3. **Handle insufficient enrollment levels**
   - Aksi: Review options untuk Tahfidz students (only 1 student)
   - Verifikasi:
     - Alternative placement suggestions
     - Combined class options
     - Individual instruction consideration
     - Parent communication recommended

4. **Resource strain management**
   - Verifikasi:
     - Teacher requirement calculation shows strain
     - Alternative distribution suggestions
     - Class size adjustment recommendations
     - Infrastructure capacity warnings

### Kriteria Sukses
- [ ] Extreme distribution detection working
- [ ] Class formation warnings accurate
- [ ] Alternative solutions suggested
- [ ] Resource strain alerts functional

---

## PS-AP-005: Teacher Availability - Insufficient Submission

### Informasi Skenario
- **ID Skenario**: PS-AP-005
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Availability collection period active
- Multiple teachers haven't submitted availability

### Data Test
```
Insufficient Availability Scenario:
Total Teachers: 20 active instructors
Submissions Received: 8/20 (40%)
Missing Submissions: 12/20 (60%)
Days Until Deadline: 2 days
Critical Status: Collection failing
```

### Langkah Pengujian

1. **Monitor insufficient submissions**
   - Aksi: Access `/academic/availability-monitoring`
   - Verifikasi:
     - Critical status indicators
     - Only 40% submission rate displayed
     - Red warning alerts active
     - Urgent action recommendations

2. **Attempt to proceed dengan insufficient data**
   - Aksi: Try to close availability collection early
   - Verifikasi:
     - Validation error: "Minimum 80% submission required"
     - Process blocked dengan clear reasoning
     - Extension options presented
     - Escalation procedures available

3. **Emergency reminder system**
   - Aksi: Send urgent reminders ke all missing teachers
   - Verifikasi:
     - Batch urgent notification sent
     - Phone call list generated
     - Management escalation triggered
     - Individual teacher tracking activated

4. **Deadline extension handling**
   - Aksi: Request deadline extension
   - Verifikasi:
     - Extension approval workflow
     - Management notification sent
     - New deadline communication
     - Teacher notification updated

### Kriteria Sukses
- [ ] Insufficient submission detection accurate
- [ ] Validation prevents premature closure
- [ ] Emergency escalation functional
- [ ] Deadline extension workflow working

---

## PS-AP-006: Management - Teacher Competency Mismatch

### Informasi Skenario
- **ID Skenario**: PS-AP-006
- **Prioritas**: Sedang
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Management sudah login
- Teacher competency data available
- Level requirements defined

### Data Test
```
Competency Mismatch Scenario:
Tahfidz Requirements: Advanced qualification needed
Available Tahfidz-qualified teachers: 2
Tahfidz classes needed: 4
Deficit: 2 qualified teachers

Junior Teacher Overload:
Junior teachers: 8
Advanced level assignments attempted: 6
Competency warnings expected
```

### Langkah Pengujian

1. **Attempt inappropriate level assignments**
   - Aksi: Try to assign junior teacher ke advanced Tahfidz level
   - Verifikasi:
     - Competency warning displayed
     - Assignment blocked atau warning issued
     - Alternative teacher suggestions
     - Training recommendations provided

2. **Handle insufficient qualified teachers**
   - Aksi: Try to assign all Tahfidz classes dengan only 2 qualified teachers
   - Verifikasi:
     - Resource shortage warning
     - Workload overload alerts
     - Class consolidation suggestions
     - External instructor recommendations

3. **Workload imbalance scenarios**
   - Aksi: Assign 9 classes to one teacher (exceeding 8 max)
   - Verifikasi:
     - Workload violation warning
     - Alternative distribution suggested
     - Teacher welfare considerations
     - Quality impact warnings

4. **Competency gap analysis**
   - Verifikasi:
     - Teacher training needs identified
     - Skill development recommendations
     - Resource planning adjustments
     - Timeline impact assessment

### Kriteria Sukses
- [ ] Competency validation working
- [ ] Resource shortage detection accurate
- [ ] Workload limits enforced
- [ ] Alternative solutions suggested

---

## PS-AP-007: Class Generation - Algorithm Failure

### Informasi Skenario
- **ID Skenario**: PS-AP-007
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Prerequisites met tetapi algorithm constraints impossible
- Conflicting requirements

### Data Test
```
Algorithm Failure Scenario:
Room Capacity: Only 10 rooms available
Classes Needed: 18 classes
Schedule Slots: Limited time slots
Teacher Availability: Highly restrictive
Student Preferences: Conflicting requirements

Impossible Constraints:
- More classes than rooms
- Teacher availability gaps
- Student schedule conflicts
- Resource insufficiency
```

### Langkah Pengujian

1. **Attempt class generation dengan impossible constraints**
   - Aksi: Run class generation algorithm
   - Verifikasi:
     - Algorithm detects impossibility
     - Clear error message: "Cannot generate feasible schedule"
     - Constraint analysis provided
     - Suggested resolutions listed

2. **Analyze constraint conflicts**
   - Verifikasi:
     - Room capacity shortage identified (18 classes, 10 rooms)
     - Teacher availability gaps highlighted
     - Schedule conflict visualization
     - Student preference conflicts mapped

3. **Alternative solution suggestions**
   - Verifikasi:
     - Class consolidation options
     - Extended time slot recommendations
     - Mixed delivery mode suggestions
     - Phased implementation proposals

4. **Manual override attempts**
   - Aksi: Try manual class placement despite conflicts
   - Verifikasi:
     - Conflict warnings persist
     - Quality metrics degradation alerts
     - Academic impact warnings
     - Approval escalation required

### Kriteria Sukses
- [ ] Algorithm failure detection accurate
- [ ] Constraint analysis comprehensive
- [ ] Alternative solutions provided
- [ ] Manual override safeguards working

---

## PS-AP-008: Class Refinement - Size Constraint Violations

### Informasi Skenario
- **ID Skenario**: PS-AP-008
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Initial class generation completed
- Manual refinement in progress

### Data Test
```
Size Constraint Violations:
Class A: 15 students (exceeds max 12 untuk Tahsin 1)
Class B: 3 students (below min 7)
Class C: 14 students (exceeds max 10 default)

Teacher Workload Violations:
Teacher X: 10 classes (exceeds max 8)
Teacher Y: 1 class (below min 2)
```

### Langkah Pengujian

1. **Create oversized class**
   - Aksi: Move students to create 15-student class
   - Verifikasi:
     - Size violation warning displayed
     - Red indicator on class size
     - Quality impact warning
     - Override justification required

2. **Create undersized class**
   - Aksi: Remove students to create 3-student class
   - Verifikasi:
     - Minimum size violation alert
     - Economic impact warning
     - Consolidation suggestions
     - Approval requirement triggered

3. **Exceed teacher workload limits**
   - Aksi: Assign 10 classes to one teacher
   - Verifikasi:
     - Workload violation error
     - Teacher welfare warning
     - Quality degradation alert
     - Alternative teacher suggestions

4. **Submit dengan violations**
   - Aksi: Attempt to save configuration dengan violations
   - Verifikasi:
     - Submission blocked until resolution
     - Violation summary displayed
     - Justification fields required
     - Management approval needed

### Kriteria Sukses
- [ ] Size constraint validation working
- [ ] Workload limit enforcement active
- [ ] Violation warnings clear
- [ ] Resolution workflow functional

---

## PS-AP-009: Final Review - Stakeholder Rejection

### Informasi Skenario
- **ID Skenario**: PS-AP-009
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN + MANAGEMENT
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Class generation completed
- Final review submitted untuk approval
- Simulated stakeholder concerns

### Data Test
```
Stakeholder Rejection Scenario:
Management Concerns:
- Teacher workload imbalance
- Class size variations excessive
- Schedule conflicts dengan facility usage
- Resource allocation concerns

Teacher Feedback:
- Assignment dissatisfaction
- Schedule preference conflicts
- Competency concerns
- Workload distribution complaints
```

### Langkah Pengujian

1. **Submit untuk stakeholder review**
   - Aksi: Send final schedule untuk management approval
   - Verifikasi:
     - Review workflow triggered
     - Stakeholder notifications sent
     - Review timeline established
     - Status tracking active

2. **Receive management rejection**
   - Aksi: Simulate management rejection dengan specific concerns
   - Verifikasi:
     - Rejection notification received
     - Specific concerns documented
     - Revision requirements clear
     - Back to refinement mode

3. **Handle teacher feedback complaints**
   - Verifikasi:
     - Teacher complaint system active
     - Feedback collection functional
     - Issue categorization working
     - Resolution tracking enabled

4. **Implement revision cycle**
   - Aksi: Make required changes based on feedback
   - Verifikasi:
     - Revision workflow smooth
     - Changes tracked properly
     - Re-submission capability
     - Iterative improvement supported

### Kriteria Sukses
- [ ] Stakeholder review workflow functional
- [ ] Rejection handling graceful
- [ ] Feedback collection comprehensive
- [ ] Revision cycle efficient

---

## PS-AP-010: System Go-Live - Infrastructure Failure

### Informasi Skenario
- **ID Skenario**: PS-AP-010
- **Prioritas**: Kritical
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Final approval received
- Go-live execution attempted
- Simulated infrastructure issues

### Data Test
```
Infrastructure Failure Scenario:
Database Connection: Intermittent failures
Notification System: Email server down
Integration Services: External system unavailable
Backup Systems: Partial failure
Data Integrity: Verification issues
```

### Langkah Pengujian

1. **Attempt go-live dengan infrastructure issues**
   - Aksi: Execute go-live with simulated database issues
   - Verifikasi:
     - Infrastructure check detects issues
     - Go-live execution halted
     - Clear error messages provided
     - Rollback procedures activated

2. **Handle notification system failure**
   - Aksi: Go-live dengan email system down
   - Verifikasi:
     - Notification failure detected
     - Alternative notification methods activated
     - Manual notification procedures provided
     - Service restoration timeline given

3. **Data integrity verification failure**
   - Aksi: Simulate data integrity issues during go-live
   - Verifikasi:
     - Data verification process working
     - Integrity failures detected
     - Go-live halted untuk safety
     - Data recovery procedures available

4. **Rollback dan recovery procedures**
   - Verifikasi:
     - Rollback capability functional
     - Data state preserved
     - System stability maintained
     - Recovery timeline established

### Kriteria Sukses
- [ ] Infrastructure validation comprehensive
- [ ] Failure detection accurate
- [ ] Go-live safety mechanisms working
- [ ] Rollback procedures functional

---

## PS-AP-011: Teacher Availability - Late Submission

### Informasi Skenario
- **ID Skenario**: PS-AP-011
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR + ACADEMIC_ADMIN
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Teacher account: `ustadz.ahmad`
- Availability deadline passed
- Collection period officially closed

### Data Test
```
Late Submission Scenario:
Deadline: 3 days ago
Submission Attempt: Now (late)
Collection Status: CLOSED
Other Teachers: All submitted on time
```

### Langkah Pengujian

1. **Attempt late availability submission**
   - Aksi: Teacher login dan try to submit availability after deadline
   - Verifikasi:
     - Late submission blocked
     - Clear deadline message displayed
     - Contact information for manual process
     - Escalation procedure available

2. **Handle late submission request**
   - Aksi: Academic admin receives late submission request
   - Verifikasi:
     - Late submission workflow available
     - Manual data entry option
     - Impact assessment provided
     - Approval requirement clear

3. **Process emergency availability update**
   - Aksi: Admin manually updates teacher availability
   - Verifikasi:
     - Manual update capability
     - Validation rules still enforced
     - Audit trail maintained
     - Impact on scheduling noted

### Kriteria Sukses
- [ ] Deadline enforcement working
- [ ] Late submission handling graceful
- [ ] Manual override capability available
- [ ] Audit trail maintained

---

## PS-AP-012: Concurrent Operations - Data Race Conditions

### Informasi Skenario
- **ID Skenario**: PS-AP-012
- **Prioritas**: Tinggi
- **Role**: Multiple concurrent users
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Multiple users logged in simultaneously
- Concurrent operations on same data

### Data Test
```
Concurrent Operation Scenario:
User 1: Academic admin editing class composition
User 2: Management adjusting teacher assignments
User 3: Academic admin modifying schedule
Time: Simultaneous operations
Data: Same student dan teacher records
```

### Langkah Pengujian

1. **Concurrent class composition edits**
   - Aksi: Two academic admin modify same class simultaneously
   - Verifikasi:
     - Optimistic locking working
     - Conflict detection accurate
     - Last-writer-wins atau merge options
     - Data integrity preserved

2. **Simultaneous teacher assignment changes**
   - Aksi: Management dan academic admin modify teacher assignments concurrently
   - Verifikasi:
     - Lock management functional
     - Conflict resolution available
     - User notification of conflicts
     - Data consistency maintained

3. **Race condition dalam status updates**
   - Aksi: Multiple users update same registration status
   - Verifikasi:
     - Status transition integrity
     - Audit trail accuracy
     - Conflict resolution working
     - System stability maintained

### Kriteria Sukses
- [ ] Concurrent access managed properly
- [ ] Data integrity preserved
- [ ] Conflict resolution functional
- [ ] System stability maintained

---

## Security Testing Focus

### Authentication & Authorization
- [ ] Unauthenticated access prevention across all academic endpoints
- [ ] Role-based access control strict enforcement
- [ ] Session management proper untuk concurrent operations
- [ ] URL manipulation protection comprehensive

### Data Security
- [ ] Student data access controlled properly
- [ ] Teacher assignment confidentiality
- [ ] Management decision audit trails
- [ ] Academic data integrity maintained

### Business Logic Security
- [ ] Workflow state validation secure
- [ ] Status transition authorization enforced
- [ ] Data modification audit comprehensive
- [ ] System state consistency maintained

### System Security
- [ ] Infrastructure failure handling secure
- [ ] Data backup dan recovery secure
- [ ] Error messages don't expose sensitive data
- [ ] System boundaries properly enforced

## Edge Cases Testing

### Data Boundary Conditions
- [ ] Maximum student capacity handling
- [ ] Minimum class size enforcement
- [ ] Teacher workload limits
- [ ] Schedule constraint boundaries

### System Boundaries
- [ ] Database connection limits
- [ ] Concurrent user limits
- [ ] Processing time limits
- [ ] Memory dan resource constraints

### Business Logic Boundaries
- [ ] Assessment completion thresholds
- [ ] Class generation algorithm limits
- [ ] Stakeholder approval timeouts
- [ ] System go-live prerequisites

### Recovery dan Resilience
- [ ] Database failure recovery
- [ ] Network interruption handling
- [ ] Partial system failure scenarios
- [ ] Data corruption detection dan recovery

This comprehensive alternate path testing ensures robust validation of the semester preparation workflow under various failure conditions, edge cases, dan security scenarios.