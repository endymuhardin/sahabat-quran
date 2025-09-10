# Skenario Pengujian: Student Exam Taking - Happy Path

## Informasi Umum
- **Kategori**: Ujian Semester - Student Experience
- **Modul**: Online Exam Taking and Submission
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario untuk student exam experience
- **Playwright Test**: `student-exam.StudentExamTakingTest`

---

## SET-HP-001: Student - Complete Midterm Exam Successfully

### Informasi Skenario
- **ID Skenario**: SET-HP-001 (Student Exam Taking - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 15-20 menit (simulated exam time)
- **Playwright Test**: `StudentExamTakingTest.completeMidtermExam()`

### Prasyarat
- Student account: `siswa.ali` / `Welcome@YSQ2024`
- Midterm exam scheduled dan available
- Student enrolled in Tahsin 2
- Exam window open (Oct 15, 08:00-10:00)
- Stable internet connection

### Data Test
```
Student Information:
Name: Ali Rahman
Student ID: STD-2024-001
Level: Tahsin 2
Class: Tahsin 2 - Senin Pagi

Exam Details:
Exam: Ujian Tengah Semester - Tahsin 2
Duration: 90 minutes
Total Questions: 40
- Multiple Choice: 30 questions
- Essay: 5 questions
- Recitation: 5 questions
Passing Score: 70/100
```

### Langkah Pengujian

#### Bagian 1: Pre-Exam Preparation
1. **Login dan Access Exam Portal**
   - Aksi: Login sebagai student
   - Verifikasi:
     - Student dashboard tampil
     - "Available Exams" section visible
     - Midterm exam listed dengan status "Ready"
     - Countdown timer to exam start

2. **Review Exam Information**
   - Aksi: Klik exam card untuk details
   - Verifikasi:
     - Exam instructions displayed
     - Duration clearly shown (90 minutes)
     - Question breakdown visible
     - Technical requirements listed
     - "Start Exam" button available

3. **Perform System Check**
   - Aksi: Klik "System Check"
   - Verifikasi:
     - Browser compatibility: ✓ Passed
     - Internet speed: ✓ Adequate
     - Microphone test: ✓ Working (for recitation)
     - Auto-save test: ✓ Functional
     - All checks passed

#### Bagian 2: Start Exam Session
4. **Begin Exam with Identity Verification**
   - Aksi: Klik "Start Exam"
   - Verifikasi:
     - Identity verification prompt
     - Student ID confirmation
     - Honor code agreement checkbox
     - Photo capture (if required)
     - Final confirmation button

5. **Confirm dan Enter Exam**
   - Aksi: Complete verification dan enter exam
   - Verifikasi:
     - Exam interface loads completely
     - Timer starts (90:00 counting down)
     - Question navigation panel visible
     - First question displayed
     - Auto-save indicator active

#### Bagian 3: Answer Multiple Choice Questions
6. **Navigate MCQ Section**
   - Aksi: Answer first 10 MCQ questions
   - Sample answers:
     - Q1: Select option B
     - Q2: Select option A
     - Q3: Select option C
     - Mark Q4 for review
     - Continue through Q10
   - Verifikasi:
     - Answer selection smooth
     - Navigation between questions works
     - "Mark for Review" functioning
     - Progress saved automatically
     - Question status indicators update

7. **Use Exam Tools**
   - Aksi: Utilize available tools:
     - Calculator (if allowed)
     - Highlight important text
     - Strike-through wrong options
     - Add personal notes
   - Verifikasi:
     - Tools functioning properly
     - Highlights preserved
     - Notes saved with questions

#### Bagian 4: Complete Essay Questions
8. **Answer Essay Questions**
   - Aksi: Navigate to essay section (Q31-35)
   - Sample essay answer:
     ```
     Q31: Jelaskan perbedaan Idgham Bighunnah dan Bilaghunnah
     
     Answer: Idgham Bighunnah adalah peleburan huruf dengan 
     dengung, terjadi ketika nun mati atau tanwin bertemu 
     dengan huruf Ya, Nun, Mim, atau Wau (YANMU). Sedangkan 
     Idgham Bilaghunnah adalah peleburan tanpa dengung, 
     terjadi dengan huruf Lam dan Ra...
     ```
   - Verifikasi:
     - Text editor working properly
     - Character count displayed
     - Formatting tools available
     - Auto-save after each paragraph
     - Can paste text (if allowed)

9. **Save Essay Progress**
   - Aksi: Ensure essays are saved
   - Verifikasi:
     - Manual save button works
     - Last saved timestamp shown
     - Draft recovery available
     - No data loss on navigation

#### Bagian 5: Complete Recitation Questions
10. **Record Quran Recitation**
    - Aksi: Navigate to recitation section (Q36-40)
    - Sample task:
      - Q36: Recite Al-Fatihah with proper tajwid
      - Click "Start Recording"
      - Recite clearly for 60 seconds
      - Click "Stop Recording"
      - Preview recording
      - Submit if satisfied or re-record
    - Verifikasi:
      - Microphone activation successful
      - Recording quality indicator
      - Playback functionality
      - Re-record option available
      - File uploaded successfully

11. **Complete All Recitations**
    - Aksi: Record remaining 4 recitations
    - Verifikasi:
      - Each recording saved
      - Can review all recordings
      - File size within limits
      - Upload progress shown

#### Bagian 6: Review dan Submit
12. **Review All Answers**
    - Aksi: Use question navigator to review
    - Verifikasi:
      - Question summary panel shows:
        * Answered: 38 (green)
        * Marked for review: 2 (yellow)
        * Unanswered: 0 (red)
      - Can jump to any question
      - Can change answers

13. **Answer Remaining Questions**
    - Aksi: Complete the 2 marked questions
    - Verifikasi:
      - All 40 questions now answered
      - No warnings about unanswered
      - Submit button enabled

14. **Final Submission**
    - Aksi: Click "Submit Exam"
    - Verifikasi:
      - Confirmation dialog appears
      - Shows submission summary
      - Final warning about no changes after submit
      - Confirm submission
      - Success message displayed
      - Return to dashboard

### Hasil Diharapkan
- Student completes all 40 questions successfully
- All answer types handled properly (MCQ, essay, recitation)
- Auto-save prevents any data loss
- Submission confirmed dengan timestamp
- Exam locked after submission

### Kriteria Sukses
- [ ] Exam interface user-friendly dan responsive
- [ ] All question types functioning properly
- [ ] Timer accurate dan visible throughout
- [ ] Auto-save reliable (every 30 seconds)
- [ ] Recording feature works smoothly
- [ ] Submission process clear dan confirmed

---

## SET-HP-002: Complete Quick Quiz Successfully

### Informasi Skenario
- **ID Skenario**: SET-HP-002
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Student has class quiz available
- Quiz released by instructor
- 20-minute time limit
- 10 questions total

### Data Test
```
Quiz Details:
Title: Quiz Bab 3 - Makharijul Huruf
Duration: 20 minutes
Questions: 10 (8 MCQ, 2 short answer)
Attempts Allowed: 2
Current Attempt: 1 of 2
Due Date: 3 days from now
```

### Langkah Pengujian

#### Bagian 1: Access Quiz
1. **Find Available Quiz**
   - Aksi: Check class dashboard
   - Verifikasi:
     - Quiz notification visible
     - Due date displayed
     - Attempts remaining shown
     - "Start Quiz" button active

2. **Begin Quiz**
   - Aksi: Click "Start Quiz"
   - Verifikasi:
     - Quiz loads immediately
     - 20:00 timer starts
     - All questions visible (or paginated)
     - Progress bar displayed

#### Bagian 2: Complete Quiz
3. **Answer MCQ Questions**
   - Aksi: Quickly answer 8 MCQ questions
   - Verifikasi:
     - Quick selection response
     - Can change answers
     - Progress updates

4. **Answer Short Answer Questions**
   - Aksi: Type brief answers for 2 questions
   - Verifikasi:
     - Text input smooth
     - Word count shown
     - Auto-save active

#### Bagian 3: Submit Quiz
5. **Submit Before Time Limit**
   - Aksi: Submit with 5 minutes remaining
   - Verifikasi:
     - Early submission allowed
     - Confirmation requested
     - Score displayed immediately (for MCQ)
     - Attempt recorded

6. **Review Results**
   - Aksi: View quiz results
   - Verifikasi:
     - MCQ scores shown
     - Correct answers displayed (if enabled)
     - Short answers pending review
     - Can attempt again (1 attempt left)

### Kriteria Sukses
- [ ] Quiz completion under 20 minutes
- [ ] Immediate scoring for objective questions
- [ ] Multiple attempts tracked properly
- [ ] Results clearly presented

---

## SET-HP-003: Take Practice Exam

### Informasi Skenario
- **ID Skenario**: SET-HP-003
- **Prioritas**: Sedang
- **Role**: STUDENT
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- Practice exam available for students
- No grade impact
- Unlimited attempts
- Immediate feedback enabled

### Data Test
```
Practice Exam:
Purpose: Final exam preparation
Questions: 25 (mixed types)
Time Limit: None (suggested 45 min)
Attempts: Unlimited
Feedback: Immediate with explanations
```

### Langkah Pengujian

#### Bagian 1: Access Practice Mode
1. **Enter Practice Exam**
   - Aksi: Select "Practice Exams" from menu
   - Verifikasi:
     - Practice exams listed
     - "No grade impact" label clear
     - Previous attempt history shown
     - Can start anytime

2. **Begin Practice Session**
   - Aksi: Start practice exam
   - Verifikasi:
     - Relaxed interface (no stress indicators)
     - Timer optional/hideable
     - "Check Answer" available per question
     - Can pause and resume

#### Bagian 2: Use Learning Features
3. **Check Answers Immediately**
   - Aksi: Answer question and check
   - Verifikasi:
     - Instant feedback provided
     - Explanation displayed
     - Can try different answers
     - Learning mode active

4. **Use Study Resources**
   - Aksi: Access help resources
   - Verifikasi:
     - Hints available
     - Reference materials linked
     - Related lessons suggested
     - Can bookmark difficult questions

#### Bagian 3: Complete Practice
5. **Finish Practice Session**
   - Aksi: Complete all questions
   - Verifikasi:
     - Detailed score breakdown
     - Strength/weakness analysis
     - Study recommendations
     - Can retake immediately

### Kriteria Sukses
- [ ] Practice mode clearly differentiated
- [ ] Learning tools helpful
- [ ] Immediate feedback valuable
- [ ] Can repeat without restrictions

---

## SET-HP-004: Resume Interrupted Exam

### Informasi Skenario
- **ID Skenario**: SET-HP-004
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Student previously started exam
- Connection lost after 20 questions
- 45 minutes remaining
- Auto-save captured progress

### Data Test
```
Interruption Scenario:
Original Start: 08:00
Interruption: 08:45 (after 45 min)
Questions Completed: 20 of 40
Time Remaining: 45 minutes
Resume Window: Within 15 minutes
```

### Langkah Pengujian

#### Bagian 1: Handle Disconnection
1. **Experience Connection Loss**
   - Aksi: Simulate connection drop
   - Verifikasi:
     - Auto-save triggered before disconnect
     - Last action preserved
     - Local storage backup created

2. **Reconnect to System**
   - Aksi: Restore connection and login
   - Verifikasi:
     - System detects incomplete exam
     - Resume option prominent
     - Time remaining displayed
     - Warning about resume window

#### Bagian 2: Resume Exam
3. **Click Resume Exam**
   - Aksi: Select "Resume Exam"
   - Verifikasi:
     - Exam loads at last position (Q21)
     - Previous answers intact
     - Timer resumes correctly (45:00)
     - No data loss

4. **Verify Saved Progress**
   - Aksi: Check previous answers
   - Verifikasi:
     - All 20 answers preserved
     - Essay text recovered
     - Recordings still available
     - Review marks maintained

#### Bagian 3: Complete Remaining
5. **Finish Exam**
   - Aksi: Complete remaining 20 questions
   - Verifikasi:
     - Normal functionality restored
     - Can navigate all questions
     - Auto-save reactivated
     - Submit normally

### Kriteria Sukses
- [ ] Interruption handled gracefully
- [ ] No data loss during disconnect
- [ ] Resume process seamless
- [ ] Time calculation fair
- [ ] Student confidence maintained

---

## SET-HP-005: Take Adaptive Exam

### Informasi Skenario
- **ID Skenario**: SET-HP-005
- **Prioritas**: Sedang
- **Role**: STUDENT
- **Estimasi Waktu**: 15-18 menit

### Prasyarat
- Adaptive testing enabled
- Question difficulty adjusts based on performance
- Personalized assessment path
- 30-50 questions depending on performance

### Data Test
```
Adaptive Exam:
Type: Placement reassessment
Starting Difficulty: Medium
Question Pool: 150 questions
Minimum Questions: 30
Maximum Questions: 50
Adaptation Algorithm: Item Response Theory
```

### Langkah Pengujian

#### Bagian 1: Begin Adaptive Test
1. **Start Adaptive Exam**
   - Aksi: Begin placement test
   - Verifikasi:
     - Explanation of adaptive nature
     - No question count shown
     - Progress bar shows percentage only
     - Cannot skip questions

2. **Answer Initial Questions**
   - Aksi: Answer first 5 questions correctly
   - Verifikasi:
     - No indication of right/wrong
     - Next question loads automatically
     - Cannot return to previous
     - Difficulty appears to increase

#### Bagian 2: Experience Adaptation
3. **Notice Difficulty Changes**
   - Aksi: Continue answering with mixed success
   - Verifikasi:
     - Questions adapt to performance
     - Some become easier after mistakes
     - Some become harder after success
     - Smooth difficulty progression

4. **Reach Proficiency Decision**
   - Aksi: Continue until exam ends
   - Verifikasi:
     - Exam ends when proficiency determined
     - May be 30-50 questions
     - Clear completion message
     - No manual submit needed

#### Bagian 3: Review Adaptive Results
5. **View Personalized Results**
   - Aksi: Check results page
   - Verifikasi:
     - Proficiency level determined
     - Strength areas identified
     - Improvement areas highlighted
     - Personalized study plan provided

### Kriteria Sukses
- [ ] Adaptive algorithm working smoothly
- [ ] Difficulty adjustment appropriate
- [ ] Student experience not confusing
- [ ] Results meaningful and actionable
- [ ] Fair assessment achieved

---

## SET-HP-006: Complete Group Exam

### Informasi Skenario
- **ID Skenario**: SET-HP-006
- **Prioritas**: Rendah
- **Role**: STUDENT (Group Member)
- **Estimasi Waktu**: 20-25 menit

### Prasyarat
- Group exam assigned to 3 students
- Collaborative features enabled
- Shared responsibility for answers
- Real-time collaboration required

### Data Test
```
Group Exam:
Type: Collaborative case study
Group Members: 3 students
- Ali Rahman (Leader)
- Fatima Zahra
- Omar Hassan
Duration: 2 hours
Questions: 15 (all collaborative)
Communication: Built-in chat
```

### Langkah Pengujian

#### Bagian 1: Join Group Session
1. **Access Group Exam**
   - Aksi: Enter group exam portal
   - Verifikasi:
     - Group members listed
     - Online status visible
     - Chat panel available
     - Shared workspace ready

2. **Coordinate with Team**
   - Aksi: Use chat to coordinate
   - Verifikasi:
     - Real-time messaging works
     - Can see who's typing
     - File sharing enabled
     - Screen sharing option (if enabled)

#### Bagian 2: Collaborate on Answers
3. **Work on Shared Questions**
   - Aksi: Collaborate on first question
   - Verifikasi:
     - Multiple users can edit
     - Changes tracked by user
     - Conflict resolution works
     - Auto-save for all members

4. **Divide Responsibilities**
   - Aksi: Assign questions to members
   - Verifikasi:
     - Task assignment visible
     - Progress tracking per member
     - Can review others' work
     - Comments/suggestions enabled

#### Bagian 3: Submit as Group
5. **Finalize Group Submission**
   - Aksi: Review and submit together
   - Verifikasi:
     - All members must agree to submit
     - Final review by all required
     - Group confirmation needed
     - Individual contribution tracked

6. **Confirm Submission**
   - Aksi: All members confirm
   - Verifikasi:
     - Submission timestamp recorded
     - Individual participation logged
     - Group score will be shared
     - Peer evaluation option (if enabled)

### Kriteria Sukses
- [ ] Collaboration tools effective
- [ ] Real-time sync working
- [ ] Fair contribution tracking
- [ ] Group submission process clear
- [ ] Communication smooth

---

## Integration dengan Student Experience

### User Experience Metrics

| Aspect | Target | Measurement |
|--------|--------|-------------|
| Page Load Time | < 2 seconds | Performance monitor |
| Auto-save Frequency | Every 30 seconds | Save indicator |
| Question Navigation | < 0.5 seconds | Response time |
| Recording Upload | < 10 seconds | Progress bar |
| Results Display | Immediate (MCQ) | After submission |

## Accessibility Features

### Required Accommodations
- [ ] Screen reader compatibility
- [ ] Keyboard-only navigation
- [ ] High contrast mode option
- [ ] Font size adjustment
- [ ] Extended time settings
- [ ] Break allowances

### Technical Support During Exam
- [ ] Live chat available
- [ ] Technical hotline active
- [ ] FAQ readily accessible
- [ ] Incident reporting simple
- [ ] Recovery procedures clear

## Post-Exam Experience

### Immediate After Submission
- [ ] Confirmation receipt emailed
- [ ] Submission summary provided
- [ ] Next steps communicated
- [ ] Support contacts given
- [ ] Anxiety reduction measures

### Results Communication
- [ ] Clear result timeline
- [ ] Multiple notification channels
- [ ] Detailed score breakdown
- [ ] Improvement suggestions
- [ ] Appeal process explained

---

**Student Experience Notes**:
- User interface must be intuitive and calming
- Clear progress indicators reduce anxiety
- Auto-save critical for confidence
- Practice mode essential for preparation
- Support must be immediately available