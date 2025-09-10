# Skenario Pengujian: Exam Management - Happy Path

## Informasi Umum
- **Kategori**: Ujian Semester - Manajemen Ujian
- **Modul**: Exam Creation and Configuration
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario utama untuk exam management
- **Playwright Test**: `exam-management.ExamManagementTest`

---

## EM-HP-001: Academic Admin - Create Midterm Exam

### Informasi Skenario
- **ID Skenario**: EM-HP-001 (Exam Management - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 15-20 menit

### Prasyarat
- Academic admin account: `academic.admin1` / `Welcome@YSQ2024`
- Active academic term exists
- Classes have been created dan assigned
- Question bank tersedia dengan minimal 50 questions
- Exam schedule period sudah ditentukan

### Data Test
```
Exam Configuration:
Exam Type: Midterm Examination (UTS)
Academic Term: Semester 1 2024/2025
Target Level: Tahsin 2
Total Questions: 40 questions
Duration: 90 minutes
Exam Period: Week 8 (Oct 14-18, 2024)

Question Distribution:
- Multiple Choice: 30 questions (75%)
- Essay: 5 questions (12.5%)
- Quran Recitation: 5 questions (12.5%)

Scoring Configuration:
- Multiple Choice: 2 points each (60 points)
- Essay: 6 points each (30 points)
- Recitation: 2 points each (10 points)
- Total: 100 points
- Passing Grade: 70
```

### Langkah Pengujian

#### Bagian 1: Access Exam Management
1. **Login sebagai Academic Admin**
   - Aksi: Login dengan credentials admin
   - Verifikasi:
     - Admin dashboard tampil
     - Menu "Exam Management" tersedia
     - Current term information displayed

2. **Navigate ke Exam Creation**
   - Aksi: Klik menu "Exam Management" → "Create New Exam"
   - Verifikasi:
     - Exam creation form tampil
     - Academic term auto-selected
     - Available levels listed
     - Exam templates tersedia

#### Bagian 2: Configure Basic Exam Settings
3. **Set Exam Basic Information**
   - Aksi: Isi basic exam details:
     - Exam Name: "Ujian Tengah Semester - Tahsin 2"
     - Exam Type: "Midterm Examination"
     - Target Level: "Tahsin 2"
     - Description: "UTS untuk mengukur pemahaman materi paruh semester"
   - Verifikasi:
     - Form validation working
     - Character limits enforced
     - Required fields marked

4. **Configure Exam Schedule**
   - Aksi: Set exam scheduling:
     - Exam Period Start: Oct 14, 2024
     - Exam Period End: Oct 18, 2024
     - Duration: 90 minutes
     - Attempt Limit: 1 attempt
     - Late Submission: Not allowed
   - Verifikasi:
     - Date picker functional
     - Duration calculator working
     - Schedule conflict detection active

#### Bagian 3: Question Selection dan Configuration
5. **Select Questions from Bank**
   - Aksi: Navigate ke question selection
   - Verifikasi:
     - Question bank interface loaded
     - Filter by category available
     - Preview questions functional
     - Question statistics visible

6. **Add Multiple Choice Questions**
   - Aksi: Select 30 multiple choice questions
     - Filter: Category = "Tajwid Rules"
     - Select questions dengan difficulty mix:
       - Easy: 10 questions
       - Medium: 15 questions
       - Hard: 5 questions
   - Verifikasi:
     - Questions added to exam
     - Total count updated (30/40)
     - Points calculation correct (60 points)

7. **Add Essay Questions**
   - Aksi: Select 5 essay questions
     - Category: "Islamic Understanding"
     - All medium difficulty
   - Verifikasi:
     - Essay questions added
     - Rubric templates available
     - Point allocation editable

8. **Add Recitation Questions**
   - Aksi: Select 5 Quran recitation tasks
     - Surah selection varied
     - Recording time limit set
   - Verifikasi:
     - Recitation tasks configured
     - Audio recording settings visible
     - Verse references accurate

#### Bagian 4: Scoring dan Rules Configuration
9. **Configure Scoring System**
   - Aksi: Set scoring parameters:
     - Enable automatic grading for MCQ
     - Set point values per question type
     - Configure passing grade: 70%
     - Enable partial credit for essays
   - Verifikasi:
     - Point totals calculate correctly (100)
     - Passing score computed (70 points)
     - Grading rubric templates attached

10. **Set Exam Rules**
    - Aksi: Configure exam rules:
      - Randomize question order: Yes
      - Randomize answer options: Yes
      - Show one question at a time: No
      - Allow navigation between questions: Yes
      - Auto-save answers: Every 30 seconds
      - Show remaining time: Yes
    - Verifikasi:
      - Rules properly configured
      - Preview mode available
      - Settings saved successfully

#### Bagian 5: Review dan Publish
11. **Preview Exam Configuration**
    - Aksi: Klik "Preview Exam"
    - Verifikasi:
      - Student view simulation available
      - All questions display correctly
      - Timer functionality working
      - Navigation as configured

12. **Publish Exam**
    - Aksi: Klik "Save and Publish"
    - Verifikasi:
      - Validation checks passed
      - Exam status = "PUBLISHED"
      - Notification sent to instructors
      - Exam appears in schedule

### Hasil Diharapkan
- Midterm exam successfully created dengan 40 questions
- Scoring system configured correctly (total 100 points)
- Exam scheduled untuk Week 8
- All validation rules enforced
- Exam ready untuk student access during exam period

### Kriteria Sukses
- [ ] Exam creation workflow smooth dan intuitive
- [ ] Question selection dari bank efficient
- [ ] Scoring configuration accurate
- [ ] Schedule conflicts detected properly
- [ ] Preview functionality working correctly
- [ ] Publishing process includes all validations

---

## EM-HP-002: Instructor - Create Class-Specific Quiz

### Informasi Skenario
- **ID Skenario**: EM-HP-002
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Instructor account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Assigned to active class
- Permission to create quizzes
- Class has completed Chapter 3

### Data Test
```
Quiz Configuration:
Quiz Name: Quiz Bab 3 - Makharijul Huruf
Target Class: Tahsin 1 - Senin Pagi
Question Count: 10 questions
Duration: 20 minutes
Available: Immediate (after class session)
Due Date: 3 days after release

Question Types:
- Multiple Choice: 8 questions
- Short Answer: 2 questions
Total Points: 20 points
```

### Langkah Pengujian

#### Bagian 1: Access Quiz Creation
1. **Navigate ke My Classes**
   - Aksi: Login dan go to "My Classes"
   - Verifikasi:
     - Class list displayed
     - "Create Quiz" button available
     - Current class progress visible

2. **Initiate Quiz Creation**
   - Aksi: Select class dan klik "Create Quiz"
   - Verifikasi:
     - Quiz creation form opens
     - Class auto-selected
     - Student count displayed

#### Bagian 2: Configure Quiz
3. **Set Quiz Properties**
   - Aksi: Configure basic settings:
     - Quiz Name: "Quiz Bab 3 - Makharijul Huruf"
     - Duration: 20 minutes
     - Attempts: 2 attempts allowed
     - Show correct answers: After all attempts
   - Verifikasi:
     - Settings saved
     - Validation active

4. **Add Quiz Questions**
   - Aksi: Create questions directly:
     - Add 8 MCQ questions about Makharijul Huruf
     - Add 2 short answer questions
     - Set points per question
   - Verifikasi:
     - Question editor functional
     - Preview available
     - Point calculation correct

#### Bagian 3: Schedule dan Release
5. **Set Availability**
   - Aksi: Configure release:
     - Available from: Today after class
     - Due date: 3 days from now
     - Late submission: -10% per day
   - Verifikasi:
     - Schedule set correctly
     - Late penalty configured

6. **Release Quiz**
   - Aksi: Klik "Save and Release"
   - Verifikasi:
     - Quiz published to class
     - Students notified
     - Quiz appears in class dashboard

### Kriteria Sukses
- [ ] Quiz creation by instructor successful
- [ ] Class-specific configuration working
- [ ] Student notifications sent
- [ ] Quiz accessible to enrolled students only

---

## EM-HP-003: System Admin - Configure Final Exam Settings

### Informasi Skenario
- **ID Skenario**: EM-HP-003
- **Prioritas**: Tinggi
- **Role**: SYSTEM_ADMIN
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- System admin account available
- Final exam period approaching
- All classes have completed syllabus
- Grading committees assigned

### Data Test
```
Final Exam Configuration:
Exam Type: Final Examination (UAS)
Scope: All active levels
Period: Dec 16-20, 2024
Mode: Hybrid (Online + Practical)

Security Settings:
- Browser lockdown: Enabled
- Copy-paste: Disabled
- Tab switching: Limited (warning after 3)
- Webcam monitoring: Required
- ID verification: Required before start

Special Accommodations:
- Extra time students: +30 minutes
- Technical issues: 15-minute grace period
```

### Langkah Pengujian

#### Bagian 1: Access System Configuration
1. **Login sebagai System Admin**
   - Aksi: Access system settings
   - Verifikasi:
     - Admin panel accessible
     - Exam configuration menu available
     - System status dashboard visible

2. **Navigate ke Final Exam Settings**
   - Aksi: Go to "Exam Settings" → "Final Exam Configuration"
   - Verifikasi:
     - Configuration interface loaded
     - Current settings displayed
     - Backup option available

#### Bagian 2: Configure Security Settings
3. **Enable Security Features**
   - Aksi: Configure security:
     - Enable browser lockdown mode
     - Disable copy-paste functionality
     - Set tab switch limit to 3
     - Enable webcam monitoring requirement
   - Verifikasi:
     - Settings applied
     - Test mode available
     - Compatibility check tool working

4. **Set Authentication Requirements**
   - Aksi: Configure ID verification:
     - Require photo ID upload
     - Enable face matching
     - Set verification timeout: 5 minutes
   - Verifikasi:
     - Verification workflow configured
     - Test verification working

#### Bagian 3: Configure Accommodations
5. **Set Special Accommodations**
   - Aksi: Add accommodation rules:
     - Create extra time group
     - Add eligible students
     - Set time extension: +30 minutes
   - Verifikasi:
     - Accommodation groups created
     - Students properly assigned
     - Time calculations correct

6. **Configure Technical Support**
   - Aksi: Set support parameters:
     - Enable live chat during exam
     - Set grace period: 15 minutes
     - Configure incident reporting
   - Verifikasi:
     - Support channels active
     - Incident forms working
     - Escalation paths defined

#### Bagian 4: System-wide Settings
7. **Apply Global Configuration**
   - Aksi: Set system-wide parameters:
     - Exam period dates
     - Backup frequency during exams
     - Result processing timeline
     - Certificate generation rules
   - Verifikasi:
     - Settings propagated to all levels
     - Conflict detection working
     - Rollback option available

8. **Test Configuration**
   - Aksi: Run configuration test
   - Verifikasi:
     - All features functioning
     - Performance metrics acceptable
     - Security measures active
     - Audit logging enabled

### Kriteria Sukses
- [ ] System-wide configuration successful
- [ ] Security features properly enabled
- [ ] Accommodation system working
- [ ] Technical support channels configured
- [ ] Audit trail comprehensive

---

## EM-HP-004: Question Bank Management

### Informasi Skenario
- **ID Skenario**: EM-HP-004
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Academic admin atau instructor account
- Question authoring permissions
- Existing question categories
- Review committee assigned

### Data Test
```
Question Bank Update:
New Questions: 25 questions
Categories:
- Tajwid Advanced: 10 questions
- Fiqh Basics: 8 questions
- Arabic Grammar: 7 questions

Question Quality Metrics:
- Peer reviewed: Required
- Difficulty calibrated: Yes
- Usage statistics: Tracked
- Student feedback: Collected
```

### Langkah Pengujian

#### Bagian 1: Access Question Bank
1. **Navigate ke Question Bank**
   - Aksi: Access "Exam Management" → "Question Bank"
   - Verifikasi:
     - Question bank dashboard loaded
     - Categories displayed with counts
     - Search functionality available
     - Import/Export options visible

2. **Browse Existing Questions**
   - Aksi: Browse current questions
   - Verifikasi:
     - Questions organized by category
     - Metadata visible (difficulty, usage count)
     - Preview functionality working
     - Edit permissions checked

#### Bagian 2: Add New Questions
3. **Create Multiple Choice Question**
   - Aksi: Add new MCQ:
     - Question: "Huruf Qalqalah ada berapa?"
     - Options: 5, 6, 7, 8
     - Correct: 5
     - Category: Tajwid Advanced
     - Difficulty: Easy
     - Explanation: "Qaf, Tho, Ba, Jim, Dal"
   - Verifikasi:
     - Question saved successfully
     - Preview accurate
     - Metadata recorded

4. **Create Essay Question**
   - Aksi: Add essay question:
     - Question: "Jelaskan perbedaan Idgham Bighunnah dan Bilaghunnah"
     - Category: Tajwid Advanced
     - Difficulty: Medium
     - Rubric: 3-point scale
     - Sample answer provided
   - Verifikasi:
     - Essay question created
     - Rubric attached
     - Grading guide available

5. **Create Practical Question**
   - Aksi: Add recitation task:
     - Task: "Recite Al-Fatihah dengan tajwid yang benar"
     - Recording time: 2 minutes
     - Assessment criteria defined
   - Verifikasi:
     - Practical question configured
     - Recording settings saved
     - Criteria checklist created

#### Bagian 3: Question Review Process
6. **Submit for Review**
   - Aksi: Submit new questions for peer review
   - Verifikasi:
     - Review request sent
     - Questions marked "Pending Review"
     - Reviewers notified

7. **Review dan Approve**
   - Aksi: (As reviewer) Review submitted questions
   - Verifikasi:
     - Review interface functional
     - Feedback options available
     - Approval workflow working
     - Questions marked "Approved"

#### Bagian 4: Question Analytics
8. **View Question Performance**
   - Aksi: Access question analytics
   - Verifikasi:
     - Usage statistics displayed
     - Difficulty index calculated
     - Discrimination index shown
     - Student feedback aggregated

9. **Export Question Set**
   - Aksi: Export approved questions
   - Verifikasi:
     - Export formats available (CSV, JSON, QTI)
     - Metadata included
     - Import compatibility verified

### Kriteria Sukses
- [ ] Question creation smooth untuk all types
- [ ] Review process functioning properly
- [ ] Analytics providing useful insights
- [ ] Import/Export working correctly
- [ ] Version control maintaining history

---

## EM-HP-005: Exam Schedule Coordination

### Informasi Skenario
- **ID Skenario**: EM-HP-005
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Multiple exams created for semester
- Various levels and classes active
- Room assignments needed
- Proctor assignments required

### Data Test
```
Exam Schedule Coordination:
Week 8 (Midterm Week):
- Monday: Tahsin 1 & 2 (Morning)
- Tuesday: Tahsin 3 & Tahfidz 1 (Morning)
- Wednesday: Tahfidz 2 & 3 (Morning)
- Thursday: Make-up exams
- Friday: Practical exams

Resources:
- Exam Rooms: 3 available
- Proctors: 6 instructors
- Time Slots: 08:00-10:00, 10:30-12:30
- Capacity: 30 students per room
```

### Langkah Pengujian

#### Bagian 1: Access Schedule Coordinator
1. **Open Exam Scheduler**
   - Aksi: Navigate ke "Exam Schedule Coordinator"
   - Verifikasi:
     - Calendar view loaded
     - Drag-drop interface ready
     - Resource panels visible
     - Conflict detection active

2. **View Current Schedule**
   - Aksi: Review week 8 schedule
   - Verifikasi:
     - All exams listed
     - Student counts shown
     - Room availability displayed
     - Proctor assignments visible

#### Bagian 2: Schedule Optimization
3. **Assign Exam Rooms**
   - Aksi: Drag exams to room slots:
     - Tahsin 1: Room A, Monday 08:00
     - Tahsin 2: Room B, Monday 08:00
     - Check capacity limits
   - Verifikasi:
     - Room assignments successful
     - Capacity warnings if exceeded
     - No double-booking allowed

4. **Assign Proctors**
   - Aksi: Assign instructors as proctors:
     - Each room gets 2 proctors
     - Avoid instructor's own class
     - Balance workload
   - Verifikasi:
     - Proctor assignments valid
     - Conflict rules enforced
     - Workload distributed

#### Bagian 3: Handle Conflicts
5. **Resolve Schedule Conflicts**
   - Aksi: Identify and resolve conflicts:
     - Student enrolled in 2 levels
     - Instructor teaching conflict
     - Room maintenance window
   - Verifikasi:
     - Conflicts highlighted
     - Resolution options provided
     - Alternative slots suggested

6. **Finalize Schedule**
   - Aksi: Lock exam schedule
   - Verifikasi:
     - Schedule validated
     - All conflicts resolved
     - Notifications queued
     - Calendar published

### Kriteria Sukses
- [ ] Schedule optimization efficient
- [ ] Conflict detection comprehensive
- [ ] Resource allocation balanced
- [ ] Notification system triggered
- [ ] Schedule accessible to all stakeholders

---

## EM-HP-006: Emergency Exam Procedures

### Informasi Skenario
- **ID Skenario**: EM-HP-006
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, SYSTEM_ADMIN
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Exam in progress
- Emergency procedures defined
- Backup systems ready
- Communication channels active

### Data Test
```
Emergency Scenario:
Issue: Power outage during exam
Affected: 45 students in online exam
Time: 30 minutes into 90-minute exam
Required Action: Pause, extend, resume

Recovery Plan:
- Pause all affected exams
- Save current progress
- Add 30 minutes extension
- Generate incident report
- Resume when ready
```

### Langkah Pengujian

#### Bagian 1: Detect Emergency
1. **Monitor Exam Status**
   - Aksi: Access live monitoring dashboard
   - Verifikasi:
     - Real-time status visible
     - Alert system active
     - Student connection status shown
     - Issue detection automatic

2. **Identify Affected Students**
   - Aksi: Filter disconnected students
   - Verifikasi:
     - Affected count accurate
     - Last save timestamps shown
     - Contact information available

#### Bagian 2: Execute Emergency Protocol
3. **Pause Affected Exams**
   - Aksi: Execute emergency pause:
     - Select affected exam session
     - Click "Emergency Pause"
     - Confirm action
   - Verifikasi:
     - Exams paused immediately
     - Timer stopped
     - Students notified
     - Answers auto-saved

4. **Configure Recovery Settings**
   - Aksi: Set recovery parameters:
     - Add 30 minutes extension
     - Allow resume from last question
     - Reset security violations
     - Enable support priority
   - Verifikasi:
     - Extensions applied
     - Settings saved
     - Audit log updated

#### Bagian 3: Resume Operations
5. **Communicate with Students**
   - Aksi: Send recovery instructions:
     - Email blast to affected
     - SMS for critical updates
     - Dashboard notifications
   - Verifikasi:
     - Messages sent successfully
     - Delivery confirmed
     - Instructions clear

6. **Resume Exams**
   - Aksi: Restart exam sessions:
     - Verify system stability
     - Click "Resume All"
     - Monitor restart
   - Verifikasi:
     - Students can re-enter
     - Progress restored
     - Timer adjusted
     - No data loss

#### Bagian 4: Post-Incident
7. **Generate Incident Report**
   - Aksi: Create official report:
     - Document timeline
     - List affected students
     - Record actions taken
     - Note compensation given
   - Verifikasi:
     - Report comprehensive
     - Timestamps accurate
     - Approved by admin
     - Filed properly

8. **Review and Adjust**
   - Aksi: Post-incident review:
     - Analyze response time
     - Evaluate effectiveness
     - Update procedures
   - Verifikasi:
     - Lessons learned documented
     - Procedures updated
     - Team briefed

### Kriteria Sukses
- [ ] Emergency detection rapid
- [ ] Response protocol executed smoothly
- [ ] Student data preserved
- [ ] Communication effective
- [ ] Recovery successful
- [ ] Documentation complete

---

## Integration dengan Automated Testing

### Playwright Test Mapping

| Skenario Manual | Playwright Test | Status | Lokasi File |
|-----------------|-----------------|--------|-------------|
| Create Midterm Exam | `ExamManagementTest.testCreateMidtermExam` | 🔄 Planned | `scenarios/exam/ExamManagementTest.java` |
| Create Class Quiz | `InstructorExamTest.testCreateClassQuiz` | 🔄 Planned | `scenarios/exam/InstructorExamTest.java` |
| Configure Final Exam | `SystemExamTest.testConfigureFinalExam` | 🔄 Planned | `scenarios/exam/SystemExamTest.java` |
| Question Bank Management | `QuestionBankTest.testQuestionManagement` | 🔄 Planned | `scenarios/exam/QuestionBankTest.java` |
| Schedule Coordination | `ExamScheduleTest.testScheduleCoordination` | 🔄 Planned | `scenarios/exam/ExamScheduleTest.java` |
| Emergency Procedures | `EmergencyExamTest.testEmergencyProtocol` | 🔄 Planned | `scenarios/exam/EmergencyExamTest.java` |

## Business Rules dan Validations

### Exam Creation Rules
- [ ] Minimum 10 questions per exam
- [ ] Maximum 100 questions per exam
- [ ] Duration between 30-180 minutes
- [ ] At least 3 days advance notice required
- [ ] Cannot overlap with other exams for same level

### Security Requirements
- [ ] Browser lockdown untuk final exams
- [ ] IP address logging mandatory
- [ ] Session timeout after 5 minutes inactivity
- [ ] Automatic submission at time limit
- [ ] Encrypted answer storage

### Grading Policies
- [ ] Multiple choice auto-graded immediately
- [ ] Essay questions require manual grading
- [ ] Practical exams need 2 evaluators
- [ ] Results released within 7 days
- [ ] Grade appeals within 3 days of release

---

**Implementation Notes**:
- Exam module integrates dengan existing academic term management
- Question bank shared across all exam types
- Analytics track question performance over time
- Emergency procedures required untuk all exam types
- Accessibility features mandatory untuk special needs students