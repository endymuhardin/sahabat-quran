# Skenario Pengujian: Kegiatan Harian Kelas - Alternate Path

## Informasi Umum
- **Kategori**: Aktivitas Semester Harian - Error Handling
- **Modul**: Daily Class Session Management - Edge Cases
- **Tipe Skenario**: Alternate Path (Jalur Alternatif)
- **Total Skenario**: 10 skenario error handling dan validasi

---

## AKH-AP-001: Instructor - Late Check-in Handling

### Informasi Skenario
- **ID Skenario**: AKH-AP-001 (Aktivitas Kelas Harian - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `SessionExecutionTest.lateCheckinValidation()`

### Prasyarat
- Instructor account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Session scheduled untuk 08:00-10:00
- Current time: 08:45 (45 minutes late)
- Session preparation status: READY

### Data Test
```
Instructor Login:
Username: ustadz.ahmad  
Password: Welcome@YSQ2024

Late Check-in Scenario:
Scheduled Time: 08:00-10:00
Current Time: 08:45 (45 minutes late)
Late Threshold: 15 minutes (exceeded)
Session Status: SCHEDULED (not started)
Students Waiting: 8 students present
```

### Langkah Pengujian

#### Bagian 1: Attempt Late Check-in
1. **Login dan Access Session**
   - Aksi: Login at 08:45, navigate to My Classes
   - Verifikasi:
     - Session tampil dengan warning indicator
     - "LATE" badge visible pada session card
     - Red/orange color coding untuk overdue session

2. **Attempt Check-in**
   - Aksi: Click "Check-in" button
   - Verifikasi:
     - Late check-in modal appears
     - Warning message: "Anda terlambat 45 menit dari jadwal"
     - Explanation field required (mandatory)
     - Estimated session end time updated

3. **Provide Late Reason**
   - Aksi: Fill explanation field
   - Data: "Macet parah di jalan, tidak bisa contact admin"
   - Verifikasi:
     - Text field accepts explanation
     - Character minimum enforced (min 10 characters)
     - "Acknowledge Late Arrival" checkbox required

#### Bagian 2: Handle Session Time Reduction
4. **Review Shortened Session**
   - Aksi: Check updated session duration
   - Verifikasi:
     - Original duration: 2 hours
     - Remaining time: 1 hour 15 minutes
     - Warning: "Session waktu berkurang, adjust learning objectives"
     - Suggested actions provided

5. **Adjust Learning Objectives**
   - Aksi: Modify learning objectives untuk shortened time
   - Verifikasi:
     - Objectives list editable
     - Recommendation untuk prioritas objectives
     - Save changes button available

#### Bagian 3: Administrative Notification
6. **Confirm Late Check-in**
   - Aksi: Click "Proceed with Late Check-in"
   - Verifikasi:
     - Session starts dengan late timestamp recorded
     - Admin notification sent (visible indicator)
     - Late incident logged for records
     - Student apology message option provided

### Hasil Diharapkan
- Late check-in processed dengan appropriate warnings
- Session duration adjusted automatically
- Administrative record of late arrival created
- Learning objectives adapted untuk shortened session

### Kriteria Sukses
- ✅ Late check-in handled gracefully
- ✅ Appropriate warnings dan notifications shown
- ✅ Session time calculations correct
- ✅ Administrative logging functional
- ✅ User guided through resolution process

---

## AKH-AP-002: Student - Duplicate Feedback Submission Attempt

### Informasi Skenario
- **ID Skenario**: AKH-AP-002 (Aktivitas Kelas Harian - Alternate Path - 002)
- **Prioritas**: Sedang
- **Role**: STUDENT
- **Estimasi Waktu**: 5-7 menit
- **Playwright Test**: `StudentFeedbackTest.duplicateFeedbackPrevention()`

### Prasyarat
- Student account: `siswa.ali` / `Welcome@YSQ2024`
- Student already submitted feedback for active campaign
- Same campaign still active (not expired)
- Anonymous token already exists dalam database

### Data Test
```
Student Login:
Username: siswa.ali
Password: Welcome@YSQ2024

Previous Submission:
Campaign: Teacher Evaluation - Ustadz Ahmad
Submission Date: 2 days ago
Anonymous Token: Already generated
Campaign Status: Still Active
Days Remaining: 5 days
```

### Langkah Pengujian

#### Bagian 1: Attempt Access to Completed Feedback
1. **Login dan Navigate to Feedback**
   - Aksi: Login dan go to Feedback section
   - Verifikasi:
     - Feedback campaigns list tampil
     - Previously completed campaign shows "COMPLETED" status
     - Green checkmark atau similar indicator
     - "Start Feedback" button disabled atau hidden

2. **Attempt to Re-access Completed Campaign**
   - Aksi: Try clicking on completed campaign
   - Verifikasi:
     - Click blocked atau no response
     - Tooltip message: "Feedback sudah diserahkan"
     - Alternative: Modal showing completion status

#### Bagian 2: Test Direct URL Access Prevention
3. **Attempt Direct URL Access**
   - Aksi: Try accessing feedback URL directly (copy dari email/SMS)
   - Verifikasi:
     - Redirect to already completed page
     - Message: "Anda sudah memberikan feedback untuk campaign ini"
     - Option to view general feedback status
     - No access to actual feedback form

4. **Verify Anonymous Token Protection**
   - Aksi: System should detect existing anonymous token
   - Verifikasi:
     - Database lookup for existing response
     - Anonymous token conflict detection
     - Proper error message without revealing identity

#### Bagian 3: Alternative Actions Available
5. **Check Alternative Options**
   - Aksi: Review what options are available
   - Verifikasi:
     - "Thank you" message untuk completed feedback
     - Information about when results akan available
     - Link to other active campaigns (if any)
     - Contact info untuk feedback concerns

### Hasil Diharapkan
- Duplicate feedback submission prevented
- Clear communication about previous submission
- Anonymous token system protects against duplicates
- User guided to alternative actions

### Kriteria Sukses
- ✅ Duplicate prevention works correctly
- ✅ Anonymous token system functions properly
- ✅ Clear error messages provided
- ✅ User experience remains positive
- ✅ Data integrity maintained

---

## AKH-AP-003: Admin Staff - Handle Teacher No-Show

### Informasi Skenario
- **ID Skenario**: AKH-AP-003 (Aktivitas Kelas Harian - Alternate Path - 003)
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `EmergencyManagementTest.teacherNoShowHandling()`

### Prasyarat
- Admin staff account: `staff.admin1` / `Welcome@YSQ2024`
- Session scheduled untuk 08:00 (started 30 minutes ago)
- Teacher tidak check-in dan tidak responding
- Students waiting di classroom
- No prior notice dari teacher

### Data Test
```
Admin Staff Login:
Username: staff.admin1
Password: Welcome@YSQ2024

Emergency Situation:
Session: Tahsin 1 - Senin Pagi 08:00-10:00
Teacher: Ustadz Ahmad (no check-in, no contact)
Time Now: 08:30 (30 minutes overdue)  
Students Present: 8 students waiting
Parent Inquiries: 2 parents called asking
Substitute Pool: 2 emergency substitutes available
```

### Langkah Pengujian

#### Bagian 1: Detect dan Assess Emergency
1. **Emergency Alert Detection**
   - Aksi: Admin dashboard should show alert for no-show
   - Verifikasi:
     - Red alert indicator untuk overdue session
     - "Teacher No-Show" alert prominent
     - Session details clearly shown
     - Emergency action buttons available

2. **Assess Situation Details**
   - Aksi: Click on emergency alert untuk full details
   - Verifikasi:
     - Complete session information
     - Teacher contact attempts logged
     - Student count dan parent contacts
     - Severity assessment (HIGH - students waiting)

#### Bagian 2: Attempt Teacher Contact
3. **Initiate Emergency Contact**
   - Aksi: Try contacting missing teacher
   - Verifikasi:
     - Phone call button/integration
     - SMS sending capability
     - WhatsApp message option
     - Contact attempt logging

4. **Log Contact Attempts**
   - Aksi: Record contact attempts
   - Data: "Called 3x, SMS sent, no response - proceeding with substitute"
   - Verifikasi:
     - Contact log updates in real-time
     - Timestamp untuk each attempt
     - Action history maintained

#### Bagian 3: Emergency Substitute Assignment
5. **Access Emergency Substitute Pool**
   - Aksi: Open emergency substitute assignment
   - Verifikasi:
     - Emergency-qualified teachers only
     - Immediate availability filter
     - Distance/location consideration
     - Emergency contact info visible

6. **Assign Emergency Substitute**
   - Aksi: Select dan assign substitute
   - Data: Choose "Ustadzah Siti" (available, nearby)
   - Verifikasi:
     - One-click assignment process
     - Emergency notification sent immediately
     - ETA calculation provided
     - Substitute acknowledgment required

#### Bagian 4: Communicate with Stakeholders
7. **Notify Students/Parents**
   - Aksi: Send emergency notifications
   - Verifikasi:
     - Bulk SMS/message to parents
     - Apology dan explanation included
     - Substitute teacher introduction
     - Updated session timeline

8. **Update Session Status**
   - Aksi: Mark session as "EMERGENCY_SUBSTITUTE"
   - Verifikasi:
     - Status change logged
     - Original teacher marked as "NO_SHOW"
     - Incident report initiated
     - Follow-up tasks created

#### Bagian 5: Monitor Resolution
9. **Track Substitute Arrival**
   - Aksi: Monitor substitute teacher progress
   - Verifikasi:
     - ETA tracking active
     - Substitute check-in notification
     - Session resumption confirmed
     - Parent satisfaction follow-up

10. **Document Incident**
    - Aksi: Complete incident documentation
    - Verifikasi:
      - Full incident timeline recorded
      - Action taken summary
      - Original teacher follow-up required
      - HR notification triggered

### Hasil Diharapkan
- Emergency handled swiftly (under 45 minutes total)
- Substitute assigned dan session resumed
- All stakeholders notified appropriately
- Complete incident documentation created

### Kriteria Sukses
- ✅ Emergency detection immediate
- ✅ Contact protocols followed
- ✅ Substitute assignment efficient
- ✅ Stakeholder communication effective
- ✅ Incident properly documented

---

## AKH-AP-004: Instructor - Session Reschedule Conflict

### Informasi Skenario
- **ID Skenario**: AKH-AP-004 (Aktivitas Kelas Harian - Alternate Path - 004)
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `SessionRescheduleTest.conflictDetectionAndResolution()`

### Prasyarat
- Instructor account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Session to reschedule: Tomorrow 08:00-10:00
- Conflict: Teacher already has another session at proposed new time
- Student schedules may also have conflicts

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Reschedule Request:
Original Session: Tahsin 1 - Selasa 08:00-10:00
Reason: Personal emergency
Proposed New Time: Rabu 08:00-10:00
Conflict Detected: Teacher has Tahsin 2 - Rabu 08:00-10:00
Student Conflicts: 3 students have other classes at proposed time
```

### Langkah Pengujian

#### Bagian 1: Initiate Reschedule with Conflict
1. **Start Reschedule Process**
   - Aksi: Select session dan click "Reschedule"
   - Verifikasi:
     - Reschedule modal opens
     - Current session details shown
     - Date/time picker available

2. **Propose Conflicting New Time**
   - Aksi: Select Rabu 08:00-10:00 as new time
   - Verifikasi:
     - Date/time selection works
     - System performs conflict check
     - Conflict detection runs automatically

#### Bagian 2: Handle Teacher Schedule Conflict
3. **Receive Teacher Conflict Warning**
   - Aksi: System detects teacher scheduling conflict
   - Verifikasi:
     - Red warning message appears
     - Specific conflict shown: "You have Tahsin 2 class at this time"
     - Conflict details include class names dan student counts
     - Resolution options provided

4. **View Alternative Time Suggestions**
   - Aksi: Check system-suggested alternatives
   - Verifikasi:
     - Alternative slots suggested based on availability
     - Ranking by preference (fewer conflicts)
     - Student availability considered
     - Room availability checked

#### Bagian 3: Handle Student Schedule Conflicts
5. **Review Student Conflicts**
   - Aksi: Check student availability for proposed time
   - Verifikasi:
     - Student conflict summary shown
     - Affected students listed by name (if permitted)
     - Impact assessment: "3/8 students unavailable"
     - Alternative suggestions provided

6. **Assess Impact**
   - Aksi: Review impact analysis
   - Verifikasi:
     - Student attendance impact calculated
     - Learning continuity assessment
     - Makeup session requirements
     - Parent notification complexity

#### Bagian 4: Resolve Conflicts
7. **Select Conflict-Free Alternative**
   - Aksi: Choose alternative time dengan minimal conflicts
   - Data: Select Rabu 10:00-12:00 (only 1 student conflict)
   - Verifikasi:
     - New time selection validated
     - Remaining conflicts clearly shown
     - Impact reduced significantly

8. **Handle Remaining Student Conflicts**
   - Aksi: Address the 1 remaining student conflict
   - Verifikasi:
     - Student conflict resolution options
     - Makeup session scheduling available
     - Individual consultation options
     - Parent communication automated

#### Bagian 5: Submit Resolved Reschedule
9. **Final Reschedule Submission**
   - Aksi: Submit reschedule dengan resolved conflicts
   - Verifikasi:
     - All major conflicts resolved
     - Impact minimized dan documented
     - Approval workflow appropriate
     - Stakeholder notifications prepared

10. **Confirm Resolution**
    - Aksi: Verify reschedule acceptance
    - Verifikasi:
      - New session created at conflict-free time
      - Affected students notified appropriately
      - Makeup arrangements confirmed
      - Original session properly cancelled

### Hasil Diharapkan
- Schedule conflicts detected dan resolved
- Minimal impact on students dan other classes
- Alternative time slots successfully identified
- All stakeholders appropriately notified

### Kriteria Sukses
- ✅ Conflict detection accurate dan comprehensive
- ✅ Alternative suggestions helpful
- ✅ Impact assessment realistic
- ✅ Resolution process guided dan efficient
- ✅ Final outcome satisfactory for all parties

---

## AKH-AP-005: Admin Staff - Substitute Teacher Unavailable

### Informasi Skenario
- **ID Skenario**: AKH-AP-005 (Aktivitas Kelas Harian - Alternate Path - 005)
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF
- **Estimasi Waktu**: 15-18 menit
- **Playwright Test**: `SubstituteAssignmentTest.noSubstituteAvailableScenario()`

### Prasyarat
- Admin staff account: `staff.admin1` / `Welcome@YSQ2024`
- Teacher emergency call-in: 2 hours before session
- All qualified substitutes unavailable atau already assigned
- Multiple classes potentially affected
- Parents expecting normal session

### Data Test
```
Admin Staff Login:
Username: staff.admin1
Password: Welcome@YSQ2024

Crisis Situation:
Emergency Call: Ustadz Ahmad - family emergency
Session: Tahsin 1 - Today 14:00-16:00
Notice Period: 2 hours before session
Substitute Pool Check: 
- 5 qualified teachers untuk Tahsin 1
- 3 already teaching other sessions
- 1 unavailable (personal leave)
- 1 not responding (sick)
Result: NO SUBSTITUTE AVAILABLE
```

### Langkah Pengujian

#### Bagian 1: Exhaust Substitute Options
1. **Initial Substitute Search**
   - Aksi: Access emergency substitute assignment
   - Verifikasi:
     - Qualified teacher pool shown (5 teachers)
     - Availability status untuk each teacher
     - Contact status dan last response times

2. **Attempt First Round Contacts**
   - Aksi: Contact all available substitutes
   - Verifikasi:
     - Mass contact capability
     - Individual response tracking
     - Escalating urgency in messages
     - Time-sensitive notifications sent

3. **Receive Negative Responses**
   - Aksi: Log responses dari contacted substitutes
   - Data: All decline atau don't respond within deadline
   - Verifikasi:
     - Response logging accurate
     - Availability status updates
     - Escalation triggers activated

#### Bagian 2: Explore Alternative Solutions
4. **Check Cross-Level Qualified Teachers**
   - Aksi: Expand search to teachers qualified for multiple levels
   - Verifikasi:
     - Cross-qualification search works
     - Teachers dari other levels shown
     - Qualification matching algorithms
     - Availability across broader pool

5. **Consider Senior Students/Alumni**
   - Aksi: Check if senior students/alumni available
   - Verifikasi:
     - Alumni teacher database accessible
     - Emergency volunteer pool options
     - Qualification verification process
     - Quick background check status

6. **Evaluate Session Consolidation**
   - Aksi: Check if session can be combined dengan another class
   - Verifikasi:
     - Compatible class identification
     - Room capacity assessment
     - Learning level compatibility
     - Teacher workload analysis

#### Bagian 3: Implement Alternative Solution
7. **Select Best Alternative**
   - Aksi: Choose session postponement dengan makeup
   - Verifikasi:
     - Postponement vs cancellation options
     - Automatic makeup session scheduling
     - Student impact assessment
     - Resource reallocation planning

8. **Schedule Makeup Session**
   - Aksi: Find suitable makeup session slot
   - Verifikasi:
     - Teacher availability for makeup
     - Student schedule compatibility
     - Room availability check
     - Extended hour compensation

#### Bagian 4: Manage Stakeholder Communication
9. **Craft Emergency Communication**
   - Aksi: Prepare parent/student notification
   - Data: "Due to emergency, today's session postponed. Makeup: Saturday 10:00"
   - Verifikasi:
     - Apologetic but reassuring tone
     - Clear explanation without too much detail
     - Makeup session details prominent
     - Contact info untuk questions

10. **Execute Multi-channel Notification**
    - Aksi: Send notifications via SMS, email, WhatsApp
    - Verifikasi:
      - Multi-channel delivery successful
      - Delivery confirmation received
      - Response handling prepared
      - Follow-up system activated

#### Bagian 5: Document dan Learn
11. **Complete Crisis Documentation**
    - Aksi: Document the full crisis dan resolution
    - Verifikasi:
      - Timeline of events recorded
      - All attempted solutions logged
      - Final resolution documented
      - Lessons learned captured

12. **Improve Future Preparedness**
    - Aksi: Update emergency protocols based on experience
    - Verifikasi:
      - Substitute pool expansion recommendations
      - Emergency response time improvements
      - Communication template updates
      - Preventive measure suggestions

### Hasil Diharapkan
- Crisis managed dengan minimal disruption
- Alternative solution implemented successfully
- All stakeholders informed appropriately
- System improvements identified untuk future

### Kriteria Sukses
- ✅ All substitute options thoroughly explored
- ✅ Creative alternative solution found
- ✅ Stakeholder communication effective
- ✅ Documentation comprehensive
- ✅ Future improvements identified

---

## AKH-AP-006: Teacher - Incomplete Weekly Progress Submission

### Informasi Skenario
- **ID Skenario**: AKH-AP-006 (Aktivitas Kelas Harian - Alternate Path - 006)
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `WeeklyProgressTest.incompleteProgressValidation()`

### Prasyarat
- Instructor account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Weekly progress due today (deadline approaching)
- Some students have missing attendance atau assessment data
- System validation rules active

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Progress Status:
Week: 6 (current week)
Deadline: Today 23:59
Students: 8 total
Complete Data: 5 students
Incomplete Data: 3 students (missing scores/attendance)
Validation Rules: All fields required for submission
```

### Langkah Pengujian

#### Bagian 1: Attempt Incomplete Submission
1. **Access Progress Interface Near Deadline**
   - Aksi: Navigate to weekly progress recording
   - Verifikasi:
     - Deadline warning banner visible
     - Hours remaining countdown shown
     - Completion status indicator (5/8 complete)
     - Urgent priority styling

2. **Review Current Progress Status**
   - Aksi: Check which students have incomplete data
   - Verifikasi:
     - Clear indicators untuk incomplete students
     - Missing field highlighting
     - Student names dengan incomplete status
     - Required field validation visible

3. **Attempt Early Submission**
   - Aksi: Try clicking "Submit Progress" with incomplete data
   - Verifikasi:
     - Submission blocked by validation
     - Error message: "3 students have incomplete progress data"
     - Specific missing fields listed
     - Return to completion guidance

#### Bagian 2: Handle Missing Data Scenarios
4. **Student 1 - Absent All Week**
   - Aksi: Handle student yang absent entire week
   - Data: Mark as "Absent - Extended Illness"
   - Verifikasi:
     - Absence reason dropdown available
     - Special handling untuk extended absence
     - Progress marked as "N/A - Medical Leave"
     - Parent communication flag available

5. **Student 2 - Partial Attendance**
   - Aksi: Handle student dengan irregular attendance
   - Data: Present 1/2 sessions, partial assessment possible
   - Verifikasi:
     - Partial assessment options
     - Pro-rated scoring available
     - Notes field untuk context
     - Incomplete progress acceptable dengan notes

6. **Student 3 - Assessment Issues**
   - Aksi: Handle student dengan assessment difficulties
   - Data: Present but struggling dengan evaluation
   - Verifikasi:
     - Alternative assessment methods
     - "Assessment Pending" status option
     - Follow-up action items
     - Parent consultation scheduling

#### Bagian 3: Complete All Required Fields
7. **Fill Missing Recitation Scores**
   - Aksi: Add recitation assessment untuk partial students
   - Verifikasi:
     - Score entry accepts partial/estimated values
     - Confidence level indicators
     - Notes required untuk estimates
     - Validation accepts completed fields

8. **Add Comprehensive Teacher Notes**
   - Aksi: Provide detailed context untuk each incomplete case
   - Verifikasi:
     - Unlimited text dalam notes fields
     - Context saves automatically
     - Teacher recommendations captured
     - Follow-up actions documented

#### Bagian 4: Final Validation dan Submission
9. **Pre-submission Validation Check**
   - Aksi: Use "Validate Progress" button before final submission
   - Verifikasi:
     - Validation report generated
     - All issues resolved atau acceptably handled
     - Warnings vs errors clearly distinguished
     - Final submission enabled

10. **Submit Complete Progress**
    - Aksi: Final submission dengan all validation passed
    - Verifikasi:
      - Submission successful
      - Confirmation message dengan submission timestamp
      - Progress locked dari further editing
      - Parent notifications triggered untuk completed students

#### Bagian 5: Handle Post-deadline Scenarios
11. **Late Submission Handling**
    - Aksi: Simulate submission after deadline (if missed)
    - Verifikasi:
      - Late submission allowed dengan manager approval
      - Late submission penalty/notification
      - Reason required untuk late submission
      - Escalation to academic management

### Hasil Diharapkan
- All students properly assessed or appropriately marked as exceptions
- Teacher guidance effective untuk handling edge cases
- Validation system flexible but maintains data quality
- Deadline management supportive but enforces accountability

### Kriteria Sukses
- ✅ Validation system comprehensive dan helpful
- ✅ Exception handling appropriate
- ✅ Guidance clear untuk difficult cases
- ✅ Deadline management balanced
- ✅ Final submission successful dan complete

---

## AKH-AP-007: Student - Feedback System Technical Failure

### Informasi Skenario
- **ID Skenario**: AKH-AP-007 (Aktivitas Kelas Harian - Alternate Path - 007)
- **Prioritas**: Sedang
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `StudentFeedbackTest.technicalFailureHandling()`

### Prasyarat
- Student account: `siswa.ali` / `Welcome@YSQ2024`
- Active feedback campaign
- Simulated technical issues: network timeout, session expire, browser crash
- Auto-save functionality testing

### Data Test
```
Student Login:
Username: siswa.ali
Password: Welcome@YSQ2024

Technical Scenario:
Campaign: Teacher Evaluation (12 questions)
Progress: 8/12 questions completed
Time Spent: 15 minutes
Issue Types: Network timeout, session expiry, browser refresh
Auto-save Status: Should preserve answers
Recovery Expected: Resume from question 9
```

### Langkah Pengujian

#### Bagian 1: Normal Progress Until Failure
1. **Start Feedback Normally**
   - Aksi: Begin feedback campaign
   - Verifikasi:
     - Feedback form loads properly
     - Auto-save indicators active
     - Progress tracking functional

2. **Complete First 8 Questions**
   - Aksi: Answer questions 1-8 normally
   - Data: Various ratings dan text responses
   - Verifikasi:
     - Answers saving automatically
     - Progress indicator updates (8/12)
     - Auto-save confirmations visible

#### Bagian 2: Simulate Technical Failure
3. **Simulate Network Timeout (Question 9)**
   - Aksi: Disconnect network during question 9 submission
   - Verifikasi:
     - Timeout error message appears
     - Connection lost indicator shown
     - Retry mechanism available
     - Data preservation indication

4. **Attempt Recovery**
   - Aksi: Reconnect network dan refresh page
   - Verifikasi:
     - Recovery process initiates
     - "Restoring your feedback..." message
     - Previous answers loaded successfully
     - Resume from correct question (9)

#### Bagian 3: Test Session Expiry
5. **Simulate Session Timeout**
   - Aksi: Wait for session to expire during feedback
   - Verifikasi:
     - Session expiry warning appears
     - "Your session will expire in 5 minutes" notification
     - Option to extend session
     - Auto-save continues working

6. **Handle Session Re-authentication**
   - Aksi: Re-authenticate after session expiry
   - Verifikasi:
     - Login prompt appears
     - Background data preserved
     - Seamless return to feedback form
     - Progress maintained

#### Bagian 4: Test Browser Recovery
7. **Simulate Browser Crash**
   - Aksi: Force browser close/refresh during feedback
   - Verifikasi:
     - Browser recovery detection
     - "Unsaved changes detected" prompt
     - Data recovery successful
     - Resume capability available

8. **Complete Feedback After Recovery**
   - Aksi: Complete remaining questions 9-12
   - Verifikasi:
     - All previous answers still intact
     - New answers save properly
     - Completion process normal
     - Final submission successful

#### Bagian 5: Verify Data Integrity
9. **Validate Submitted Data**
   - Aksi: Check feedback submission completeness
   - Verifikasi:
     - All 12 questions properly submitted
     - No data loss dari technical failures
     - Response timestamps accurate
     - Anonymous token preserved

10. **User Experience Assessment**
    - Aksi: Evaluate overall recovery experience
    - Verifikasi:
      - User frustration minimized
      - Recovery process intuitive
      - Error messages helpful
      - Confidence in system maintained

### Hasil Diharapkan
- Technical failures handled gracefully
- Data recovery successful dalam all scenarios
- User experience remains positive despite issues
- Feedback submission completed successfully

### Kriteria Sukses
- ✅ Auto-save functionality robust
- ✅ Recovery mechanisms effective
- ✅ Error messages helpful dan reassuring
- ✅ Data integrity maintained
- ✅ User experience graceful under failure

---

## AKH-AP-008: Admin Staff - Feedback Analytics Insufficient Data

### Informasi Skenario
- **ID Skenario**: AKH-AP-008 (Aktivitas Kelas Harian - Alternate Path - 008)
- **Prioritas**: Rendah
- **Role**: ADMIN_STAFF (Quality Assurance)
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `FeedbackAnalyticsTest.insufficientDataHandling()`

### Prasyarat
- Admin staff account: `staff.admin1` / `Welcome@YSQ2024`
- Feedback campaign dengan very low response rate
- Minimum response threshold: 5 responses per analysis
- Statistical significance requirements

### Data Test
```
Admin Staff Login:
Username: staff.admin1
Password: Welcome@YSQ2024

Low Response Scenario:
Teacher Evaluation Campaign: Only 3 responses (need min 5)
Facility Assessment Campaign: Only 2 responses (need min 5)
Overall Experience Campaign: 4 responses (just below threshold)
Analysis Request: Monthly comprehensive report
Statistical Validity: Below minimum requirements
```

### Langkah Pengujian

#### Bagian 1: Attempt Analysis with Insufficient Data
1. **Access Analytics Dashboard**
   - Aksi: Navigate to feedback analytics
   - Verifikasi:
     - Low response warnings visible
     - Campaign status indicators show "INSUFFICIENT_DATA"
     - Response rate percentages shown (very low)
     - Warning badges on affected campaigns

2. **Attempt Report Generation**
   - Aksi: Try generating monthly report with low data
   - Verifikasi:
     - Warning modal appears
     - "Insufficient responses untuk reliable analysis" message
     - Minimum threshold requirements shown
     - Options untuk proceed atau wait

#### Bagian 2: Handle Statistical Warnings
3. **Review Statistical Validity Warnings**
   - Aksi: Check detailed warnings untuk each campaign
   - Verifikasi:
     - Specific response counts shown
     - Statistical significance explanations
     - Reliability confidence levels
     - Impact on report accuracy

4. **Choose Partial Analysis Options**
   - Aksi: Opt untuk "Generate Report dengan Caveats"
   - Verifikasi:
     - Caveat options clearly explained
     - Data limitations documented
     - Report reliability disclaimers
     - Audience warning requirements

#### Bagian 3: Generate Limited Report
5. **Create Report dengan Data Limitations**
   - Aksi: Proceed dengan limited data report generation
   - Verifikasi:
     - Report includes prominent disclaimers
     - Low response rate clearly mentioned
     - Statistical significance warnings included
     - Recommendations marked as "preliminary"

6. **Review Report Content**
   - Aksi: Check generated report content
   - Verifikasi:
     - Data presented dengan appropriate caveats
     - Charts include "Limited Data" watermarks
     - Recommendations qualified dengan uncertainty
     - Action items marked as "requiring more data"

#### Bagian 4: Plan Response Rate Improvement
7. **Access Response Rate Enhancement Tools**
   - Aksi: Use campaign improvement recommendations
   - Verifikasi:
     - Response rate analysis tools
     - Campaign optimization suggestions
     - Student engagement strategies
     - Incentive program options

8. **Schedule Follow-up Campaigns**
   - Aksi: Plan additional data collection
   - Verifikasi:
     - Reminder campaign scheduling
     - Extended deadline options
     - Alternative collection methods
     - Targeted student outreach

### Hasil Diharapkan
- Low response rate handled appropriately dengan statistical warnings
- Report generated dengan proper limitations documented
- Improvement strategies identified untuk future campaigns
- Data integrity maintained despite insufficient responses

### Kriteria Sukses
- ✅ Statistical warnings appropriate dan clear
- ✅ Report limitations properly documented
- ✅ Improvement recommendations provided
- ✅ Data integrity maintained
- ✅ Professional handling of data limitations

---

## AKH-AP-009: Instructor - Multiple Session Conflicts

### Informasi Skenario
- **ID Skenario**: AKH-AP-009 (Aktivitas Kelas Harian - Alternate Path - 009)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 15-18 menit
- **Playwright Test**: `SessionConflictTest.multipleSessionConflictResolution()`

### Prasyarat
- Instructor account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Teacher assigned to 3 different classes
- Scheduling error creates overlapping sessions
- All sessions have enrolled students expecting classes

### Data Test
```
Instructor Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Conflict Scenario:
Today's Sessions:
1. Tahsin 1 - 08:00-10:00 (8 students)
2. Tahsin 2 - 09:00-11:00 (10 students) [CONFLICT]
3. Tahfidz - 10:30-12:30 (6 students) [PARTIAL CONFLICT]

System Error: Scheduling allowed overlaps
Detection: Teacher attempting check-in triggers alert
Resolution Needed: Immediate (students waiting)
```

### Langkah Pengujian

#### Bagian 1: Detect Multiple Conflicts
1. **Attempt First Session Check-in**
   - Aksi: Try checking in to Tahsin 1 at 08:00
   - Verifikasi:
     - Check-in successful untuk first session
     - System shows upcoming conflict warnings
     - "Scheduling conflicts detected" alert appears
     - Conflict resolution options provided

2. **Review All Conflicting Sessions**
   - Aksi: Check full day schedule dengan conflicts
   - Verifikasi:
     - Visual timeline shows overlaps clearly
     - Conflict severity indicated (full vs partial)
     - Student impact assessment shown
     - Resolution urgency levels displayed

#### Bagian 2: Immediate Conflict Management
3. **Handle Ongoing Session (Tahsin 1)**
   - Aksi: Continue dengan current session while planning conflicts
   - Verifikasi:
     - Current session continues normally
     - Conflict countdown timer for next session
     - Emergency contact options visible
     - Admin notification sent automatically

4. **Contact Administration untuk Support**
   - Aksi: Request immediate admin assistance
   - Verifikasi:
     - One-click admin alert functionality
     - Conflict details sent automatically
     - Emergency substitute request option
     - Estimated resolution time provided

#### Bagian 3: Resolve Second Session Conflict
5. **Handle Tahsin 2 Session (Major Conflict)**
   - Aksi: Session supposed to start at 09:00
   - Verifikasi:
     - "Cannot start - teacher unavailable" message
     - Students notified automatically
     - Admin working on substitute assignment
     - Estimated delay komunikated

6. **Monitor Substitute Assignment**
   - Aksi: Track admin efforts to find substitute
   - Verifikasi:
     - Real-time status updates
     - Substitute assignment progress
     - Student wait time management
     - Parent communication handling

#### Bagian 4: Manage Third Session (Partial Conflict)
7. **Plan Tahfidz Session Adjustment**
   - Aksi: Current session ends 10:00, Tahfidz starts 10:30
   - Verifikasi:
     - 30-minute gap identified as manageable
     - Quick transition plan available
     - Location change considerations
     - Material preparation time adequate

8. **Execute Transition**
   - Aksi: Move from Tahsin 1 to Tahfidz session
   - Verifikasi:
     - Check-out dari first session smooth
     - Transit time warning if location different
     - Check-in to third session successful
     - Student notification of any delays

#### Bagian 5: Document dan Prevent Future Conflicts
9. **Complete Incident Report**
   - Aksi: Document the full conflict resolution
   - Verifikasi:
     - Timeline of events recorded
     - Impact assessment completed
     - Resolution effectiveness noted
     - Student satisfaction measured

10. **Review Scheduling System Issues**
    - Aksi: Work dengan admin to prevent future conflicts
    - Verifikasi:
      - Scheduling system bug reported
      - Validation rules improvement suggested
      - Teacher workload review initiated
      - Conflict prevention measures implemented

### Hasil Diharapkan
- All three sessions handled dengan minimal student impact
- Scheduling conflicts resolved efficiently
- System improvements identified to prevent recurrence
- Teacher workload properly managed

### Kriteria Sukses
- ✅ Conflict detection immediate dan accurate
- ✅ Resolution process coordinated effectively
- ✅ Student impact minimized
- ✅ Administrative support responsive
- ✅ System improvements implemented

---

## AKH-AP-010: System - Database Connection Failure During Peak Usage

### Informasi Skenario
- **ID Skenario**: AKH-AP-010 (Aktivitas Kelas Harian - Alternate Path - 010)
- **Prioritas**: Tinggi
- **Role**: SYSTEM (Multiple users affected)
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `SystemFailureTest.databaseConnectionFailureRecovery()`

### Prasyarat
- Multiple users active: instructors, students, admin
- Peak usage time: morning session start (08:00-09:00)
- Database connection pool exhaustion/failure
- Critical operations affected: check-ins, feedback, monitoring

### Data Test
```
System Status:
Time: 08:15 AM (peak session start time)
Active Users: 25+ concurrent (teachers checking in)
Database Status: Connection pool exhausted
Affected Operations: 
- Teacher check-ins failing
- Student feedback submissions blocked
- Admin monitoring dashboard down
- Session data not saving

Expected Recovery: Automatic failover + manual intervention
```

### Langkah Pengujian

#### Bagian 1: Detect System Failure
1. **Multiple User Impact**
   - Aksi: Various users experience simultaneous failures
   - Verifikasi:
     - Database connection errors displayed
     - "System temporarily unavailable" messages
     - Retry mechanisms activated
     - Error logging functional

2. **System Health Monitoring**
   - Aksi: Check system status dashboard
   - Verifikasi:
     - Database connection failure alerts
     - Service status indicators red
     - Error rate spike detection
     - Automatic monitoring alerts sent

#### Bagian 2: Immediate Failure Handling
3. **Graceful Degradation**
   - Aksi: System attempts to continue dengan limited functionality
   - Verifikasi:
     - Read-only mode activated
     - Cached data served when possible
     - Non-critical features disabled
     - Essential services prioritized

4. **User Communication**
   - Aksi: Inform users about system issues
   - Verifikasi:
     - System status banner displayed
     - Estimated recovery time communicated
     - Alternative procedures suggested
     - Contact information provided

#### Bagian 3: Recovery Process
5. **Database Connection Recovery**
   - Aksi: System administrator intervenes
   - Verifikasi:
     - Connection pool reset
     - Database connectivity restored
     - Service health checks pass
     - Gradual service restoration

6. **Data Synchronization**
   - Aksi: Sync cached dan pending data
   - Verifikasi:
     - Pending teacher check-ins processed
     - Failed feedback submissions recovered
     - Data integrity verified
     - Conflict resolution handled

#### Bagian 4: Service Restoration Verification
7. **Teacher Check-in Recovery**
   - Aksi: Verify teacher check-in functionality restored
   - Verifikasi:
     - Check-in process working
     - Late check-ins processed with appropriate handling
     - Session data saving correctly
     - Admin monitoring functional

8. **Student Feedback Recovery**
   - Aksi: Test student feedback system restoration
   - Verifikasi:
     - Feedback forms accessible
     - Previous partial responses restored
     - Submission process working
     - Anonymous token system functional

#### Bagian 5: Post-Incident Analysis
9. **System Performance Monitoring**
   - Aksi: Monitor system stability post-recovery
   - Verifikasi:
     - Performance metrics normal
     - Error rates returned to baseline
     - User sessions stable
     - Database performance optimal

10. **Incident Documentation**
    - Aksi: Complete post-incident analysis
    - Verifikasi:
      - Root cause identified
      - Resolution steps documented
      - Prevention measures identified
      - System resilience improvements planned

### Hasil Diharapkan
- System failure handled dengan minimal data loss
- Service restored within acceptable timeframe
- User experience degraded gracefully during outage
- Post-incident improvements implemented

### Kriteria Sukses
- ✅ Failure detection immediate
- ✅ Graceful degradation effective
- ✅ Recovery process efficient
- ✅ Data integrity maintained
- ✅ System resilience improved

---

## Summary Skenario Alternate Path

### Total Coverage
- **10 Skenario Error Handling** covering edge cases dan failure modes
- **Multiple Failure Types**: Technical, operational, user error, system failure
- **Recovery Mechanisms**: Graceful degradation, data recovery, alternative solutions

### Error Categories Covered
- ✅ User errors (late check-in, duplicate submissions)
- ✅ Operational challenges (no substitute available, scheduling conflicts)
- ✅ Technical failures (network issues, database problems)
- ✅ Data quality issues (insufficient feedback data)
- ✅ System failures (peak load, connection issues)

### Success Metrics untuk Error Handling
- ✅ Error detection quick dan accurate
- ✅ User guidance clear dan helpful
- ✅ Recovery processes efficient
- ✅ Data integrity maintained
- ✅ User experience remains professional

### Integration dengan Happy Path
- **Error scenarios** complement success scenarios
- **Recovery testing** validates system resilience
- **User experience** maintained under stress
- **Business continuity** assured through alternatives