# Skenario Pengujian: Manajemen Registrasi - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Operasional
- **Modul**: Manajemen Registrasi
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 3 skenario utama (Academic Admin, Management, Teacher)

---

## MR-HP-001: Academic Admin - Assign Teacher to Review Registration

### Informasi Skenario
- **ID Skenario**: MR-HP-001 (Manajemen Registrasi - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Sudah ada registrasi siswa dengan status "Submitted" (dari PS-HP-001)
- Academic admin account tersedia: `academic.admin1` / `Welcome@YSQ2024`
- Minimal satu teacher account aktif

### Data Test
```
Academic Admin Login:
Username: academic.admin1
Password: Welcome@YSQ2024

Student Data (yang akan di-assign):
Nama: Ahmad Zaki Mubarak
Email: ahmad.zaki@email.com
Status Awal: Submitted

Teacher Assignment:
Assigned to: ustadz.ahmad
Assignment Notes: Please review this registration and evaluate the placement test
```

### Langkah Pengujian

#### Bagian 1: Login sebagai Academic Admin
1. **Akses halaman login**
   - Aksi: Buka `/login`
   - Verifikasi: Form login muncul

2. **Login sebagai academic admin**
   - Aksi: Isi username "academic.admin1" dan password "Welcome@YSQ2024"
   - Aksi: Klik "Login"
   - Verifikasi:
     - Login berhasil
     - Menu academic admin tersedia
     - Dapat akses registration management

#### Bagian 2: View Submitted Registrations
3. **Navigate ke registrations management**
   - Aksi: Akses `/registrations` atau klik menu "Kelola Registrasi"
   - Verifikasi:
     - List registrasi muncul
     - Filter status tersedia
     - Registrasi dengan status "Submitted" terlihat

4. **Filter registrasi by status**
   - Aksi: Filter dengan status "Submitted"
   - Verifikasi:
     - Hanya registrasi dengan status "Submitted" yang muncul
     - Action button "Assign Teacher" tersedia

#### Bagian 3: Assign Teacher
5. **Akses detail registrasi**
   - Aksi: Klik nama siswa atau "Detail"
   - Verifikasi:
     - Detail registrasi lengkap ditampilkan
     - Informasi personal, pendidikan, dan placement test terlihat
     - Button "Assign Teacher" tersedia

6. **Assign teacher untuk review**
   - Aksi: Klik "Assign Teacher"
   - Verifikasi:
     - Form assignment muncul
     - Dropdown list teacher tersedia

7. **Pilih teacher dan submit**
   - Aksi: 
     - Pilih "ustadz.ahmad" dari dropdown
     - Isi notes: "Please review this registration and evaluate the placement test"
     - Klik "Assign"
   - Verifikasi:
     - Assignment berhasil
     - Status berubah menjadi "Assigned"
     - Teacher name ditampilkan sebagai assigned reviewer
     - Timestamp assignment tercatat

### Hasil Diharapkan
- Admin staff berhasil login dan akses registration management
- Dapat melihat dan filter registrasi by status
- Berhasil assign teacher untuk review registrasi
- Status berubah dari "Submitted" ke "Assigned"
- Audit trail assignment tercatat

### Kriteria Sukses
- [ ] Login sebagai academic admin berhasil
- [ ] List registrasi dapat difilter by status
- [ ] Detail registrasi ditampilkan lengkap
- [ ] Teacher assignment form berfungsi
- [ ] Status update setelah assignment
- [ ] Assignment history tercatat

---

## MR-HP-002: Management - Monitor and Assign Registrations

### Informasi Skenario
- **ID Skenario**: MR-HP-002
- **Prioritas**: Sedang
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Management account: `management.director` / `Welcome@YSQ2024`
- Multiple registrasi dengan berbagai status

### Data Test
```
Management Login:
Username: management.director
Password: Welcome@YSQ2024
```

### Langkah Pengujian

1. **Login sebagai Management**
   - Aksi: Login dengan credentials management
   - Verifikasi: Akses ke dashboard dan reports

2. **View registration reports**
   - Aksi: Akses `/registrations/reports`
   - Verifikasi:
     - Summary statistics ditampilkan
     - Breakdown by status terlihat
     - List pending assignments

3. **Monitor teacher workload**
   - Verifikasi:
     - Dapat melihat berapa registrasi per teacher
     - Status progress tiap teacher

4. **Assign registration ke teacher**
   - Aksi: Pilih unassigned registration
   - Aksi: Assign ke teacher dengan workload rendah
   - Verifikasi: Assignment berhasil

### Kriteria Sukses
- [ ] Management dapat view reports
- [ ] Statistics dan metrics akurat
- [ ] Dapat monitor teacher workload
- [ ] Dapat melakukan assignment

---

## MR-HP-003: Teacher - Review Registration and Evaluate

### Informasi Skenario
- **ID Skenario**: MR-HP-003
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Teacher account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Registrasi sudah di-assign ke teacher ini (dari MR-HP-001)

### Data Test
```
Teacher Login:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Evaluation Data:
Teacher Remarks: "Bacaan cukup baik dengan tajwid yang benar pada sebagian besar ayat. 
                 Masih perlu perbaikan pada mad dan ghunnah. 
                 Rekomendasikan untuk masuk Tahsin Level 2."
Recommended Level: Tahsin 2
Placement Result: Level 2
```

### Langkah Pengujian

#### Bagian 1: Login dan View Assigned Registrations
1. **Login sebagai Teacher**
   - Aksi: Login dengan credentials teacher
   - Verifikasi:
     - Login berhasil
     - Dashboard teacher muncul
     - Notifikasi assignment terlihat

2. **View assigned registrations**
   - Aksi: Akses `/registrations/assigned` atau klik "My Assignments"
   - Verifikasi:
     - List registrasi yang di-assign muncul
     - Status "Assigned" terlihat
     - Detail button tersedia

#### Bagian 2: Review Registration Detail
3. **Akses detail registrasi**
   - Aksi: Klik "Review" pada registrasi Ahmad Zaki
   - Verifikasi:
     - Semua informasi siswa ditampilkan
     - Placement test verse terlihat
     - Link rekaman dapat diakses
     - Form evaluasi tersedia

4. **Review rekaman bacaan**
   - Aksi: Klik link Google Drive rekaman
   - Verifikasi:
     - Rekaman dapat diputar
     - Ayat yang dibaca sesuai dengan yang ditampilkan

#### Bagian 3: Input Evaluation
5. **Isi form evaluasi**
   - Aksi: 
     - Status review: "In Review"
     - Klik "Save Draft" (optional)
   - Verifikasi: Draft tersimpan

6. **Complete evaluation**
   - Aksi:
     - Isi Teacher Remarks dengan evaluasi detail
     - Pilih Recommended Level: "Tahsin 2"
     - Set Placement Result: "Level 2"
     - Status: "Completed"
   - Verifikasi: Semua field terisi

7. **Submit evaluation**
   - Aksi: Klik "Submit Evaluation"
   - Verifikasi:
     - Evaluation berhasil tersimpan
     - Status registrasi: "Reviewed"
     - Teacher review status: "Completed"
     - Timestamp evaluasi tercatat
     - Remarks dan level recommendation tersimpan

#### Bagian 4: Verify Completion
8. **Check registration status**
   - Verifikasi:
     - Registration status: "Reviewed"
     - Teacher evaluation complete
     - Recommended level terlihat
     - Student dapat proceed ke enrollment

### Hasil Diharapkan
- Teacher berhasil login dan melihat assigned registrations
- Dapat review detail registrasi dan placement test
- Berhasil input evaluation dengan remarks dan level recommendation
- Status update menjadi "Reviewed" setelah evaluation
- Complete audit trail dari submission → assignment → review

### Kriteria Sukses
- [ ] Teacher dapat melihat assigned registrations
- [ ] Detail registrasi dan placement test accessible
- [ ] Form evaluasi berfungsi dengan baik
- [ ] Dapat save draft evaluation
- [ ] Submit final evaluation berhasil
- [ ] Status updates sesuai workflow
- [ ] Audit trail lengkap

---

## Workflow Summary

### Complete Registration Flow:
1. **Student** → Submit registration (Status: SUBMITTED)
2. **Academic Admin/Management** → Assign teacher (Status: ASSIGNED)
3. **Teacher** → Review & evaluate (Status: REVIEWED)
4. **System** → Ready for enrollment (Status: COMPLETED)

### Role Permissions:
- **System Admin**: No operational permissions (system maintenance only)
- **Academic Admin**: View, Edit, Assign Teacher
- **Management**: View, Assign Teacher, Reports
- **Teacher**: View assigned, Review, Evaluate
- **Student**: Create registration only

### Status Transitions:
```
DRAFT → SUBMITTED → ASSIGNED → REVIEWED → COMPLETED
                 ↘           ↗
                  REJECTED
```

---

## Catatan untuk Tester

### Implementasi Teknis
- **Controller**: Menggunakan `RegistrationController` unified dengan role-based access control
- **URL Base**: `/registrations` untuk staff/management, `/registrations/assigned` untuk teacher
- **Authentication**: Spring Security dengan `@PreAuthorize` annotations
- **Permission**: `STUDENT_REG_VIEW`, `STUDENT_REG_REVIEW`, `STUDENT_REG_ASSIGN_TEACHER`

### Focus Areas
- **Role Separation**: Pastikan setiap role hanya bisa akses sesuai permission
- **Workflow Integrity**: Status harus berubah sesuai urutan
- **Teacher Assignment**: Hanya assigned teacher yang bisa review
- **Audit Trail**: Semua action harus tercatat dengan timestamp dan user

### Edge Cases
- Teacher mencoba review registration yang tidak di-assign
- Multiple assignment ke same teacher
- Admin staff mencoba evaluate (seharusnya tidak bisa)
- System admin mencoba akses operational features

### Performance Testing
- Load test dengan multiple registrations
- Concurrent teacher evaluations
- Report generation dengan large dataset