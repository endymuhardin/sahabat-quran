# Skenario Pengujian: Admin Registrasi - Alternate Path

## Informasi Umum
- **Kategori**: Validasi dan Error Handling Admin
- **Modul**: Manajemen Registrasi Admin
- **Tipe Skenario**: Alternate Path (Jalur Alternatif)
- **Automated Test**: `AdminRegistrationValidationTest.java`
- **Total Skenario**: 6 skenario validasi

---

## AR-AP-001: Akses Tanpa Otentikasi

### Informasi Skenario
- **ID Skenario**: AR-AP-001 (Admin Registrasi - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldPreventUnauthorizedAccessToAdminRegistrationPage()`
- **Estimasi Waktu**: 2-3 menit

### Prasyarat
- Browser dalam kondisi logout (tidak ada session admin)
- Database dalam kondisi normal

### Langkah Pengujian

1. **Akses halaman admin tanpa login**
   - Aksi: Langsung akses URL `/admin/registrations`
   - Verifikasi:
     - Automatic redirect ke halaman login (`/login`)
     - Tidak dapat akses halaman admin registrations
     - URL berubah ke login page

2. **Verifikasi akses denied message**
   - Verifikasi: 
     - Halaman login muncul dengan normal
     - Tidak ada error 500 atau crash
     - Security mechanism berfungsi dengan baik

### Hasil Diharapkan
- Sistem secara otomatis redirect user ke halaman login
- Tidak ada akses unauthorized ke admin functions
- Security policy ter-enforce dengan baik

### Kriteria Sukses
- [ ] Direct admin URL access ditolak tanpa authentication
- [ ] Redirect ke login page berhasil
- [ ] Tidak ada error server atau application crash
- [ ] Security mechanism responsive

---

## AR-AP-002: Akses dengan User Non-Admin

### Informasi Skenario
- **ID Skenario**: AR-AP-002
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldPreventNonAdminUsersFromAccessingAdminRegistrationPage()`
- **Estimasi Waktu**: 3-4 menit

### Prasyarat
- User account tersedia: `user` / `UserYSQ@2024`
- User account tidak memiliki admin privileges

### Data Test
```
Non-Admin User:
Username: user
Password: UserYSQ@2024
Role: Student/Regular User (bukan admin)
```

### Langkah Pengujian

1. **Login sebagai regular user**
   - Aksi: Login dengan credentials non-admin
   - Verifikasi: Login berhasil, redirect ke dashboard user

2. **Coba akses admin page**
   - Aksi: Langsung akses `/admin/registrations` via URL
   - Verifikasi:
     - Access denied (403 Forbidden)
     - Atau redirect ke unauthorized page
     - Pesan error yang jelas: "Access Denied" atau "Forbidden"

3. **Verifikasi menu admin tidak muncul**
   - Verifikasi:
     - Menu "Admin" atau "Kelola Registrasi" tidak muncul di navigasi
     - User interface sesuai dengan role yang dimiliki
     - Tidak ada akses ke admin functions

### Kriteria Sukses
- [ ] Regular user tidak dapat akses admin pages
- [ ] Role-based access control berfungsi dengan benar
- [ ] Error message informatif dan sesuai
- [ ] UI menghilangkan admin menu untuk non-admin users

---

## AR-AP-003: Form Review Kosong

### Informasi Skenario
- **ID Skenario**: AR-AP-003
- **Prioritas**: Sedang
- **Selenium Method**: `shouldValidateReviewFormRequiredFields()`
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Admin sudah login
- Ada registrasi dengan status "Submitted" untuk di-review

### Langkah Pengujian

1. **Akses form review registrasi**
   - Aksi: Navigate ke form review registrasi yang submitted
   - Verifikasi: Form review terbuka dengan field kosong

2. **Submit form review kosong**
   - Aksi: Langsung klik "Submit Review" tanpa mengisi apapun
   - Verifikasi:
     - Form tidak ter-submit
     - Validation error muncul
     - Field required di-highlight

3. **Verifikasi error messages**
   - Verifikasi:
     - "Status keputusan wajib dipilih" untuk dropdown status
     - "Review notes wajib diisi" untuk field catatan
     - "Alasan keputusan wajib diisi" untuk field reason

### Kriteria Sukses
- [ ] Form review tidak bisa disubmit kosong
- [ ] Validation messages jelas dan informatif
- [ ] Required fields di-highlight dengan proper styling
- [ ] User experience smooth dengan error handling

---

## AR-AP-004: Evaluasi Placement Test Tanpa Data

### Informasi Skenario
- **ID Skenario**: AR-AP-004
- **Prioritas**: Sedang
- **Selenium Method**: `shouldPreventPlacementTestEvaluationWithoutRequiredFields()`
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Admin sudah login
- Ada registrasi yang sudah approved dan siap untuk placement test evaluation

### Langkah Pengujian

1. **Akses form placement test evaluation**
   - Aksi: Navigate ke form evaluasi tes penempatan
   - Verifikasi: Form evaluasi terbuka dengan field kosong

2. **Submit evaluasi kosong**
   - Aksi: Klik "Submit Evaluation" tanpa mengisi field required
   - Verifikasi:
     - Form tidak ter-submit
     - Validation error muncul untuk field yang kosong

3. **Verifikasi required field validation**
   - Verifikasi:
     - "Hasil tes wajib dipilih" untuk dropdown level
     - "Catatan evaluasi wajib diisi" untuk evaluation notes
     - "Alasan evaluasi wajib diisi" untuk evaluation reason

### Kriteria Sukses
- [ ] Placement test evaluation form memiliki proper validation
- [ ] Empty submission dicegah dengan error messages
- [ ] Field validation konsisten dengan business requirements

---

## AR-AP-005: Evaluasi Registrasi Tanpa Recording

### Informasi Skenario
- **ID Skenario**: AR-AP-005
- **Prioritas**: Sedang
- **Selenium Method**: `shouldPreventEvaluationOfPlacementTestWithoutRecordingLink()`
- **Estimasi Waktu**: 6-7 menit

### Prasyarat
- Registrasi siswa tanpa link rekaman placement test
- Admin sudah login

### Data Test
```
Student Registration (tanpa recording):
Nama: No Recording Student
Email: no.recording@example.com
Program: Tahsin 1
Recording Link: (kosong/tidak diisi)
```

### Langkah Pengujian

1. **Buat registrasi tanpa recording link**
   - Aksi: Gunakan existing registration atau buat baru tanpa link rekaman
   - Verifikasi: Registrasi tersimpan tapi field recording kosong

2. **Review dan approve registrasi**
   - Aksi: Admin review dan approve registrasi tersebut
   - Verifikasi: Status berubah menjadi approved

3. **Coba akses placement test evaluation**
   - Aksi: Navigate ke placement test evaluation untuk registrasi ini
   - Verifikasi:
     - Error message muncul: "Link rekaman belum tersedia"
     - Atau form evaluation tidak dapat diakses
     - Instruksi untuk siswa melengkapi recording link

4. **Verifikasi business rule enforcement**
   - Verifikasi:
     - System mencegah evaluasi tanpa recording
     - Error message memberikan guidance yang jelas
     - Admin diberitahu next action yang diperlukan

### Kriteria Sukses
- [ ] System mencegah evaluasi placement test tanpa recording
- [ ] Error message informatif dan actionable
- [ ] Business rule enforcement konsisten
- [ ] Admin mendapat guidance untuk next steps

---

## AR-AP-006: Double Review Prevention

### Informasi Skenario
- **ID Skenario**: AR-AP-006
- **Prioritas**: Sedang
- **Selenium Method**: `shouldPreventDuplicateReviewSubmission()`
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Registrasi yang sudah pernah di-review dan approved
- Admin sudah login

### Langkah Pengujian

1. **Review dan approve registrasi**
   - Aksi: Review dan approve sebuah registrasi (completed workflow)
   - Verifikasi: Status menjadi "Approved" dan review completed

2. **Coba akses review form lagi**
   - Aksi: Coba akses URL review untuk registrasi yang sama
   - Verifikasi:
     - Redirect ke detail page registrasi
     - Atau message "Registrasi sudah direview"
     - Form review tidak dapat diakses lagi

3. **Verifikasi workflow state protection**
   - Verifikasi:
     - System mendeteksi registrasi sudah di-review
     - Prevent double review attempts
     - UI menunjukkan status current dengan jelas

### Kriteria Sukses
- [ ] Double review dicegah oleh system
- [ ] Workflow state protection berfungsi
- [ ] User mendapat feedback yang jelas tentang status current
- [ ] No data corruption dari multiple review attempts

---

## AR-AP-007: Akses Registrasi yang Belum Siap

### Informasi Skenario
- **ID Skenario**: AR-AP-007
- **Prioritas**: Rendah
- **Estimasi Waktu**: 3-4 menit

### Prasyarat
- Registrasi dengan status "Draft" (belum disubmit untuk review)
- Admin sudah login

### Langkah Pengujian

1. **Coba review registrasi Draft**
   - Aksi: Coba akses review form untuk registrasi yang masih Draft
   - Verifikasi:
     - Error message: "Registrasi belum disubmit untuk review"
     - Review form tidak dapat diakses
     - Redirect ke list registrations

2. **Coba evaluasi placement test registrasi yang belum approved**
   - Aksi: Coba akses placement test evaluation untuk registrasi Draft/Submitted
   - Verifikasi:
     - Error: "Registrasi harus disetujui terlebih dahulu"
     - Access denied ke placement test evaluation

### Kriteria Sukses
- [ ] Business workflow rules di-enforce dengan ketat
- [ ] Status-based access control berfungsi
- [ ] Error messages menjelaskan prerequisite yang diperlukan

---

## Referensi Automated Test

### Lokasi File
`src/test/java/com/sahabatquran/webapp/functional/AdminRegistrationValidationTest.java`

### Method Mapping
- **AR-AP-001**: `shouldPreventUnauthorizedAccessToAdminRegistrationPage()`
- **AR-AP-002**: `shouldPreventNonAdminUsersFromAccessingAdminRegistrationPage()`
- **AR-AP-003**: `shouldValidateReviewFormRequiredFields()`
- **AR-AP-004**: `shouldPreventPlacementTestEvaluationWithoutRequiredFields()`
- **AR-AP-005**: `shouldPreventEvaluationOfPlacementTestWithoutRecordingLink()`
- **AR-AP-006**: `shouldPreventDuplicateReviewSubmission()`

### Eksekusi Automated Test
```bash
# Jalankan admin validation tests
./mvnw test -Dtest="AdminRegistrationValidationTest"

# Dengan debugging
./mvnw test -Dtest="AdminRegistrationValidationTest" -Dselenium.debug.vnc.enabled=true
```

### Catatan untuk Tester

#### Security Focus Areas
- **Authentication**: Pastikan login requirement di-enforce
- **Authorization**: Role-based access control harus strict
- **Session Management**: Test dengan multiple admin sessions
- **URL Manipulation**: Test direct URL access attempts

#### Admin UX Focus
- **Error Messages**: Apakah error messages membantu admin understand what to do
- **Workflow State**: Apakah status registrasi jelas di setiap step
- **Form Validation**: Konsistensi validation across admin forms
- **Performance**: Loading time untuk admin operations

#### Edge Cases
- **Concurrent Access**: Multiple admin accessing same registration
- **Session Timeout**: Admin session expiry during review process
- **Browser Navigation**: Back/forward button behavior
- **Data Consistency**: State changes reflected immediately across UI

#### Business Rule Testing
- **Workflow Sequence**: Ensure proper order: Submit → Review → Evaluate
- **Status Transitions**: Only allowed status changes permitted
- **Data Integrity**: No orphaned or inconsistent states
- **Audit Trail**: All admin actions properly logged