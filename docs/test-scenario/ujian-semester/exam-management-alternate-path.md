# Skenario Pengujian: Exam Management - Alternate Path

## Informasi Umum
- **Kategori**: Ujian Semester - Manajemen Ujian (Error Cases)
- **Modul**: Exam Creation and Configuration - Error Handling
- **Tipe Skenario**: Alternate Path (Jalur Alternatif/Error)
- **Total Skenario**: 6 skenario error handling dan edge cases
- **Playwright Test**: `exam-management.ExamManagementAlternateTest`

---

## EM-AP-001: Insufficient Questions in Question Bank

### Informasi Skenario
- **ID Skenario**: EM-AP-001 (Exam Management - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit
- **Tipe Error**: Business Rule Violation

### Prasyarat
- Academic admin account: `academic.admin1` / `Welcome@YSQ2024`
- Question bank has only 15 questions for selected category
- Attempting to create 40-question exam
- System requires minimum question availability

### Data Test
```
Scenario Context:
Attempted Exam: Midterm for Tahfidz 3
Required Questions: 40
Available in Bank: 15 (for selected category)
Expected Error: Insufficient questions available

Alternative Solutions:
- Reduce question count
- Add more questions to bank
- Use questions from multiple categories
- Import questions from external source
```

### Langkah Pengujian

#### Bagian 1: Attempt Exam Creation
1. **Start Creating Exam with Insufficient Questions**
   - Aksi: Navigate ke exam creation
   - Verifikasi:
     - Form loads normally
     - Question bank status visible

2. **Configure Exam Requirements**
   - Aksi: Set requirement for 40 questions
   - Verifikasi:
     - Requirement accepted in form
     - No immediate validation

#### Bagian 2: Encounter Error
3. **Attempt Question Selection**
   - Aksi: Try to select 40 questions from bank with only 15
   - Verifikasi:
     - Warning message appears: "Only 15 questions available"
     - Selection limited to available count
     - Suggestion to add more questions displayed

4. **Handle Insufficient Questions**
   - Aksi: System provides options:
     - Reduce exam question count
     - Add questions from other categories
     - Create new questions
     - Import question set
   - Verifikasi:
     - All alternative options functional
     - Clear guidance provided
     - Progress can be saved as draft

#### Bagian 3: Resolution
5. **Choose Alternative Solution**
   - Aksi: Select "Add from other categories"
   - Verifikasi:
     - Category browser opens
     - Can mix questions from multiple sources
     - Total count updates correctly

6. **Complete Exam Creation**
   - Aksi: Add 25 more questions from other categories
   - Verifikasi:
     - Total reaches 40 questions
     - Exam can now be saved
     - Category mix documented

### Hasil Diharapkan
- System prevents creation of exam with insufficient questions
- Clear error messages dan alternative solutions provided
- Exam creation can continue with mixed categories
- Audit log captures the exception handling

### Kriteria Sukses
- [ ] Error detected before exam publication
- [ ] Alternative solutions clearly presented
- [ ] User can recover from error state
- [ ] Data integrity maintained
- [ ] Helpful guidance provided

---

## EM-AP-002: Schedule Conflict with Existing Exams

### Informasi Skenario
- **ID Skenario**: EM-AP-002
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Tipe Error**: Scheduling Conflict

### Prasyarat
- Instructor attempting to schedule quiz
- Existing midterm exam already scheduled for same time
- Students would have conflicting exams
- System must prevent double-booking

### Data Test
```
Existing Exam:
- Midterm Exam: Oct 15, 2024, 08:00-10:00
- Students enrolled: 25

Attempted Quiz:
- Quiz Chapter 4: Oct 15, 2024, 09:00-09:30
- Same students affected
- Conflict Type: Time overlap

Resolution Options:
- Reschedule quiz
- Merge with existing exam
- Create make-up session
```

### Langkah Pengujian

#### Bagian 1: Attempt Conflicting Schedule
1. **Create Quiz with Conflicting Time**
   - Aksi: Set quiz for Oct 15, 09:00
   - Verifikasi:
     - Date/time selector works
     - No immediate warning

2. **Submit Quiz Configuration**
   - Aksi: Try to save quiz
   - Verifikasi:
     - Validation triggered
     - Conflict detection runs

#### Bagian 2: Handle Conflict
3. **Receive Conflict Warning**
   - Aksi: System shows conflict alert
   - Verifikasi:
     - Clear conflict message
     - Shows existing exam details
     - Lists affected students (25)
     - Suggests alternative times

4. **View Alternative Slots**
   - Aksi: Click "Show Available Slots"
   - Verifikasi:
     - Calendar with free slots
     - No-conflict times highlighted
     - Student availability considered

#### Bagian 3: Resolve Conflict
5. **Select Alternative Time**
   - Aksi: Choose Oct 16, 14:00
   - Verifikasi:
     - New time validated
     - No conflicts detected
     - Can proceed with save

6. **Save with New Schedule**
   - Aksi: Confirm new schedule
   - Verifikasi:
     - Quiz saved successfully
     - Students notified of schedule
     - Calendar updated

### Kriteria Sukses
- [ ] Scheduling conflicts detected accurately
- [ ] Clear conflict information provided
- [ ] Alternative solutions offered
- [ ] Resolution process smooth
- [ ] Student notifications sent

---

## EM-AP-003: System Overload During Exam Creation

### Informasi Skenario
- **ID Skenario**: EM-AP-003
- **Prioritas**: Sedang
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit
- **Tipe Error**: System Performance Issue

### Prasyarat
- Multiple users creating exams simultaneously
- System under heavy load
- Database response slow
- Potential timeout situations

### Data Test
```
System Load Scenario:
- 50 concurrent exam creators
- Database response time: 5+ seconds
- Session timeout risk: 30 minutes
- Auto-save interval: 2 minutes

Expected Issues:
- Slow page loads
- Save operations delayed
- Possible session timeout
- Data loss risk
```

### Langkah Pengujian

#### Bagian 1: Experience System Slowdown
1. **Start Exam Creation During Peak Time**
   - Aksi: Begin creating complex exam
   - Verifikasi:
     - Page loads slowly
     - Loading indicators visible
     - System responsive but delayed

2. **Work with Slow Response**
   - Aksi: Continue adding questions
   - Verifikasi:
     - Each action takes longer
     - Progress indicators shown
     - Auto-save status visible

#### Bagian 2: Handle Performance Issues
3. **Monitor Auto-Save Function**
   - Aksi: Check auto-save indicator
   - Verifikasi:
     - Auto-save working despite slowdown
     - Last save timestamp shown
     - Unsaved changes marked

4. **Experience Timeout Warning**
   - Aksi: Remain idle for 25 minutes
   - Verifikasi:
     - Session warning appears at 25 minutes
     - Option to extend session
     - Countdown timer visible

#### Bagian 3: Prevent Data Loss
5. **Extend Session**
   - Aksi: Click "Extend Session"
   - Verifikasi:
     - Session renewed
     - Work can continue
     - No data lost

6. **Use Draft Save Feature**
   - Aksi: Save as draft frequently
   - Verifikasi:
     - Draft saves faster than publish
     - Can resume later
     - All data preserved

### Kriteria Sukses
- [ ] Auto-save prevents data loss
- [ ] Session warnings timely
- [ ] Draft functionality reliable
- [ ] System remains usable under load
- [ ] Recovery options available

---

## EM-AP-004: Invalid Question Format Import

### Informasi Skenario
- **ID Skenario**: EM-AP-004
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit
- **Tipe Error**: Data Validation Error

### Prasyarat
- Instructor importing questions from Excel
- Some questions have formatting errors
- Mixed valid and invalid data
- System must validate and report issues

### Data Test
```
Import File Issues:
Total Questions: 20
Valid Questions: 14
Invalid Questions: 6

Error Types:
- Missing correct answer marker: 3 questions
- Exceeded character limit: 2 questions
- Invalid question type: 1 question

Expected Behavior:
- Validate all questions
- Report specific errors
- Allow partial import
- Provide correction guidance
```

### Langkah Pengujian

#### Bagian 1: Attempt Import
1. **Upload Question File**
   - Aksi: Upload Excel file with mixed valid/invalid questions
   - Verifikasi:
     - File upload successful
     - Validation process starts
     - Progress indicator shown

2. **Receive Validation Report**
   - Aksi: View validation results
   - Verifikasi:
     - Summary shows 14 valid, 6 invalid
     - Specific errors listed
     - Line numbers identified

#### Bagian 2: Review Errors
3. **Examine Error Details**
   - Aksi: Click on each error for details
   - Verifikasi:
     - Error type explained
     - Original data shown
     - Correction suggestion provided
     - Can edit inline

4. **Decide on Partial Import**
   - Aksi: Choose to import valid questions only
   - Verifikasi:
     - Option to import 14 valid questions
     - Option to fix and retry
     - Option to cancel entirely

#### Bagian 3: Complete Partial Import
5. **Import Valid Questions**
   - Aksi: Proceed with partial import
   - Verifikasi:
     - 14 questions imported successfully
     - Import log generated
     - Failed questions listed for later fixing

6. **Download Error Report**
   - Aksi: Download detailed error report
   - Verifikasi:
     - Excel file with error annotations
     - Correction template provided
     - Can fix and re-import

### Kriteria Sukses
- [ ] Validation comprehensive and accurate
- [ ] Error reporting clear and actionable
- [ ] Partial import functionality works
- [ ] Error recovery smooth
- [ ] Audit trail maintained

---

## EM-AP-005: Unauthorized Exam Modification Attempt

### Informasi Skenario
- **ID Skenario**: EM-AP-005
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR (without permission)
- **Estimasi Waktu**: 6-8 menit
- **Tipe Error**: Authorization Error

### Prasyarat
- Instructor trying to modify another instructor's exam
- Lacks necessary permissions
- System must enforce access control
- Security audit required

### Data Test
```
Security Scenario:
Actor: ustadz.ahmad
Target Exam: Created by ustadz.ibrahim
Action Attempted: Modify questions
Expected Result: Access denied

Permission Model:
- Can edit: Own exams only
- Can view: Assigned class exams
- Cannot edit: Others' exams
- Exception: Admin override
```

### Langkah Pengujian

#### Bagian 1: Attempt Unauthorized Access
1. **Try to Access Another's Exam**
   - Aksi: Navigate to exam created by different instructor
   - Verifikasi:
     - Can view exam details (read-only)
     - Edit buttons disabled/hidden
     - Permission indicator visible

2. **Attempt Direct URL Manipulation**
   - Aksi: Try to access edit URL directly
   - Verifikasi:
     - Access denied page shown
     - Security warning logged
     - Redirect to authorized area

#### Bagian 2: Handle Security Response
3. **Receive Security Alert**
   - Aksi: System shows authorization error
   - Verifikasi:
     - Clear error message
     - No technical details exposed
     - Help text provided
     - "Request Access" option available

4. **Request Proper Authorization**
   - Aksi: Click "Request Access"
   - Verifikasi:
     - Request form opens
     - Can specify reason
     - Sent to exam owner
     - Admin notified

#### Bagian 3: Audit Trail
5. **Check Security Log**
   - Aksi: Admin reviews security log
   - Verifikasi:
     - Unauthorized attempt logged
     - User, time, action recorded
     - IP address captured
     - No breach occurred

### Kriteria Sukses
- [ ] Unauthorized access prevented
- [ ] Clear authorization errors
- [ ] Proper access request channel
- [ ] Security audit complete
- [ ] No data exposure

---

## EM-AP-006: Exam Deletion with Active Submissions

### Informasi Skenario
- **ID Skenario**: EM-AP-006
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit
- **Tipe Error**: Data Integrity Protection

### Prasyarat
- Admin attempting to delete exam
- 15 students already submitted answers
- 10 students currently taking exam
- System must protect active data

### Data Test
```
Deletion Attempt:
Target Exam: Midterm Tahsin 2
Status: ACTIVE
Submissions: 15 completed, 10 in-progress
Total Students: 35 enrolled

Protection Rules:
- Cannot delete active exams
- Cannot delete with submissions
- Must archive instead
- Requires confirmation
```

### Langkah Pengujian

#### Bagian 1: Attempt Deletion
1. **Try to Delete Active Exam**
   - Aksi: Click delete on active exam
   - Verifikasi:
     - Confirmation dialog appears
     - Warning about active status
     - Shows impact summary

2. **Review Impact Analysis**
   - Aksi: View deletion impact
   - Verifikasi:
     - Shows 15 completed submissions
     - Shows 10 active sessions
     - Lists affected students
     - Data loss warning displayed

#### Bagian 2: Handle Protection
3. **Receive Protection Error**
   - Aksi: Confirm deletion attempt
   - Verifikasi:
     - Deletion blocked
     - Error: "Cannot delete exam with active submissions"
     - Alternative options provided

4. **Choose Alternative Action**
   - Aksi: Select "Archive Exam" instead
   - Verifikasi:
     - Archive option available
     - Preserves all data
     - Removes from active list
     - Can be restored later

#### Bagian 3: Proper Workflow
5. **End Exam Properly**
   - Aksi: First end all active sessions
   - Verifikasi:
     - Force-end option for active sessions
     - Students notified
     - Submissions auto-saved

6. **Archive Completed Exam**
   - Aksi: Archive after all sessions ended
   - Verifikasi:
     - Archival successful
     - All data preserved
     - Can generate reports
     - Restoration possible

### Kriteria Sukses
- [ ] Active exam deletion prevented
- [ ] Data integrity protected
- [ ] Clear error messages provided
- [ ] Alternative workflows available
- [ ] Audit trail comprehensive

---

## Integration dengan Error Recovery

### Error Recovery Matrix

| Error Type | Recovery Method | Data Loss Risk | User Guidance |
|------------|----------------|----------------|---------------|
| Insufficient Questions | Add from multiple categories | None | Clear |
| Schedule Conflict | Alternative time slots | None | Automated |
| System Overload | Auto-save and drafts | Minimal | Visible |
| Invalid Import | Partial import option | None | Detailed |
| Unauthorized Access | Request proper access | None | Clear |
| Active Deletion | Archive alternative | None | Protected |

## Validation Rules Summary

### Pre-Creation Validations
- [ ] Sufficient questions in bank
- [ ] No schedule conflicts
- [ ] Required fields complete
- [ ] Permission verification
- [ ] System capacity check

### During Creation Validations
- [ ] Auto-save every 2 minutes
- [ ] Session timeout warnings
- [ ] Concurrent edit detection
- [ ] Data format validation
- [ ] Progress preservation

### Post-Creation Validations
- [ ] Publishing prerequisites met
- [ ] Student notifications sent
- [ ] Calendar updates applied
- [ ] Audit logs recorded
- [ ] Backup created

---

**Testing Notes**:
- All alternate paths harus maintain data integrity
- Error messages must be user-friendly, not technical
- Recovery options should always be available
- Audit logging mandatory for all errors
- Performance degradation handled gracefully