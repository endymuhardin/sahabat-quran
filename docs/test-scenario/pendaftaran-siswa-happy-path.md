# Skenario Pengujian: Pendaftaran Siswa - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Utama
- **Modul**: Pendaftaran Siswa
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Automated Test**: `StudentRegistrationHappyPathTest.java`
- **Total Skenario**: 3 skenario utama

---

## PS-HP-001: Pendaftaran Siswa Baru Lengkap

### Informasi Skenario
- **ID Skenario**: PS-HP-001 (Pendaftaran Siswa - Happy Path - 001)
- **Prioritas**: Tinggi
- **Selenium Method**: `shouldCompleteStudentRegistrationWorkflowSuccessfully()`
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Aplikasi dapat diakses di `http://localhost:8080`
- Database dalam kondisi bersih (fresh data)
- Browser dalam mode incognito/private

### Data Test
```
Nama Lengkap: Siti Aisyah Rahmania
Jenis Kelamin: Perempuan
Tanggal Lahir: 15/08/1995
Tempat Lahir: Jakarta
Nomor Telepon: 081234567890
Email: siti.aisyah@example.com
Alamat: Jl. Masjid Raya No. 123, Jakarta Selatan

Kontak Darurat:
- Nama: Ahmad Rahmania
- Telepon: 081987654321
- Hubungan: Orang Tua

Pendidikan:
- Tingkat Pendidikan: S1
- Pengalaman Tahsin: Belum Pernah

Program: Tahsin 1
Sesi Pilihan: Sesi 2 (08:30 - 10:00)
Hari Pilihan: Senin, Rabu
Prioritas: 1

Link Rekaman: https://drive.google.com/file/d/sample-recording/view
```

### Langkah Pengujian

#### Bagian 1: Akses Form Pendaftaran
1. **Buka halaman utama** aplikasi
   - Aksi: Akses `http://localhost:8080`
   - Verifikasi: Homepage muncul dengan menu navigasi

2. **Akses form pendaftaran**
   - Aksi: Klik menu "Pendaftaran" atau akses langsung `/register`
   - Verifikasi: Form pendaftaran muncul dengan 5 section:
     - Informasi Pribadi
     - Pendidikan
     - Program
     - Jadwal
     - Tes Penempatan

#### Bagian 2: Mengisi Informasi Pribadi
3. **Isi section Informasi Pribadi**
   - Aksi: Isi semua field sesuai data test
   - Verifikasi: 
     - Semua field dapat diisi
     - Validasi real-time untuk format email dan telepon
     - Dropdown jenis kelamin dan hubungan darurat berfungsi

4. **Lanjut ke section Pendidikan**
   - Aksi: Scroll atau klik next ke section berikutnya
   - Verifikasi: Section pendidikan terbuka dan dapat diakses

#### Bagian 3: Mengisi Pendidikan
5. **Isi section Pendidikan**
   - Aksi: Pilih tingkat pendidikan "S1" dan pengalaman "Belum Pernah"
   - Verifikasi: Pilihan tersimpan dengan benar

#### Bagian 4: Memilih Program
6. **Pilih program Tahsin 1**
   - Aksi: Pilih radio button "Tahsin 1"
   - Verifikasi: 
     - Program terpilih dengan highlight
     - Deskripsi program muncul
     - Pilihan lain ter-deselect

#### Bagian 5: Mengatur Jadwal
7. **Pilih preferensi jadwal**
   - Aksi: 
     - Pilih "Sesi 2 (08:30 - 10:00)" dari dropdown
     - Set prioritas ke "1"
     - Centang hari "Senin" dan "Rabu"
   - Verifikasi:
     - Sesi terpilih dalam dropdown
     - Checkbox hari ter-centang
     - Prioritas tersimpan

8. **Tambah preferensi jadwal kedua (opsional)**
   - Aksi: Klik tombol "Tambah Preferensi Jadwal"
   - Verifikasi: Form preferensi kedua muncul dengan opsi yang sama

#### Bagian 6: Tes Penempatan
9. **Lihat soal tes penempatan**
   - Verifikasi:
     - Ayat Al-Quran muncul dalam bahasa Arab
     - Transliterasi tersedia
     - Nama dan nomor surah ditampilkan
     - Range ayat yang harus dibaca jelas

10. **Isi link rekaman**
    - Aksi: Isi field "Link Rekaman Google Drive" dengan data test
    - Verifikasi: Link tersimpan dan divalidasi formatnya

#### Bagian 7: Submit Pendaftaran
11. **Review dan submit**
    - Aksi: Scroll ke bawah dan klik "Submit Pendaftaran"
    - Verifikasi:
      - Form diproses (loading indicator muncul)
      - Redirect ke halaman konfirmasi

#### Bagian 8: Konfirmasi Pendaftaran
12. **Verifikasi halaman konfirmasi**
    - Verifikasi:
      - Header sukses dengan icon centang
      - ID pendaftaran ditampilkan
      - Detail pendaftaran lengkap muncul
      - Status "Draft" ditampilkan
      - Informasi tes penempatan muncul
      - Langkah selanjutnya dijelaskan

