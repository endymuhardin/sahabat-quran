# Skenario Pengujian: Pengunduran Diri Siswa - Alternate Path

## Informasi Umum
- **Kategori**: Proses Bisnis Akademik - Pengunduran Diri (Kasus Edge & Penanganan Error)
- **Modul**: Manajemen Pengunduran Diri - Alur Alternatif
- **Tipe Skenario**: Alternate Path (Jalur Alternatif & Kasus Error)
- **Total Skenario**: 8 skenario validasi dan kasus edge

---

## PD-AP-001: Wali - Penolakan Pengunduran Diri

### Informasi Skenario
- **ID Skenario**: PD-AP-001 (Pengunduran Diri - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: PARENT/GUARDIAN
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `ResignationValidationTest.parentRejectsResignation()`

### Prasyarat
- Pengajuan pengunduran diri dari siswa ada
- Wali tidak setuju dengan keputusan siswa
- Konseling tersedia sebagai alternatif

### Data Test
```
Login Wali:
Username: wali.ahmad
Password: Welcome@YSQ2024

Pengajuan:
Siswa: Ahmad Fauzi
Alasan: Kesulitan mengikuti program
Status: MENUNGGU_PERSETUJUAN_WALI
Keputusan Wali: TOLAK
```

### Langkah Pengujian

1. **Review pengajuan siswa**
   - Aksi: Wali membuka pengajuan resign
   - Verifikasi:
     - Detail alasan terlihat
     - Opsi tolak tersedia
     - Form konseling alternatif ada

2. **Tolak pengunduran diri**
   - Aksi: Pilih "Tolak Pengunduran Diri"
   - Verifikasi:
     - Form penolakan muncul
     - Alasan penolakan wajib diisi
     - Opsi request konseling tersedia
     - Saran alternatif dapat diinput

3. **Request sesi konseling**
   - Aksi: Pilih "Minta Sesi Konseling"
   - Verifikasi:
     - Jadwal konseling ter-create
     - Notifikasi ke konselor
     - Status: "DITOLAK_WALI"
     - Siswa ternotifikasi

### Kriteria Sukses
- [ ] Wali dapat menolak pengajuan
- [ ] Alternatif konseling tersedia
- [ ] Komunikasi keluarga difasilitasi
- [ ] Status ter-update dengan benar

---

## PD-AP-002: Siswa - Pembatalan Pengunduran Diri

### Informasi Skenario
- **ID Skenario**: PD-AP-002
- **Prioritas**: Medium
- **Role**: STUDENT
- **Estimasi Waktu**: 5-6 menit
- **Playwright Test**: `ResignationValidationTest.studentCancelsResignation()`

### Prasyarat
- Pengajuan sudah dikirim tapi belum disetujui wali
- Siswa berubah pikiran
- Dalam periode pembatalan (48 jam)

### Data Test
```
Login Siswa:
Username: siswa.sarah
Password: Welcome@YSQ2024

Pengajuan:
Referensi: RES-2024-00067
Status: MENUNGGU_PERSETUJUAN_WALI
Dikirim: 24 jam lalu
Aksi: BATALKAN
```

### Langkah Pengujian

1. **Akses pengajuan aktif**
   - Aksi: Buka status pengunduran diri
   - Verifikasi:
     - Status masih pending wali
     - Tombol batalkan tersedia
     - Timer pembatalan terlihat
     - Warning pembatalan ditampilkan

2. **Batalkan pengunduran diri**
   - Aksi: Klik "Batalkan Pengajuan"
   - Verifikasi:
     - Konfirmasi pembatalan muncul
     - Alasan pembatalan required
     - Kata sandi diperlukan

3. **Konfirmasi pembatalan**
   - Aksi: Input alasan dan konfirmasi
   - Verifikasi:
     - Status: "DIBATALKAN_SISWA"
     - Notifikasi ke wali
     - Status siswa tetap aktif
     - Jejak audit tercatat

### Kriteria Sukses
- [ ] Pembatalan dalam waktu yang ditentukan
- [ ] Status kembali normal
- [ ] Notifikasi terkirim
- [ ] Tidak ada dampak akademik

---

## PD-AP-003: Keuangan - Dispute Kalkulasi Refund

### Informasi Skenario
- **ID Skenario**: PD-AP-003
- **Prioritas**: Tinggi
- **Role**: FINANCE → PARENT
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `ResignationFinanceTest.refundDispute()`

### Prasyarat
- Proses refund sudah dikalkulasi
- Wali tidak setuju dengan jumlah refund
- Kebijakan khusus mungkin berlaku

### Data Test
```
Kalkulasi Awal:
Total Bayar: Rp 20,000,000
Terpakai: 2.5 bulan dari 6 bulan
Refund Standard: Rp 11,000,000

Dispute:
Klaim Wali: Harusnya Rp 13,000,000
Alasan: Force majeure (sakit berkepanjangan)
Dokumen: Surat keterangan dokter
```

### Langkah Pengujian

1. **Wali ajukan dispute**
   - Aksi: Submit dispute refund amount
   - Verifikasi:
     - Form dispute tersedia
     - Upload bukti pendukung
     - Kalkulasi alternatif dapat diajukan
     - Eskalasi ke manajemen

2. **Review oleh keuangan**
   - Aksi: Staf keuangan review dispute
   - Verifikasi:
     - Dokumen pendukung valid
     - Kebijakan force majeure checked
     - Rekomendasi adjustment dibuat
     - Approval dari supervisor required

3. **Adjustment approval**
   - Aksi: Supervisor approve adjustment
   - Verifikasi:
     - Refund ter-adjust: Rp 12,500,000
     - Penjelasan adjustment clear
     - Wali ternotifikasi
     - Dokumen revisi ter-generate

### Kriteria Sukses
- [ ] Dispute dapat diajukan
- [ ] Review process transparan
- [ ] Adjustment ter-dokumentasi
- [ ] Resolusi memuaskan kedua pihak

---

## PD-AP-004: Sistem - Pengunduran Diri Massal

### Informasi Skenario
- **ID Skenario**: PD-AP-004
- **Prioritas**: High
- **Role**: SYSTEM → MANAGEMENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `ResignationSystemTest.massResignationHandling()`

### Prasyarat
- Multiple siswa resign bersamaan (> 5)
- Kemungkinan ada masalah sistemik
- Alert manajemen diperlukan

### Data Test
```
Resignation Spike:
Periode: 1 minggu
Jumlah: 8 siswa
Pattern: Semua dari level yang sama
Alasan Dominan: Kurikulum terlalu berat
```

### Langkah Pengujian

1. **Sistem deteksi anomali**
   - Trigger: > 5 resignation dalam seminggu
   - Verifikasi:
     - Alert otomatis ter-trigger
     - Dashboard warning muncul
     - Email ke manajemen
     - Report auto-generated

2. **Analisis pattern**
   - Aksi: Sistem analyze resignation pattern
   - Verifikasi:
     - Common factors identified
     - Level/kelas analysis
     - Alasan kategorisasi
     - Trend visualization

3. **Management intervention**
   - Aksi: Manajemen review dan action
   - Verifikasi:
     - Emergency meeting scheduled
     - Root cause investigation
     - Retention program activated
     - Follow-up dengan siswa lain

### Kriteria Sukses
- [ ] Early warning system berfungsi
- [ ] Pattern analysis akurat
- [ ] Management dapat quick response
- [ ] Preventive action ter-trigger

---

## PD-AP-005: Admin - Pengunduran Diri Tengah Ujian

### Informasi Skenario
- **ID Skenario**: PD-AP-005
- **Prioritas**: High
- **Role**: ACADEMIC_ADMIN
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `ResignationAcademicTest.midExamResignation()`

### Prasyarat
- Siswa resign di tengah periode ujian
- Ujian sudah sebagian diikuti
- Nilai partial perlu di-handle

### Data Test
```
Status Ujian:
Total Mata Pelajaran: 6
Ujian Selesai: 3
Ujian Pending: 3
Nilai Tersedia: 3 dari 6
Keputusan: Resign immediate
```

### Langkah Pengujian

1. **Handle incomplete exams**
   - Aksi: Admin review exam status
   - Verifikasi:
     - Partial grades visible
     - Options untuk completion
     - Certificate eligibility checked
     - Special transcript notation

2. **Generate partial transcript**
   - Aksi: Create transcript dengan nilai partial
   - Verifikasi:
     - Completed subjects listed
     - Incomplete marked clearly
     - Official notation added
     - Partial certificate (jika eligible)

3. **Academic closure**
   - Aksi: Close academic record
   - Verifikasi:
     - Status: "INCOMPLETE_RESIGNATION"
     - Partial achievements recorded
     - Future re-enrollment noted
     - Special case documented

### Kriteria Sukses
- [ ] Partial progress ter-dokumentasi
- [ ] Fair assessment untuk completed work
- [ ] Clear incomplete status
- [ ] Re-enrollment path available

---

## PD-AP-006: Manajemen - Penolakan Pengunduran Diri

### Informasi Skenario
- **ID Skenario**: PD-AP-006
- **Prioritas**: Medium
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `ResignationManagementTest.rejectResignation()`

### Prasyarat
- Siswa berprestasi ingin resign
- Manajemen ingin retain
- Scholarship atau incentive available

### Data Test
```
Siswa:
Nama: Fatima Az-Zahra
Prestasi: Hafalan 10 Juz
Ranking: Top 5%
Alasan Resign: Kesulitan finansial
Tawaran: Beasiswa penuh
```

### Langkah Pengujian

1. **Management review high-performer**
   - Aksi: Flag high-value student resignation
   - Verifikasi:
     - Performance metrics shown
     - Retention value calculated
     - Alternative options available
     - Authority to offer incentives

2. **Offer retention package**
   - Aksi: Create special offer
   - Verifikasi:
     - Scholarship offer generated
     - Flexible schedule option
     - Mentoring program offered
     - Deadline untuk response

3. **Negotiate dengan wali dan siswa**
   - Aksi: Schedule retention meeting
   - Verifikasi:
     - Meeting scheduled
     - Offer documented
     - Decision timeline set
     - Follow-up process defined

### Kriteria Sukses
- [ ] High-performers identified
- [ ] Retention offers available
- [ ] Negotiation process exists
- [ ] Win-back capability

---

## PD-AP-007: Siswa - Request Kembali Setelah Resign

### Informasi Skenario
- **ID Skenario**: PD-AP-007
- **Prioritas**: Medium
- **Role**: FORMER_STUDENT
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `ResignationReentryTest.formerStudentReapply()`

### Prasyarat
- Siswa sudah resign (3 bulan lalu)
- Ingin kembali ke program
- Re-admission policy exists

### Data Test
```
Former Student:
Nama: Muhammad Rizki
Resign Date: 3 bulan lalu
Alasan Resign: Pindah kota
Current: Kembali ke kota asal
Request: Re-enrollment
```

### Langkah Pengujian

1. **Submit re-enrollment request**
   - Aksi: Former student apply kembali
   - Verifikasi:
     - Re-enrollment form available
     - Previous record retrieved
     - Gap period acknowledged
     - New assessment required

2. **Academic evaluation**
   - Aksi: Assess readiness to return
   - Verifikasi:
     - Previous performance reviewed
     - Placement test scheduled
     - Level determination process
     - Probation period defined

3. **Re-admission decision**
   - Aksi: Process re-admission
   - Verifikasi:
     - Conditional acceptance
     - New student ID issued
     - Previous credits evaluated
     - Fresh start dengan conditions

### Kriteria Sukses
- [ ] Re-enrollment path exists
- [ ] Fair assessment process
- [ ] Previous record considered
- [ ] Clear re-entry conditions

---

## PD-AP-008: Sistem - Data Integrity Saat Resign

### Informasi Skenario
- **ID Skenario**: PD-AP-008
- **Prioritas**: High
- **Role**: SYSTEM
- **Estimasi Waktu**: Automated Testing
- **Playwright Test**: `ResignationIntegrityTest.validateDataConsistency()`

### Skenario Pengujian

#### Concurrent Updates
1. **Multiple approvals bersamaan**
   - Skenario: Wali approve saat admin proses
   - Verifikasi:
     - Lock mechanism berfungsi
     - Sequential processing
     - No data corruption
     - Status consistency maintained

2. **System failure during resignation**
   - Skenario: Database error saat proses
   - Verifikasi:
     - Transaction rollback
     - Partial update prevented
     - Recovery process available
     - Audit trail intact

#### Data Validation
3. **Refund exceeds payment**
   - Test: Calculate refund > total paid
   - Verifikasi:
     - Validation prevents error
     - Alert to finance
     - Manual review triggered
     - Cap at maximum paid

4. **Duplicate resignation**
   - Test: Student submits twice
   - Verifikasi:
     - Duplicate prevented
     - Existing application shown
     - No double processing
     - Clear user message

### Integrity Checks
- [ ] Referential integrity maintained
- [ ] Financial reconciliation accurate
- [ ] Archive process complete
- [ ] No orphaned records
- [ ] Audit trail unbroken

---

## Referensi Pesan Error

### Pesan untuk Siswa
```
ERR_RESIGN_001: "Anda belum memenuhi periode minimal (1 bulan)"
ERR_RESIGN_002: "Pengajuan resign sudah ada dan sedang diproses"
ERR_RESIGN_003: "Tidak dapat resign selama periode ujian akhir"
ERR_RESIGN_004: "Persetujuan wali diperlukan untuk siswa di bawah 18 tahun"
ERR_RESIGN_005: "Ada tunggakan pembayaran yang harus diselesaikan"
```

### Pesan untuk Admin
```
ADM_RESIGN_001: "Siswa memiliki ujian yang sedang berlangsung"
ADM_RESIGN_002: "Kalkulasi refund melebihi pembayaran total"
ADM_RESIGN_003: "Dokumen pendukung tidak lengkap"
ADM_RESIGN_004: "Proses akademik belum selesai"
ADM_RESIGN_005: "Approval wali kadaluarsa (> 7 hari)"
```

### Pesan Sistem
```
SYS_RESIGN_001: "Transaksi gagal - data inkonsisten"
SYS_RESIGN_002: "Proses sedang berjalan - coba lagi nanti"
SYS_RESIGN_003: "Integrasi keuangan timeout"
SYS_RESIGN_004: "Batch job resignation gagal"
SYS_RESIGN_005: "Archive process incomplete"
```

---

## Tolok Ukur Performa

### Target Waktu Respons
- Form submission: < 2 detik
- Refund calculation: < 1 detik
- Document generation: < 5 detik
- Status update: < 500ms
- Batch processing: < 10 detik untuk 10 records

### Concurrent Testing
- 20 resignation submissions bersamaan
- 10 approval processes paralel
- 50 status checks per detik
- No deadlock atau timeout
- Data integrity 100%

---

## Pertimbangan Keamanan

### Access Control
- Wali approval mandatory untuk minor
- Finance approval limits enforced
- Management override documented
- Audit trail tamper-proof

### Data Protection
- Personal data archived securely
- Financial data encrypted
- Documents retained per policy
- GDPR compliance for deletion

---

## Recovery Procedures

### Failure Scenarios
1. **Approval chain broken**
   - Manual override process
   - Skip to next approver
   - Escalation path defined
   - Maximum wait time: 48 jam

2. **Refund process failed**
   - Manual refund initiation
   - Alternative payment method
   - Finance team backup process
   - Resolution SLA: 72 jam

3. **System deactivation failed**
   - Manual account disable
   - Batch retry mechanism
   - IT support involvement
   - Complete within end of day

---

## Exit Interview Process

### Data Collection
1. **Alasan Pengunduran Diri**
   - Kategorisasi sistematis
   - Trend analysis
   - Feedback untuk improvement

2. **Satisfaction Survey**
   - Academic experience
   - Facility feedback
   - Teacher evaluation
   - Recommendation likelihood

3. **Retention Insights**
   - What could prevent resignation
   - Improvement suggestions
   - Re-enrollment possibility

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi
**Dokumen Terkait**:
- pengunduran-diri-siswa-happy-path.md
- Kebijakan Refund Institusi
- Panduan Retention Management