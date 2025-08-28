# Skenario Pengujian: Tes Penempatan - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Tes Penempatan
- **Modul**: Manajemen Tes Penempatan
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Automated Test**: `PlacementTestHappyPathTest.java`
- **Total Skenario**: 3 skenario utama

---

## TP-HP-001: Workflow Evaluasi Tes Penempatan Lengkap

### Informasi Skenario
- **ID Skenario**: TP-HP-001 (Tes Penempatan - Happy Path - 001)
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldCompletePlacementTestWorkflow()`
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Registrasi siswa dengan status "Approved" dan sudah memiliki placement test
- Admin account tersedia: `admin` / `AdminYSQ@2024`
- Database memiliki placement test verses yang sudah di-assign

### Data Test
```
Student Data (yang akan dievaluasi):
Nama: Placement Test Student
Email: placement.test@example.com
Program: Tahsin 1
Status: Approved
Recording Link: https://drive.google.com/file/d/placement-test/view

Admin Login:
Username: admin
Password: AdminYSQ@2024

Placement Test Evaluation:
Result Level: 3 (dari 1-6)
Evaluation Notes: Good recitation with minor improvements needed
Evaluation Reason: Based on clear audio recording
```

### Langkah Pengujian

#### Bagian 1: Setup dan Verifikasi Registrasi (Bisa skip jika sudah ada)
1. **Pastikan registrasi approved tersedia**
   - Prasyarat: Ada registrasi dengan status "Approved" dan placement test assigned
   - Verifikasi: Registrasi memiliki ayat Al-Quran yang ter-assign dan link rekaman

#### Bagian 2: Login dan Akses Management
2. **Login sebagai admin**
   - Aksi: Login dengan credentials admin
   - Verifikasi: Berhasil masuk ke dashboard admin

3. **Navigate ke placement tests management**
   - Aksi: Akses `/admin/registrations/placement-tests` atau menu "Tes Penempatan"
   - Verifikasi:
     - Halaman list tes penempatan muncul
     - Tabel menampilkan registrasi yang siap untuk evaluasi
     - Column: Nama siswa, Program, Status tes, Action buttons
     - Filter dan search tersedia

#### Bagian 3: Search dan Akses Evaluasi
4. **Search registrasi untuk evaluasi**
   - Aksi: Isi search field dengan "Placement Test Student"
   - Aksi: Klik "Search" atau submit search form
   - Verifikasi:
     - Results muncul dengan registrasi yang dicari
     - Data siswa ditampilkan: nama, program, status placement test
     - Button "Evaluasi" tersedia dan enabled

5. **Akses form evaluasi placement test**
   - Aksi: Klik tombol "Evaluasi" untuk registrasi yang ditemukan
   - Verifikasi:
     - Halaman evaluasi placement test terbuka
     - URL berubah ke `/admin/registrations/{id}/placement-test`
     - Form evaluasi ter-load dengan data siswa

#### Bagian 4: Review Informasi Placement Test
6. **Verifikasi informasi ayat Al-Quran**
   - Verifikasi:
     - Nama surah dan nomor surah ditampilkan dengan jelas
     - Teks ayat dalam bahasa Arab ter-render dengan benar (RTL direction)
     - Transliterasi ayat mudah dibaca dan akurat
     - Range ayat jelas (misal: "Ayat 1-3" atau "Ayat 5")
     - Design UI menampilkan informasi ayat dengan estetis

7. **Verifikasi akses rekaman**
   - Verifikasi:
     - Link rekaman Google Drive ditampilkan sebagai clickable link
     - Icon atau indicator yang jelas untuk "Lihat Rekaman"
     - Link dapat diklik (terbuka di tab baru - optional test)
     - Informasi upload date/time jika tersedia

#### Bagian 5: Proses Evaluasi
8. **Isi form evaluasi placement test**
   - Aksi: 
     - Pilih level hasil tes "3" dari dropdown "Placement Result"
     - Isi field "Evaluation Notes" dengan "Good recitation with minor improvements needed"
     - Isi field "Evaluation Reason" dengan "Based on clear audio recording"
   - Verifikasi:
     - Semua field dapat diisi tanpa error
     - Dropdown menampilkan level 1-6 dengan label yang jelas
     - Text areas menerima input dengan normal

9. **Submit evaluasi**
   - Aksi: Klik "Submit Evaluation" atau "Simpan Evaluasi"
   - Verifikasi:
     - Form ter-submit berhasil (loading indicator muncul)
     - Tidak ada error atau validation failure
     - Success feedback muncul

#### Bagian 6: Verifikasi Hasil Evaluasi
10. **Verifikasi redirect dan status update**
    - Verifikasi:
      - Redirect kembali ke detail registrasi (`/admin/registrations/{id}`)
      - Status placement test berubah menjadi "Evaluated" atau "Dinilai"
      - Level hasil tes ditampilkan: "Level 3"
      - Timestamp evaluasi tersimpan dan ditampilkan

11. **Verifikasi informasi evaluasi tersimpan**
    - Verifikasi:
      - Evaluation notes muncul di section placement test
      - Evaluation reason tersimpan dan ditampilkan
      - Admin yang mengevaluasi tercatat (audit info)
      - History timeline menunjukkan evaluasi completed

#### Bagian 7: Verifikasi Final Workflow Status
12. **Verifikasi workflow completion**
    - Verifikasi:
      - Status registrasi tetap "Approved"
      - Status placement test: "Evaluated"
      - Tidak ada action button evaluasi lagi (sudah completed)
      - UI menunjukkan workflow telah selesai
      - Summary information akurat dan lengkap

### Hasil Diharapkan
- Tes penempatan berhasil dievaluasi oleh admin
- Level hasil tes tersimpan dengan benar (Level 3)
- Semua catatan evaluasi tersimpan dalam database
- Status progression: Pending → Evaluated
- Workflow placement test complete tanpa error
- Audit trail lengkap untuk evaluasi

### Kriteria Sukses
- [ ] Admin dapat akses halaman placement tests management
- [ ] Search dan filter placement tests berfungsi
- [ ] Informasi ayat Al-Quran ditampilkan dengan benar dan estetis
- [ ] Link rekaman dapat diakses dan fungsional
- [ ] Form evaluasi dapat diisi dan disubmit berhasil
- [ ] Level hasil tes tersimpan dengan akurat
- [ ] Status placement test berubah ke "Evaluated"
- [ ] Evaluation notes dan reason tersimpan
- [ ] Audit information tercatat dengan benar
- [ ] UI menunjukkan workflow completion dengan jelas
- [ ] Tidak ada error atau bug selama seluruh proses

---

## TP-HP-002: Assignment dan Random Verse Selection

### Informasi Skenario
- **ID Skenario**: TP-HP-002
- **Prioritas**: Sedang
- **Selenium Method**: `shouldHandlePlacementTestAssignmentAndRandomVerseSelection()`
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Registrasi baru tanpa placement test yang sudah di-assign
- Database memiliki placement test verses

### Langkah Pengujian

1. **Buat registrasi baru**
   - Aksi: Buat registrasi siswa baru dan submit untuk review
   - Verifikasi: Registrasi tersimpan dengan status Submitted

2. **Admin approve registrasi**
   - Aksi: Login admin dan approve registrasi
   - Verifikasi: Status berubah menjadi Approved

3. **Verifikasi automatic placement test assignment**
   - Aksi: Akses detail registrasi yang sudah approved
   - Verifikasi:
     - Section placement test muncul dengan ayat yang ter-assign
     - Ayat dipilih secara random dari database verses
     - Informasi surah, ayat, teks Arab, dan transliterasi lengkap
     - Assignment ter-handle otomatis oleh system

4. **Verifikasi random assignment behavior**
   - Verifikasi:
     - Setiap registrasi baru mendapat ayat yang mungkin berbeda
     - Assignment logic berfungsi tanpa manual intervention
     - Ayat yang di-assign sesuai dengan difficulty level (jika ada)

### Kriteria Sukses
- [ ] Placement test otomatis ter-assign saat registrasi approved
- [ ] Random verse selection berfungsi dengan benar
- [ ] Informasi ayat lengkap dan akurat
- [ ] Automatic assignment tidak memerlukan admin intervention

---

## TP-HP-003: Update Recording Link oleh Siswa

### Informasi Skenario
- **ID Skenario**: TP-HP-003
- **Prioritas**: Sedang
- **Selenium Method**: `shouldAllowStudentToUpdateRecordingLink()`
- **Estimasi Waktu**: 6-7 menit

### Prasyarat
- Registrasi dengan status Draft atau yang memerlukan update recording

### Data Test
```
Original Registration:
Recording Link: (kosong atau placeholder)

