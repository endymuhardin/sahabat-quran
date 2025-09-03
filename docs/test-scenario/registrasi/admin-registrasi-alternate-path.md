# Skenario Pengujian: Manajemen Registrasi - Alternate Path

## Informasi Umum
- **Kategori**: Validasi dan Error Handling
- **Modul**: Manajemen Registrasi (Staff, Management, Teacher)
- **Tipe Skenario**: Alternate Path (Jalur Alternatif)
- **Total Skenario**: 8 skenario validasi untuk 3 roles

---

## MR-AP-001: Staff - Akses Tanpa Otentikasi

### Informasi Skenario
- **ID Skenario**: MR-AP-001 (Manajemen Registrasi - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: Unauthenticated User
- **Estimasi Waktu**: 2-3 menit

### Prasyarat
- Browser dalam kondisi logout (tidak ada session)
- Database dalam kondisi normal

### Langkah Pengujian

1. **Akses halaman staff tanpa login**
   - Aksi: Langsung akses URL `/registrations`
   - Verifikasi:
     - Automatic redirect ke halaman login (`/login`)
     - Tidak dapat akses halaman staff registrations
     - URL berubah ke login page

2. **Coba akses teacher assignments**
   - Aksi: Langsung akses `/registrations/assigned`
   - Verifikasi:
     - Redirect ke login page
     - Security mechanism berfungsi

### Kriteria Sukses
- [ ] Direct URL access ditolak tanpa authentication
- [ ] Redirect ke login page berhasil
- [ ] Tidak ada error server atau application crash

---

## MR-AP-002: Role-Based Access Violation

### Informasi Skenario
- **ID Skenario**: MR-AP-002
- **Prioritas**: Tinggi
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Multiple user accounts dengan different roles

### Data Test
```
Student User: siswa.ali / Welcome@YSQ2024
Staff User: staff.admin1 / Welcome@YSQ2024  
Teacher User: ustadz.ahmad / Welcome@YSQ2024
```

### Langkah Pengujian

1. **Student coba akses staff functions**
   - Aksi: Login sebagai student, akses `/registrations`
   - Verifikasi: 403 Forbidden atau access denied

2. **Teacher coba akses staff assignment**
   - Aksi: Login sebagai teacher, akses `/registrations/{id}/assign`
   - Verifikasi: Access denied, dapat't assign teachers

3. **Staff coba evaluate langsung**
   - Aksi: Login sebagai staff, coba akses teacher evaluation
   - Verifikasi: Hanya bisa assign, tidak bisa evaluate

### Kriteria Sukses
- [ ] Role-based access control berfungsi ketat
- [ ] Setiap role hanya akses sesuai permission
- [ ] Error message informatif

---

## MR-AP-003: Staff - Teacher Assignment Kosong

### Informasi Skenario
- **ID Skenario**: MR-AP-003
- **Prioritas**: Sedang
- **Role**: ADMIN_STAFF
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Staff sudah login
- Ada registrasi dengan status "Submitted"

### Langkah Pengujian

1. **Akses form assign teacher**
   - Aksi: Navigate ke form assignment untuk registrasi submitted
   - Verifikasi: Form assignment terbuka

2. **Submit assignment kosong**
   - Aksi: Klik "Assign" tanpa pilih teacher
   - Verifikasi:
     - Form tidak ter-submit
     - Validation error: "Teacher wajib dipilih"
     - Dropdown teacher di-highlight

3. **Submit dengan teacher yang sudah overloaded**
   - Aksi: Pilih teacher dengan terlalu banyak assignment
   - Verifikasi: 
     - Warning message atau prevention
     - Saran teacher alternatif

### Kriteria Sukses
- [ ] Form assignment memiliki proper validation
- [ ] Teacher workload consideration
- [ ] Error messages jelas dan actionable

---

## MR-AP-004: Teacher - Review Registration Bukan Assignment

### Informasi Skenario
- **ID Skenario**: MR-AP-004
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Teacher account: `ustadz.ahmad`
- Registrasi yang di-assign ke teacher lain

### Data Test
```
Teacher Login: ustadz.ahmad
Registration assigned to: ustadzah.fatimah
```

### Langkah Pengujian

1. **Login sebagai Teacher A**
   - Aksi: Login sebagai ustadz.ahmad
   - Verifikasi: Dashboard teacher muncul

2. **Coba akses registrasi assigned ke Teacher B**
   - Aksi: Direct URL ke `/registrations/assigned/{id}/review` (assigned to other teacher)
   - Verifikasi:
     - Access denied
     - Error: "Anda tidak memiliki akses untuk mereview registrasi ini"
     - Redirect ke teacher dashboard

3. **Verifikasi list hanya menampilkan assigned**
   - Verifikasi:
     - Teacher list hanya menampilkan registrasi yang di-assign ke mereka
     - Tidak ada registrasi dari teacher lain

### Kriteria Sukses
- [ ] Teacher hanya bisa access assigned registrations
- [ ] Security enforcement ketat
- [ ] List filtering benar

---

## MR-AP-005: Teacher - Submit Review Kosong

### Informasi Skenario
- **ID Skenario**: MR-AP-005
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Teacher sudah login
- Ada registrasi assigned untuk review

### Langkah Pengujian

1. **Akses form review**
   - Aksi: Navigate ke review form registrasi assigned
   - Verifikasi: Form review terbuka dengan field kosong

2. **Submit evaluation kosong**
   - Aksi: Set status "Completed" dan submit tanpa isi remarks
   - Verifikasi:
     - Form tidak ter-submit
     - Validation error: "Teacher remarks wajib diisi"
     - Minimum character validation

3. **Submit tanpa recommended level**
   - Aksi: Isi remarks tapi tidak pilih recommended level
   - Verifikasi:
     - Validation error: "Recommended level wajib dipilih"
     - Business rule enforced

### Kriteria Sukses
- [ ] Teacher evaluation form memiliki proper validation
- [ ] Required fields enforced
- [ ] Business rules validated

---

## MR-AP-006: Teacher - Review Registrasi Sudah Completed

### Informasi Skenario
- **ID Skenario**: MR-AP-006
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Registrasi yang sudah completed evaluation
- Teacher sudah login

### Langkah Pengujian

1. **Complete evaluation terlebih dahulu**
   - Aksi: Complete evaluation untuk satu registrasi
   - Verifikasi: Status menjadi "Reviewed", teacher_review_status: "Completed"

2. **Coba akses review form lagi**
   - Aksi: Coba akses URL review untuk registrasi yang sama
   - Verifikasi:
     - Redirect ke detail page
     - Message: "Review untuk registrasi ini sudah selesai"
     - Form review tidak dapat diakses

3. **Verifikasi workflow protection**
   - Verifikasi:
     - Status tidak dapat diubah kembali
     - Double evaluation dicegah
     - Audit trail preserved

### Kriteria Sukses
- [ ] Double evaluation dicegah
- [ ] Workflow state protection berfungsi
- [ ] Data integrity maintained

---

## MR-AP-007: Management - Assign Teacher yang Tidak Aktif

### Informasi Skenario
- **ID Skenario**: MR-AP-007
- **Prioritas**: Sedang
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Management account sudah login
- Ada teacher account yang inactive/disabled

### Langkah Pengujian

1. **Login sebagai Management**
   - Aksi: Login dengan management.director
   - Verifikasi: Dashboard management muncul

2. **Coba assign ke inactive teacher**
   - Aksi: Pilih teacher yang inactive dari dropdown
   - Verifikasi:
     - Teacher inactive tidak muncul di dropdown
     - Atau warning jika dipilih
     - Validation error

3. **Verifikasi only active teachers available**
   - Verifikasi:
     - Dropdown hanya menampilkan active teachers
     - Status teacher jelas terlihat
     - Business rule enforced

### Kriteria Sukses
- [ ] Hanya active teachers dapat di-assign
- [ ] Teacher status validation
- [ ] UI filtering benar

---

## MR-AP-008: Workflow Status Violation

### Informasi Skenario
- **ID Skenario**: MR-AP-008
- **Prioritas**: Tinggi
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Multiple registrasi dengan berbagai status
- Staff dan teacher sudah login

### Data Test
```
Registration Status Test Cases:
1. DRAFT → Should not be assignable
2. SUBMITTED → Can be assigned  
3. ASSIGNED → Cannot be reassigned
4. REVIEWED → Cannot be assigned again
5. COMPLETED → Read only
```

### Langkah Pengujian

1. **Coba assign registrasi DRAFT**
   - Aksi: Staff coba assign registrasi yang masih draft
   - Verifikasi: 
     - Error: "Registrasi belum disubmit"
     - Assignment tidak dapat dilakukan

2. **Coba reassign registrasi ASSIGNED**
   - Aksi: Staff coba assign ulang registrasi yang sudah assigned
   - Verifikasi:
     - Warning atau prevention
     - Option untuk "Reassign" dengan confirmation

3. **Coba review registrasi SUBMITTED (belum assigned)**
   - Aksi: Teacher coba review registrasi yang belum di-assign
   - Verifikasi:
     - Access denied
     - Error: "Registrasi belum di-assign"

### Kriteria Sukses
- [ ] Status-based workflow rules enforced
- [ ] Proper status transition validation
- [ ] Business logic integrity maintained

---

## MTR-AP-001: Multi-Term Registration Validation

### Informasi Skenario
- **ID Skenario**: MTR-AP-001 (Multi-Term Registration - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Admin account dengan registration management access
- Multiple academic terms available (ACTIVE, PLANNING, COMPLETED)
- Student dengan existing registration dalam different terms
- Cross-term business rules configured

### Data Test
```
Multi-Term Registration Scenarios:
Student: Ali Rahman (siswa.ali@example.com)
Existing History:
- Semester 1 2023/2024: COMPLETED (Level: Tahsin 2)
- Semester 2 2023/2024: COMPLETED (Level: Tahfidz 1)
- Semester 1 2024/2025: ACTIVE (Currently enrolled: Tahfidz 2)

New Registration Attempts:
1. Duplicate active registration dalam same term
2. Registration untuk COMPLETED term (historical)
3. Level regression request (Tahfidz 2 → Tahsin 1)
4. Multiple PLANNING term registrations simultaneously
```

### Langkah Pengujian

#### Bagian 1: Duplicate Registration Prevention
1. **Coba register student yang sudah enrolled dalam ACTIVE term**
   - Aksi: Create new registration untuk student dengan active enrollment
   - Verifikasi:
     - System detects existing ACTIVE registration
     - Clear error message: "Student sudah terdaftar dalam semester aktif"
     - Current enrollment details displayed
     - Option untuk update current registration instead
     - No duplicate registration created

2. **Multiple PLANNING term registration attempts**
   - Aksi: Register student untuk multiple PLANNING terms simultaneously
   - Verifikasi:
     - Business rule validation blocks multiple future registrations
     - Clear messaging about registration limits
     - Priority registration system explained
     - Alternative approach suggested (waitlist, etc.)

#### Bagian 2: Historical Term Access Prevention
3. **Registration untuk COMPLETED term**
   - Aksi: Try to create registration untuk closed/completed semester
   - Verifikasi:
     - Access to COMPLETED terms blocked
     - Clear message: "Registrasi tidak dapat dibuat untuk semester selesai"
     - Historical data viewing only available
     - Alternative current term registration suggested

4. **Invalid level progression attempts**
   - Aksi: Register student untuk level regression (higher to lower level)
   - Verifikasi:
     - Level progression validation working
     - Warning message about level regression
     - Academic advisor consultation required
     - Special approval process outlined
     - Student academic history displayed

#### Bagian 3: Cross-Term Data Validation
5. **Inconsistent academic progression**
   - Aksi: Register student dengan level tidak consistent dengan history
   - Verifikasi:
     - Academic progression rules enforced
     - Historical performance data referenced
     - Placement test requirement triggered if needed
     - Academic advisor notification sent
     - Override process available dengan justification

### Kriteria Sukses
- [ ] Duplicate registrations properly prevented
- [ ] Cross-term business rules enforced
- [ ] Historical data integrity maintained
- [ ] Academic progression validation working
- [ ] Clear error messaging dan alternative solutions

---

## MTR-AP-002: Cross-Term Academic History Inconsistencies  

### Informasi Skenario
- **ID Skenario**: MTR-AP-002
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Teacher account: `ustadz.ahmad`
- Student registration dengan inconsistent academic history
- Placement test results conflicting dengan previous assessments
- Cross-term data integrity issues

### Data Test
```
Academic History Inconsistency:
Student: Fatima Zahra
Historical Records:
- Semester 1 2023: Tahsin 2 (Grade: A-, Teacher: Ustadz Ahmad)
- Semester 2 2023: Missing records (data corruption/migration issue)
- Semester 1 2024: Registered untuk Tahsin 1 (level regression?)

Current Registration:
- New placement test: Suggests Tahfidz 1 level
- Historical performance: Indicates Tahsin 2+ capability  
- Missing semester: No progression data available
- Teacher evaluation needed: Conflicting information
```

### Langkah Pengujian

#### Bagian 1: Inconsistent Level Assessment
1. **Review placement test dengan conflicting history**
   - Aksi: Access placement test evaluation dengan inconsistent background
   - Verifikasi:
     - Historical performance data displayed untuk context
     - Placement test results clearly shown
     - Conflict indicators highlighted
     - Academic advisor consultation suggested
     - Additional assessment options available

2. **Handle missing historical data**
   - Aksi: Evaluate student dengan missing semester records
   - Verifikasi:
     - Missing data clearly flagged
     - Available historical data maximized
     - Conservative level recommendation suggested
     - Additional verification process available
     - Data completeness impact explained

#### Bagian 2: Cross-Term Performance Correlation
3. **Validate performance consistency**
   - Aksi: Compare current assessment dengan historical performance
   - Verifikasi:
     - Performance trend analysis available
     - Inconsistencies highlighted untuk review
     - Multiple assessment correlation shown
     - Teacher observations weighted appropriately
     - Academic intervention triggers working

### Kriteria Sukses
- [ ] Historical data inconsistencies properly flagged
- [ ] Teacher guidance provided untuk conflict resolution
- [ ] Conservative academic placement ensured
- [ ] Additional verification processes available
- [ ] Student academic welfare prioritized

---

## Security Testing Focus

### Authentication & Authorization
- [ ] Unauthenticated access prevention
- [ ] Role-based access control strict
- [ ] Session management proper
- [ ] URL manipulation protection

### Data Security
- [ ] Teachers only see assigned registrations
- [ ] Staff cannot evaluate directly
- [ ] Management reporting accurate
- [ ] Audit trail complete

### Business Logic
- [ ] Status transitions valid only
- [ ] Workflow sequence enforced
- [ ] Assignment logic consistent
- [ ] Evaluation requirements met

### Error Handling
- [ ] Graceful error messages
- [ ] No sensitive data exposure
- [ ] Proper redirect behavior
- [ ] User guidance clear

## Edge Cases Testing

### Concurrent Operations
- [ ] Multiple staff assigning same registration
- [ ] Teacher evaluation during reassignment
- [ ] Status changes during active sessions

### System Boundaries
- [ ] Maximum teacher workload limits
- [ ] Registration assignment queues
- [ ] Bulk operation handling
- [ ] Performance under load

This updated document reflects the new business process where System Administrator has no operational permissions, and proper role separation is enforced throughout the workflow.