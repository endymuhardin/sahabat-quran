# Panduan Pengguna - Sistem Manajemen Yayasan Sahabat Quran

## Gambaran Umum

Sistem Manajemen Yayasan Sahabat Quran adalah aplikasi berbasis web untuk mengelola pendaftaran siswa, penugasan guru, dan evaluasi tes penempatan untuk program pendidikan Islam.

## 🎓 Program Pendidikan

### Program Tahsin (Perbaikan Bacaan)
- **Tahsin 1**: Tingkat dasar untuk pemula
- **Tahsin 2**: Tingkat menengah 
- **Tahsin 3**: Tingkat lanjutan

### Program Tahfidz (Hafalan)
- **Tahfidz Pemula**: Untuk hafalan awal
- **Tahfidz Menengah**: Tingkat menengah
- **Tahfidz Lanjutan**: Tingkat mahir

## ⏰ Jadwal Sesi Pembelajaran

Tersedia 7 slot waktu pembelajaran:
- **Sesi 1**: 07:00 - 08:30
- **Sesi 2**: 08:30 - 10:00  
- **Sesi 3**: 10:00 - 11:30
- **Sesi 4**: 13:00 - 14:30
- **Sesi 5**: 14:30 - 16:00
- **Sesi 6**: 16:00 - 17:30
- **Sesi 7**: 19:00 - 20:30

## 👥 Peran Pengguna

### Siswa/Calon Siswa
- Mengisi formulir pendaftaran
- Mengedit pendaftaran (status DRAFT)
- Melihat status pendaftaran

### Staff Administrasi
- Mengelola daftar pendaftaran siswa
- Menugaskan guru untuk review
- Melihat laporan pendaftaran

### Guru/Ustadz
- Mereview pendaftaran yang ditugaskan
- Mengevaluasi tes penempatan
- Memberikan rekomendasi level

### Manajemen
- Melihat laporan dan statistik
- Menugaskan guru
- Monitoring proses pendaftaran

---

# Panduan Pendaftaran Siswa

## 📝 Proses Pendaftaran

### Langkah 1: Mengisi Formulir Pendaftaran

1. **Akses halaman pendaftaran**: Buka `http://[alamat-aplikasi]/register`
2. **Isi informasi pribadi**:
   - Nama lengkap
   - Jenis kelamin
   - Tanggal dan tempat lahir
   - Nomor telepon
   - Email (harus unik)
   - Alamat lengkap

3. **Isi kontak darurat**:
   - Nama kontak darurat
   - Nomor telepon kontak darurat
   - Hubungan dengan siswa

4. **Isi informasi pendidikan**:
   - Tingkat pendidikan
   - Pengalaman membaca Quran
   - Pengalaman tahsin sebelumnya

5. **Pilih program**: Pilih salah satu program yang tersedia (Tahsin atau Tahfidz)

6. **Atur preferensi jadwal**:
   - Pilih hingga 3 sesi yang diinginkan
   - Tentukan prioritas (1-3)
   - Pilih hari-hari yang diinginkan

7. **Upload tes penempatan**:
   - Rekam bacaan ayat yang ditentukan
   - Upload ke Google Drive
   - Masukkan link sharing Google Drive

8. **Tinjau dan kirim**: Periksa semua informasi dan kirim pendaftaran

### Status Pendaftaran

#### DRAFT (Konsep)
- Pendaftaran masih dapat diedit
- Belum disubmit untuk review
- **Aksi**: Dapat mengedit dan menyempurnakan pendaftaran

#### SUBMITTED (Diajukan)  
- Pendaftaran sudah disubmit
- Menunggu review dari staff
- **Aksi**: Tidak dapat diedit, menunggu review

#### ASSIGNED (Ditugaskan)
- Staff telah menugaskan guru untuk review
- Guru akan mengevaluasi tes penempatan
- **Aksi**: Menunggu evaluasi guru

#### REVIEWED (Sudah Direview)
- Guru telah selesai mengevaluasi
- Mendapat rekomendasi level
- **Aksi**: Siap untuk proses enrollment

#### COMPLETED (Selesai)
- Proses pendaftaran selesai
- Dapat melanjutkan ke pendaftaran kelas
- **Aksi**: Ikuti instruksi selanjutnya dari admin

#### REJECTED (Ditolak)
- Pendaftaran ditolak dengan alasan tertentu
- **Aksi**: Hubungi admin untuk klarifikasi

---

# Panduan untuk Staff Administrasi

## 📊 Mengelola Pendaftaran

### Melihat Daftar Pendaftaran
1. Login ke sistem dengan akun staff
2. Akses menu "Kelola Registrasi"
3. Gunakan filter untuk melihat pendaftaran berdasarkan:
   - Status pendaftaran
   - Nama siswa
   - Email
   - Program yang dipilih

### Menugaskan Guru untuk Review
1. Pilih pendaftaran dengan status "SUBMITTED"
2. Klik "Assign Teacher" atau "Tugaskan Guru"  
3. Pilih guru yang akan melakukan review
4. Tambahkan catatan untuk guru (opsional)
5. Klik "Assign" untuk mengirim penugasan

### Melihat Detail Pendaftaran
- Informasi pribadi siswa lengkap
- Preferensi jadwal dan program
- Status tes penempatan
- Riwayat perubahan status

---

# Panduan untuk Guru/Ustadz

## 🎯 Mereview Pendaftaran Siswa

### Melihat Tugas Review
1. Login dengan akun guru
2. Akses "My Assignments" atau "Tugas Saya"
3. Lihat daftar pendaftaran yang ditugaskan
4. Status review: PENDING, IN_REVIEW, COMPLETED

### Melakukan Review dan Evaluasi
1. **Klik "Review"** pada pendaftaran yang ditugaskan
2. **Tinjau informasi siswa**:
   - Data pribadi dan pendidikan
   - Program yang dipilih
   - Preferensi jadwal

3. **Evaluasi tes penempatan**:
   - Klik link rekaman Google Drive
   - Dengarkan bacaan siswa
   - Perhatikan kualitas tajwid dan kelancaran

4. **Isi form evaluasi**:
   - **Status Review**: Set ke "IN_REVIEW" saat mulai, "COMPLETED" saat selesai
   - **Catatan Guru**: Berikan feedback detail tentang bacaan siswa
   - **Hasil Tes Penempatan**: Nilai 1-5 berdasarkan kualitas bacaan
   - **Program yang Direkomendasikan**: Pilih level yang sesuai
   - **Catatan Penempatan**: Penjelasan tambahan untuk rekomendasi

5. **Submit Review**: Klik "Submit Evaluation" untuk menyelesaikan review

### Tips Evaluasi Tes Penempatan
- **Perhatikan makhorijul huruf**: Kebenaran pengucapan huruf
- **Evaluasi tajwid**: Penerapan kaidah tajwid
- **Nilai kelancaran**: Kemampuan membaca tanpa terputus-putus
- **Berikan feedback konstruktif**: Saran perbaikan yang dapat ditindaklanjuti

---

# Panduan untuk Manajemen

## 📈 Monitoring dan Laporan

### Dashboard Manajemen
- Total pendaftaran per periode
- Breakdown status pendaftaran
- Performa guru dalam review
- Statistik program yang diminati

### Laporan Pendaftaran
1. Akses menu "Reports" atau "Laporan"
2. Pilih jenis laporan yang diinginkan
3. Set periode laporan
4. Export ke Excel/PDF jika diperlukan

### Monitoring Teacher Workload
- Melihat beban kerja per guru
- Distribusi penugasan review
- Waktu rata-rata penyelesaian review

---

# FAQ (Frequently Asked Questions)

## Untuk Siswa

**Q: Bisakah saya mengubah pendaftaran setelah disubmit?**
A: Pendaftaran hanya bisa diubah selama status masih DRAFT. Setelah SUBMITTED, hubungi admin jika ada perubahan mendesak.

**Q: Bagaimana jika link Google Drive saya tidak bisa diakses?**
A: Pastikan setting sharing set ke "Anyone with the link can view". Jika masih bermasalah, hubungi admin.

**Q: Berapa lama proses review?**
A: Biasanya 3-7 hari kerja setelah pendaftaran disubmit, tergantung ketersediaan guru.

## Untuk Staff

**Q: Bagaimana kriteria pemilihan guru untuk assignment?**
A: Pertimbangkan beban kerja guru, keahlian sesuai program, dan ketersediaan waktu.

**Q: Apa yang harus dilakukan jika ada pendaftaran duplikat?**
A: Sistem otomatis mencegah duplikasi email/telepon. Jika terdeteksi, hubungi developer.

## Untuk Guru

**Q: Bagaimana jika rekaman tes penempatan tidak jelas?**
A: Hubungi admin untuk koordinasi dengan siswa agar mengupload rekaman yang lebih baik.

**Q: Bisakah saya mengubah evaluasi yang sudah disubmit?**
A: Evaluasi yang sudah disubmit tidak bisa diubah. Hubungi admin jika ada koreksi mendesak.

---

## Kontak dan Dukungan

- **Email Support**: admin@sahabatquran.org
- **WhatsApp**: +62-xxx-xxxx-xxxx
- **Jam Operasional**: Senin-Jumat 08:00-17:00 WIB

---

*Dokumen ini akan terus diperbarui seiring perkembangan sistem. Pastikan selalu menggunakan versi terbaru.*