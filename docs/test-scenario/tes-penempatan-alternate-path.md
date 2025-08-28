# Skenario Pengujian: Tes Penempatan - Alternate Path

## Informasi Umum
- **Kategori**: Validasi dan Error Handling Tes Penempatan
- **Modul**: Manajemen Tes Penempatan
- **Tipe Skenario**: Alternate Path (Jalur Alternatif)
- **Automated Test**: `PlacementTestValidationTest.java`
- **Total Skenario**: 6 skenario validasi

---

## TP-AP-001: Form Evaluasi Kosong

### Informasi Skenario
- **ID Skenario**: TP-AP-001 (Tes Penempatan - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldPreventPlacementTestEvaluationWithoutRequiredFields()`
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Registrasi dengan status "Approved" dan placement test assigned
- Admin sudah login dan dapat akses evaluation form

### Langkah Pengujian

1. **Akses form evaluasi placement test**
   - Aksi: Navigate ke form evaluasi tes penempatan
   - Verifikasi: Form terbuka dengan semua field kosong

2. **Submit form tanpa mengisi apapun**
   - Aksi: Langsung klik "Submit Evaluation" tanpa input
   - Verifikasi:
     - Form tidak ter-submit ke server
     - Validation errors muncul
     - Tetap berada di halaman evaluasi

3. **Verifikasi pesan validasi**
   - Verifikasi:
     - "Hasil tes wajib dipilih" untuk dropdown placement result
     - "Catatan evaluasi wajib diisi" untuk evaluation notes
     - "Alasan evaluasi wajib diisi" untuk evaluation reason
     - Field yang error di-highlight dengan styling yang jelas

4. **Test partial form submission**
   - Aksi: Isi hanya dropdown result, kosongkan notes dan reason
   - Aksi: Coba submit
   - Verifikasi: Validation tetap muncul untuk field yang masih kosong

### Hasil Diharapkan
- Form validation mencegah submission tanpa data required
- Error messages jelas dan membantu admin understand requirement
- Field yang error ter-highlight dengan visual feedback
- Form state preserved saat validation error

### Kriteria Sukses
- [ ] Empty form submission dicegah oleh validation
- [ ] Error messages informatif untuk setiap required field
- [ ] Visual feedback jelas untuk field yang error
- [ ] Partial submission juga divalidasi dengan benar

---

## TP-AP-002: Evaluasi Tanpa Link Rekaman

### Informasi Skenario
- **ID Skenario**: TP-AP-002
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldPreventEvaluationOfPlacementTestWithoutRecordingLink()`
- **Estimasi Waktu**: 6-7 menit

### Prasyarat
- Registrasi yang approved tapi tidak memiliki recording link
- Admin sudah login

### Data Test
```
Student Registration (tanpa recording):
Nama: No Recording Student
Email: no.recording@example.com
Program: Tahsin 1
Recording Link: (null/empty)
Status: Approved
```

### Langkah Pengujian

#### Bagian 1: Setup Registrasi Tanpa Recording
1. **Pastikan registrasi tanpa recording exists**
   - Prasyarat: Registrasi approved tapi field recording link kosong
   - Verifikasi: Status "Approved" tapi placement test incomplete

2. **Admin akses evaluasi**
   - Aksi: Admin coba akses form evaluasi untuk registrasi ini
   - Verifikasi: System mendeteksi missing recording

#### Bagian 2: Business Rule Enforcement
3. **Verifikasi error handling untuk missing recording**
   - Verifikasi:
     - Error message: "Link rekaman belum tersedia untuk evaluasi"
     - Atau "Siswa harus melengkapi rekaman terlebih dahulu"
     - Form evaluasi tidak dapat diakses/disabled

4. **Verifikasi guidance untuk next steps**
   - Verifikasi:
     - Instruksi jelas untuk admin: "Minta siswa untuk mengupload rekaman"
     - Link atau instruksi untuk contact siswa
     - Status placement test menunjukkan "Waiting for Recording"

#### Bagian 3: Alternative Workflow
5. **Test workflow jika siswa kemudian upload recording**
   - Aksi: Siswa update registrasi dengan recording link valid
   - Verifikasi: Placement test menjadi available untuk evaluasi admin

### Kriteria Sukses
- [ ] System enforce business rule: no evaluation without recording
- [ ] Error message memberikan context dan guidance yang jelas
- [ ] Workflow dapat dilanjutkan setelah recording ditambahkan
- [ ] Status placement test akurat reflect current state

---

## TP-AP-003: Akses Tanpa Otentikasi

### Informasi Skenario
- **ID Skenario**: TP-AP-003
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldPreventUnauthorizedAccessToPlacementTestEvaluation()`
- **Estimasi Waktu**: 2-3 menit

### Prasyarat
- Browser dalam kondisi logout (no admin session)
- Registrasi dengan placement test tersedia

### Langkah Pengujian

1. **Logout dari admin session**
   - Aksi: Pastikan tidak ada session admin yang aktif
   - Verifikasi: Browser dalam kondisi unauthenticated

2. **Coba akses URL placement test evaluation**
   - Aksi: Langsung akses `/admin/registrations/{id}/placement-test`
   - Verifikasi:
     - Automatic redirect ke halaman login
     - URL berubah ke `/login`
     - Tidak dapat akses evaluation form

3. **Verifikasi security enforcement**
   - Verifikasi:
     - Authentication required untuk access admin functions
     - No data leakage atau unauthorized access
     - Redirect behavior smooth dan expected

### Kriteria Sukses
- [ ] Unauthenticated access dicegah secara otomatis
- [ ] Redirect ke login page berfungsi dengan benar
- [ ] Security policy di-enforce consistently

---

## TP-AP-004: Validasi Range Level Hasil

### Informasi Skenario
- **ID Skenario**: TP-AP-004
- **Prioritas**: Sedang
- **Selenium Method**: `shouldValidatePlacementTestResultRange()`
- **Estimasi Waktu**: 3-4 menit

### Prasyarat
- Admin sudah login
- Form evaluasi placement test accessible

### Langkah Pengujian

1. **Verifikasi dropdown options untuk level**
   - Verifikasi:
     - Dropdown "Hasil Tes" menampilkan level 1-6
     - Setiap level memiliki description yang jelas
     - No invalid options tersedia (seperti level 0 atau 7+)

2. **Test valid level selection**
   - Aksi: Pilih level valid (1-6) dan isi field lainnya
   - Aksi: Submit evaluation
   - Verifikasi: Submission berhasil dan level tersimpan

3. **Verifikasi business logic untuk level**
   - Verifikasi:
     - Level 1-6 represent progression tingkat kemampuan
     - Level descriptions sesuai dengan Islamic education context
     - UI menunjukkan meaning dari setiap level

### Kriteria Sukses
- [ ] Dropdown level berisi options yang valid dan appropriate
- [ ] Level selection berfungsi dengan benar
- [ ] Business logic untuk level evaluation sound dan consistent

---

## TP-AP-005: Double Evaluation Prevention

### Informasi Skenario
- **ID Skenario**: TP-AP-005
- **Prioritas**: Sedang
- **Selenium Method**: `shouldPreventDoubleEvaluationOfSamePlacementTest()`
- **Estimasi Waktu**: 6-7 menit

### Prasyarat
- Placement test yang sudah pernah dievaluasi dan completed
- Admin sudah login

### Data Test
```
Evaluated Placement Test:
Student: Already Evaluated Student
Previous Evaluation:
- Level: 2
- Notes: Already evaluated test
- Status: Evaluated
```

### Langkah Pengujian

1. **Complete placement test evaluation**
   - Aksi: Evaluasi placement test hingga completion (status = "Evaluated")
   - Verifikasi: Status berubah menjadi "Evaluated" dengan level tersimpan

2. **Coba akses evaluation form lagi**
   - Aksi: Coba akses URL evaluation untuk placement test yang sama
   - Verifikasi:
     - Redirect ke detail registrasi
     - Error message: "Tes penempatan sudah dievaluasi"
     - Form evaluation tidak dapat diakses

3. **Verifikasi UI menunjukkan completed state**
   - Verifikasi:
     - Button "Evaluasi" tidak muncul atau disabled
     - Status "Evaluated" jelas terlihat
     - Previous evaluation results ditampilkan
     - No action buttons untuk re-evaluation

4. **Verifikasi data protection**
   - Verifikasi:
     - Previous evaluation data tidak dapat diubah
     - Audit trail maintained untuk original evaluation
     - No data corruption dari double evaluation attempts

### Kriteria Sukses
- [ ] Double evaluation dicegah oleh system
- [ ] UI clearly indicate completed evaluation state
- [ ] Previous evaluation data protected dari modification
- [ ] Error handling informative dan user-friendly

---

## TP-AP-006: Workflow Status Validation

### Informasi Skenario
- **ID Skenario**: TP-AP-006
- **Prioritas**: Sedang
- **Selenium Method**: `shouldValidateStudentRegistrationStatusBeforePlacementTest()`
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Registrasi dengan status selain "Approved" (misal: Draft, Submitted, Rejected)
- Admin sudah login

### Data Test
```
Registration States to Test:
1. Draft Status - belum submit untuk review
2. Submitted Status - submitted tapi belum approved
3. Rejected Status - sudah di-review tapi ditolak
```

### Langkah Pengujian

1. **Test evaluation access untuk Draft registration**
   - Aksi: Coba akses placement test evaluation untuk registrasi Draft
   - Verifikasi:
     - Error: "Registrasi harus disubmit dan disetujui terlebih dahulu"
     - No access ke evaluation form

2. **Test evaluation access untuk Submitted registration**
   - Aksi: Coba akses evaluation untuk registrasi yang Submitted (belum approved)
   - Verifikasi:
     - Error: "Registrasi harus disetujui terlebih dahulu"
     - Workflow sequence di-enforce

3. **Test evaluation access untuk Rejected registration**
   - Aksi: Coba akses evaluation untuk registrasi yang Rejected
   - Verifikasi:
     - Error: "Registrasi ditolak, tidak dapat melanjutkan ke tes penempatan"
     - Logical workflow prevents evaluation pada rejected registration

4. **Verifikasi workflow guidance**
   - Verifikasi:
     - Error messages menjelaskan required status dan next steps
     - UI menunjukkan current status dengan jelas
     - Admin diberi guidance tentang workflow yang benar

### Kriteria Sukses
- [ ] Workflow status validation strict dan consistent
- [ ] Error messages menjelaskan prerequisite untuk evaluation
- [ ] Business rules di-enforce across different registration states
- [ ] UI guidance membantu admin understand proper workflow

---

## Referensi Automated Test

### Lokasi File
`src/test/java/com/sahabatquran/webapp/functional/PlacementTestValidationTest.java`

### Method Mapping
- **TP-AP-001**: `shouldPreventPlacementTestEvaluationWithoutRequiredFields()`
- **TP-AP-002**: `shouldPreventEvaluationOfPlacementTestWithoutRecordingLink()`
- **TP-AP-003**: `shouldPreventUnauthorizedAccessToPlacementTestEvaluation()`
- **TP-AP-004**: `shouldValidatePlacementTestResultRange()`
- **TP-AP-005**: `shouldPreventDoubleEvaluationOfSamePlacementTest()`
- **TP-AP-006**: `shouldValidateStudentRegistrationStatusBeforePlacementTest()`

### Eksekusi Automated Test
```bash
# Jalankan placement test validation tests
./mvnw test -Dtest="PlacementTestValidationTest"

# Dengan debugging untuk error scenarios
./mvnw test -Dtest="PlacementTestValidationTest" -Dselenium.debug.vnc.enabled=true

# Test specific validation
./mvnw test -Dtest="PlacementTestValidationTest#shouldPreventDoubleEvaluationOfSamePlacementTest"
```

### Catatan untuk Tester

#### Validation Focus Areas
- **Form Validation**: Consistency dan clarity dari validation messages
- **Business Rules**: Enforcement dari workflow requirements
- **Security**: Authentication dan authorization untuk admin functions
- **Data Integrity**: Protection dari invalid states dan data corruption
- **User Experience**: Error handling yang helpful dan informative

#### Islamic Education Context
- **Level System**: Pastikan level 1-6 sesuai dengan Islamic education progression
- **Quranic Content**: Handling untuk Arabic text dan Islamic terminology
- **Assessment Logic**: Level assignment logic sesuai dengan tahsin/tahfidz standards
- **Cultural Sensitivity**: UI dan messages appropriate untuk Islamic educational context

#### Edge Cases untuk Testing
- **Concurrent Evaluations**: Multiple admin evaluate same test simultaneously
- **Network Issues**: Form submission dengan connection problems
- **Session Timeout**: Admin session expire during evaluation process
- **Data Corruption**: Handle edge cases yang dapat cause data inconsistency
- **Arabic Text Issues**: Handle rendering problems dengan Quranic verses

#### Performance Considerations
- **Loading Times**: Time untuk load evaluation forms dan Arabic content
- **Database Queries**: Efficiency untuk lookup placement test data
- **Form Responsiveness**: UI performance dengan large evaluation notes
- **Search Performance**: Admin search untuk placement tests ready for evaluation

#### Documentation Testing
- **Error Messages**: Apakah messages cukup clear untuk non-technical admin
- **Help Text**: Guidance untuk admin yang tidak familiar dengan system
- **Workflow Documentation**: Apakah admin understand proper sequence untuk evaluation process