# Skenario Pengujian: Teacher Availability Submission - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Teacher Availability
- **Modul**: Teacher Availability Management
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 2 skenario utama (Teacher Submission)

---

## TAS-HP-001: Teacher - Complete Availability Submission

### Informasi Skenario
- **ID Skenario**: TAS-HP-001 (Teacher Availability Submission - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Teacher account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Academic term created dengan availability collection active
- Availability submission period open
- Email notification received about submission requirement

### Data Test
```
Teacher Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Academic Term: Semester Genap 2024
Submission Deadline: 7 hari dari sekarang
Collection Status: ACTIVE

Availability Data:
Weekly Schedule Preferences:
- Monday: Pagi (08:00-10:00), Siang (10:00-12:00)
- Tuesday: Pagi Awal (06:00-08:00), Sore (16:00-18:00)
- Wednesday: Pagi (08:00-10:00), Malam (19:00-21:00)
- Thursday: Siang (10:00-12:00), Sore (16:00-18:00)
- Friday: Pagi Awal (06:00-08:00), Pagi (08:00-10:00)
- Saturday: Sore (16:00-18:00), Malam (19:00-21:00)
- Sunday: Available all sessions

Additional Preferences:
- Maximum classes per week: 5
- Preferred levels: Tahsin 1, Tahsin 2
- Special constraints: "Prefer not to teach on Friday evening"
- Minimum commitment: 3 classes per week
```

### Langkah Pengujian

#### Bagian 1: Login dan Access Availability Form
1. **Login sebagai Teacher**
   - Aksi: Login dengan credentials teacher
   - Verifikasi:
     - Login berhasil, redirect ke teacher dashboard
     - Notification badge atau message tentang availability submission
     - Menu link tersedia ke availability form

2. **Access availability submission form**
   - Aksi: Navigate ke `/instructor/availability-submission` atau klik notification
   - Verifikasi:
     - Availability submission form muncul
     - Academic term information displayed (Semester Genap 2024)
     - Submission deadline clearly shown (7 hari remaining)
     - Weekly availability matrix displayed (7 days × 5 sessions)

#### Bagian 2: Weekly Availability Matrix Input
3. **Review availability matrix structure**
   - Verifikasi:
     - 7-day weekly grid (Monday-Sunday)
     - 5 daily time sessions:
       - 🌅 Pagi Awal: 06:00-08:00
       - ☀️ Pagi: 08:00-10:00
       - 🌤️ Siang: 10:00-12:00
       - 🌅 Sore: 16:00-18:00
       - 🌙 Malam: 19:00-21:00
     - Clear time slot labels
     - Interactive checkbox grid

4. **Fill Monday availability**
   - Aksi: 
     - Check "Pagi (08:00-10:00)" untuk Monday
     - Check "Siang (10:00-12:00)" untuk Monday
   - Verifikasi:
     - Checkboxes respond properly
     - Visual feedback on selection
     - Multiple selections allowed per day

5. **Complete remaining weekdays**
   - Aksi: Fill availability according to data test:
     - Tuesday: Pagi Awal, Sore
     - Wednesday: Pagi, Malam
     - Thursday: Siang, Sore
     - Friday: Pagi Awal, Pagi
     - Saturday: Sore, Malam
   - Verifikasi:
     - All selections register correctly
     - Grid updates responsively
     - No conflicts atau errors

6. **Fill Sunday (full availability)**
   - Aksi: Check all 5 time slots untuk Sunday
   - Verifikasi:
     - All checkboxes selectable
     - Full day availability indicated
     - Selection count updates

#### Bagian 3: Additional Preferences
7. **Set maximum classes preference**
   - Aksi: Set "Maximum classes per week" ke 5
   - Verifikasi:
     - Dropdown atau input field responsive
     - Validation range appropriate (likely 1-8)
     - Selection saved properly

8. **Select preferred levels**
   - Aksi: Select "Tahsin 1" dan "Tahsin 2" dari multi-select
   - Verifikasi:
     - Multiple level selection working
     - All program levels available
     - Selections clearly indicated

9. **Add special constraints**
   - Aksi: Isi text area dengan: "Prefer not to teach on Friday evening"
   - Verifikasi:
     - Text area accepts input
     - Character limit reasonable (at least 200 chars)
     - Formatting preserved

10. **Set minimum commitment**
    - Aksi: Set minimum commitment ke 3 classes per week
    - Verifikasi:
      - Input validation working
      - Logical constraint (min ≤ max)
      - Clear field labeling

#### Bagian 4: Form Validation dan Draft Save
11. **Save as draft (optional)**
    - Aksi: Click "Save Draft" button
    - Verifikasi:
      - Draft saved successfully
      - Success message displayed
      - Form remains editable
      - Can return later to continue

12. **Form validation check**
    - Verifikasi:
      - At least some availability slots selected
      - Maximum classes ≥ minimum commitment
      - Required fields completed
      - No obvious conflicts

#### Bagian 5: Final Submission
13. **Review submission summary**
    - Verifikasi:
      - Summary of selected time slots
      - Total availability hours calculated
      - Preferences clearly listed
      - All data accurate

14. **Submit final availability**
    - Aksi: Click "Submit Availability"
    - Verifikasi:
      - Confirmation dialog appears
      - Warning about finality (if applicable)
      - Submit confirmation successful

15. **Verify submission completion**
    - Verifikasi:
      - Success message displayed
      - Submission status: SUBMITTED
      - Timestamp recorded
      - Redirect ke confirmation page

#### Bagian 6: Post-Submission Verification
16. **Access submission confirmation**
    - Verifikasi:
      - Confirmation page displays complete submission
      - All selected time slots listed
      - Preferences accurately shown
      - Submission ID atau reference provided

17. **Verify submission is read-only**
    - Verifikasi:
      - Form now in read-only mode
      - Edit button disabled atau hidden
      - Clear indication of submitted status
      - Contact info untuk changes if needed

### Hasil Diharapkan
- Teacher berhasil login dan akses availability form
- Complete weekly availability matrix filled (35 total slots, ~17 selected)
- Additional preferences set appropriately
- Form validation passes without errors
- Successful submission dengan timestamp
- Read-only confirmation accessible
- Admin staff dapat view submission dalam monitoring system

### Kriteria Sukses
- [ ] Teacher login dan form access successful
- [ ] Weekly matrix display dan interaction working
- [ ] All 7 days × 5 sessions responsive
- [ ] Additional preferences input functional  
- [ ] Draft save capability working
- [ ] Form validation comprehensive
- [ ] Final submission successful
- [ ] Post-submission read-only state
- [ ] Confirmation page complete
- [ ] Data persistence verified

---

## TAS-HP-002: Teacher - Modify Draft Before Deadline

### Informasi Skenario
- **ID Skenario**: TAS-HP-002
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Teacher sudah save draft availability (dari TAS-HP-001 step 11)
- Submission deadline belum terlewat
- Still within modification period

### Data Test
```
Modification Scenario:
Original Draft:
- Monday: Pagi, Siang
- Tuesday: Pagi Awal, Sore
- Max classes: 5

Modified Version:
- Monday: Pagi only (remove Siang)
- Tuesday: Add Malam session
- Wednesday: Add Sore session
- Max classes: 6 (increased)
- Add note: "Can take extra classes if needed"
```

### Langkah Pengujian

#### Bagian 1: Access dan Resume Draft
1. **Return to availability form**
   - Aksi: Login dan navigate ke availability form
   - Verifikasi:
     - Draft data loaded correctly
     - All previous selections preserved
     - Form editable (not read-only)
     - Draft status indicated

#### Bagian 2: Modify Existing Selections
2. **Modify Monday availability**
   - Aksi: Uncheck "Siang (10:00-12:00)" untuk Monday
   - Verifikasi:
     - Checkbox unchecks properly
     - Selection count updates
     - Change tracked for saving

3. **Add Tuesday evening session**
   - Aksi: Check "Malam (19:00-21:00)" untuk Tuesday
   - Verifikasi:
     - Additional selection registered
     - No conflict dengan existing selections
     - Total availability updated

4. **Add Wednesday afternoon**
   - Aksi: Check "Sore (16:00-18:00)" untuk Wednesday
   - Verifikasi:
     - New selection added successfully
     - Pattern flexibility demonstrated
     - Multiple modifications tracked

#### Bagian 3: Update Preferences
5. **Increase maximum classes**
   - Aksi: Change maximum classes dari 5 ke 6
   - Verifikasi:
     - Update accepted dan validated
     - Logical consistency maintained
     - Change reflected immediately

6. **Add additional note**
   - Aksi: Append to special constraints: "Can take extra classes if needed"
   - Verifikasi:
     - Text addition successful
     - Previous text preserved
     - Character limit accommodation

#### Bagian 4: Save Modifications
7. **Update draft**
   - Aksi: Click "Update Draft" atau "Save Changes"
   - Verifikasi:
     - All modifications saved
     - Success confirmation
     - Updated timestamp
     - Form remains editable

8. **Final submission of modified draft**
   - Aksi: Click "Submit Availability"
   - Verifikasi:
     - Modified data submitted
     - Final submission successful
     - All changes included in final version
     - Transition ke read-only state

### Kriteria Sukses
- [ ] Draft modification capability working
- [ ] Previous data loading correctly
- [ ] Individual slot modifications functional
- [ ] Preference updates successful
- [ ] Save modifications working
- [ ] Final submission includes all changes
- [ ] Data consistency maintained

---

## TAS-HP-003: Teacher - Request Schedule Change After Submission

### Informasi Skenario
- **ID Skenario**: TAS-HP-003
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Teacher sudah submit availability (from TAS-HP-001)
- Submission masih dalam periode modifikasi (belum deadline change request)
- Admin staff available untuk approval process
- Change request system active

### Data Test
```
Change Request Scenario:
Original Submission:
- Monday: Pagi (08:00-10:00), Siang (10:00-12:00)
- Tuesday: Pagi Awal (06:00-08:00), Sore (16:00-18:00)
- Max classes: 5
- Submitted 3 days ago

Requested Changes:
- Monday: Remove Siang (personal appointment)
- Wednesday: Add Malam (19:00-21:00) to compensate
- Max classes: Reduce to 4 (workload adjustment)
- Reason: "Medical appointment scheduled every Monday 11:00 AM"

Expected Impact:
- Total availability reduced by 1 slot
- Weekly capacity reduced
- Need admin approval due to capacity reduction
```

### Langkah Pengujian

#### Bagian 1: Access Change Request System
1. **Login dan navigate ke change request**
   - Aksi: Login sebagai teacher (`ustadz.ahmad`)
   - Navigate ke dashboard dan look for availability change option
   - Verifikasi:
     - Dashboard shows current availability status: SUBMITTED
     - Change request option visible dan accessible
     - Clear indication that changes require approval
     - Change request deadline displayed

2. **Access current availability summary**
   - Aksi: Click "Request Availability Change" atau similar link
   - Verifikasi:
     - Current submission displayed as read-only summary
     - All original data accurately shown
     - Submission date dan timestamp visible
     - Change request form accessible

#### Bagian 2: Submit Change Request
3. **Specify changes with reasons**
   - Aksi: Fill change request form:
     - Select Monday Siang slot untuk removal
     - Add Wednesday Malam slot
     - Reduce max classes dari 5 ke 4
     - Enter reason: "Medical appointment scheduled every Monday 11:00 AM"
   - Verifikasi:
     - Change specification interface clear
     - Multiple change types supported (add/remove/modify)
     - Reason field mandatory dengan adequate character limit
     - Impact calculation shown

4. **Review change impact**
   - Verifikasi:
     - System calculates availability impact
     - Shows before/after comparison
     - Highlights capacity reduction
     - Warns about potential class generation impact
     - Confirmation required untuk capacity-reducing changes

5. **Submit change request**
   - Aksi: Click "Submit Change Request"
   - Verifikasi:
     - Confirmation dialog dengan change summary
     - Clear warning about approval requirement
     - Successful submission dengan request ID
     - Email confirmation sent to teacher
     - Admin notification triggered

#### Bagian 3: Track Request Status
6. **Monitor request status**
   - Verifikasi:
     - Request status page accessible
     - Current status: PENDING_APPROVAL
     - Request details viewable
     - Estimated approval timeframe shown
     - Contact information untuk urgent cases

7. **Admin approval simulation**
   - Aksi: (Test helper) Login sebagai admin dan approve request
   - Verifikasi:
     - Admin has access ke pending change requests
     - Request details clearly presented
     - Impact assessment available
     - Approval/rejection options clear

#### Bagian 4: Approved Change Verification
8. **Verify approved changes applied**
   - Aksi: Return to teacher dashboard after approval
   - Verifikasi:
     - Availability status updated: CHANGED_APPROVED
     - New availability matches requested changes
     - Change history maintained
     - Updated availability feeds into class generation
     - Confirmation notification received

### Hasil Diharapkan
- Teacher dapat request changes to submitted availability
- Clear change specification dan impact assessment
- Proper approval workflow dengan admin involvement
- Change history maintained untuk audit
- Updated availability data integrated into planning process

### Kriteria Sukses
- [ ] Change request system accessible post-submission
- [ ] Multiple change types supported
- [ ] Impact calculation accurate
- [ ] Approval workflow functional
- [ ] Change history dan audit trail maintained
- [ ] Integration dengan class generation preserved

---

## TAS-HP-004: Teacher - Emergency Change Request After Deadline

### Informasi Skenario
- **ID Skenario**: TAS-HP-004
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Teacher availability already submitted
- Normal change request deadline passed
- Classes not yet generated (still in planning phase)
- Emergency change request system available

### Data Test
```
Emergency Change Request:
Original Deadline: Passed 2 days ago
Current Phase: Final availability review
Classes: Not yet generated
Emergency Reason: "Sudden family emergency - need to care for sick parent"

Requested Emergency Changes:
- Remove all Friday availability (emergency caregiving)
- Reduce max classes from 6 to 4
- Add note about temporary nature (expected 4-6 weeks)
```

### Langkah Pengujian

#### Bagian 1: Emergency Request Access
1. **Attempt normal change request**
   - Aksi: Try normal change request process
   - Verifikasi:
     - Normal process blocked due to deadline
     - Emergency request option available
     - Clear distinction between normal dan emergency process
     - Emergency criteria explained

2. **Access emergency change request**
   - Aksi: Click "Emergency Change Request"
   - Verifikasi:
     - Emergency form accessible
     - Additional documentation requirements
     - Higher approval threshold indicated
     - Urgency indicators present

#### Bagian 2: Emergency Request Submission
3. **Complete emergency request form**
   - Aksi: Fill emergency request dengan detailed justification
   - Verifikasi:
     - Comprehensive reason field (minimum characters)
     - Impact documentation required
     - Temporary vs. permanent change options
     - Supporting documentation upload option

4. **Submit emergency request**
   - Aksi: Submit dengan full documentation
   - Verifikasi:
     - High-priority notification triggered
     - Multiple academic admin notified
     - Emergency request ID generated
     - SLA commitment displayed
     - Direct contact information provided

#### Bagian 3: Expedited Processing
5. **Verify expedited processing**
   - Verifikasi:
     - Request marked HIGH_PRIORITY
     - Faster approval SLA (e.g., 24 hours vs. 3 days)
     - Multiple approvers notified
     - Emergency escalation process available

### Kriteria Sukses
- [ ] Emergency request system available post-deadline
- [ ] Higher documentation standards enforced
- [ ] Expedited approval process functional
- [ ] Priority handling working
- [ ] Emergency escalation options available

---

## Business Process Integration

### Integration dengan Academic Planning Workflow:
1. **Academic Admin** launches availability collection period
2. **Teachers** receive notifications to submit availability
3. **Teachers** complete availability submission (TAS-HP-001, TAS-HP-002)
4. **Academic Admin** monitors submission progress
5. **System** compiles availability data untuk class generation
6. **Management** uses availability data untuk teacher-level assignments

### Key Benefits:
- **Accurate Scheduling**: Teachers provide actual availability
- **Teacher Satisfaction**: Input into schedule preferences
- **Workload Management**: Self-declared capacity limits
- **Quality Planning**: Level preferences considered
- **Conflict Reduction**: Constraints identified early

### Technical Implementation:
- **Controller**: `TeacherAvailabilityController.submissionForm()`
- **Entity**: `TeacherAvailability` dengan 35 time slot combinations
- **Validation**: Logical consistency checks, deadline enforcement
- **Security**: Teacher can only edit own availability
- **Integration**: Data feeds into `ClassGenerationService`

---

## Catatan untuk Tester

### Implementation Reference
- **URL**: `/instructor/availability-submission`
- **Template**: Teacher availability matrix form
- **Security**: `@PreAuthorize("hasAuthority('INSTRUCTOR')")`
- **Database**: 7 × 5 = 35 time slot records per teacher per term

### Focus Areas
- **UI Responsiveness**: 35-checkbox matrix interaction
- **Data Persistence**: Draft save dan final submission
- **Validation Logic**: Constraint checking dan consistency
- **User Experience**: Clear instructions dan feedback
- **Integration**: Data quality untuk downstream processes

### Edge Cases
- **Late submissions**: Deadline enforcement
- **Concurrent modifications**: Multiple browser sessions
- **Data consistency**: Validation rule comprehensive testing
- **System load**: Multiple teachers submitting simultaneously

### Performance Considerations
- Matrix rendering dengan 35 interactive elements
- Real-time validation feedback
- Auto-save draft functionality (if implemented)
- Responsive design untuk mobile devices