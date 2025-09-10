# Skenario Pengujian: Student Exam Taking - Alternate Path

## Informasi Umum
- **Kategori**: Ujian Semester - Student Experience (Error Cases)
- **Modul**: Online Exam Taking - Error Handling and Edge Cases
- **Tipe Skenario**: Alternate Path (Jalur Alternatif/Error)
- **Total Skenario**: 6 skenario error handling untuk student exam experience
- **Playwright Test**: `student-exam.StudentExamAlternateTest`

---

## SET-AP-001: Connection Lost During Exam

### Informasi Skenario
- **ID Skenario**: SET-AP-001 (Student Exam Taking - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 10-12 menit
- **Tipe Error**: Network/Connection Issue

### Prasyarat
- Student in middle of exam
- 30 questions completed out of 40
- Internet connection unstable
- 30 minutes remaining

### Data Test
```
Disconnection Scenario:
Student: Ali Rahman
Exam Progress: 30/40 questions answered
Time Elapsed: 60 minutes
Time Remaining: 30 minutes
Connection Lost Duration: 5 minutes
Last Auto-save: 2 minutes before disconnect

Recovery Requirements:
- Preserve all answered questions
- Maintain timer fairness
- Allow seamless resume
- Document incident
```

### Langkah Pengujian

#### Bagian 1: Experience Connection Loss
1. **Lose Connection Mid-Exam**
   - Aksi: Internet disconnects while answering Q31
   - Verifikasi:
     - Offline indicator appears
     - Warning message displayed
     - Local storage kicks in
     - Answer cached locally
     - Timer pauses (or continues based on policy)

2. **Attempt to Continue Offline**
   - Aksi: Try to answer more questions offline
   - Verifikasi:
     - Can view current question
     - Can input answer locally
     - Cannot navigate to new questions
     - "Connection Required" message
     - Reconnection attempts visible

#### Bagian 2: Handle Offline Period
3. **Monitor Recovery Attempts**
   - Aksi: System tries to reconnect
   - Verifikasi:
     - Auto-reconnect every 10 seconds
     - Attempt counter shown
     - Instructions displayed:
       * "Do not close browser"
       * "Do not refresh page"
       * "Answers are saved locally"
     - Manual reconnect button available

4. **Local Data Preservation**
   - Aksi: Check local storage
   - Verifikasi:
     - All 30 answers preserved
     - Current question draft saved
     - Timer state recorded
     - Session token maintained

#### Bagian 3: Reconnection and Recovery
5. **Restore Connection**
   - Aksi: Internet connection restored
   - Verifikasi:
     - Auto-reconnect succeeds
     - "Syncing data..." message
     - Progress bar for sync
     - All answers uploaded
     - Timer resumes correctly

6. **Continue Exam**
   - Aksi: Resume answering from Q31
   - Verifikasi:
     - All previous answers intact
     - Q31 draft recovered
     - Timer shows adjusted time
     - Can complete remaining questions
     - Incident logged in system

### Hasil Diharapkan
- Zero data loss despite 5-minute disconnection
- Fair time compensation (if policy allows)
- Student confidence maintained
- Incident properly documented for review
- Exam completed successfully

### Kriteria Sukses
- [ ] Offline handling graceful
- [ ] Local storage effective
- [ ] Recovery process automatic
- [ ] No answer data lost
- [ ] Fair treatment of time
- [ ] Incident tracking complete

---

## SET-AP-002: Time Expires with Unsaved Essay

### Informasi Skenario
- **ID Skenario**: SET-AP-002
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Tipe Error**: Time Management Issue

### Prasyarat
- Student writing long essay answer
- 2 minutes remaining on exam timer
- Essay half-complete, not saved
- Other questions completed

### Data Test
```
Time Pressure Scenario:
Current Question: Essay Q35
Essay Length: 250 words written, 500 expected
Time Remaining: 2:00 minutes
Auto-save: Last triggered 45 seconds ago
Other Questions: 39/40 completed

Critical Moments:
- 2:00 - Still writing
- 1:00 - Warning appears
- 0:30 - Critical warning
- 0:10 - Final countdown
- 0:00 - Auto-submission
```

### Langkah Pengujian

#### Bagian 1: Time Warning Sequence
1. **Receive 2-Minute Warning**
   - Aksi: Continue writing essay
   - Verifikasi:
     - Yellow warning banner appears
     - "2 minutes remaining" prominent
     - Sound alert (if enabled)
     - Timer turns yellow
     - Auto-save triggers

2. **One-Minute Warning**
   - Aksi: Still writing, trying to finish
   - Verifikasi:
     - Orange warning intensifies
     - "Save your work" message
     - Timer flashing
     - Modal popup warning
     - Force save triggered

#### Bagian 2: Final Countdown
3. **30-Second Critical Warning**
   - Aksi: Frantically trying to complete
   - Verifikasi:
     - Red alert banner
     - "SAVE NOW" button prominent
     - Countdown in center screen
     - All work auto-saving
     - Cannot navigate away

4. **10-Second Final Alert**
   - Aksi: Last attempt to save
   - Verifikasi:
     - Full-screen countdown
     - Everything else disabled
     - Forced save every 2 seconds
     - "Submitting in X seconds"
     - No new input accepted after 0:05

#### Bagian 3: Auto-Submission
5. **Time Expires**
   - Aksi: Timer reaches 0:00
   - Verifikasi:
     - Immediate auto-submission
     - "Saving and submitting..." message
     - Progress indicator shown
     - All partial answers included
     - Cannot make any changes

6. **Post-Submission Confirmation**
   - Aksi: View submission receipt
   - Verifikasi:
     - Submission confirmed
     - Partial essay saved (250 words)
     - All 39 complete answers saved
     - Timestamp recorded
     - Incident note added

### Kriteria Sukses
- [ ] Warning sequence clear and escalating
- [ ] Auto-save prevents total loss
- [ ] Partial work credited
- [ ] Submission process automatic
- [ ] Clear communication throughout

---

## SET-AP-003: Browser Crash During Exam

### Informasi Skenario
- **ID Skenario**: SET-AP-003
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 10-12 menit
- **Tipe Error**: Technical Failure

### Prasyarat
- Student 45 minutes into exam
- Browser crashes unexpectedly
- 25/40 questions answered
- 45 minutes remaining

### Data Test
```
Browser Crash Scenario:
Crash Point: Question 26
Questions Completed: 25
Time Used: 45 minutes
Last Auto-save: 30 seconds before crash
Browser: Chrome/Firefox/Safari
Recovery Window: 15 minutes to resume

Recovery Data:
- Session token in cookies
- Answers in local storage
- Server-side backup every 2 min
```

### Langkah Pengujian

#### Bagian 1: Browser Crash
1. **Experience Browser Crash**
   - Aksi: Browser closes unexpectedly
   - Verifikasi:
     - All browser windows closed
     - No warning given
     - Local data persists
     - Server has recent backup

2. **Restart Browser**
   - Aksi: Open browser again
   - Verifikasi:
     - Browser restore session option
     - Cookies intact
     - Local storage accessible
     - Can navigate to exam site

#### Bagian 2: Recovery Process
3. **Return to Exam Site**
   - Aksi: Navigate to exam URL
   - Verifikasi:
     - Auto-redirect to login
     - Quick login (saved credentials)
     - System detects incomplete exam
     - "Resume Exam" prominent

4. **Resume Interrupted Exam**
   - Aksi: Click "Resume Exam"
   - Verifikasi:
     - Verification of identity
     - Incident report auto-generated
     - Time adjustment consideration
     - Loading previous state

#### Bagian 3: Verify Recovery
5. **Check Recovered Data**
   - Aksi: Review recovered progress
   - Verifikasi:
     - All 25 answers restored
     - Last working question shown (Q26)
     - Timer adjusted fairly
     - Essay text recovered
     - Review flags maintained

6. **Complete Remaining Exam**
   - Aksi: Continue from Q26
   - Verifikasi:
     - Full functionality restored
     - No repeated questions
     - Can complete normally
     - Incident noted in submission

### Kriteria Sukses
- [ ] Browser crash handled gracefully
- [ ] Quick recovery process
- [ ] Minimal data loss (< 2 minutes work)
- [ ] Fair time compensation
- [ ] Student can complete exam

---

## SET-AP-004: Accidental Early Submission

### Informasi Skenario
- **ID Skenario**: SET-AP-004
- **Prioritas**: Sedang
- **Role**: STUDENT
- **Estimasi Waktu**: 6-8 menit
- **Tipe Error**: User Error

### Prasyarat
- Student accidentally clicks submit
- Only 20/40 questions answered
- 60 minutes remaining
- Panic situation

### Data Test
```
Accidental Submission:
Questions Answered: 20/40
Time Remaining: 60 minutes
Submit Type: Accidental click
Grace Period: 2-minute undo window
Policy: May allow resumption

Student State:
- Panicked
- Needs immediate help
- May close browser in panic
```

### Langkah Pengujian

#### Bagian 1: Accidental Submission
1. **Accidentally Click Submit**
   - Aksi: Mistakenly click submit button
   - Verifikasi:
     - Confirmation dialog appears
     - "Are you sure?" message
     - Shows unanswered count (20)
     - Warning about incompleteness
     - Cancel option prominent

2. **Confirm by Mistake**
   - Aksi: Accidentally confirm submission
   - Verifikasi:
     - Processing submission message
     - 2-minute grace period starts
     - "UNDO SUBMISSION" button appears
     - Countdown timer shown
     - Help text displayed

#### Bagian 2: Grace Period Recovery
3. **Use Undo Option**
   - Aksi: Click "UNDO SUBMISSION" within 2 minutes
   - Verifikasi:
     - Submission cancelled
     - Return to exam immediately
     - All answers intact
     - Timer continues
     - Warning to be careful

4. **If Grace Period Expires**
   - Aksi: Miss the 2-minute window
   - Verifikasi:
     - Submission finalized
     - Contact support option
     - Incident report generated
     - May need admin intervention
     - Clear next steps provided

#### Bagian 3: Support Resolution
5. **Contact Support**
   - Aksi: Use emergency support
   - Verifikasi:
     - Live chat/phone available
     - Incident number provided
     - Quick response time
     - Verification process
     - Possible exam reset

6. **Resolution Options**
   - Verifikasi available options:
     - Immediate resumption (if within policy)
     - Schedule makeup exam
     - Special consideration
     - Incident documentation
     - Fair resolution

### Kriteria Sukses
- [ ] Multiple confirmation steps prevent accidents
- [ ] Grace period allows quick recovery
- [ ] Support process efficient
- [ ] Fair resolution available
- [ ] Student stress minimized

---

## SET-AP-005: Invalid Answer Format

### Informasi Skenario
- **ID Skenario**: SET-AP-005
- **Prioritas**: Sedang
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Tipe Error**: Validation Error

### Prasyarat
- Student entering answers in wrong format
- Numeric answers required but text entered
- File upload size exceeds limit
- Special characters causing issues

### Data Test
```
Format Errors:
Question Type: Numeric calculation
Expected: "42.5"
Entered: "Forty two point five"

File Upload Error:
Maximum Size: 5MB
Attempted: 12MB audio file
Format Required: MP3
Uploaded: WAV file

Special Characters:
Copy-pasted from Word with formatting
Contains non-standard quotes
Hidden characters present
```

### Langkah Pengujian

#### Bagian 1: Numeric Format Error
1. **Enter Text for Numeric Answer**
   - Aksi: Type "Forty two point five" 
   - Verifikasi:
     - Real-time validation warning
     - Red border on input field
     - "Please enter a number" message
     - Example format shown
     - Cannot save invalid format

2. **Correct Format**
   - Aksi: Change to "42.5"
   - Verifikasi:
     - Green checkmark appears
     - Warning disappears
     - Can save answer
     - Auto-advance works

#### Bagian 2: File Upload Issues
3. **Upload Oversized File**
   - Aksi: Try uploading 12MB audio
   - Verifikasi:
     - Upload starts then stops
     - "File too large" error
     - Shows size limit (5MB)
     - Suggests compression
     - Links to help guide

4. **Wrong File Format**
   - Aksi: Upload WAV instead of MP3
   - Verifikasi:
     - Format error message
     - Lists accepted formats
     - Conversion tool suggested
     - Can try again
     - Previous file not lost

#### Bagian 3: Special Character Issues
5. **Paste Formatted Text**
   - Aksi: Paste from Word with formatting
   - Verifikasi:
     - Formatting stripped automatically
     - Plain text retained
     - Warning about conversion
     - Can undo if needed
     - Character count accurate

6. **Handle Validation Errors**
   - Aksi: Fix all format issues
   - Verifikasi:
     - Clear error indicators
     - Helpful error messages
     - Examples provided
     - Can proceed when fixed
     - No data loss during correction

### Kriteria Sukses
- [ ] Validation messages helpful
- [ ] Format requirements clear
- [ ] Auto-correction where possible
- [ ] Examples guide correct input
- [ ] Recovery without data loss

---

## SET-AP-006: Exam Access Outside Window

### Informasi Skenario
- **ID Skenario**: SET-AP-006
- **Prioritas**: Sedang
- **Role**: STUDENT
- **Estimasi Waktu**: 6-8 menit
- **Tipe Error**: Access Control

### Prasyarat
- Student tries to access exam early
- Or tries to access after deadline
- Or from unauthorized location
- System must enforce access rules

### Data Test
```
Access Scenarios:
1. Too Early:
   Exam Opens: Oct 15, 08:00
   Current Time: Oct 15, 07:45
   
2. Too Late:
   Exam Closes: Oct 15, 10:00
   Current Time: Oct 15, 10:15

3. Wrong Location:
   Required: Campus network
   Attempting: Home network

4. Device Not Authorized:
   Required: Registered device
   Using: Public computer
```

### Langkah Pengujian

#### Bagian 1: Early Access Attempt
1. **Try to Start Exam Early**
   - Aksi: Access exam 15 minutes early
   - Verifikasi:
     - Exam visible but locked
     - Countdown timer to start time
     - "Opens in 15 minutes" message
     - Can read instructions
     - Cannot start exam

2. **Wait and Auto-Enable**
   - Aksi: Wait until 08:00
   - Verifikasi:
     - Auto-refresh at start time
     - "Start Exam" enables
     - Notification appears
     - Can now begin

#### Bagian 2: Late Access Attempt
3. **Try After Deadline**
   - Aksi: Access after close time
   - Verifikasi:
     - Exam marked "Closed"
     - Cannot start new attempt
     - Shows submission deadline passed
     - Contact instructor option
     - Request extension link

4. **Request Late Access**
   - Aksi: Submit late access request
   - Verifikasi:
     - Request form available
     - Reason required
     - Evidence upload option
     - Sent to instructor
     - Confirmation received

#### Bagian 3: Location/Device Restrictions
5. **Wrong Network Location**
   - Aksi: Try from home network
   - Verifikasi:
     - Location check fails
     - "Access from campus only" message
     - VPN instructions provided (if allowed)
     - Alternative: Schedule campus visit
     - Support contact shown

6. **Unauthorized Device**
   - Aksi: Use unregistered device
   - Verifikasi:
     - Device verification fails
     - Registration process offered
     - Quick verification available
     - Or must use authorized device
     - Clear instructions provided

### Kriteria Sukses
- [ ] Access controls enforced strictly
- [ ] Clear messaging about restrictions
- [ ] Helpful alternatives provided
- [ ] Support options available
- [ ] Fair exception process

---

## Error Recovery Matrix

| Error Type | Severity | Recovery Time | Data Loss Risk | Support Level |
|------------|----------|---------------|----------------|---------------|
| Connection Lost | High | < 5 minutes | None with auto-save | Automatic |
| Time Expiry | High | Immediate | Partial possible | Automatic |
| Browser Crash | High | < 10 minutes | Minimal | Self-service |
| Accidental Submit | Medium | 2 minutes | None if caught | Quick support |
| Format Errors | Low | Immediate | None | Self-help |
| Access Issues | Medium | Varies | None | Admin required |

## Student Support Protocols

### During Exam Issues
- [ ] Live chat response < 1 minute
- [ ] Phone support available
- [ ] Clear escalation path
- [ ] Incident auto-documentation
- [ ] Fair resolution policies

### Technical Requirements Communication
- [ ] Pre-exam system check mandatory
- [ ] Clear browser requirements
- [ ] Network speed minimums
- [ ] Backup plan communicated
- [ ] Practice environment available

### Stress Reduction Measures
- [ ] Clear error messages (not technical)
- [ ] Always provide next steps
- [ ] Multiple recovery options
- [ ] Assurance about fairness
- [ ] Incident tracking transparent

---

**Testing Notes**:
- All error scenarios must maintain data integrity
- Student confidence is priority in error handling
- Recovery should be as automatic as possible
- Support must be immediately available
- Fair treatment essential for student trust