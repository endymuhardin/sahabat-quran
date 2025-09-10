# Skenario Pengujian: Kegiatan Harian Semester - Happy Path

## Informasi Umum
- **Kategori**: Aktivitas Semester - Operasional Harian
- **Modul**: Daily Academic Operations and Session Management
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario utama untuk kegiatan harian semester
- **Playwright Test**: `semester-activities.DailyOperationsTest`

---

## KHS-HP-001: Instruktur - Pelaksanaan Sesi Kelas Harian

### Informasi Skenario
- **ID Skenario**: KHS-HP-001 (Kegiatan Harian Semester - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 15-20 menit
- **Playwright Test**: `SessionExecutionTest.instructorSessionExecution()`

### Prasyarat
- Instructor account tersedia: `ustadz.ahmad` / `Welcome@YSQ2024`
- Class session sudah dijadwalkan untuk hari ini
- Preparation status = 'READY'
- Student roster tersedia dengan minimal 5 siswa

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Session Data:
Class Group: Tahsin 1 - Senin Pagi
Session Date: Today
Scheduled Time: 08:00-10:00
Total Students: 8 siswa
Session Number: 5 (Week 5)
Preparation Status: READY
```

### Langkah Pengujian

#### Bagian 1: Teacher Check-in
1. **Login sebagai Instructor**
   - Aksi: Login dengan credentials instructor
   - Verifikasi: 
     - Dashboard instructor tampil dengan benar
     - Menu "My Classes" tersedia
     - Welcome message menampilkan nama ustadz

2. **Navigate ke My Classes**
   - Aksi: Klik menu "My Classes" atau "Kelas Saya"
   - Verifikasi:
     - List class assignments tampil
     - Today's session terlihat dengan status "READY"
     - Tombol "Check-in" tersedia dan enabled

3. **Teacher Self Check-in**
   - Aksi: Klik tombol "Check-in" untuk session hari ini
   - Verifikasi:
     - Modal check-in muncul dengan informasi session
     - Arrival time otomatis terisi dengan waktu sekarang
     - Field "Location Confirmation" tersedia
     - Tombol "Confirm Check-in" aktif

4. **Confirm Check-in**
   - Aksi: Konfirm check-in dengan klik "Confirm Check-in"
   - Verifikasi:
     - Success message "Check-in berhasil" tampil
     - Session status berubah ke "IN_PROGRESS"
     - Timer session mulai berjalan
     - Tombol "Start Session" muncul

#### Bagian 2: Session Execution
5. **Start Class Session**
   - Aksi: Klik tombol "Start Session"
   - Verifikasi:
     - Session dashboard terbuka
     - Student attendance interface tampil
     - List siswa dengan status "Not Marked" tampil
     - Session objectives dan materials terlihat

6. **Mark Student Attendance**
   - Aksi: Mark attendance untuk setiap siswa (6 hadir, 2 izin)
   - Verifikasi:
     - Attendance status berubah per siswa
     - Real-time attendance counter update (6/8 hadir)
     - Tombol untuk add notes tersedia per siswa
     - Save attendance otomatis aktif

7. **Add Session Notes**
   - Aksi: Tambahkan session notes di field "Session Summary"
   - Data: "Materi Makharijul Huruf - siswa menunjukkan progress baik"
   - Verifikasi:
     - Text tersimpan secara otomatis (auto-save)
     - Character counter tampil
     - Format text options tersedia

8. **Record Learning Objectives Achievement**
   - Aksi: Mark learning objectives yang tercapai
   - Verifikasi:
     - Checklist objectives dapat di-toggle
     - Progress indicator menunjukkan % completion
     - Additional notes field untuk objectives

#### Bagian 3: Session Completion
9. **End Session dan Check-out**
   - Aksi: Klik tombol "End Session"
   - Verifikasi:
     - End session modal muncul
     - Session summary review tampil
     - Attendance summary tampil (6/8 hadir)
     - Departure time otomatis terisi

10. **Final Session Submission**
    - Aksi: Klik "Submit Session" untuk complete
    - Verifikasi:
      - Session status berubah ke "COMPLETED"
      - Success message tampil
      - Redirect ke My Classes dashboard
      - Session card menampilkan "Completed" status

### Hasil Diharapkan
- Teacher berhasil check-in dan check-out
- Session executed dengan attendance tercatat
- Session summary dan objectives recorded
- Session status = "COMPLETED"
- Teacher attendance record tersimpan di database

### Kriteria Sukses
- [ ] Check-in process lancar tanpa error
- [ ] Attendance marking berfungsi untuk semua siswa  
- [ ] Session notes dan objectives tersimpan
- [ ] Check-out process completed successfully
- [ ] Database records accurate (teacher_attendance, session_data)

---

## KHS-HP-002: Siswa - Pengumpulan Feedback Anonim

### Informasi Skenario
- **ID Skenario**: KHS-HP-002
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `StudentFeedbackTest.anonymousFeedbackSubmission()`

### Prasyarat
- Student account tersedia: `siswa.ali` / `Welcome@YSQ2024`
- Student enrolled in active class
- Feedback campaign active untuk student tersebut
- Template "Teacher Evaluation" tersedia

### Data Test
```
Student Login:
Username: siswa.ali
Password: Welcome@YSQ2024

Feedback Campaign:
Type: Teacher Evaluation
Target: Ustadz Ahmad - Tahsin 1 Class
Duration: 7 days remaining
Questions: 12 questions total
Anonymity: Full anonymity maintained
```

### Langkah Pengujian

#### Bagian 1: Access Feedback System
1. **Student Login**
   - Aksi: Login sebagai student
   - Verifikasi:
     - Student dashboard tampil
     - Notification badge untuk feedback tampil
     - Menu "Feedback" tersedia

2. **View Available Feedback**
   - Aksi: Klik menu "Feedback" atau notification
   - Verifikasi:
     - Active feedback campaigns tampil
     - "Teacher Evaluation" campaign terlihat
     - Anonymous badge/indicator tampil
     - "Start Feedback" button tersedia

3. **Start Feedback Session**
   - Aksi: Klik "Start Feedback" untuk teacher evaluation
   - Verifikasi:
     - Feedback form terbuka
     - Anonymity notice tampil jelas
     - Progress indicator (1/12) tampil
     - Question categories terlihat

#### Bagian 2: Complete Feedback Questions
4. **Answer Teaching Quality Questions (Q1-3)**
   - Aksi: Jawab pertanyaan kualitas pengajaran
   - Data: 
     - Q1: "Bagaimana kualitas pengajaran ustadz?" → Rating 4/5
     - Q2: "Apakah materi disampaikan dengan jelas?" → Rating 5/5
     - Q3: "Contoh mudah dipahami?" → Yes
   - Verifikasi:
     - Rating stars/scale berfungsi
     - Progress bar update (3/12)
     - Auto-save indicator active

5. **Answer Communication Questions (Q4-6)**
   - Aksi: Jawab pertanyaan komunikasi
   - Data:
     - Q4: "Kemampuan menjawab pertanyaan?" → Rating 4/5
     - Q5: "Bersikap sabar dan ramah?" → Rating 5/5
     - Q6: "Mudah berkomunikasi?" → Rating 4/5
   - Verifikasi:
     - Consistent rating interface
     - Progress update (6/12)

6. **Answer Punctuality Questions (Q7-8)**
   - Aksi: Jawab pertanyaan ketepatan waktu
   - Data:
     - Q7: "Hadir tepat waktu?" → Rating 4/5
     - Q8: "Siap dengan materi?" → Rating 5/5
   - Verifikasi: Progress update (8/12)

7. **Answer Fairness Questions (Q9-10)**
   - Aksi: Jawab pertanyaan keadilan
   - Data:
     - Q9: "Berlaku adil kepada semua siswa?" → Rating 5/5
     - Q10: "Suasana kelas kondusif?" → Rating 4/5
   - Verifikasi: Progress update (10/12)

#### Bagian 3: Complete Open-ended Questions
8. **Provide Constructive Feedback (Q11-12)**
   - Aksi: Isi open-ended questions
   - Data:
     - Q11: "Yang paling disukai?" → "Penjelasan tajwid sangat jelas dan sabar"
     - Q12: "Saran untuk perbaikan?" → "Mungkin bisa lebih banyak contoh praktik"
   - Verifikasi:
     - Text area berfungsi dengan baik
     - Character limit indicator tampil
     - Progress bar completion (12/12)

#### Bagian 4: Submit Feedback
9. **Review Feedback Before Submit**
   - Aksi: Review answers pada summary page
   - Verifikasi:
     - All answers displayed correctly
     - Anonymous submission reminder tampil
     - "Submit Feedback" button enabled
     - "Edit Answers" option tersedia

10. **Final Submission**
    - Aksi: Klik "Submit Feedback"
    - Verifikasi:
      - Confirmation modal tampil
      - "Feedback submitted anonymously" message
      - Thank you message tampil
      - Redirect ke dashboard
      - Campaign status berubah ke "Completed"

### Hasil Diharapkan
- Student berhasil submit feedback secara anonymous
- Semua 12 questions terjawab dengan complete
- Feedback data tersimpan dengan anonymous token
- Student tidak dapat re-submit untuk campaign sama

### Kriteria Sukses
- [ ] Anonymous feedback process lancar
- [ ] All question types berfungsi (rating, yes/no, text)
- [ ] Progress tracking accurate
- [ ] Data submitted dengan anonymity maintained
- [ ] UI feedback clear dan user-friendly

---

## KHS-HP-003: Academic Admin - Monitoring Real-time Session Activities

### Informasi Skenario
- **ID Skenario**: KHS-HP-003
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `SessionMonitoringTest.adminRealTimeMonitoring()`

### Prasyarat
- Admin staff account tersedia: `academic.admin1` / `Welcome@YSQ2024`
- Multiple active sessions running
- Teacher attendance data available
- Student feedback campaigns active

### Data Test
```
Academic Admin Login:
Username: academic.admin1
Password: Welcome@YSQ2024

Real-time Data Expected:
Active Sessions: 5 sessions ongoing
Teacher Check-ins: 4/5 teachers checked in
Student Attendance: Average 85% across all classes
Pending Feedback: 3 campaigns with responses
System Alerts: Any issues to monitor
```

### Langkah Pengujian

#### Bagian 1: Access Real-time Dashboard
1. **Academic Admin Login**
   - Aksi: Login sebagai academic admin
   - Verifikasi:
     - Admin dashboard with real-time widgets
     - "Live Session Monitor" card tersedia
     - Quick stats tampil (active sessions, attendance)

2. **Open Session Monitoring Dashboard**
   - Aksi: Navigate ke "Session Monitoring" atau "Live Classes"
   - Verifikasi:
     - Real-time session grid tampil
     - Session status indicators (IN_PROGRESS, READY, COMPLETED)
     - Teacher check-in status visible
     - Auto-refresh indicator active

#### Bagian 2: Monitor Active Sessions
3. **View Session Details**
   - Aksi: Click pada active session untuk detail
   - Verifikasi:
     - Session info popup atau panel terbuka
     - Teacher attendance status (checked-in time)
     - Student attendance live count
     - Session progress indicators

4. **Monitor Attendance Patterns**
   - Aksi: Review attendance summary across sessions
   - Verifikasi:
     - Attendance rates per class visible
     - Real-time updates as teachers mark attendance
     - Alert indicators untuk low attendance

5. **Check System Alerts**
   - Aksi: Review alert panel atau notifications
   - Verifikasi:
     - Late teacher check-ins flagged
     - Sessions not started alerts
     - Any technical issues highlighted

#### Bagian 3: Review Feedback Analytics
6. **Access Feedback Dashboard**
   - Aksi: Navigate ke feedback analytics section
   - Verifikasi:
     - Active campaigns dengan response rates
     - Real-time feedback statistics
     - Recent feedback summary/highlights

7. **Generate Quick Reports**
   - Aksi: Generate today's activity report
   - Verifikasi:
     - Report generation successful
     - PDF/Excel download options
     - Email distribution options

### Hasil Diharapkan
- Real-time monitoring dashboard functional
- Accurate session dan attendance data
- Effective alert system untuk issues
- Quick reporting capabilities available

### Kriteria Sukses
- [ ] Real-time data updates correctly
- [ ] Session monitoring comprehensive
- [ ] Alert system responsive to issues
- [ ] Reporting functionality works
- [ ] UI responsive and informative

---

## KHS-HP-004: Instruktur - Penjadwalan Ulang Sesi (Reschedule)

### Informasi Skenario
- **ID Skenario**: KHS-HP-004
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `SessionRescheduleTest.instructorRescheduleRequest()`

### Prasyarat
- Instructor account tersedia: `ustadz.ahmad` / `Welcome@YSQ2024`
- Scheduled session for tomorrow exists
- Reschedule policies configured
- Student notification system active

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Session to Reschedule:
Class: Tahsin 1 - Selasa Pagi  
Original Date: Tomorrow (Selasa)
Original Time: 08:00-10:00
Students: 8 enrolled students
Reason: Teacher illness (auto-approved category)
Proposed New Date: Day after tomorrow (Rabu)
Proposed New Time: 08:00-10:00
```

### Langkah Pengujian

#### Bagian 1: Request Session Reschedule
1. **Access My Classes**
   - Aksi: Login dan navigate ke "My Classes"
   - Verifikasi:
     - Upcoming sessions tampil
     - Tomorrow's session visible dengan status "SCHEDULED"
     - "Reschedule" option tersedia per session

2. **Initiate Reschedule Request**
   - Aksi: Klik "Reschedule" untuk tomorrow's session
   - Verifikasi:
     - Reschedule modal terbuka
     - Session details tampil correctly
     - Reason dropdown dengan predefined options
     - Date/time picker tersedia

3. **Select Reschedule Reason**
   - Aksi: Pilih reason "Teacher Illness" dari dropdown
   - Verifikasi:
     - Reason selected
     - Auto-approval indicator tampil (if applicable)
     - Additional notes field available

4. **Choose New Date and Time**
   - Aksi: Set new date (day after tomorrow) dan time (08:00)
   - Verifikasi:
     - Date picker berfungsi
     - Time slot availability check
     - Conflict detection (if any)
     - Student impact assessment tampil

#### Bagian 2: Submit Reschedule Request
5. **Add Detailed Notes**
   - Aksi: Tambah notes "Sakit demam, tidak bisa mengajar besok"
   - Verifikasi:
     - Text field accepts input
     - Character limit indicator
     - Optional field clearly marked

6. **Review Impact Assessment**
   - Aksi: Review impact summary
   - Verifikasi:
     - Affected students count (8 students)
     - Parent notification requirement
     - Auto-approval status (for illness)

7. **Submit Reschedule Request**
   - Aksi: Klik "Submit Reschedule Request"
   - Verifikasi:
     - Request submitted successfully
     - Confirmation message tampil
     - Request ID generated
     - Status changed to "RESCHEDULE_PENDING" atau "APPROVED"

#### Bagian 3: Confirm Notifications Sent
8. **Check Request Status**
   - Aksi: Refresh My Classes dan check status
   - Verifikasi:
     - Original session status = "RESCHEDULED"
     - New session created with "SCHEDULED" status
     - Reschedule log entry visible

9. **Verify Student Notifications**
   - Aksi: Check notifications sent indicator
   - Verifikasi:
     - "Students Notified" status = true
     - Notification timestamp recorded
     - Parent notification status available

### Hasil Diharapkan
- Reschedule request processed successfully
- Original session cancelled, new session created
- Students dan parents notified automatically
- Reschedule reason dan impact documented

### Kriteria Sukses
- [ ] Reschedule workflow intuitive dan efficient
- [ ] Auto-approval working untuk valid reasons
- [ ] Session created correctly dengan new date/time
- [ ] Notification system triggers properly
- [ ] Audit trail maintained for reschedule

---

## KHS-HP-005: Progres Tracking Mingguan

### Informasi Skenario
- **ID Skenario**: KHS-HP-005
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR, ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Multiple sessions completed within current week
- Student progress data available
- Learning objectives tracking active
- Weekly reporting enabled

### Data Test
```
Weekly Progress Context:
Week: Week 5 of Semester 1 2024/2025
Class: Tahsin 1 - Senin Pagi
Sessions This Week: 2 sessions completed
Student Progress:
- 8 students enrolled
- Average attendance: 87.5%
- Learning objectives: 85% completion rate
- Student satisfaction: 4.2/5.0 average

Progress Metrics:
- Skill development tracking
- Participation levels
- Assignment completion
- Individual student notes
```

### Langkah Pengujian

#### Bagian 1: Access Weekly Progress Dashboard
1. **Navigate ke Progress Tracking**
   - Aksi: Access "My Classes" → "Weekly Progress"
   - Verifikasi:
     - Weekly progress interface available
     - Current week highlighted
     - Class selection options shown
     - Progress metrics overview visible

2. **Select Class untuk Progress Review**
   - Aksi: Select "Tahsin 1 - Senin Pagi" class
   - Verifikasi:
     - Week 5 progress data displayed
     - Session completion status shown (2/2)
     - Student roster dengan progress indicators
     - Skill development tracking visible

#### Bagian 2: Review Student Progress
3. **Analyze Individual Student Progress**
   - Aksi: Click pada individual students untuk detailed view
   - Verifikasi:
     - Student progress profile displayed
     - Session-by-session attendance shown
     - Learning objective achievement tracked
     - Individual notes dan observations recorded

4. **Review Class Performance Metrics**
   - Aksi: Examine class-level progress indicators
   - Verifikasi:
     - Overall attendance rate (87.5%)
     - Objective completion rate (85%)
     - Student satisfaction scores (4.2/5.0)
     - Performance trends compared to previous weeks

#### Bagian 3: Generate Progress Reports
5. **Create Weekly Progress Summary**
   - Aksi: Generate "Week 5 Progress Report"
   - Verifikasi:
     - Comprehensive weekly summary generated
     - Individual student progress included
     - Class performance metrics calculated
     - Learning objectives assessment completed
     - Recommendations untuk improvement noted

6. **Share Progress Update**
   - Aksi: Distribute progress report to stakeholders
   - Verifikasi:
     - Academic admin notification sent
     - Parent progress summaries prepared
     - Individual student feedback compiled
     - Archive copy maintained

### Kriteria Sukses
- [ ] Weekly progress tracking comprehensive
- [ ] Individual student development monitored
- [ ] Class performance metrics accurate
- [ ] Progress reporting systematic
- [ ] Stakeholder communication effective

---

## KHS-HP-006: Emergency Response dan Contingency Handling

### Informasi Skenario
- **ID Skenario**: KHS-HP-006
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN, SYSTEM_ADMIN
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Emergency response procedures defined
- Backup communication systems active
- Alternative session arrangements available
- Staff notification systems operational

### Data Test
```
Emergency Scenario:
Type: Natural disaster (flood warning)
Impact: Campus closure untuk 2 days
Affected Sessions: 15 sessions across 3 days
Students Affected: 45 students total
Alternative Arrangements: Online sessions

Emergency Response Requirements:
- Immediate notification to all stakeholders
- Session rescheduling atau online alternatives
- Student/parent communication
- Teacher coordination
- System backup dan data protection
```

### Langkah Pengujian

#### Bagian 1: Emergency Declaration
1. **Activate Emergency Response Protocol**
   - Aksi: Access "Emergency Management" → "Activate Emergency Response"
   - Verifikasi:
     - Emergency activation interface available
     - Emergency types selectable (natural disaster)
     - Impact assessment tools present
     - Notification systems ready

2. **Assess Impact dan Affected Sessions**
   - Aksi: Select affected dates dan sessions
   - Verifikasi:
     - Automatic session impact calculation (15 sessions)
     - Student impact assessment (45 students)
     - Teacher notification requirements identified
     - Alternative arrangement options presented

#### Bagian 2: Execute Emergency Notifications
3. **Send Emergency Notifications**
   - Aksi: Trigger emergency notification system
   - Verifikasi:
     - Mass notification system activated
     - Students notified via multiple channels
     - Parents received emergency communication
     - Teachers alerted dengan instructions
     - Management team notified

4. **Coordinate Alternative Arrangements**
   - Aksi: Setup alternative session arrangements
   - Verifikasi:
     - Online session alternatives configured
     - Reschedule options presented
     - Resource allocation adjusted
     - Technology requirements communicated

#### Bagian 3: Monitor Emergency Response
5. **Track Response Effectiveness**
   - Aksi: Monitor emergency response progress
   - Verifikasi:
     - Notification delivery rates tracked
     - Response acknowledgments received
     - Alternative session adoption rates
     - System performance under emergency load

6. **Document Emergency Response**
   - Aksi: Create emergency response documentation
   - Verifikasi:
     - Response timeline documented
     - Effectiveness metrics recorded
     - Lessons learned captured
     - Process improvement recommendations noted

### Kriteria Sukses
- [ ] Emergency response activated quickly
- [ ] Stakeholder notifications comprehensive
- [ ] Alternative arrangements effective
- [ ] System stability maintained during emergency
- [ ] Documentation complete untuk future reference

---

## Integration dengan Academic Operations

### Daily Operations Workflow

| Activity | Responsibility | Frequency | Integration Points |
|----------|---------------|-----------|-------------------|
| Session Check-in | Instructor | Per Session | Attendance, Scheduling |
| Student Feedback | Student | Weekly | Performance, Quality |
| Real-time Monitoring | Academic Admin | Continuous | All Sessions |
| Progress Tracking | Instructor/Admin | Weekly | Learning Outcomes |
| Emergency Response | System Admin | As Needed | All Operations |

## Business Rules dan Operational Standards

### Session Management Rules
- [ ] Teacher check-in required before session start
- [ ] Attendance marking mandatory untuk all sessions
- [ ] Session notes required untuk documentation
- [ ] Learning objectives tracking enabled
- [ ] Auto-save functionality active untuk data protection

### Feedback System Requirements
- [ ] Student anonymity strictly maintained
- [ ] Feedback campaigns time-limited
- [ ] Response aggregation automated
- [ ] Quality improvement tracking active
- [ ] Multi-language support available

### Monitoring dan Reporting Standards
- [ ] Real-time session monitoring operational
- [ ] Alert system responsive to issues
- [ ] Weekly progress reporting automated
- [ ] Emergency response procedures tested
- [ ] Data backup dan recovery verified

---

**Implementation Notes**:
- Daily operations focus on session execution dan monitoring
- Student feedback system maintains anonymity while providing insights
- Real-time monitoring ensures operational visibility
- Emergency procedures ensure continuity of education
- All activities integrated with broader academic management system