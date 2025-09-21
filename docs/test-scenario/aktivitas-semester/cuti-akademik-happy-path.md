# Skenario Pengujian: Cuti Akademik Siswa - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Akademik - Manajemen Cuti
- **Modul**: Manajemen Cuti Siswa
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 8 skenario utama (Siswa, Admin Akademik, Manajemen, Sistem)

---

## CA-HP-001: Siswa - Pengajuan Cuti Awal Semester

### Informasi Skenario
- **ID Skenario**: CA-HP-001 (Cuti Akademik - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `StudentLeaveRequestTest.studentEarlyTermLeaveRequest()`

### Prasyarat
- Student account tersedia: `siswa.ali` / `Welcome@YSQ2024`
- Semester baru sudah dimulai (dalam 2 minggu pertama)
- Student sudah enrolled dalam kelas semester berjalan
- Tidak ada tunggakan pembayaran
- Student belum pernah cuti di 2 semester terakhir

### Data Test
```
Login Siswa:
Username: siswa.ali
Password: Welcome@YSQ2024

Profil Siswa:
Nama: Muhammad Ali
NIS: STD-2024-00045
Level Saat Ini: Tahfiz 2
Semester Berjalan: 2024/2025 Ganjil
Status Pendaftaran: Aktif
Status Pembayaran: Lunas

Detail Pengajuan Cuti:
Jenis: Cuti Kesehatan
Tanggal Mulai: [Tanggal Sekarang + 3 hari]
Tanggal Selesai: [Tanggal Sekarang + 90 hari]
Alasan: Perawatan kesehatan intensif
Dokumen Pendukung: Surat keterangan dokter (PDF)
```

### Langkah Pengujian

#### Bagian 1: Login dan Akses Form Cuti
1. **Login sebagai Siswa**
   - Aksi: Login dengan kredensial siswa
   - Verifikasi:
     - Login berhasil
     - Dashboard siswa muncul
     - Status pendaftaran aktif terlihat
     - Menu "Layanan Akademik" tersedia

2. **Navigasi ke Pengajuan Cuti**
   - Aksi: Klik menu "Layanan Akademik" → "Pengajuan Cuti"
   - Verifikasi:
     - Halaman pengajuan cuti terbuka
     - Form pengajuan kosong siap diisi
     - Informasi semester berjalan ditampilkan
     - Status kelayakan untuk cuti: "Memenuhi Syarat"

#### Bagian 2: Pengisian Form Cuti
3. **Periksa status kelayakan**
   - Aksi: Klik tombol "Periksa Kelayakan"
   - Verifikasi:
     - Sistem memeriksa status pembayaran: ✓ Tidak ada tunggakan
     - Sistem memeriksa kehadiran: ✓ Di atas persyaratan minimum
     - Sistem memeriksa riwayat cuti: ✓ Tidak ada cuti dalam 2 semester terakhir
     - Kelayakan dikonfirmasi dengan tanda centang hijau

4. **Isi form pengajuan cuti**
   - Aksi: Lengkapi semua field wajib:
     - Jenis Cuti: "Cuti Kesehatan"
     - Tanggal Mulai: [Pilih tanggal]
     - Tanggal Selesai: [Pilih tanggal]
     - Durasi terhitung otomatis: "3 bulan"
     - Alasan: [Input alasan detail]
   - Verifikasi:
     - Validasi tanggal berfungsi (tidak bisa pilih tanggal lampau)
     - Perhitungan durasi otomatis dan akurat
     - Penghitung karakter untuk field alasan
     - Pesan validasi untuk field kosong

5. **Unggah dokumen pendukung**
   - Aksi: Unggah surat keterangan dokter (PDF, maks 5MB)
   - Verifikasi:
     - Progress bar upload file muncul
     - File berhasil diunggah
     - Preview dokumen tersedia
     - Validasi ukuran dan tipe file

#### Bagian 3: Kirim dan Konfirmasi
6. **Tinjau ringkasan pengajuan**
   - Aksi: Klik "Tinjau Pengajuan"
   - Verifikasi:
     - Halaman ringkasan muncul dengan semua detail
     - Informasi siswa benar
     - Periode cuti tertera jelas
     - Dokumen terdaftar
     - Syarat & ketentuan ditampilkan

7. **Kirim pengajuan cuti**
   - Aksi: Centang kotak persetujuan dan klik "Kirim Pengajuan"
   - Verifikasi:
     - Dialog konfirmasi muncul
     - Peringatan tentang konsekuensi akademik ditampilkan
     - Konfirmasi pengiriman diperlukan

8. **Terima tanda terima konfirmasi**
   - Aksi: Konfirmasi pengiriman
   - Verifikasi:
     - Pesan sukses ditampilkan
     - Nomor referensi dibuat: "LR-2024-00123"
     - Notifikasi email terkirim
     - Status berubah menjadi "Menunggu Peninjauan"
     - Tersedia opsi cetak/unduh tanda terima

### Kriteria Sukses
- [ ] Validasi form berfungsi dengan baik
- [ ] Unggah dokumen berhasil
- [ ] Pemeriksaan kelayakan akurat
- [ ] Nomor referensi dibuat
- [ ] Notifikasi email terkirim
- [ ] Pelacakan status tersedia

### Data Verifikasi Akhir
```
Status Database yang Diharapkan:
- Tabel student_leave_requests: Entri baru dibuat
- Status: MENUNGGU_PENINJAUAN
- Status workflow: Menunggu peninjauan admin akademik
- Jejak audit: Pengiriman pengajuan tercatat
- Antrian notifikasi: Email ke siswa dan admin akademik
```

---

## CA-HP-002: Admin Akademik - Peninjauan dan Persetujuan Cuti

### Informasi Skenario
- **ID Skenario**: CA-HP-002
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `StudentLeaveApprovalTest.academicAdminReviewProcess()`

### Prasyarat
- Academic admin account: `academic.admin1` / `Welcome@YSQ2024`
- Ada pending leave requests dari CA-HP-001
- System dalam operational hours

### Data Test
```
Login Admin Akademik:
Username: academic.admin1
Password: Welcome@YSQ2024

Pengajuan Tertunda:
Referensi: LR-2024-00123
Siswa: Muhammad Ali (STD-2024-00045)
Jenis: Cuti Kesehatan
Durasi: 3 bulan
Status: MENUNGGU_PENINJAUAN
```

### Langkah Pengujian

#### Bagian 1: Access Leave Management Dashboard
1. **Login sebagai Academic Admin**
   - Aksi: Login dengan credentials academic admin
   - Verifikasi:
     - Dashboard admin terbuka
     - Notification badge untuk pending leaves
     - Quick stats widget menunjukkan pending count

2. **Navigate ke Leave Management**
   - Aksi: Klik "Student Services" → "Leave Management"
   - Verifikasi:
     - Leave management dashboard terbuka
     - Pending requests tab active
     - List of pending requests visible
     - Filter dan search options available

#### Bagian 2: Review Student Leave Request
3. **Open specific leave request**
   - Aksi: Klik request LR-2024-00123
   - Verifikasi:
     - Detailed view terbuka
     - Student profile summary displayed
     - Academic history visible
     - Leave request details complete
     - Supporting documents viewable

4. **Verify student eligibility**
   - Aksi: Klik "Verify Details" button
   - Verifikasi:
     - Academic record: Current GPA, attendance percentage
     - Financial status: No outstanding dues confirmed
     - Leave history: No previous leaves in 2 terms
     - Class enrollment: 5 subjects enrolled
     - System recommendation: "Eligible for Leave"

5. **Review supporting documents**
   - Aksi: Open attached medical certificate
   - Verifikasi:
     - Document viewer opens
     - Medical certificate legitimate
     - Date coverage matches request
     - Hospital/clinic details visible
     - Document authenticity checkable

#### Bagian 3: Process Approval
6. **Add academic notes**
   - Aksi: Input academic recommendation:
     - Impact assessment: "Minimal - early in semester"
     - Re-enrollment plan: "Next semester guaranteed"
     - Special conditions: "Must complete online assignments"
   - Verifikasi:
     - Notes saved to request
     - Timestamp recorded
     - Admin name attached

7. **Approve leave request**
   - Aksi: Select "Approve" dan add approval comments
   - Verifikasi:
     - Approval confirmation dialog
     - Final review of details
     - Warning about system changes
     - Approval button active

8. **Confirm system updates**
   - Aksi: Confirm approval
   - Verifikasi:
     - Success notification
     - Status changed to "APPROVED_ACADEMIC"
     - Student enrollment suspended
     - Classes updated (student marked as on-leave)
     - Email sent to student
     - Forwarded to Management for final approval

### Kriteria Sukses
- [ ] Comprehensive review tools available
- [ ] Document verification smooth
- [ ] Eligibility auto-verified
- [ ] Academic impact assessed
- [ ] System updates triggered correctly
- [ ] Notification chain activated

---

## CA-HP-003: Management - Final Approval dan Policy Check

### Informasi Skenario
- **ID Skenario**: CA-HP-003
- **Prioritas**: Tinggi
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `StudentLeaveApprovalTest.managementFinalApproval()`

### Prasyarat
- Management account: `management.director` / `Welcome@YSQ2024`
- Request sudah di-approve oleh Academic Admin (CA-HP-002)
- Management dashboard accessible

### Langkah Pengujian

#### Bagian 1: Management Review
1. **Access Management Dashboard**
   - Aksi: Login sebagai Management
   - Verifikasi:
     - Executive dashboard visible
     - Pending approvals widget
     - Leave requests requiring attention

2. **Review leave statistics**
   - Aksi: Check current leave statistics
   - Verifikasi:
     - Total students on leave: 3
     - Pending requests: 1
     - Leave trend chart visible
     - Impact on class capacity shown

#### Bagian 2: Final Approval
3. **Open academically approved request**
   - Aksi: Open LR-2024-00123
   - Verifikasi:
     - Complete history visible
     - Academic admin notes present
     - Policy compliance indicators green
     - Ready for final approval

4. **Apply final approval**
   - Aksi: Click "Grant Final Approval"
   - Verifikasi:
     - Digital signature required
     - Approval recorded
     - Status: "FULLY_APPROVED"
     - Effective date confirmed

### Kriteria Sukses
- [ ] Management oversight effective
- [ ] Policy compliance checked
- [ ] Final approval binding
- [ ] All stakeholders notified

---

## CA-HP-004: Siswa - Pengajuan Cuti Tengah Semester

### Informasi Skenario
- **ID Skenario**: CA-HP-004
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `StudentLeaveRequestTest.studentMidTermEmergencyLeave()`

### Prasyarat
- Student account: `siswa.fatima` / `Welcome@YSQ2024`
- Semester sudah berjalan > 30% (setelah mid-term)
- Student memiliki attendance record > 75%
- Emergency situation (keluarga sakit/meninggal)

### Data Test
```
Login Siswa:
Username: siswa.fatima
Password: Welcome@YSQ2024

Pengajuan Cuti Darurat:
Jenis: Cuti Darurat Keluarga
Tanggal Mulai: [Tanggal Sekarang + 1 hari]
Tanggal Selesai: [Tanggal Sekarang + 30 hari]
Alasan: Darurat medis keluarga mendesak
Prioritas: TINGGI
Dokumen Pendukung: Surat rawat inap rumah sakit

Status Akademik:
Minggu Berjalan: Minggu 8 dari 16
Kehadiran: 82%
Tugas Dikumpulkan: 6/8
Ujian Tengah Semester: Selesai
```

### Langkah Pengujian

#### Bagian 1: Pengajuan Cuti Darurat
1. **Akses form cuti darurat**
   - Aksi: Login → Layanan Akademik → Cuti Darurat
   - Verifikasi:
     - Form cuti darurat berbeda dari reguler
     - Indikator urgensi tersedia
     - Pemberitahuan pemrosesan cepat

2. **Isi detail cuti darurat**
   - Aksi: Lengkapi form dengan detail darurat
   - Verifikasi:
     - Periode pemberitahuan lebih singkat diterima
     - Kontak darurat diperlukan
     - Opsi notifikasi pembimbing langsung

#### Bagian 2: Penilaian Dampak Akademik
3. **Tinjau dampak akademik**
   - Aksi: Sistem otomatis membuat laporan dampak
   - Verifikasi:
     - Kelas yang terlewat dihitung: 8 sesi
     - Tugas tertunda terdaftar: 2
     - Persyaratan ujian susulan ditampilkan
     - Kondisi kembali ditentukan

4. **Terima kondisi akademik**
   - Aksi: Tinjau dan terima persyaratan susulan
   - Verifikasi:
     - Persetujuan digital tercatat
     - Jadwal susulan sementara
     - Penugasan mentor untuk mengejar ketertinggalan

#### Bagian 3: Pemrosesan Cepat
5. **Kirim untuk peninjauan cepat**
   - Aksi: Kirim dengan tanda prioritas TINGGI
   - Verifikasi:
     - Notifikasi langsung ke admin
     - SLA 24 jam diaktifkan
     - Notifikasi SMS/WhatsApp terkirim
     - Eskalasi jika tidak ditinjau dalam 4 jam

6. **Terima persetujuan bersyarat**
   - Aksi: Periksa status setelah 2 jam
   - Verifikasi:
     - Persetujuan bersyarat diberikan
     - Dokumentasi lengkap diperlukan dalam 7 hari
     - Penangguhan sementara diaktifkan
     - Tanggal kembali terkunci dalam sistem

### Kriteria Sukses
- [ ] Proses darurat dibedakan
- [ ] Alur persetujuan dipercepat
- [ ] Kontinuitas akademik direncanakan
- [ ] Persyaratan susulan jelas
- [ ] Komunikasi segera
- [ ] Sistem mengakomodasi urgensi

### Data Verifikasi Akhir
```
Status Sistem Setelah Cuti Tengah Semester:
- Status Siswa: CUTI_DARURAT
- Status Kelas: Ditangguhkan Sementara
- Kehadiran: Dibekukan pada 82%
- Rencana Susulan: Dibuat dan ditugaskan
- Tanggal Kembali: Ditegakkan sistem
- Notifikasi: Semua pemangku kepentingan diberitahu
```

---

## CA-HP-005: Siswa - Kembali dari Cuti

### Informasi Skenario
- **ID Skenario**: CA-HP-005 (Cuti Akademik - Happy Path - 005)
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `StudentReturnFromLeaveTest.studentInitiateReturn()`

### Prasyarat
- Student account: `siswa.ali` / `Welcome@YSQ2024`
- Student currently on approved leave (from CA-HP-003)
- Return date approaching (within 2 weeks)
- New semester registration open
- No outstanding documents required

### Data Test
```
Login Siswa:
Username: siswa.ali
Password: Welcome@YSQ2024

Status Cuti:
Referensi Cuti: LR-2024-00123
Status: CUTI_AKTIF
Tanggal Kembali Awal: [90 hari dari mulai]
Tanggal Kembali Aktual: [Sesuai jadwal]
Semester Saat Ini: 2024/2025 Genap (Berikutnya)

Persyaratan Kembali:
- Surat keterangan sehat (jika cuti kesehatan)
- Form pendaftaran ulang
- Informasi kontak terbaru
- Pemilihan preferensi kelas
```

### Langkah Pengujian

#### Bagian 1: Memulai Proses Kembali
1. **Login dan periksa status cuti**
   - Aksi: Login sebagai siswa sedang cuti
   - Verifikasi:
     - Dashboard menampilkan status "SEDANG CUTI"
     - Hitung mundur tanggal kembali terlihat
     - Tombol "Mulai Kembali" tersedia
     - Notifikasi pengingat ditampilkan

2. **Akses form Kembali dari Cuti**
   - Aksi: Klik "Mulai Kembali dari Cuti"
   - Verifikasi:
     - Form aplikasi kembali terbuka
     - Detail cuti terisi otomatis
     - Checklist persyaratan ditampilkan
     - Timeline untuk kembali ditampilkan

#### Bagian 2: Lengkapi Persyaratan Kembali
3. **Unggah dokumen yang diperlukan**
   - Aksi: Unggah surat keterangan sehat
   - Verifikasi:
     - Unggah dokumen berhasil
     - Validasi untuk tipe dokumen
     - Preview tersedia
     - Item checklist ditandai selesai

4. **Perbarui informasi siswa**
   - Aksi: Tinjau dan perbarui detail kontak
   - Verifikasi:
     - Info saat ini ditampilkan
     - Field dapat diedit untuk pembaruan
     - Kontak darurat terverifikasi
     - Konfirmasi penyimpanan diterima

5. **Pilih preferensi kelas**
   - Aksi: Pilih jadwal yang disukai untuk semester baru
   - Verifikasi:
     - Slot tersedia ditampilkan
     - Penilaian level ditampilkan
     - Konflik jadwal diperiksa
     - Preferensi tersimpan

#### Bagian 3: Kirim Aplikasi Kembali
6. **Tinjau aplikasi kembali**
   - Aksi: Klik "Tinjau Aplikasi"
   - Verifikasi:
     - Halaman ringkasan dengan semua detail
     - Dokumen terdaftar
     - Pendaftaran semester baru ditampilkan
     - Rencana akademik terlihat

7. **Kirim pengajuan kembali**
   - Aksi: Kirim aplikasi untuk diproses
   - Verifikasi:
     - Konfirmasi diterima
     - Nomor referensi: "RTN-2024-00045"
     - Status: "KEMBALI_TERTUNDA"
     - Notifikasi email terkirim

### Kriteria Sukses
- [ ] Proses kembali dipandu dengan jelas
- [ ] Persyaratan dokumen terpenuhi
- [ ] Pemilihan kelas lancar
- [ ] Komunikasi jelas
- [ ] Timeline dipatuhi

---

## CA-HP-006: Admin Akademik - Proses Kembali Siswa

### Informasi Skenario
- **ID Skenario**: CA-HP-006
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `StudentReturnFromLeaveTest.academicProcessReturn()`

### Prasyarat
- Academic admin logged in
- Return application submitted (CA-HP-005)
- Class availability for returning student
- Academic records accessible

### Data Test
```
Aplikasi Kembali:
Referensi: RTN-2024-00045
Siswa: Muhammad Ali
Level Awal: Tahfiz 2
Durasi Cuti: 3 bulan
Semester Kembali: 2024/2025 Genap
```

### Langkah Pengujian

#### Bagian 1: Tinjau Aplikasi Kembali
1. **Akses aplikasi kembali**
   - Aksi: Navigasi ke "Manajemen Cuti" → "Aplikasi Kembali"
   - Verifikasi:
     - Daftar kembali tertunda
     - RTN-2024-00045 terlihat
     - Indikator prioritas ditampilkan
     - Tanggal jatuh tempo untuk pemrosesan

2. **Buka file kembali siswa**
   - Aksi: Klik pada aplikasi kembali spesifik
   - Verifikasi:
     - Riwayat cuti lengkap
     - Catatan akademik sebelum cuti
     - Dokumen yang diserahkan
     - Surat keterangan sehat (jika diperlukan)

#### Bagian 2: Penilaian Akademik
3. **Evaluasi kontinuitas akademik**
   - Aksi: Tinjau posisi akademik
   - Verifikasi:
     - Nilai sebelumnya ditampilkan
     - Kredit yang diselesaikan ditampilkan
     - Persyaratan tersisa
     - Level penempatan yang disarankan

4. **Tentukan penempatan yang sesuai**
   - Aksi: Nilai apakah siswa melanjutkan level sama atau disesuaikan
   - Verifikasi:
     - Alat penilaian level tersedia
     - Dapat mempertahankan Level: Tahfiz 2
     - Opsi alternatif disajikan
     - Alasan didokumentasikan

5. **Periksa ketersediaan kelas**
   - Aksi: Verifikasi ruang di kelas yang diminta
   - Verifikasi:
     - Pemeriksaan ketersediaan real-time
     - Kompatibilitas jadwal terverifikasi
     - Tidak ada konflik terdeteksi
     - Kursi dipesan sementara

#### Bagian 3: Setujui Kembali
6. **Buat rencana pendaftaran ulang**
   - Aksi: Buat rencana akademik untuk siswa yang kembali
   - Verifikasi:
     - Jadwal mata pelajaran dibuat
     - Mentor ditugaskan
     - Rencana mengejar ketertinggalan jika diperlukan
     - Sesi orientasi dijadwalkan

7. **Setujui aplikasi kembali**
   - Aksi: Berikan persetujuan dengan kondisi
   - Verifikasi:
     - Persetujuan tercatat
     - Status: "KEMBALI_DISETUJUI"
     - Pendaftaran diaktifkan
     - Email selamat datang kembali terkirim

8. **Jadwalkan pertemuan orientasi**
   - Aksi: Atur janji orientasi
   - Verifikasi:
     - Undangan kalender terkirim
     - Pertemuan Zoom/fisik dijadwalkan
     - Mentor diberitahu
     - Materi disiapkan

### Kriteria Sukses
- [ ] Evaluasi komprehensif selesai
- [ ] Penempatan yang sesuai ditentukan
- [ ] Sistem dukungan diaktifkan
- [ ] Re-integrasi lancar direncanakan
- [ ] Semua pemangku kepentingan diberitahu

---

## CA-HP-007: Siswa - Selesaikan Kembali dan Pendaftaran Ulang

### Informasi Skenario
- **ID Skenario**: CA-HP-007
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `StudentReturnFromLeaveTest.completeReenrollment()`

### Prasyarat
- Return approved (CA-HP-006)
- Orientation scheduled
- Payment system ready
- Class registration open

### Langkah Pengujian

#### Bagian 1: Terima Persetujuan
1. **Periksa status kembali**
   - Aksi: Login dan periksa status aplikasi
   - Verifikasi:
     - Status: "KEMBALI_DISETUJUI"
     - Detail pendaftaran terlihat
     - Langkah berikutnya terdaftar
     - Info orientasi ditampilkan

2. **Tinjau penugasan kelas**
   - Aksi: Lihat jadwal yang ditugaskan
   - Verifikasi:
     - Jadwal lengkap ditampilkan
     - Lokasi ruang kelas
     - Penugasan pengajar
     - Jadwal dapat diunduh

#### Bagian 2: Selesaikan Pendaftaran
3. **Konfirmasi pendaftaran kelas**
   - Aksi: Terima kelas yang ditugaskan
   - Verifikasi:
     - Halaman konfirmasi pendaftaran
     - Penerimaan syarat diperlukan
     - Tanda tangan digital ditangkap
     - Nomor konfirmasi diterbitkan

4. **Proses pembayaran (jika berlaku)**
   - Aksi: Selesaikan pembayaran untuk semester baru
   - Verifikasi:
     - Jumlah pembayaran benar
     - Beberapa metode pembayaran
     - Kwitansi dibuat
     - Pembayaran dikonfirmasi

#### Bagian 3: Aktivasi
5. **Hadiri sesi orientasi**
   - Aksi: Ikuti orientasi terjadwal
   - Verifikasi:
     - Kehadiran tercatat
     - Materi diterima
     - Pertanyaan dijawab
     - Konfirmasi siap kembali

6. **Aktifkan status siswa**
   - Aksi: Selesaikan aktivasi akhir
   - Verifikasi:
     - Status: "AKTIF"
     - Status cuti: "SELESAI"
     - Akses penuh dipulihkan
     - Pengingat kelas pertama diatur

### Kriteria Sukses
- [ ] Transisi lancar kembali ke aktif
- [ ] Semua sistem diperbarui
- [ ] Siswa terdaftar penuh
- [ ] Sistem dukungan aktif
- [ ] Siap untuk kelas

---

## CA-HP-008: Sistem - Pemantauan Kembali Otomatis

### Informasi Skenario
- **ID Skenario**: CA-HP-008
- **Prioritas**: Medium
- **Role**: SYSTEM
- **Estimasi Waktu**: Automated/Background
- **Playwright Test**: `SystemLeaveMonitoringTest.automaticReturnReminders()`

### Prasyarat
- Scheduled job running
- Students on leave in database
- Notification service active
- Reporting system operational

### Proses Otomatis

#### Bagian 1: Pengingat Pra-Kembali
1. **Pengingat 30 hari**
   - Pemicu: 30 hari sebelum tanggal kembali
   - Tindakan:
     - Email ke siswa
     - Notifikasi dashboard
     - SMS jika diaktifkan
     - Peringatan admin

2. **Pengingat 14 hari**
   - Pemicu: 2 minggu sebelum kembali
   - Tindakan:
     - Email persyaratan detail
     - Link form kembali
     - Checklist dokumen
     - Tugas tindak lanjut admin

3. **Pengingat mendesak 7 hari**
   - Pemicu: 1 minggu sebelum kembali
   - Tindakan:
     - Notifikasi mendesak
     - Pemicu panggilan telepon untuk admin
     - Notifikasi orang tua (jika di bawah umur)
     - Eskalasi ke manajemen

#### Bagian 2: Pemrosesan Tanggal Kembali
4. **Tanggal kembali tercapai**
   - Pemicu: Tanggal kembali terjadwal
   - Tindakan:
     - Periksa jika kembali dimulai
     - Jika tidak: Kirim peringatan
     - Buat tugas tindak lanjut
     - Tandai untuk peninjauan

5. **Pemantauan masa tenggang**
   - Pemicu: 7 hari setelah tanggal kembali
   - Tindakan:
     - Pemberitahuan akhir dikirim
     - Admin akademik diperingatkan
     - Peringatan penarikan potensial
     - Notifikasi manajemen

#### Bagian 3: Penanganan Pengecualian
6. **Penanganan tidak ada respons**
   - Pemicu: Tidak ada respons setelah masa tenggang
   - Tindakan:
     - Buat pemberitahuan penarikan otomatis
     - Tahan untuk peninjauan manajemen
     - Arsipkan catatan siswa
     - Perbarui statistik

### Poin Pemantauan Sistem
- [ ] Pekerjaan terjadwal berjalan tepat waktu
- [ ] Notifikasi terkirim dengan sukses
- [ ] Rantai eskalasi berfungsi
- [ ] Pembaruan database konsisten
- [ ] Jejak audit lengkap

---

## Poin Integrasi dan Dependensi

### Persyaratan Integrasi Sistem
1. **Sistem Informasi Siswa**
   - Pembaruan status pendaftaran real-time
   - Pembekuan catatan kehadiran
   - Preservasi nilai

2. **Sistem Keuangan**
   - Verifikasi status pembayaran
   - Perhitungan penyesuaian biaya
   - Pemrosesan pengembalian/kredit

3. **Manajemen Kelas**
   - Pembaruan ketersediaan kursi
   - Notifikasi pengajar
   - Penyesuaian jadwal

4. **Sistem Komunikasi**
   - Notifikasi email
   - Peringatan SMS untuk kasus mendesak
   - Notifikasi dashboard

### Status Alur Kerja
```
DRAF → DIKIRIM → MENUNGGU_PENINJAUAN → DISETUJUI_AKADEMIK
→ DISETUJUI_MANAJEMEN → AKTIF → SELESAI → SISWA_KEMBALI

Alur alternatif:
- DITOLAK_AKADEMIK (dengan alasan)
- DITOLAK_MANAJEMEN (dengan alasan)
- DIBATALKAN_OLEH_SISWA
- DIPERPANJANG (untuk kasus medis)
```

### Validasi Aturan Bisnis
1. **Kriteria Kelayakan**
   - Tidak ada cuti dalam 2 semester sebelumnya
   - Kehadiran minimum 75% (dikendorkan untuk medis)
   - Tidak ada tunggakan keuangan
   - Bukan semester akhir

2. **Batasan Durasi**
   - Minimum: 2 minggu
   - Maksimum: 1 semester
   - Perpanjangan: Memerlukan aplikasi baru

3. **Dampak Akademik**
   - Penarikan otomatis jika > 50% semester terlewat
   - Ujian susulan diperlukan
   - Tenggat tugas diperpanjang
   - Mentor ditugaskan untuk mengejar ketertinggalan

### Persyaratan Pelaporan
- Laporan harian pengajuan cuti tertunda
- Dashboard statistik cuti mingguan
- Analisis tren bulanan
- Laporan dampak cuti akhir semester

## Catatan Eksekusi Pengujian

### Persyaratan Setup
```bash
# Seeding database untuk skenario cuti
./mvnw test:db-seed --scenario=student-leave

# Jalankan pengujian cuti spesifik
./mvnw test -Dtest="StudentLeaveRequestTest"
./mvnw test -Dtest="StudentLeaveApprovalTest"
```

### Ekspektasi Performa
- Pengiriman form: < 2 detik
- Pemeriksaan kelayakan: < 1 detik
- Unggah dokumen: < 5 detik untuk 5MB
- Alur persetujuan: < 500ms per langkah
- Pengiriman notifikasi: < 2 detik

### Kasus Edge yang Perlu Dipertimbangkan
1. Siswa mengajukan cuti pada hari terakhir yang memenuhi syarat
2. Beberapa pengajuan cuti dari siswa yang sama
3. Perilaku sistem selama periode pendaftaran batch
4. Permintaan perpanjangan cuti
5. Permintaan kembali lebih awal dari cuti

## Dokumentasi Teknis

### Implikasi Skema Database
```sql
student_leave_requests:
- id (UUID)
- student_id (FK)
- request_type
- start_date
- end_date
- status
- academic_notes
- management_notes
- created_at
- approved_at
- returned_at

student_leave_documents:
- id (UUID)
- request_id (FK)
- document_type
- file_path
- uploaded_at

student_leave_audit:
- Semua perubahan status
- Rantai persetujuan
- Modifikasi sistem
```

### Endpoint API
- POST /api/siswa/pengajuan-cuti
- GET /api/siswa/status-cuti/{id}
- GET /api/admin/cuti-tertunda
- PUT /api/admin/cuti/{id}/setujui
- PUT /api/admin/cuti/{id}/tolak
- GET /api/laporan/statistik-cuti

### Pertimbangan Keamanan
- Pemindaian virus unggah dokumen
- Pembatasan tipe file (hanya PDF, JPG, PNG)
- Penegakan ukuran file maksimum
- Pembatasan rate pada pengiriman
- Jejak audit untuk semua tindakan

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi