# Skenario Pengujian: Pengunduran Diri Siswa - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis Akademik - Pengunduran Diri Siswa
- **Modul**: Manajemen Pengunduran Diri
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario utama (Siswa, Wali, Admin Akademik, Keuangan, Manajemen)

---

## PD-HP-001: Siswa - Pengajuan Pengunduran Diri

### Informasi Skenario
- **ID Skenario**: PD-HP-001 (Pengunduran Diri - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: STUDENT
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `StudentResignationTest.studentSubmitResignation()`

### Prasyarat
- Akun siswa aktif: `siswa.zahra` / `Welcome@YSQ2024`
- Siswa terdaftar di semester berjalan
- Tidak ada tunggakan pembayaran
- Minimal sudah 1 bulan dalam program

### Data Test
```
Login Siswa:
Username: siswa.zahra
Password: Welcome@YSQ2024

Profil Siswa:
Nama: Zahra Amalia
NIS: STD-2024-00078
Level Saat Ini: Tahfiz 1
Semester Berjalan: 2024/2025 Ganjil
Status Pendaftaran: Aktif
Status Pembayaran: Lunas
Tanggal Bergabung: 1 Juli 2024

Detail Pengunduran Diri:
Alasan: Pindah domisili ke luar kota
Tanggal Efektif: [Tanggal Sekarang + 14 hari]
Kategori: Pribadi/Keluarga
Dokumen Pendukung: Surat pindah domisili (PDF)
```

### Langkah Pengujian

#### Bagian 1: Akses Form Pengunduran Diri
1. **Login sebagai Siswa**
   - Aksi: Login dengan kredensial siswa
   - Verifikasi:
     - Login berhasil
     - Dashboard siswa muncul
     - Status aktif terlihat
     - Menu "Layanan Akademik" tersedia

2. **Navigasi ke Pengunduran Diri**
   - Aksi: Klik menu "Layanan Akademik" → "Pengunduran Diri"
   - Verifikasi:
     - Halaman pengunduran diri terbuka
     - Peringatan tentang konsekuensi ditampilkan
     - Form pengajuan tersedia
     - Informasi siswa terisi otomatis

#### Bagian 2: Pengisian Form Pengunduran Diri
3. **Baca dan pahami konsekuensi**
   - Aksi: Baca informasi konsekuensi pengunduran diri
   - Verifikasi:
     - Konsekuensi akademik dijelaskan
     - Konsekuensi keuangan dijelaskan
     - Prosedur pengembalian biaya dijelaskan
     - Checkbox konfirmasi pemahaman tersedia

4. **Isi form pengunduran diri**
   - Aksi: Lengkapi semua field wajib:
     - Alasan pengunduran diri: "Pindah domisili ke luar kota"
     - Kategori: "Pribadi/Keluarga"
     - Tanggal efektif: [Pilih tanggal]
     - Detail alasan: [Jelaskan secara detail]
     - Rencana ke depan: [Opsional]
   - Verifikasi:
     - Validasi tanggal (minimal 14 hari ke depan)
     - Counter karakter untuk detail alasan
     - Kategori alasan tersedia dengan jelas
     - Field opsional ditandai dengan jelas

5. **Unggah dokumen pendukung**
   - Aksi: Unggah surat pindah domisili
   - Verifikasi:
     - Upload berhasil
     - Preview dokumen tersedia
     - Validasi tipe file (PDF, JPG, PNG)
     - Maksimal ukuran file 10MB

#### Bagian 3: Konfirmasi dan Kirim
6. **Isi informasi kontak alternatif**
   - Aksi: Input kontak setelah resign:
     - Email pribadi alternatif
     - Nomor telepon yang akan tetap aktif
     - Alamat baru (jika pindah)
   - Verifikasi:
     - Validasi format email
     - Validasi nomor telepon
     - Field alamat baru opsional

7. **Review pengajuan**
   - Aksi: Klik "Tinjau Pengajuan"
   - Verifikasi:
     - Halaman ringkasan lengkap
     - Semua data terisi benar
     - Dokumen terlampir
     - Pernyataan disclaimer ditampilkan

8. **Kirim pengajuan pengunduran diri**
   - Aksi: Centang persetujuan dan klik "Kirim Pengajuan"
   - Verifikasi:
     - Dialog konfirmasi final muncul
     - Warning tidak bisa dibatalkan
     - Konfirmasi dengan kata sandi diperlukan
     - Pengajuan terkirim
     - Nomor referensi: "RES-2024-00045"
     - Status: "MENUNGGU_PERSETUJUAN_WALI"

### Kriteria Sukses
- [ ] Form pengunduran diri dapat diakses
- [ ] Konsekuensi dijelaskan dengan jelas
- [ ] Validasi form berfungsi
- [ ] Dokumen berhasil diunggah
- [ ] Nomor referensi dibuat
- [ ] Notifikasi terkirim ke wali

### Data Verifikasi Akhir
```
Status Database yang Diharapkan:
- Tabel student_resignations: Entri baru dibuat
- Status: MENUNGGU_PERSETUJUAN_WALI
- Dokumen tersimpan di sistem
- Jejak audit: Pengajuan tercatat
- Antrian notifikasi: Email ke wali siswa
```

---

## PD-HP-002: Wali Siswa - Persetujuan Pengunduran Diri

### Informasi Skenario
- **ID Skenario**: PD-HP-002
- **Prioritas**: Tinggi
- **Role**: PARENT/GUARDIAN
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `StudentResignationTest.parentApproval()`

### Prasyarat
- Akun wali siswa aktif
- Pengajuan dari PD-HP-001 sudah dikirim
- Email notifikasi diterima wali

### Data Test
```
Login Wali:
Username: wali.zahra
Password: Welcome@YSQ2024

Pengajuan Tertunda:
Referensi: RES-2024-00045
Siswa: Zahra Amalia
Alasan: Pindah domisili
Status: MENUNGGU_PERSETUJUAN_WALI
```

### Langkah Pengujian

#### Bagian 1: Akses Pengajuan
1. **Login sebagai Wali**
   - Aksi: Login dengan kredensial wali
   - Verifikasi:
     - Dashboard wali terbuka
     - Notifikasi pengajuan resign terlihat
     - Alert merah untuk perhatian urgent

2. **Buka detail pengajuan**
   - Aksi: Klik notifikasi pengunduran diri
   - Verifikasi:
     - Detail pengajuan terbuka
     - Alasan siswa terlihat jelas
     - Dokumen pendukung dapat dilihat
     - Riwayat akademik siswa ditampilkan

#### Bagian 2: Review dan Persetujuan
3. **Review alasan pengunduran diri**
   - Aksi: Baca detail alasan dan dokumen
   - Verifikasi:
     - Alasan lengkap ditampilkan
     - Timeline pengunduran diri jelas
     - Konsekuensi keuangan dijelaskan
     - Opsi diskusi dengan konselor tersedia

4. **Berikan persetujuan wali**
   - Aksi: Klik "Setujui Pengunduran Diri"
   - Verifikasi:
     - Form persetujuan wali muncul
     - Field komentar wali tersedia
     - Checkbox konfirmasi pemahaman
     - Tanda tangan digital diperlukan

5. **Konfirmasi persetujuan**
   - Aksi: Isi komentar dan konfirmasi
   - Verifikasi:
     - Persetujuan tercatat
     - Status: "DISETUJUI_WALI"
     - Notifikasi ke admin akademik
     - Salinan persetujuan ke email wali

### Kriteria Sukses
- [ ] Wali dapat mengakses pengajuan
- [ ] Informasi lengkap tersedia
- [ ] Proses persetujuan jelas
- [ ] Tanda tangan digital berfungsi
- [ ] Status berubah sesuai alur

---

## PD-HP-003: Admin Akademik - Proses Administratif

### Informasi Skenario
- **ID Skenario**: PD-HP-003
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `StudentResignationTest.academicProcessing()`

### Prasyarat
- Login admin akademik tersedia
- Pengajuan sudah disetujui wali (PD-HP-002)
- Sistem terintegrasi dengan modul lain

### Data Test
```
Login Admin Akademik:
Username: academic.admin1
Password: Welcome@YSQ2024

Pengajuan untuk Diproses:
Referensi: RES-2024-00045
Status: DISETUJUI_WALI
Tanggal Efektif: [14 hari ke depan]
```

### Langkah Pengujian

#### Bagian 1: Review Pengajuan
1. **Akses daftar pengunduran diri**
   - Aksi: Navigasi ke "Pengunduran Diri" → "Daftar Pengajuan"
   - Verifikasi:
     - Daftar pengajuan terbuka
     - Filter status tersedia
     - RES-2024-00045 terlihat
     - Status "DISETUJUI_WALI"

2. **Buka detail pengajuan**
   - Aksi: Klik pada pengajuan RES-2024-00045
   - Verifikasi:
     - Detail lengkap terbuka
     - Riwayat persetujuan terlihat
     - Dokumen dapat diakses
     - Checklist proses tersedia

#### Bagian 2: Proses Administratif
3. **Verifikasi status akademik**
   - Aksi: Klik "Verifikasi Status Akademik"
   - Verifikasi:
     - Status kehadiran: 85%
     - Nilai terakhir tersedia
     - Tidak ada ujian tertunda
     - Tidak ada tugas tertunda

4. **Proses penonaktifan akses**
   - Aksi: Jalankan proses administratif:
     - Nonaktifkan akun siswa (efektif pada tanggal resign)
     - Keluarkan dari semua kelas
     - Arsipkan data akademik
     - Generate surat keterangan
   - Verifikasi:
     - Setiap langkah ter-checklist
     - Jadwal penonaktifan diatur
     - Data arsip dibuat
     - Surat keterangan ter-generate

5. **Koordinasi dengan bagian terkait**
   - Aksi: Kirim notifikasi ke:
     - Bagian Keuangan
     - Pengajar kelas
     - Bagian IT (untuk akses sistem)
   - Verifikasi:
     - Notifikasi terkirim
     - Task dibuat untuk setiap bagian
     - Timeline koordinasi jelas

#### Bagian 3: Finalisasi Akademik
6. **Generate dokumen akhir**
   - Aksi: Buat dokumen:
     - Transkrip nilai terakhir
     - Surat keterangan siswa
     - Laporan kehadiran
   - Verifikasi:
     - Dokumen ter-generate dengan benar
     - Format PDF tersedia
     - Watermark resmi ada
     - Dapat diunduh

7. **Update status pengajuan**
   - Aksi: Tandai "Proses Akademik Selesai"
   - Verifikasi:
     - Status: "PROSES_KEUANGAN"
     - Dokumen akademik final tersimpan
     - Notifikasi ke bagian keuangan
     - Checklist akademik lengkap

### Kriteria Sukses
- [ ] Verifikasi akademik lengkap
- [ ] Proses administratif ter-checklist
- [ ] Dokumen ter-generate dengan benar
- [ ] Koordinasi antar bagian berjalan
- [ ] Status ter-update dengan tepat

---

## PD-HP-004: Keuangan - Penyelesaian Keuangan

### Informasi Skenario
- **ID Skenario**: PD-HP-004
- **Prioritas**: Tinggi
- **Role**: FINANCE
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `StudentResignationTest.financialSettlement()`

### Prasyarat
- Login staf keuangan tersedia
- Proses akademik selesai (PD-HP-003)
- Data pembayaran siswa lengkap

### Data Test
```
Login Keuangan:
Username: staff.finance1
Password: Welcome@YSQ2024

Status Pembayaran Siswa:
Total Dibayar: Rp 15,000,000
Periode Terpakai: 3 bulan
Periode Tersisa: 3 bulan
Refund Eligible: Rp 7,500,000
Potongan Admin: Rp 500,000
```

### Langkah Pengujian

#### Bagian 1: Review Status Keuangan
1. **Akses pengunduran diri di modul keuangan**
   - Aksi: Navigasi ke "Keuangan" → "Pengunduran Diri"
   - Verifikasi:
     - Daftar siswa resign tertunda
     - RES-2024-00045 terlihat
     - Status "PROSES_KEUANGAN"

2. **Review detail pembayaran siswa**
   - Aksi: Buka detail keuangan siswa
   - Verifikasi:
     - Histori pembayaran lengkap
     - Kalkulasi refund otomatis
     - Periode terpakai dihitung benar
     - Tidak ada tunggakan

#### Bagian 2: Kalkulasi Pengembalian
3. **Hitung pengembalian dana**
   - Aksi: Klik "Kalkulasi Refund"
   - Verifikasi:
     - Total pembayaran: Rp 15,000,000
     - Periode terpakai: 3/6 bulan
     - Biaya terpakai: Rp 7,500,000
     - Eligible refund: Rp 7,500,000
     - Biaya admin: Rp 500,000
     - Total refund: Rp 7,000,000

4. **Review kebijakan refund**
   - Aksi: Cek kebijakan yang berlaku
   - Verifikasi:
     - Kebijakan refund ditampilkan
     - Persentase refund sesuai kebijakan
     - Potongan administrasi jelas
     - Approval limits checked

5. **Proses pengembalian dana**
   - Aksi: Input detail pengembalian:
     - Metode: Transfer Bank
     - Bank: BCA
     - No Rekening: [Input]
     - Nama Penerima: [Verifikasi]
   - Verifikasi:
     - Validasi nomor rekening
     - Nama penerima cocok
     - Schedule transfer dapat dipilih

#### Bagian 3: Finalisasi Keuangan
6. **Generate bukti penyelesaian**
   - Aksi: Buat dokumen:
     - Surat penyelesaian keuangan
     - Rincian kalkulasi refund
     - Bukti proses refund
   - Verifikasi:
     - Dokumen ter-generate
     - Stempel digital keuangan
     - Dapat diunduh

7. **Update status keuangan**
   - Aksi: Tandai "Keuangan Selesai"
   - Verifikasi:
     - Status: "MENUNGGU_PERSETUJUAN_AKHIR"
     - Refund terjadwal
     - Notifikasi ke manajemen
     - Dokumen keuangan final

### Kriteria Sukses
- [ ] Kalkulasi refund akurat
- [ ] Kebijakan diterapkan dengan benar
- [ ] Proses refund terjadwal
- [ ] Dokumen keuangan lengkap
- [ ] Status ter-update

---

## PD-HP-005: Manajemen - Persetujuan Akhir

### Informasi Skenario
- **ID Skenario**: PD-HP-005
- **Prioritas**: Tinggi
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `StudentResignationTest.managementApproval()`

### Prasyarat
- Login manajemen tersedia
- Semua proses selesai (akademik & keuangan)
- Dokumen lengkap

### Data Test
```
Login Manajemen:
Username: management.director
Password: Welcome@YSQ2024

Pengajuan Final:
Referensi: RES-2024-00045
Status: MENUNGGU_PERSETUJUAN_AKHIR
Semua Proses: Selesai
```

### Langkah Pengujian

#### Bagian 1: Review Final
1. **Akses dashboard manajemen**
   - Aksi: Login dan akses pending approvals
   - Verifikasi:
     - Dashboard eksekutif terbuka
     - Pending resignations terlihat
     - Metrics pengunduran diri tersedia

2. **Review lengkap pengajuan**
   - Aksi: Buka RES-2024-00045
   - Verifikasi:
     - Timeline lengkap proses
     - Semua dokumen tersedia
     - Checklist semua bagian complete
     - Rekomendasi dari setiap bagian

#### Bagian 2: Persetujuan Akhir
3. **Berikan persetujuan final**
   - Aksi: Klik "Setujui Pengunduran Diri"
   - Verifikasi:
     - Form persetujuan manajemen
     - Field catatan manajemen
     - Tanda tangan digital direktur

4. **Finalisasi pengunduran diri**
   - Aksi: Konfirmasi persetujuan
   - Verifikasi:
     - Status: "DISETUJUI"
     - Tanggal efektif dikonfirmasi
     - Semua pihak ternotifikasi
     - Dokumen final ter-generate

### Kriteria Sukses
- [ ] Review manajemen komprehensif
- [ ] Persetujuan final terekam
- [ ] Notifikasi ke semua pihak
- [ ] Status final: DISETUJUI

---

## PD-HP-006: Sistem - Eksekusi Otomatis Pengunduran Diri

### Informasi Skenario
- **ID Skenario**: PD-HP-006
- **Prioritas**: Medium
- **Role**: SYSTEM
- **Estimasi Waktu**: Automated
- **Playwright Test**: `SystemResignationTest.automaticExecution()`

### Prasyarat
- Job scheduler aktif
- Pengunduran diri disetujui (PD-HP-005)
- Tanggal efektif tercapai

### Proses Otomatis

#### Bagian 1: Eksekusi pada Tanggal Efektif
1. **Penonaktifan akun siswa**
   - Trigger: Tanggal efektif tercapai (00:01)
   - Tindakan:
     - Akun siswa dinonaktifkan
     - Session aktif di-terminate
     - Akses portal ditutup
     - Login diblokir

2. **Update status enrollment**
   - Tindakan:
     - Status siswa: "RESIGNED"
     - Keluarkan dari semua kelas
     - Hapus dari daftar aktif
     - Pindah ke arsip

3. **Pembersihan data**
   - Tindakan:
     - Hapus dari grup kelas
     - Hapus dari mailing list
     - Nonaktifkan notifikasi
     - Archive chat history

#### Bagian 2: Notifikasi Final
4. **Kirim notifikasi penyelesaian**
   - Tindakan:
     - Email ke siswa (alamat alternatif)
     - Email ke wali
     - Konfirmasi ke admin
     - Update dashboard metrics

5. **Generate laporan final**
   - Tindakan:
     - Laporan pengunduran diri bulanan
     - Update statistik retention
     - Alert jika rate tinggi

### Monitoring Points
- [ ] Job berjalan tepat waktu
- [ ] Akun ternonaktifkan dengan benar
- [ ] Data terarsip dengan aman
- [ ] Notifikasi terkirim
- [ ] Metrics ter-update

---

## Poin Integrasi dan Dependensi

### Persyaratan Integrasi Sistem
1. **Sistem Informasi Siswa**
   - Update status real-time
   - Arsip data akademik
   - Generate transkrip final

2. **Sistem Keuangan**
   - Kalkulasi refund otomatis
   - Proses pengembalian dana
   - Laporan keuangan final

3. **Sistem Pembelajaran**
   - Keluarkan dari kelas
   - Nonaktifkan akses materi
   - Backup data tugas

4. **Sistem Komunikasi**
   - Notifikasi multi-channel
   - Update mailing list
   - Archive komunikasi

### Status Alur Kerja
```
DRAFT → DIKIRIM → MENUNGGU_PERSETUJUAN_WALI → DISETUJUI_WALI
→ PROSES_AKADEMIK → PROSES_KEUANGAN → MENUNGGU_PERSETUJUAN_AKHIR
→ DISETUJUI → EFEKTIF → SELESAI

Alur alternatif:
- DITOLAK_WALI
- DITOLAK_MANAJEMEN
- DIBATALKAN_SISWA (sebelum approval wali)
```

### Validasi Aturan Bisnis
1. **Persyaratan Pengunduran Diri**
   - Minimal 1 bulan dalam program
   - Tidak dalam masa ujian akhir
   - Persetujuan wali wajib (untuk siswa < 18 tahun)
   - Tidak ada tunggakan pembayaran

2. **Kebijakan Refund**
   - 100% refund: Sebelum kelas dimulai
   - 50% refund: 1-30 hari setelah mulai
   - 25% refund: 31-60 hari
   - 0% refund: Setelah 60 hari
   - Biaya administrasi: Rp 500,000

3. **Timeline Proses**
   - Notifikasi wali: Immediate
   - Persetujuan wali: Max 7 hari
   - Proses akademik: 2-3 hari
   - Proses keuangan: 2-3 hari
   - Persetujuan akhir: 1-2 hari
   - Total proses: 14 hari kerja

### Dokumen yang Dihasilkan
1. **Untuk Siswa**
   - Surat keterangan siswa
   - Transkrip nilai
   - Sertifikat (jika eligible)
   - Bukti penyelesaian keuangan

2. **Untuk Internal**
   - Laporan pengunduran diri
   - Analisis alasan
   - Exit interview notes
   - Rekomendasi perbaikan

## Catatan Eksekusi Pengujian

### Persyaratan Setup
```bash
# Seeding database untuk skenario pengunduran diri
./mvnw test:db-seed --scenario=student-resignation

# Jalankan pengujian spesifik
./mvnw test -Dtest="StudentResignationTest"
./mvnw test -Dtest="SystemResignationTest"
```

### Ekspektasi Performa
- Pengiriman form: < 2 detik
- Kalkulasi refund: < 1 detik
- Generate dokumen: < 3 detik
- Proses persetujuan: < 500ms per langkah
- Eksekusi otomatis: < 5 detik

### Kasus Edge yang Perlu Dipertimbangkan
1. Siswa mengundurkan diri di tengah semester
2. Multiple resignation dalam satu keluarga
3. Resignation kemudian ingin kembali
4. Dispute kalkulasi refund
5. Resignation karena force majeure

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi