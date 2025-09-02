# Skenario Pengujian: Evaluasi Tes Penempatan - Jalur Alternatif

## Informasi Umum
- **Kategori**: Validasi dan Penanganan Error Evaluasi Tes Penempatan
- **Modul**: Sistem Evaluasi Guru
- **Tipe Skenario**: Jalur Alternatif (Error Handling)
- **Total Skenario**: 6 skenario validasi untuk peran Guru

**Catatan**: Dalam proses bisnis yang baru, evaluasi tes penempatan dilakukan oleh **Guru**, bukan Admin.

---

## TP-AP-001: Guru - Akses Evaluasi Tanpa Penugasan

### Informasi Skenario
- **ID Skenario**: TP-AP-001 (Tes Penempatan - Jalur Alternatif - 001)
- **Prioritas**: Tinggi
- **Peran**: INSTRUKTUR/GURU
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Akun guru: `ustadz.ahmad`
- Registrasi yang tidak ditugaskan ke guru ini
- Registrasi ditugaskan ke guru lain: `ustadzah.fatimah`

### Data Test
```
Login Guru: ustadz.ahmad
Registrasi ditugaskan ke: ustadzah.fatimah
ID Registrasi: {id-yang-tidak-ditugaskan-ke-guru-saat-ini}
```

### Langkah Pengujian

1. **Login sebagai Guru A**
   - Aksi: Login sebagai ustadz.ahmad
   - Verifikasi: Dashboard guru muncul normal

2. **Coba akses evaluasi guru B**
   - Aksi: Akses langsung URL `/registrations/assigned/{id}/review` (ditugaskan ke guru lain)
   - Verifikasi:
     - Akses ditolak (403 atau redirect)
     - Pesan error: "Anda tidak memiliki akses untuk mereview registrasi ini"
     - Redirect ke dashboard guru atau daftar tugas

3. **Verifikasi pemfilteran daftar**
   - Aksi: Cek `/registrations/assigned`
   - Verifikasi:
     - Hanya registrasi yang ditugaskan ke guru saat ini yang muncul
     - Tidak ada registrasi dari guru lain
     - Pemfilteran keamanan berfungsi

### Kriteria Sukses
- [ ] Guru tidak bisa mengakses registrasi yang ditugaskan ke guru lain
- [ ] Penegakan keamanan yang ketat
- [ ] Pesan error yang jelas dan informatif
- [ ] Pemfilteran daftar yang akurat

---

## TP-AP-002: Guru - Evaluasi Tanpa Link Rekaman

### Informasi Skenario
- **ID Skenario**: TP-AP-002
- **Prioritas**: Tinggi
- **Peran**: INSTRUKTUR/GURU
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Guru sudah login
- Registrasi ditugaskan tapi siswa belum upload rekaman
- Status registrasi: ASSIGNED, tapi recording_drive_link NULL

### Data Test
```
Data Siswa:
Nama: Siswa Tanpa Rekaman
Email: tanparekaman@test.com
Status: ASSIGNED ke guru saat ini
Link Rekaman: (kosong/null)
```

### Langkah Pengujian

1. **Akses registrasi tanpa rekaman**
   - Aksi: Navigate ke form review untuk registrasi tanpa rekaman
   - Verifikasi:
     - Form review terbuka
     - Data siswa ditampilkan
     - Ayat tes penempatan ditampilkan

2. **Cek bagian link rekaman**
   - Verifikasi:
     - Bagian rekaman menampilkan "Belum tersedia"
     - Atau pesan placeholder
     - Ikon peringatan atau indikator

3. **Coba submit evaluasi tanpa rekaman**
   - Aksi: Isi form evaluasi dan coba submit
   - Verifikasi:
     - Error validasi: "Rekaman tes penempatan belum tersedia"
     - Form tidak ter-submit
     - Instruksi untuk siswa upload rekaman

4. **Penegakan aturan bisnis**
   - Verifikasi:
     - Sistem mencegah evaluasi tanpa rekaman
     - Guru diberitahu tindakan selanjutnya yang diperlukan
     - Status tetap ASSIGNED sampai rekaman tersedia

### Kriteria Sukses
- [ ] Evaluasi dicegah tanpa rekaman
- [ ] Pesan error yang informatif
- [ ] Aturan bisnis ditegakkan
- [ ] Panduan yang jelas untuk langkah selanjutnya

---

## TP-AP-003: Guru - Submit Evaluasi Kosong

### Informasi Skenario
- **ID Skenario**: TP-AP-003
- **Prioritas**: Sedang
- **Peran**: INSTRUKTUR/GURU
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Guru sudah login
- Registrasi ditugaskan dengan rekaman tersedia
- Form evaluasi dapat diakses

### Langkah Pengujian

1. **Akses form evaluasi**
   - Aksi: Navigate ke form evaluasi
   - Verifikasi: Form terbuka dengan semua field kosong

2. **Submit evaluasi kosong**
   - Aksi: Set status "COMPLETED" dan submit tanpa isi field
   - Verifikasi:
     - Error validasi form
     - Highlighting field yang wajib
     - "Catatan guru wajib diisi"

3. **Submit dengan catatan kurang dari minimum**
   - Aksi: Isi catatan dengan teks singkat (< 10 karakter)
   - Verifikasi:
     - Error validasi: "Minimum 10 karakter"
     - Indikator jumlah karakter
     - Form tidak ter-submit

4. **Submit tanpa level yang direkomendasikan**
   - Aksi: Isi catatan lengkap tapi tidak pilih level
   - Verifikasi:
     - Validasi: "Level yang direkomendasikan wajib dipilih"
     - Dropdown di-highlight
     - Aturan bisnis ditegakkan

### Kriteria Sukses
- [ ] Validasi form yang komprehensif
- [ ] Field wajib ditegakkan
- [ ] Validasi minimum karakter
- [ ] Aturan bisnis divalidasi

---

## TP-AP-004: Guru - Pencegahan Evaluasi Ganda

### Informasi Skenario
- **ID Skenario**: TP-AP-004
- **Prioritas**: Sedang
- **Peran**: INSTRUKTUR/GURU
- **Estimasi Waktu**: 5-6 menit

### Prasyarat
- Registrasi yang sudah selesai evaluasi
- Status review guru: COMPLETED
- Status registrasi: REVIEWED

### Langkah Pengujian

1. **Selesaikan evaluasi pertama**
   - Aksi: Selesaikan evaluasi untuk satu registrasi
   - Verifikasi: Status berubah ke REVIEWED/COMPLETED

2. **Coba akses form evaluasi lagi**
   - Aksi: Coba akses `/registrations/assigned/{id}/review`
   - Verifikasi:
     - Redirect ke halaman detail
     - Pesan: "Evaluasi sudah selesai"
     - Form evaluasi tidak dapat diakses

3. **Coba edit via manipulasi URL langsung**
   - Aksi: Coba berbagai upaya URL untuk bypass
   - Verifikasi:
     - Pencegahan akses yang konsisten
     - Langkah keamanan tahan
     - Tidak ada korupsi data

### Kriteria Sukses
- [ ] Evaluasi ganda dicegah
- [ ] Perlindungan state workflow
- [ ] Integritas data terjaga
- [ ] Pencegahan bypass keamanan

---

## TP-AP-005: Guru - Nilai Hasil Penempatan Tidak Valid

### Informasi Skenario
- **ID Skenario**: TP-AP-005
- **Prioritas**: Rendah
- **Peran**: INSTRUKTUR/GURU
- **Estimasi Waktu**: 4-5 menit

### Prasyarat
- Guru dengan akses form evaluasi
- Pengetahuan tentang rentang level yang diharapkan

### Langkah Pengujian

1. **Test nilai batas**
   - Aksi: Coba input hasil penempatan di luar rentang valid
   - Verifikasi:
     - Dropdown hanya menampilkan opsi valid
     - Tidak ada field input manual yang bisa disalahgunakan
     - Validasi rentang di backend

2. **Test konsistensi level**
   - Aksi: Pilih level yang tidak sesuai dengan tingkat kesulitan ayat
   - Verifikasi:
     - Pesan peringatan atau panduan
     - Saran logika bisnis
     - Izinkan override dengan catatan tambahan

3. **Test ketidakcocokan level yang direkomendasikan**
   - Aksi: Pilih hasil penempatan dan level yang direkomendasikan tidak konsisten
   - Verifikasi:
     - Peringatan validasi
     - Field penjelasan diperlukan
     - Panduan aturan bisnis

### Kriteria Sukses
- [ ] Validasi input yang tepat
- [ ] Konsistensi logika bisnis
- [ ] Validasi rentang efektif
- [ ] Panduan pengguna yang membantu

---

## TP-AP-006: Guru - Timeout Sesi Selama Evaluasi

### Informasi Skenario
- **ID Skenario**: TP-AP-006
- **Prioritas**: Rendah
- **Peran**: INSTRUKTUR/GURU
- **Estimasi Waktu**: 6-8 menit

### Prasyarat
- Sesi guru dengan timeout lebih pendek (untuk testing)
- Proses evaluasi yang lama
- Fungsi simpan draft tersedia

### Langkah Pengujian

1. **Mulai proses evaluasi**
   - Aksi: Mulai isi form evaluasi
   - Verifikasi: Form dapat diakses dan fungsional

2. **Simulasi timeout sesi**
   - Aksi: Tunggu atau simulasi expired sesi
   - Aksi: Coba submit evaluasi setelah timeout
   - Verifikasi:
     - Deteksi timeout sesi
     - Redirect ke halaman login
     - Preservasi data draft (jika diimplementasikan)

3. **Login dan pemulihan**
   - Aksi: Login ulang dengan guru yang sama
   - Aksi: Navigate kembali ke evaluasi yang sama
   - Verifikasi:
     - Data draft tersedia (jika disimpan)
     - atau form reset dengan peringatan
     - Tidak ada korupsi data

### Kriteria Sukses
- [ ] Penanganan timeout sesi yang halus
- [ ] Logika preservasi data
- [ ] Pengalaman pengguna yang lancar
- [ ] Keamanan terjaga

---

## Pengujian Keamanan & Logika Bisnis

### Autentikasi & Otorisasi
- [ ] Akses khusus guru ditegakkan
- [ ] Kontrol akses berbasis penugasan
- [ ] Manajemen sesi yang tepat
- [ ] Pencegahan manipulasi URL

### Validasi Data
- [ ] Verifikasi link rekaman
- [ ] Validasi field evaluasi
- [ ] Pengecekan konsistensi level
- [ ] Batas karakter ditegakkan

### Perlindungan Workflow
- [ ] Kontrol akses berbasis status
- [ ] Pencegahan evaluasi ganda
- [ ] Manajemen state draft vs final
- [ ] Integritas audit trail

### Penanganan Error
- [ ] Pesan error yang halus
- [ ] Panduan pengguna yang jelas
- [ ] Prosedur pemulihan
- [ ] Konsistensi data terjaga

## Kasus Edge

### Masalah Teknis
- [ ] Interupsi jaringan selama submission
- [ ] Penanganan file rekaman besar
- [ ] Evaluasi guru yang bersamaan
- [ ] Masalah koneksi database

### Skenario Bisnis
- [ ] Siswa update rekaman selama evaluasi
- [ ] Reassignment guru di tengah evaluasi
- [ ] Skenario penugasan massal
- [ ] Penanganan evaluasi prioritas

### Pengujian Integrasi
- [ ] Aksesibilitas link rekaman
- [ ] Sinkronisasi status antar modul
- [ ] Integrasi sistem notifikasi
- [ ] Akurasi data laporan

Dokumen ini mencerminkan proses bisnis yang diperbarui dimana guru, bukan administrator, menangani evaluasi tes penempatan dengan keahlian akademis yang tepat dan kontrol keamanan berbasis peran.