13. **Akses detail pendaftaran**
    - Aksi: Klik tombol "Lihat Detail Pendaftaran"
    - Verifikasi:
      - Halaman detail terbuka dengan URL `/register/{id}`
      - Semua data yang diinput ditampilkan dengan benar
      - Tombol "Edit Pendaftaran" tersedia (karena status masih Draft)

#### Bagian 9: Submit untuk Review
14. **Submit untuk review**
    - Aksi: Klik tombol "Submit untuk Review"
    - Verifikasi:
      - Konfirmasi dialog muncul
      - Setelah confirm, status berubah menjadi "Disubmit"
      - Tombol edit hilang/disabled
      - Pesan sukses muncul

### Hasil Diharapkan
- Pendaftaran berhasil tersimpan dalam database
- Status progression: Draft → Submitted
- Semua data tersimpan dengan benar dan konsisten
- UI responsif dan user-friendly
- Tidak ada error atau bug selama proses
- Tes penempatan terassign dengan ayat random

### Kriteria Sukses
- [ ] Form pendaftaran dapat diakses tanpa error
- [ ] Semua field dapat diisi dan divalidasi dengan benar
- [ ] Program dan jadwal tersimpan sesuai pilihan
- [ ] Tes penempatan menampilkan ayat Al-Quran dengan benar
- [ ] Pendaftaran berhasil disimpan dengan status Draft
- [ ] Submit untuk review berhasil mengubah status
- [ ] Halaman konfirmasi dan detail menampilkan data yang benar
- [ ] Tidak ada error 404, 500, atau JavaScript error

### Catatan untuk Tester
- **Perhatikan loading time** pada setiap section
- **Test di berbagai browser** (Chrome, Firefox, Safari)
- **Verifikasi responsive design** di mobile view
- **Screenshot setiap section** untuk dokumentasi
- **Catat waktu eksekusi** untuk performance baseline

---

## PS-HP-002: Edit Pendaftaran dalam Status Draft

### Informasi Skenario
- **ID Skenario**: PS-HP-002
- **Prioritas**: Sedang
- **Selenium Method**: Bagian dari test utama
- **Estimasi Waktu**: 3-4 menit

### Prasyarat
- Sudah ada pendaftaran dengan status Draft (dari PS-HP-001)
- Masih dalam browser session yang sama

### Langkah Pengujian

1. **Akses halaman edit**
   - Aksi: Dari halaman detail, klik "Edit Pendaftaran"
   - Verifikasi: Form edit terbuka dengan data yang sudah diisi

2. **Ubah informasi**
   - Aksi: Ubah nomor telepon menjadi "081234567891"
   - Aksi: Tambah preferensi jadwal kedua dengan Sesi 3
   - Verifikasi: Perubahan tersimpan

3. **Simpan perubahan**
   - Aksi: Klik "Simpan Perubahan"
   - Verifikasi: Kembali ke halaman detail dengan data yang diubah

### Kriteria Sukses
- [ ] Form edit dapat diakses dari pendaftaran Draft
- [ ] Data existing ter-load dengan benar
- [ ] Perubahan dapat disimpan
- [ ] Data yang diubah muncul di halaman detail

---

## PS-HP-003: Akses Informasi Tes Penempatan

### Informasi Skenario
- **ID Skenario**: PS-HP-003
- **Prioritas**: Sedang
- **Selenium Method**: Bagian dari test utama
- **Estimasi Waktu**: 2-3 menit

### Prasyarat
- Sudah ada pendaftaran yang tersimpan
- Tes penempatan sudah terassign

### Langkah Pengujian

1. **Verifikasi informasi tes penempatan**
   - Verifikasi: 
     - Nama surah dan nomor surah ditampilkan
     - Ayat dalam bahasa Arab dengan styling yang benar (RTL)
     - Transliterasi mudah dibaca
     - Range ayat jelas (misal: ayat 1-3)

2. **Verifikasi link rekaman**
   - Verifikasi:
     - Link Google Drive dapat diklik
     - Link terbuka di tab baru
     - Status rekaman ditampilkan

### Kriteria Sukses
- [ ] Informasi surah dan ayat akurat
- [ ] Teks Arab ter-render dengan benar
- [ ] Link rekaman fungsional
- [ ] UI tes penempatan informatif dan jelas

---

## Referensi Automated Test

### Lokasi File
`src/test/java/com/sahabatquran/webapp/functional/StudentRegistrationHappyPathTest.java`

### Method Mapping
- **PS-HP-001**: `shouldCompleteStudentRegistrationWorkflowSuccessfully()`
- **PS-HP-002**: Tercakup dalam test method utama (edit functionality)
- **PS-HP-003**: Tercakup dalam test method utama (placement test verification)

### Eksekusi Automated Test
```bash
# Jalankan specific test
./mvnw test -Dtest="StudentRegistrationHappyPathTest"

# Dengan VNC debugging
./mvnw test -Dtest="StudentRegistrationHappyPathTest" -Dselenium.debug.vnc.enabled=true
```

### Validasi Konsistensi
Setelah eksekusi manual test, jalankan automated test untuk memastikan:
- Semua langkah dapat dilakukan secara programmatic
- Timing dan behavior konsisten
- Validation rules sama antara manual dan automated
- Expected results match antara kedua approach