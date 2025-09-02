# Skenario Pengujian: Evaluasi Tes Penempatan - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Evaluasi Tes Penempatan
- **Modul**: Teacher Evaluation System
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 2 skenario utama (Teacher Evaluation)

**Catatan**: Dalam business process yang baru, evaluasi tes penempatan dilakukan oleh **Teacher**, bukan Admin.

---

## TP-HP-001: Teacher - Complete Placement Test Evaluation

### Informasi Skenario
- **ID Skenario**: TP-HP-001 (Tes Penempatan - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Teacher account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Registrasi sudah di-assign ke teacher ini (status: ASSIGNED)
- Student sudah upload rekaman placement test
- Database memiliki placement test verses yang sudah di-assign

### Data Test
```
Teacher Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Student Data (assigned untuk evaluasi):
Nama: Ahmad Test Placement
Email: ahmad.placement@email.com
Program: Tahsin 1
Status: ASSIGNED to ustadz.ahmad
Recording Link: https://drive.google.com/file/d/placement-test-sample/view

Placement Test Assignment:
Surah: Al-Fatiha
Ayat: 1-7
Arabic Text: بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ...
Difficulty Level: 1 (Basic)

Teacher Evaluation Data:
Placement Result: Level 3
Teacher Remarks: "Bacaan cukup baik dengan tajwid yang benar pada sebagian besar ayat. 
                 Perlu perbaikan pada mad dan qalqalah. 
                 Recommended untuk masuk Tahsin Level 2."
Recommended Level: Tahsin 2
```

### Langkah Pengujian

#### Bagian 1: Login dan Dashboard Teacher
1. **Login sebagai teacher**
   - Aksi: Login dengan credentials teacher
   - Verifikasi:
     - Login berhasil, redirect ke teacher dashboard
     - Dashboard menampilkan assignment summary
     - Notification atau badge untuk pending reviews

2. **View assigned registrations**
   - Aksi: Akses `/registrations/assigned` atau klik "My Assignments"
   - Verifikasi:
     - List registrasi yang di-assign muncul
     - Status assignment jelas (PENDING, IN_REVIEW, COMPLETED)
     - Student information ditampilkan
     - Action buttons tersedia sesuai status

#### Bagian 2: Access Registration for Review
3. **Select registration for evaluation**
   - Aksi: Klik "Review" pada registrasi Ahmad Test Placement
   - Verifikasi:
     - Redirect ke `/registrations/assigned/{id}/review`
     - Form review terbuka
     - Student information displayed lengkap

4. **Review student registration details**
   - Verifikasi:
     - Personal information: nama, kontak, background
     - Educational info: pendidikan, pengalaman
     - Program selection dan learning goals
     - All data displayed clearly dan readable

#### Bagian 3: Placement Test Information
5. **Review assigned placement test verse**
   - Verifikasi:
     - Surah name dan number ditampilkan (Al-Fatiha, Surah 1)
     - Ayat range jelas (Ayat 1-7)
     - Arabic text ter-render dengan benar (RTL direction)
     - Transliteration mudah dibaca
     - Difficulty level indicator
     - UI design yang clear dan professional

6. **Access student recording**
   - Aksi: Klik link rekaman Google Drive
   - Verifikasi:
     - Link dapat diklik dan terbuka di tab baru
     - Drive link accessible (atau mock indicator)
     - Return ke evaluation form after review

#### Bagian 4: Teacher Evaluation Process
7. **Start evaluation process**
   - Aksi: Set teacher review status ke "IN_REVIEW"
   - Aksi: Klik "Save Draft" (optional)
   - Verifikasi:
     - Status berubah ke IN_REVIEW
     - Draft tersimpan
     - Form dapat di-continue nanti

8. **Complete placement test evaluation**
   - Aksi: Isi form evaluation:
     - Teacher Review Status: "COMPLETED"
     - Placement Test Result: "3" (Level 3)
     - Teacher Remarks: Detailed evaluation text
     - Recommended Level: "Tahsin 2"
   - Verifikasi:
     - Semua field dapat diisi tanpa error
     - Dropdown options available dan relevant
     - Text areas support sufficient characters

#### Bagian 5: Submit Evaluation
9. **Submit final evaluation**
   - Aksi: Klik "Submit Evaluation"
   - Verifikasi:
     - Form validation passes
     - Success message muncul
     - Redirect ke registration detail

10. **Verify evaluation completion**
    - Verifikasi:
      - Registration status: "REVIEWED"
      - Teacher review status: "COMPLETED"
      - Placement test result: "Level 3"
      - Teacher remarks visible
      - Recommended level: "Tahsin 2"
      - Evaluation timestamp recorded
      - Teacher name recorded sebagai evaluator

#### Bagian 6: Post-Evaluation Verification
11. **Check assignment list update**
    - Aksi: Return ke `/registrations/assigned`
    - Verifikasi:
      - Evaluated registration moved to "Completed" section
      - Status badge updated
      - No longer editable
      - Available for reference

12. **Verify workflow completion**
    - Verifikasi:
      - Student can proceed to enrollment process
      - Admin/Staff dapat lihat completed evaluation
      - Data integrity maintained
      - Audit trail complete

### Hasil Diharapkan
- Teacher berhasil login dan akses assigned registrations
- Dapat review student details dan placement test information
- Berhasil evaluate recording dan input assessment
- Submit evaluation dengan complete remarks dan level recommendation
- Status updated properly: ASSIGNED → REVIEWED
- System ready untuk student enrollment dengan proper level placement

### Kriteria Sukses
- [ ] Teacher dapat login dan akses dashboard
- [ ] List assigned registrations accurate
- [ ] Student details displayed completely
- [ ] Placement test info clear dan accessible
- [ ] Recording link functional
- [ ] Evaluation form validates properly
- [ ] Draft save functionality works
- [ ] Final submission successful
- [ ] Status transitions correct
- [ ] Data persistence verified
- [ ] Audit trail complete

---

## TP-HP-002: Teacher - Bulk Evaluation Multiple Students

### Informasi Skenario
- **ID Skenario**: TP-HP-002
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Multiple registrations assigned to same teacher
- All students have uploaded recordings
- Teacher account active

### Data Test
```
Assigned Registrations:
1. Student A - Al-Fatiha (Level 1) → Evaluate to Level 2
2. Student B - Al-Baqarah 1-5 (Level 2) → Evaluate to Level 3
3. Student C - Al-Imran 1-10 (Level 3) → Evaluate to Level 4

Teacher: ustadz.ahmad
```

### Langkah Pengujian

1. **View multiple assignments**
   - Aksi: Login dan akses teacher dashboard
   - Verifikasi:
     - Multiple pending assignments visible
     - Can prioritize by due date atau difficulty
     - Workload balance visible

2. **Sequential evaluation process**
   - Aksi: Evaluate each registration one by one
   - Verifikasi:
     - Each evaluation independent
     - Progress tracking accurate
     - No data interference between evaluations

3. **Consistency in evaluation standards**
   - Verifikasi:
     - Similar criteria applied across evaluations
     - Consistent level recommendations
     - Teacher remarks quality maintained

4. **Batch completion verification**
   - Verifikasi:
     - All evaluations completed successfully
     - Status updates accurate untuk all
     - Teacher workload updated
     - Ready untuk enrollment processing

### Kriteria Sukses
- [ ] Multiple assignments handled efficiently
- [ ] Consistent evaluation standards
- [ ] Progress tracking accurate
- [ ] Batch completion successful

---

## Business Process Notes

### Key Changes from Old Process:
1. **WHO**: Teachers evaluate placement tests (not Admin)
2. **WHEN**: After staff assignment (not direct admin action)
3. **WHERE**: `/registrations/assigned/{id}/review` (not admin pages)
4. **WHY**: Academic expertise and proper role separation

### Workflow Integration:
```
Student submits → Staff assigns teacher → Teacher evaluates → Status: REVIEWED
```

### Teacher Expertise Benefits:
- Qualified academic assessment
- Consistent evaluation standards
- Better placement accuracy
- Proper tajweed evaluation
- Educational recommendations

### Security & Access Control:
- Teachers only see assigned registrations via `/registrations/assigned`
- Cannot access other teacher's assignments (enforced by `RegistrationController`)
- Evaluation tied to teacher credentials with `Authentication` parameter
- Audit trail maintains teacher accountability with user tracking

### Technical Implementation:
- **Controller Method**: `showTeacherReviewForm()` dan `submitTeacherReview()`
- **Security**: `@PreAuthorize("hasAuthority('STUDENT_REG_REVIEW')")`
- **Entity Fields**: `assignedTeacherId`, `teacherReviewStatus`, `teacherRemarks`
- **Validation**: Teacher ID must match assigned teacher, registration must be ASSIGNED status

This reflects the corrected business process where teachers, not system administrators, perform placement test evaluations using their academic expertise.