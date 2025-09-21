# Skenario Pengujian: Cuti Akademik Siswa - Alternate Path

## Informasi Umum
- **Kategori**: Proses Bisnis Akademik - Manajemen Cuti (Kasus Edge & Penanganan Error)
- **Modul**: Manajemen Cuti Siswa - Alur Alternatif
- **Tipe Skenario**: Alternate Path (Jalur Alternatif & Kasus Error)
- **Total Skenario**: 8 skenario validasi dan kasus edge

---

## CA-AP-001: Siswa - Pengajuan Cuti Ditolak (Kelayakan Gagal)

### Informasi Skenario
- **ID Skenario**: CA-AP-001 (Cuti Akademik - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 5-6 menit
- **Playwright Test**: `StudentLeaveValidationTest.rejectIneligibleStudent()`

### Prasyarat
- Student account dengan masalah eligibility
- Salah satu dari: Outstanding payment, Low attendance, Recent leave

### Data Test
```
Login Siswa:
Username: siswa.bermasalah
Password: Welcome@YSQ2024

Masalah:
- Tunggakan Pembayaran: Rp 2,500,000
- Kehadiran: 65% (Di bawah minimum 75%)
- Cuti Sebelumnya: Semester lalu
```

### Langkah Pengujian

1. **Coba ajukan cuti**
   - Aksi: Akses form pengajuan cuti
   - Verifikasi:
     - Pemeriksaan kelayakan gagal
     - Pesan error jelas ditampilkan
     - Masalah spesifik terdaftar
     - Tidak bisa melanjutkan aplikasi

2. **Tinjau alasan penolakan**
   - Verifikasi:
     - Tunggakan pembayaran ditampilkan dengan jumlah
     - Persentase kehadiran disorot
     - Riwayat cuti sebelumnya ditampilkan
     - Link untuk menyelesaikan masalah disediakan

### Kriteria Sukses
- [ ] Siswa tidak layak tidak bisa mengajukan
- [ ] Umpan balik jelas tentang masalah
- [ ] Jalur penyelesaian disediakan
- [ ] Sistem mencegah upaya bypass

---

## CA-AP-002: Admin Akademik - Tolak Cuti Karena Dampak Akademik

### Informasi Skenario
- **ID Skenario**: CA-AP-002
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `LeaveApprovalValidationTest.rejectHighImpactLeave()`

### Prasyarat
- Leave request during critical academic period
- Student in final semester or near exams

### Data Test
```
Pengajuan Periode Kritis:
Waktu: 2 minggu sebelum ujian akhir
Level Siswa: Semester akhir
Dampak Akademik: TINGGI
Rekomendasi: TOLAK
```

### Langkah Pengujian

1. **Tinjau pengajuan dampak tinggi**
   - Aksi: Buka pengajuan cuti selama periode ujian
   - Verifikasi:
     - Indikator peringatan terlihat
     - Konflik kalender akademik ditampilkan
     - Penilaian dampak: "KRITIS"

2. **Tolak dengan alasan akademik**
   - Aksi: Pilih "Tolak" dengan alasan detail
   - Verifikasi:
     - Alasan penolakan wajib
     - Saran alternatif diperlukan
     - Siswa diberitahu segera
     - Proses banding dijelaskan

### Kriteria Sukses
- [ ] Integritas akademik dilindungi
- [ ] Alasan penolakan jelas
- [ ] Opsi alternatif disediakan
- [ ] Proses banding tersedia

---

## CA-AP-003: Siswa - Kembali Terlambat dari Cuti (Lewat Waktu)

### Informasi Skenario
- **ID Skenario**: CA-AP-003
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `StudentReturnValidationTest.handleLateReturn()`

### Prasyarat
- Student on leave with return date passed
- Grace period expired (7 days)
- No communication received

### Data Test
```
Cuti Lewat Waktu:
Kembali Awal: [7 hari lalu]
Masa Tenggang: Habis
Status Saat Ini: LEWAT_WAKTU
Konsekuensi: Potensi penarikan
```

### Langkah Pengujian

1. **Login setelah tanggal kembali**
   - Aksi: Siswa coba login setelah tanggal kembali
   - Verifikasi:
     - Banner peringatan ditampilkan
     - Akun dibatasi sebagian
     - Pesan tindakan mendesak diperlukan
     - Fungsionalitas terbatas

2. **Kirim pengajuan kembali terlambat**
   - Aksi: Akses form kembali darurat
   - Verifikasi:
     - Form kembali terlambat berbeda
     - Penjelasan wajib
     - Dokumen pendukung diperlukan
     - Pemberitahuan penalti ditampilkan

3. **Terima konsekuensi**
   - Aksi: Akui penalti kembali terlambat
   - Verifikasi:
     - Penalti akademik terdaftar
     - Implikasi keuangan ditampilkan
     - Kondisi penerimaan kembali
     - Persetujuan digital diperlukan

### Kriteria Sukses
- [ ] Kembali terlambat ditangani dengan tepat
- [ ] Konsekuensi dikomunikasikan dengan jelas
- [ ] Proses darurat tersedia
- [ ] Jejak audit terpelihara

---

## CA-AP-004: Sistem - Permintaan Perpanjangan Cuti (Darurat Medis)

### Informasi Skenario
- **ID Skenario**: CA-AP-004
- **Prioritas**: Tinggi
- **Role**: STUDENT → ACADEMIC_ADMIN
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `LeaveExtensionTest.processMedicalExtension()`

### Prasyarat
- Student currently on leave
- Medical emergency requiring extension
- Original leave period ending soon

### Data Test
```
Cuti Saat Ini:
Jenis: Cuti Kesehatan
Tanggal Selesai: [5 hari dari sekarang]
Permintaan Perpanjangan: +60 hari
Alasan: Komplikasi operasi
Dokumentasi: Surat rumah sakit
```

### Langkah Pengujian

#### Sisi Siswa
1. **Minta perpanjangan cuti**
   - Aksi: Akses form perpanjangan sebelum cuti berakhir
   - Verifikasi:
     - Form perpanjangan dapat diakses
     - Detail cuti saat ini ditampilkan
     - Batas perpanjangan maksimum ditampilkan
     - Unggah dokumen diperlukan

2. **Kirim dokumentasi medis**
   - Aksi: Unggah dokumentasi rumah sakit
   - Verifikasi:
     - Dokumen medis divalidasi
     - Penilaian tingkat keparahan
     - Prioritas otomatis: TINGGI
     - Pemrosesan cepat dipicu

#### Sisi Admin
3. **Tinjau permintaan perpanjangan**
   - Aksi: Admin akademik meninjau perpanjangan mendesak
   - Verifikasi:
     - Dokumentasi medis terverifikasi
     - Perpanjangan dibenarkan
     - Rencana akademik disesuaikan
     - Batas maksimum diperiksa

4. **Setujui dengan kondisi**
   - Aksi: Berikan perpanjangan dengan persyaratan
   - Verifikasi:
     - Perpanjangan diberikan
     - Tanggal kembali baru ditetapkan
     - Kondisi didokumentasikan
     - Tindak lanjut medis diperlukan

### Kriteria Sukses
- [ ] Proses perpanjangan lancar untuk darurat
- [ ] Persyaratan dokumentasi terpenuhi
- [ ] Kontinuitas akademik terpelihara
- [ ] Sistem dukungan diaktifkan

---

## CA-AP-005: Keuangan - Masalah Pembayaran Selama Cuti

### Informasi Skenario
- **ID Skenario**: CA-AP-005
- **Prioritas**: Medium
- **Role**: STUDENT → FINANCE
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `LeaveFinanceTest.handlePaymentDuringLeave()`

### Prasyarat
- Student on approved leave
- Payment installment due during leave
- Multiple payment scenarios

### Data Test
```
Skenario Pembayaran:
1. Pembebasan penuh selama cuti
2. Pengaturan pembayaran tertunda
3. Pembayaran sebagian diperlukan
4. Kelanjutan beasiswa
```

### Langkah Pengujian

1. **Periksa status pembayaran selama cuti**
   - Aksi: Siswa mengakses portal pembayaran
   - Verifikasi:
     - Status cuti tercermin
     - Penyesuaian pembayaran ditampilkan
     - Opsi disajikan dengan jelas
     - Tenggat waktu disesuaikan

2. **Pilih pengaturan pembayaran**
   - Aksi: Pilih opsi pembayaran tertunda
   - Verifikasi:
     - Syarat penangguhan jelas
     - Jadwal baru dibuat
     - Tidak ada penalti selama cuti
     - Perjanjian dicatat

3. **Persetujuan keuangan**
   - Aksi: Staf keuangan meninjau pengaturan
   - Verifikasi:
     - Persetujuan otomatis untuk kasus cuti
     - Sistem memperbarui penagihan
     - Konfirmasi dikirim
     - Tidak ada gangguan layanan

### Kriteria Sukses
- [ ] Kontinuitas keuangan terpelihara
- [ ] Opsi pembayaran jelas
- [ ] Tidak ada penalti selama cuti disetujui
- [ ] Proses kembali lancar

---

## CA-AP-006: Manajemen - Penanganan Pengecualian Kebijakan

### Informasi Skenario
- **ID Skenario**: CA-AP-006
- **Prioritas**: Medium
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `LeavePolicyExceptionTest.handleSpecialCases()`

### Prasyarat
- Special circumstances requiring policy override
- Management approval needed
- Documentation of exception

### Data Test
```
Kasus Pengecualian:
Siswa: Siswa internasional
Masalah: Keterlambatan perpanjangan visa
Kebijakan Normal: Maks 3 bulan
Permintaan Pengecualian: 4 bulan
Justifikasi: Keterlambatan kedutaan
```

### Langkah Pengujian

1. **Eskalasi ke manajemen**
   - Aksi: Admin akademik eskalasi kasus khusus
   - Verifikasi:
     - Form eskalasi lengkap
     - Dokumen pendukung terlampir
     - Konflik kebijakan disorot
     - Manajemen diberitahu

2. **Peninjauan manajemen**
   - Aksi: Direktur meninjau permintaan pengecualian
   - Verifikasi:
     - Konteks lengkap disediakan
     - Implikasi kebijakan ditampilkan
     - Pemeriksaan preseden tersedia
     - Alat keputusan dapat diakses

3. **Berikan pengecualian**
   - Aksi: Setujui dengan kondisi khusus
   - Verifikasi:
     - Pengecualian didokumentasikan
     - Kondisi ditentukan
     - Tidak menetapkan preseden dicatat
     - Jejak audit lengkap

### Kriteria Sukses
- [ ] Pengecualian ditangani dengan hati-hati
- [ ] Dokumentasi menyeluruh
- [ ] Manajemen preseden
- [ ] Persyaratan audit terpenuhi

---

## CA-AP-007: Siswa - Pengajuan Cuti Dibatalkan

### Informasi Skenario
- **ID Skenario**: CA-AP-007
- **Prioritas**: Medium
- **Role**: STUDENT
- **Estimasi Waktu**: 5-6 menit
- **Playwright Test**: `LeaveCancellationTest.studentCancelRequest()`

### Prasyarat
- Leave request submitted but not yet approved
- Change in circumstances
- Within cancellation window

### Data Test
```
Skenario Pembatalan:
Status Pengajuan: MENUNGGU_PENINJAUAN
Dikirim: 2 hari lalu
Alasan Pembatalan: Masalah kesehatan teratasi
Jendela Pembatalan: Terbuka (dalam 48 jam)
```

### Langkah Pengujian

1. **Akses pengajuan tertunda**
   - Aksi: Lihat pengajuan cuti yang dikirim
   - Verifikasi:
     - Tombol batal tersedia
     - Status masih tertunda
     - Aturan pembatalan ditampilkan
     - Batas waktu ditampilkan

2. **Batalkan pengajuan**
   - Aksi: Klik batal dan berikan alasan
   - Verifikasi:
     - Alasan pembatalan diperlukan
     - Dialog konfirmasi
     - Pembaruan status segera
     - Notifikasi ke admin

3. **Verifikasi pembatalan**
   - Verifikasi:
     - Status: DIBATALKAN_OLEH_SISWA
     - Pendaftaran tidak berubah
     - Tidak ada dampak akademik
     - Email konfirmasi terkirim

### Kriteria Sukses
- [ ] Proses pembatalan jelas
- [ ] Batas waktu ditegakkan
- [ ] Pembalikan status bersih
- [ ] Notifikasi yang tepat dikirim

---

## CA-AP-008: Sistem - Integritas Data Selama Transisi Cuti

### Informasi Skenario
- **ID Skenario**: CA-AP-008
- **Prioritas**: High
- **Role**: SYSTEM
- **Estimasi Waktu**: Automated Testing
- **Playwright Test**: `LeaveDataIntegrityTest.validateTransitions()`

### Skenario Pengujian

#### Operasi Bersamaan
1. **Pembaruan status bersamaan**
   - Skenario: Admin menyetujui saat siswa membatalkan
   - Verifikasi:
     - Kondisi race ditangani
     - Tindakan pertama menang
     - Tindakan kedua ditolak dengan baik
     - Konsistensi data terpelihara

2. **Kegagalan sistem selama transisi**
   - Skenario: Koneksi database terputus selama persetujuan
   - Verifikasi:
     - Rollback transaksi lengkap
     - Tidak ada pembaruan parsial
     - Status tetap tidak berubah
     - Error dicatat dengan benar

#### Validasi Data
3. **Rentang tanggal tidak valid**
   - Test: Tanggal selesai sebelum tanggal mulai
   - Verifikasi:
     - Validasi mencegah pengiriman
     - Pesan error jelas
     - Status form dipertahankan
     - Tidak ada polusi database

4. **Konflik pendaftaran**
   - Test: Cuti selama kehadiran wajib
   - Verifikasi:
     - Deteksi konflik berfungsi
     - Peringatan diberikan
     - Override memerlukan otorisasi
     - Jejak audit lengkap

### Pemeriksaan Integritas Sistem
- [ ] Kepatuhan ACID terpelihara
- [ ] Integritas referensial dipertahankan
- [ ] Jejak audit lengkap
- [ ] Prosedur pemulihan berfungsi
- [ ] Performa dalam SLA

---

## Referensi Pesan Error

### Pesan untuk Siswa
```
ERR_CUTI_001: "Anda tidak memenuhi syarat cuti karena tunggakan pembayaran"
ERR_CUTI_002: "Kehadiran di bawah persyaratan minimum (75%)"
ERR_CUTI_003: "Cuti sudah diambil dalam 2 semester sebelumnya"
ERR_CUTI_004: "Tidak dapat mengajukan cuti selama periode ujian"
ERR_CUTI_005: "Tanggal kembali telah lewat. Silakan ajukan permintaan kembali terlambat"
```

### Pesan untuk Admin
```
ADM_CUTI_001: "Siswa memiliki kewajiban akademik kritis"
ADM_CUTI_002: "Durasi cuti melebihi maksimum kebijakan"
ADM_CUTI_003: "Dokumentasi yang diberikan tidak mencukupi"
ADM_CUTI_004: "Konflik dengan kalender akademik terdeteksi"
ADM_CUTI_005: "Cuti sebelumnya tidak ditutup dengan benar"
```

### Pesan Sistem
```
SYS_CUTI_001: "Transaksi gagal - silakan coba lagi"
SYS_CUTI_002: "Modifikasi bersamaan terdeteksi"
SYS_CUTI_003: "Batasan integritas data dilanggar"
SYS_CUTI_004: "Status alur kerja tidak valid untuk operasi"
SYS_CUTI_005: "Layanan notifikasi tidak tersedia"
```

---

## Tolok Ukur Performa

### Target Waktu Respons
- Muat form: < 1 detik
- Pemeriksaan kelayakan: < 2 detik
- Unggah dokumen: < 5 detik (file 5MB)
- Pembaruan status: < 500ms
- Pembuatan laporan: < 3 detik

### Pengujian Pengguna Bersamaan
- 50 aplikasi cuti bersamaan
- 20 persetujuan bersamaan
- 100 pemeriksaan status per detik
- Tidak ada degradasi performa
- Nol korupsi data

---

## Pertimbangan Keamanan

### Kontrol Akses
- Izin berbasis peran ditegakkan
- Akses dokumen dibatasi
- Jejak audit anti-tamper
- Manajemen sesi aman

### Perlindungan Data
- PII terenkripsi saat istirahat
- Dokumen disimpan dengan aman
- Log akses dipelihara
- Kepatuhan GDPR bila berlaku

---

## Prosedur Pemulihan

### Skenario Kegagalan
1. **Alur persetujuan macet**
   - Proses intervensi manual
   - Kemampuan override admin
   - Notifikasi ke dukungan IT
   - Resolusi maksimum: 4 jam

2. **Kegagalan unggah dokumen**
   - Mekanisme coba lagi (3 upaya)
   - Metode unggah alternatif
   - Pengiriman dokumen manual
   - Tiket dukungan dibuat otomatis

3. **Kegagalan integrasi**
   - Fallback ke proses manual
   - Rekonsiliasi batch tersedia
   - Alat pemulihan sinkronisasi data
   - Waktu pemulihan maksimum: 24 jam

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi
**Dokumen Terkait**:
- cuti-akademik-happy-path.md
- Buku Panduan Siswa - Bagian Kebijakan Cuti
- Panduan Admin Sistem - Manajemen Cuti