Updated Recording:
Recording Link: https://drive.google.com/file/d/updated-recording/view
```

### Langkah Pengujian

1. **Buat registrasi tanpa recording link**
   - Aksi: Buat registrasi siswa tanpa mengisi link rekaman
   - Verifikasi: Registrasi tersimpan tapi placement test section kosong/incomplete

2. **Akses halaman edit registrasi**
   - Aksi: Dari halaman detail registrasi, klik "Edit Pendaftaran"
   - Verifikasi: 
     - Halaman edit terbuka dengan form yang sudah terisi
     - Field recording link kosong atau menunjukkan placeholder

3. **Update recording link**
   - Aksi: Scroll ke section placement test
   - Aksi: Isi field "Link Rekaman Google Drive" dengan URL yang valid
   - Verifikasi: Field menerima input URL dengan normal

4. **Simpan perubahan**
   - Aksi: Scroll ke bawah dan klik "Simpan Perubahan"
   - Verifikasi:
     - Form berhasil ter-submit
     - Redirect ke halaman detail registrasi
     - Link rekaman baru muncul di placement test section

5. **Verifikasi update berhasil**
   - Verifikasi:
     - Link rekaman ter-update dengan URL baru
     - Link dapat diklik dan mengarah ke URL yang benar
     - Timestamp update tercatat
     - Status registrasi tetap konsisten (tidak berubah karena edit)

### Kriteria Sukses
- [ ] Siswa dapat mengupdate recording link via edit form
- [ ] Link rekaman ter-update dan tersimpan dengan benar
- [ ] Link baru fungsional dan dapat diakses
- [ ] Update tidak mengganggu data lain dalam registrasi
- [ ] Edit functionality user-friendly dan intuitive

---

## Referensi Automated Test

### Lokasi File
`src/test/java/com/sahabatquran/webapp/functional/PlacementTestHappyPathTest.java`

### Method Mapping
- **TP-HP-001**: `shouldCompletePlacementTestWorkflow()`
- **TP-HP-002**: `shouldHandlePlacementTestAssignmentAndRandomVerseSelection()`
- **TP-HP-003**: `shouldAllowStudentToUpdateRecordingLink()`

### Eksekusi Automated Test
```bash
# Jalankan placement test tests
./mvnw test -Dtest="PlacementTestHappyPathTest"

# Dengan VNC debugging untuk observe UI
./mvnw test -Dtest="PlacementTestHappyPathTest" -Dselenium.debug.vnc.enabled=true

# Test specific method
./mvnw test -Dtest="PlacementTestHappyPathTest#shouldCompletePlacementTestWorkflow"
```

### Catatan untuk Tester

#### Focus Areas untuk Placement Test
- **Islamic Context**: Pastikan teks Arab ter-render dengan benar dan estetis
- **User Experience**: Flow evaluasi harus intuitif untuk admin
- **Data Accuracy**: Level hasil tes dan notes harus tersimpan akurat
- **Workflow Integration**: Placement test terintegrasi smooth dengan registration workflow

#### UI/UX Considerations
- **Arabic Text Rendering**: Font dan styling untuk teks Arab
- **RTL Text Direction**: Right-to-left text alignment untuk ayat
- **Link Functionality**: Google Drive links harus accessible
- **Responsive Design**: Test di berbagai screen sizes
- **Loading Performance**: Time untuk load ayat dan form evaluasi

#### Business Logic Testing
- **Random Assignment**: Setiap registrasi mendapat ayat yang appropriate
- **Level System**: Level 1-6 mencerminkan tingkat kemampuan yang benar
- **Recording Validation**: System handle berbagai format Google Drive URLs
- **Workflow State**: Status transitions yang tepat throughout evaluation process

#### Edge Cases
- **Multiple Evaluations**: Behavior jika admin coba evaluate multiple kali
- **Concurrent Access**: Multiple admin accessing same placement test
- **Recording Link Issues**: Handle invalid atau inaccessible recording links
- **Arabic Text Issues**: Handle edge cases dalam Arabic text rendering
- **Long Evaluation Notes**: Handle text yang sangat panjang dalam notes field