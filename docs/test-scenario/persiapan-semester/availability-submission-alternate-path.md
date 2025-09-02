# Skenario Pengujian: Teacher Availability Submission - Alternate Path

## Informasi Umum
- **Kategori**: Validasi dan Error Handling
- **Modul**: Teacher Availability Management
- **Tipe Skenario**: Alternate Path (Jalur Alternatif)
- **Total Skenario**: 8 skenario validasi untuk error handling

---

## TAS-AP-001: Teacher - Akses Tanpa Submission Period Active

### Informasi Skenario
- **ID Skenario**: TAS-AP-001 (Teacher Availability Submission - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 3-4 menit

### Prasyarat
- Teacher account: `ustadz.ahmad`
- No active submission period (collection closed atau not started)
- Database dalam kondisi normal

### Langkah Pengujian

1. **Access availability form when collection closed**
   - Aksi: Navigate ke `/instructor/availability-submission`
   - Verifikasi:
     - Access blocked dengan informative message
     - Clear message: "Availability collection is currently closed"
     - Next collection period information (if available)
     - Contact admin information provided

2. **Attempt direct URL access**
   - Aksi: Try to access availability endpoints directly
   - Verifikasi:
     - Proper access control enforcement
     - Consistent blocking across all related URLs
     - No form exposure when closed

3. **Check dashboard notifications**
   - Verifikasi:
     - No misleading availability notifications
     - Clear status information displayed
     - Appropriate next steps provided

### Kriteria Sukses
- [ ] Access properly blocked when collection inactive
- [ ] Clear communication about status
- [ ] No system errors atau crashes

---

## TAS-AP-002: Teacher - Late Submission After Deadline

### Informasi Skenario
- **ID Skenario**: TAS-AP-002
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Teacher account active
- Submission deadline passed (e.g., 2 days ago)
- Collection period officially closed

### Data Test
```
Late Submission Scenario:
Original Deadline: 2 days ago
Current Status: CLOSED
Teacher Submission Status: NOT_SUBMITTED
Other Teachers: 95% submitted on time
```

### Langkah Pengujian

1. **Attempt submission after deadline**
   - Aksi: Login dan try to access availability form
   - Verifikasi:
     - Form access blocked
     - Clear deadline passed message
     - Specific deadline date/time shown
     - Current date/time untuk reference

2. **Check for late submission options**
   - Verifikasi:
     - Contact information untuk admin staff
     - Emergency submission process (if available)
     - Clear instructions untuk special cases
     - No direct form submission capability

3. **Attempt to save draft after deadline**
   - Aksi: Try any backdoor access attempts
   - Verifikasi:
     - All access points properly secured
     - Consistent deadline enforcement
     - No data corruption or invalid states

### Kriteria Sukses
- [ ] Deadline enforcement strict dan consistent
- [ ] Clear communication about late status
- [ ] Proper escalation process available
- [ ] No security bypasses possible

---

## TAS-AP-003: Teacher - Submit Empty Availability

### Informasi Skenario
- **ID Skenario**: TAS-AP-003
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Teacher logged in
- Availability submission period active
- Form accessible

### Langkah Pengujian

1. **Access form dan leave completely empty**
   - Aksi: Open availability form tanpa select any checkboxes
   - Verifikasi: Form displays properly with no selections

2. **Attempt submission with no availability**
   - Aksi: Click "Submit Availability" dengan no time slots selected
   - Verifikasi:
     - Form validation prevents submission
     - Clear error message: "At least one time slot must be selected"
     - Form remains editable
     - Error highlighting on matrix

3. **Try minimal invalid combinations**
   - Aksi: Set max classes = 0, min commitment > 0
   - Verifikasi:
     - Logical validation error
     - Clear message about invalid combination
     - Proper field highlighting

4. **Empty preferences submission**
   - Aksi: Select some time slots but leave all preferences empty
   - Verifikasi:
     - Identify which preferences are required
     - Appropriate validation messages
     - Partial completion allowed atau blocked

### Kriteria Sukses
- [ ] Empty submission properly blocked
- [ ] Validation messages clear dan helpful
- [ ] Form state maintained after validation failure
- [ ] All required fields identified

---

## TAS-AP-004: Teacher - Conflicting Preferences

### Informasi Skenario
- **ID Skenario**: TAS-AP-004
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 6-7 menit

### Prasyarat
- Teacher logged in
- Form accessible

### Data Test
```
Conflicting Preferences Scenario:
Minimum Classes: 6
Maximum Classes: 4 (INVALID - min > max)
Available Slots: Only 3 time slots selected
Preference: "Want 8 classes" in notes but max = 4
```

### Langkah Pengujian

1. **Set minimum > maximum classes**
   - Aksi: 
     - Set minimum commitment = 6
     - Set maximum classes = 4
   - Verifikasi:
     - Real-time validation error
     - Clear message: "Minimum cannot exceed maximum"
     - Field highlighting for both fields

2. **Insufficient availability untuk minimum**
   - Aksi:
     - Select only 2 time slots
     - Set minimum commitment = 5
   - Verifikasi:
     - Business logic validation
     - Warning about insufficient availability
     - Suggestion to increase slots atau reduce minimum

3. **Conflicting text preferences**
   - Aksi: Enter contradictory information in special constraints
   - Verifikasi:
     - System handling of free-text conflicts
     - No blocking (as it's free text)
     - But warning atau flag for admin review

### Kriteria Sukses
- [ ] Numerical constraint validation working
- [ ] Real-time feedback on conflicts
- [ ] Business logic validation comprehensive
- [ ] Helpful guidance untuk resolution

---

## TAS-AP-005: Teacher - Excessive Availability Claims

### Informasi Skenario
- **ID Skenario**: TAS-AP-005
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Teacher logged in
- Form accessible

### Data Test
```
Excessive Claims Scenario:
Time Slots: All 35 slots selected (7 days × 5 sessions)
Maximum Classes: 15 (extreme high)
Special Notes: "Available 24/7 for teaching"
```

### Langkah Pengujian

1. **Select all available time slots**
   - Aksi: Check all 35 time slots across all days
   - Verifikasi:
     - System accepts all selections
     - Total availability calculation correct
     - No performance issues dengan bulk selection

2. **Set unrealistic maximum classes**
   - Aksi: Set maximum classes ke 15 (very high)
   - Verifikasi:
     - System validation untuk reasonable limits
     - Warning about workload sustainability
     - Suggestion atau soft limit recommendations

3. **Verify sustainability warnings**
   - Verifikasi:
     - System flagging untuk human review
     - Workload balance considerations
     - Teacher welfare notifications

### Kriteria Sukses
- [ ] System handles extreme cases gracefully
- [ ] Workload sustainability considerations
- [ ] Proper flagging untuk admin review
- [ ] No system performance degradation

---

## TAS-AP-006: Teacher - Form Session Timeout

### Informasi Skenario
- **ID Skenario**: TAS-AP-006
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Teacher logged in
- Form partially completed
- Session timeout configured (e.g., 30 minutes)

### Langkah Pengujian

1. **Simulate session timeout**
   - Aksi: 
     - Fill half the form
     - Wait untuk session timeout (atau simulate dengan session clearing)
     - Attempt to continue filling form

2. **Attempt submission after timeout**
   - Aksi: Complete form dan try to submit
   - Verifikasi:
     - Session timeout detection
     - Appropriate redirect ke login
     - Form data preservation (if implemented)
     - Clear timeout message

3. **Recovery process**
   - Aksi: Re-login dan return to form
   - Verifikasi:
     - Draft data recovery (if implemented)
     - Clear instructions about data loss
     - Fresh form state if no recovery

### Kriteria Sukses
- [ ] Session timeout properly detected
- [ ] Graceful handling of expired sessions
- [ ] Data preservation atau clear communication about loss
- [ ] Smooth recovery process

---

## TAS-AP-007: Teacher - Multiple Concurrent Sessions

### Informasi Skenario
- **ID Skenario**: TAS-AP-007
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Teacher account
- Multiple browser sessions atau devices

### Langkah Pengujian

1. **Open form in multiple browsers**
   - Aksi: Login dan open availability form in Chrome dan Firefox
   - Verifikasi:
     - Both sessions load current data
     - Concurrent access handling
     - Data synchronization considerations

2. **Make different changes in each session**
   - Aksi:
     - Browser 1: Select Monday morning slots
     - Browser 2: Select Tuesday evening slots
   - Verifikasi: Both changes tracked appropriately

3. **Attempt submission from both sessions**
   - Aksi: Submit from one browser, then attempt from second
   - Verifikasi:
     - Optimistic locking atau conflict detection
     - Clear conflict resolution
     - Data consistency maintained
     - User notification about conflicts

### Kriteria Sukses
- [ ] Concurrent session handling graceful
- [ ] Conflict detection dan resolution working
- [ ] Data consistency maintained
- [ ] Clear user communication about conflicts

---

## TAS-AP-008: Teacher - Invalid Time Slot Manipulation

### Informasi Skenario
- **ID Skenario**: TAS-AP-008
- **Prioritas**: Tinggi (Security)
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Teacher logged in
- Developer tools access (untuk testing)

### Langkah Pengujian

1. **Manipulate form data via browser tools**
   - Aksi: Use browser developer tools to:
     - Add invalid time slots
     - Modify checkbox values to non-existent sessions
     - Change hidden form fields

2. **Submit manipulated data**
   - Aksi: Submit form dengan tampered data
   - Verifikasi:
     - Server-side validation catches manipulation
     - Invalid data rejected
     - Security logging activated
     - No system compromise

3. **Attempt SQL injection via text fields**
   - Aksi: Enter SQL-like strings in special constraints field
   - Verifikasi:
     - Proper input sanitization
     - No database errors
     - Safe data storage
     - No system vulnerability

### Kriteria Sukses
- [ ] Server-side validation comprehensive
- [ ] Client-side manipulation detected
- [ ] Security measures effective
- [ ] No data corruption atau system compromise

---

## System Integration Testing

### Database Consistency
- [ ] Availability data properly normalized
- [ ] Foreign key constraints enforced
- [ ] Data integrity maintained across submissions
- [ ] Proper transaction handling

### Notification System Integration
- [ ] Submission confirmation emails sent
- [ ] Admin notification untuk completed submissions
- [ ] Reminder system integration
- [ ] Error notification handling

### Academic Planning Integration
- [ ] Availability data accessible untuk class generation
- [ ] Data format compatibility
- [ ] Real-time availability status updates
- [ ] Integration dengan teacher workload calculations

## Performance Testing

### Form Responsiveness
- [ ] 35-checkbox matrix rendering performance
- [ ] Real-time validation response time
- [ ] Auto-save functionality (if implemented)
- [ ] Mobile device compatibility

### Database Performance
- [ ] Concurrent submission handling
- [ ] Large dataset query performance
- [ ] Index optimization untuk availability queries
- [ ] Backup dan recovery performance

### System Load Testing
- [ ] Multiple teachers submitting simultaneously
- [ ] Peak usage period handling
- [ ] Resource utilization monitoring
- [ ] Scalability considerations

This comprehensive alternate path testing ensures robust validation of the teacher availability submission system under various error conditions, security threats, dan edge cases.