# Skenario Pengujian: Admin Registrasi - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Admin
- **Modul**: Manajemen Registrasi Admin
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Automated Test**: `AdminRegistrationHappyPathTest.java`
- **Total Skenario**: 2 skenario utama

---

## AR-HP-001: Workflow Review dan Approval Registrasi

### Informasi Skenario
- **ID Skenario**: AR-HP-001 (Admin Registrasi - Happy Path - 001)
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldCompleteAdminRegistrationManagementWorkflow()`
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- Sudah ada registrasi siswa dengan status "Submitted" (bisa menggunakan hasil dari PS-HP-001)
- Admin account tersedia: `admin` / `AdminYSQ@2024`
- Database dalam kondisi konsisten

### Data Test
```
Admin Login:
Username: admin
Password: AdminYSQ@2024

Student Data (yang akan di-review):
Nama: Admin Test Student
Email: admin.test.student@example.com
Program: Tahsin 1
Status Awal: Submitted

Review Decision:
Status Baru: APPROVED
Review Notes: Registration approved after review
Decision Reason: Meets all requirements

Placement Test Evaluation:
Result Level: 4
Evaluation Notes: Good recitation quality
Evaluation Reason: Evaluated based on recording
```

### Langkah Pengujian

#### Bagian 1: Setup Data Registrasi (Bisa skip jika sudah ada)
1. **Buat registrasi siswa untuk di-review**
   - Aksi: Ikuti skenario PS-HP-001 untuk buat registrasi lengkap
   - Aksi: Submit registrasi untuk review (status menjadi "Submitted")
   - Verifikasi: Registrasi tersedia untuk admin review

#### Bagian 2: Login sebagai Admin
2. **Akses halaman login**
   - Aksi: Buka `/login`
   - Verifikasi: Form login muncul dengan field username dan password

3. **Login sebagai admin**
   - Aksi: Isi username "admin" dan password "AdminYSQ@2024"
   - Aksi: Klik "Login"
   - Verifikasi:
     - Login berhasil, redirect ke dashboard
     - Menu admin tersedia di navigasi
     - Welcome message menampilkan "admin"

#### Bagian 3: Akses Manajemen Registrasi
4. **Navigate ke registrations management**
   - Aksi: Akses `/admin/registrations` atau klik menu "Kelola Registrasi"
   - Verifikasi:
     - Halaman list registrasi muncul
     - Tabel menampilkan registrasi yang ada
     - Search dan filter tersedia
     - Action buttons (View, Review) tersedia

5. **Search registrasi yang akan di-review**
   - Aksi: Isi field search dengan "Admin Test Student"
   - Aksi: Klik "Search" atau tekan Enter
   - Verifikasi:
     - Results muncul dengan registrasi yang dicari
     - Data registrasi ditampilkan: nama, email, program, status
     - Status menunjukkan "Submitted" atau "Menunggu Review"

#### Bagian 4: Review Detail Registrasi
6. **Akses detail registrasi**
   - Aksi: Klik link "Detail" atau nama siswa
   - Verifikasi:
     - Halaman detail registrasi terbuka
     - Semua informasi siswa ditampilkan lengkap
     - Section-wise display: Personal, Education, Program, Schedule, Placement Test
     - Status registrasi jelas terlihat
     - Action buttons tersedia

7. **Verifikasi informasi lengkap**
   - Verifikasi:
     - Data personal: nama, kontak, alamat
     - Informasi pendidikan dan pengalaman
     - Pilihan program dan jadwal
     - Informasi tes penempatan dengan ayat Al-Quran
     - Link rekaman Google Drive dapat diklik

#### Bagian 5: Proses Review dan Approval
8. **Akses form review**
   - Aksi: Klik tombol "Review" atau "Review Registrasi"
   - Verifikasi:
     - Form review terbuka dengan field untuk keputusan
     - Dropdown status tersedia: Approved, Rejected
     - Field notes dan reason tersedia
     - Informasi registrasi masih terlihat sebagai referensi

9. **Isi form review - Approval**
   - Aksi: 
     - Pilih "APPROVED" dari dropdown status
     - Isi "Review Notes" dengan "Registration approved after review"
     - Isi "Decision Reason" dengan "Meets all requirements"
   - Verifikasi: Semua field dapat diisi tanpa error

10. **Submit review decision**
    - Aksi: Klik "Submit Review" atau "Simpan Keputusan"
    - Verifikasi:
      - Form ter-submit berhasil
      - Redirect kembali ke detail registrasi
      - Status berubah menjadi "Approved" atau "Disetujui"
      - Timestamp review tersimpan
      - Review notes muncul di history

#### Bagian 6: Evaluasi Tes Penempatan
11. **Navigate ke placement tests management**
    - Aksi: Akses `/admin/registrations/placement-tests`
    - Verifikasi:
      - List placement tests muncul
      - Registrasi yang sudah approved muncul untuk evaluasi
      - Status "Menunggu Evaluasi" terlihat

12. **Akses evaluasi tes penempatan**
    - Aksi: Klik "Evaluasi" untuk registrasi yang sudah approved
    - Verifikasi:
      - Form evaluasi placement test terbuka
      - Informasi ayat Al-Quran ditampilkan
      - Link rekaman dapat diakses
      - Form input untuk hasil evaluasi tersedia

13. **Evaluasi rekaman**
    - Aksi: 
      - Klik link rekaman untuk listen/review (optional - tergantung link valid)
      - Pilih level hasil tes: "4" dari dropdown
      - Isi "Evaluation Notes" dengan "Good recitation quality"
      - Isi "Evaluation Reason" dengan "Evaluated based on recording"

14. **Submit evaluasi**
    - Aksi: Klik "Submit Evaluation"
    - Verifikasi:
      - Evaluasi berhasil tersimpan
      - Redirect ke detail registrasi
      - Status placement test berubah menjadi "Evaluated" atau "Dinilai"
      - Level hasil tes ditampilkan

#### Bagian 7: Verifikasi Final Status
15. **Verifikasi workflow completion**
    - Verifikasi:
      - Status registrasi: "Approved" atau "Disetujui"
      - Status placement test: "Evaluated" atau "Dinilai"
      - Level tes penempatan tersimpan
      - History timeline menunjukkan semua step
      - Tidak ada action button yang tersisa (workflow complete)

### Hasil Diharapkan
- Registrasi berhasil di-review dan approved oleh admin
- Tes penempatan berhasil dievaluasi dengan level yang sesuai
- Status progression: Submitted → Approved → Placement Test Evaluated
- Semua data review dan evaluasi tersimpan dengan audit trail
- Workflow admin berjalan smooth tanpa error

### Kriteria Sukses
- [ ] Admin dapat login dan akses halaman management
- [ ] Search dan filter registrasi berfungsi
- [ ] Detail registrasi ditampilkan lengkap dan akurat
- [ ] Form review dapat diisi dan disubmit berhasil
- [ ] Status registrasi berubah sesuai approval
- [ ] Placement test evaluation dapat dilakukan
- [ ] Level hasil tes tersimpan dengan benar
- [ ] Final status menunjukkan workflow complete
- [ ] Tidak ada error atau bug dalam seluruh proses

---

## AR-HP-002: Bulk Review Multiple Registrations

### Informasi Skenario
- **ID Skenario**: AR-HP-002
- **Prioritas**: Sedang
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Multiple registrasi dengan status "Submitted" (minimal 3 registrasi)
- Admin sudah login

### Langkah Pengujian

1. **Lihat multiple registrations**
   - Verifikasi: List menampilkan multiple registrasi yang menunggu review

2. **Review registrasi pertama - Approve**
   - Aksi: Review dan approve registrasi pertama
   - Verifikasi: Status berubah menjadi approved

3. **Review registrasi kedua - Approve**
   - Aksi: Review dan approve registrasi kedua
   - Verifikasi: Status berubah menjadi approved

4. **Batch evaluate placement tests**
   - Aksi: Akses placement tests page
   - Aksi: Evaluate kedua registrasi yang sudah approved
   - Verifikasi: Keduanya berhasil dievaluasi

### Kriteria Sukses
- [ ] Multiple registrasi dapat di-review efficiently
- [ ] Status changes consistent across registrations
- [ ] Batch operations tidak interfere satu sama lain

---

## Referensi Automated Test

### Lokasi File
`src/test/java/com/sahabatquran/webapp/functional/AdminRegistrationHappyPathTest.java`

### Method Mapping
- **AR-HP-001**: `shouldCompleteAdminRegistrationManagementWorkflow()`
- **AR-HP-002**: Tercakup dalam test variations

### Eksekusi Automated Test
```bash
# Jalankan admin registration tests
./mvnw test -Dtest="AdminRegistrationHappyPathTest"

# Dengan VNC debugging
./mvnw test -Dtest="AdminRegistrationHappyPathTest" -Dselenium.debug.vnc.enabled=true
```

### Catatan untuk Tester

#### Focus Areas
- **Admin UX**: Efficiency dan clarity dari admin interface
- **Data Presentation**: Bagaimana registrasi data dipresentasikan
- **Review Process**: Logika dan flow dari review workflow
- **Audit Trail**: Bagaimana history dan changes dicatat
- **Performance**: Loading time untuk list dan detail pages

#### Permission Testing
- Pastikan hanya admin yang bisa akses admin pages
- Test behavior jika non-admin coba akses admin URLs
- Verify admin-specific menu items dan functions

#### Edge Cases untuk Admin
- Review registrasi dengan data minimal vs lengkap
- Handle registrasi dengan missing placement test
- Multiple admin sessions reviewing same registration
- Admin logout behavior dalam middle of review process