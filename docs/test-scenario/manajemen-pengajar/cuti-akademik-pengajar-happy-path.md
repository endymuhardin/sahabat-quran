# Skenario Pengujian: Cuti Akademik Pengajar - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis SDM - Manajemen Cuti Pengajar
- **Modul**: Manajemen Cuti Pengajar
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 7 skenario utama (Pengajar, Koordinator, Admin SDM, Admin Akademik, Manajemen, Sistem)

---

## CP-HP-001: Pengajar - Pengajuan Cuti Akademik

### Informasi Skenario
- **ID Skenario**: CP-HP-001 (Cuti Pengajar - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `TeacherLeaveTest.instructorSubmitLeave()`

### Prasyarat
- Akun pengajar aktif: `ustadz.ahmad` / `Welcome@YSQ2024`
- Pengajar memiliki jadwal mengajar aktif
- Minimal 3 bulan masa kerja
- Tidak dalam periode ujian

### Data Test
```
Login Pengajar:
Username: ustadz.ahmad
Password: Welcome@YSQ2024

Profil Pengajar:
Nama: Ahmad Hakim, Lc
NIP: TCH-2024-00015
Jabatan: Pengajar Senior
Kelas Diampu: 5 kelas (15 sesi/minggu)
Masa Kerja: 2 tahun
Status: Aktif

Detail Cuti:
Jenis: Cuti Studi/Pelatihan
Tujuan: Pelatihan Tahsin di Madinah
Tanggal Mulai: [Tanggal Sekarang + 30 hari]
Tanggal Selesai: [Tanggal Sekarang + 60 hari]
Durasi: 30 hari
Dokumen: Undangan pelatihan (PDF)
```

### Langkah Pengujian

#### Bagian 1: Akses Form Cuti
1. **Login sebagai Pengajar**
   - Aksi: Login dengan kredensial pengajar
   - Verifikasi:
     - Login berhasil
     - Dashboard pengajar muncul
     - Jadwal mengajar terlihat
     - Menu "Layanan SDM" tersedia

2. **Navigasi ke Pengajuan Cuti**
   - Aksi: Klik menu "Layanan SDM" → "Pengajuan Cuti"
   - Verifikasi:
     - Halaman pengajuan cuti terbuka
     - Sisa cuti tahunan ditampilkan
     - Form pengajuan tersedia
     - Histori cuti sebelumnya terlihat

#### Bagian 2: Pengisian Form Cuti
3. **Periksa kelayakan cuti**
   - Aksi: Klik tombol "Cek Kelayakan"
   - Verifikasi:
     - Sisa cuti: 12 hari (dari 12 hari/tahun)
     - Tidak ada cuti pending
     - Tidak ada konflik jadwal ujian
     - Status: "Memenuhi Syarat"

4. **Isi detail cuti**
   - Aksi: Lengkapi form cuti:
     - Jenis Cuti: "Studi/Pelatihan"
     - Tanggal Mulai: [Pilih tanggal]
     - Tanggal Selesai: [Pilih tanggal]
     - Durasi terhitung: 30 hari
     - Tujuan: "Pelatihan Tahsin Madinah"
     - Manfaat untuk institusi: [Detail]
   - Verifikasi:
     - Validasi tanggal (min 14 hari notice)
     - Durasi auto-calculated
     - Konflik jadwal ter-detect
     - Saran pengganti ditampilkan

5. **Usulkan pengajar pengganti**
   - Aksi: Pilih pengajar pengganti untuk setiap kelas:
     - Kelas A: Ustadz Budi
     - Kelas B: Ustadz Chairul
     - Kelas C: Ustadz Dani
     - dst.
   - Verifikasi:
     - Availability pengganti checked
     - Kualifikasi sesuai
     - Load balancing considered
     - Konfirmasi kesediaan required

#### Bagian 3: Upload Dokumen dan Submit
6. **Unggah dokumen pendukung**
   - Aksi: Upload undangan pelatihan
   - Verifikasi:
     - Upload berhasil
     - Preview tersedia
     - Validasi file type/size
     - Multiple documents supported

7. **Review dan kirim pengajuan**
   - Aksi: Review summary dan submit
   - Verifikasi:
     - Ringkasan lengkap ditampilkan
     - Pengganti ter-list dengan jelas
     - Terms & conditions
     - Submit berhasil
     - Nomor referensi: "TCL-2024-00089"
     - Status: "MENUNGGU_KOORDINATOR"

### Kriteria Sukses
- [ ] Form cuti mudah diakses
- [ ] Validasi kelayakan akurat
- [ ] Sistem saran pengganti berfungsi
- [ ] Dokumen ter-upload
- [ ] Notifikasi terkirim ke koordinator

### Data Verifikasi Akhir
```
Status Database:
- Tabel teacher_leave_requests: Entri baru
- Status: MENUNGGU_KOORDINATOR
- Substitute assignments: Ter-create
- Notification: Ke koordinator akademik
- Audit trail: Logged
```

---

## CP-HP-002: Koordinator - Review dan Rekomendasi

### Informasi Skenario
- **ID Skenario**: CP-HP-002
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_COORDINATOR
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherLeaveTest.coordinatorReview()`

### Prasyarat
- Login koordinator akademik
- Pengajuan dari CP-HP-001 masuk
- Akses ke jadwal semua pengajar

### Data Test
```
Login Koordinator:
Username: coord.academic
Password: Welcome@YSQ2024

Pengajuan:
Referensi: TCL-2024-00089
Pengajar: Ahmad Hakim
Durasi: 30 hari
Status: MENUNGGU_KOORDINATOR
```

### Langkah Pengujian

#### Bagian 1: Review Pengajuan
1. **Akses daftar cuti pengajar**
   - Aksi: Buka "Manajemen Pengajar" → "Cuti Pending"
   - Verifikasi:
     - List pengajuan cuti
     - TCL-2024-00089 terlihat
     - Priority indicator
     - Impact assessment shown

2. **Review detail cuti**
   - Aksi: Buka detail TCL-2024-00089
   - Verifikasi:
     - Detail lengkap pengajuan
     - Jadwal terdampak listed
     - Pengajar pengganti proposed
     - Dokumen dapat diakses

#### Bagian 2: Evaluasi Dampak
3. **Analisis dampak akademik**
   - Aksi: Klik "Analisis Dampak"
   - Verifikasi:
     - Total sesi terdampak: 60 sesi
     - Siswa terpengaruh: 125 siswa
     - Materi kritis: 2 ujian tengah
     - Coverage pengganti: 95%

4. **Validasi pengajar pengganti**
   - Aksi: Review setiap pengganti
   - Verifikasi:
     - Kualifikasi checked
     - Schedule compatibility
     - Workload acceptable
     - Approval dari pengganti

5. **Koordinasi jadwal adjustment**
   - Aksi: Adjust jadwal jika perlu
   - Verifikasi:
     - Minor reschedule done
     - No conflicts created
     - Students notified
     - Calendar updated

#### Bagian 3: Berikan Rekomendasi
6. **Input rekomendasi koordinator**
   - Aksi: Tulis rekomendasi:
     - Dampak: "Manageable dengan pengganti"
     - Saran: "Approve dengan monitoring"
     - Catatan: "Brief pengganti required"
   - Verifikasi:
     - Rekomendasi tersimpan
     - Supporting data attached
     - Timestamp recorded

7. **Forward ke Admin SDM**
   - Aksi: Approve dan forward
   - Verifikasi:
     - Status: "DIREKOMENDASIKAN"
     - Forward ke HR
     - Notifikasi terkirim
     - Tracking updated

### Kriteria Sukses
- [ ] Impact analysis comprehensive
- [ ] Substitute validation complete
- [ ] Schedule adjustments minimal
- [ ] Clear recommendation provided
- [ ] Process tracked properly

---

## CP-HP-003: Admin SDM - Verifikasi dan Proses

### Informasi Skenario
- **ID Skenario**: CP-HP-003
- **Prioritas**: Tinggi
- **Role**: HR_ADMIN
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `TeacherLeaveTest.hrProcessing()`

### Prasyarat
- Login Admin SDM
- Rekomendasi dari koordinator (CP-HP-002)
- Akses ke data kepegawaian

### Data Test
```
Login Admin SDM:
Username: hr.admin1
Password: Welcome@YSQ2024

Status Kepegawaian:
Sisa Cuti: 12 hari
Cuti Terpakai: 0 hari
Status Kontrak: Tetap
Performance: Excellent
```

### Langkah Pengujian

#### Bagian 1: Verifikasi Administratif
1. **Review pengajuan dari koordinator**
   - Aksi: Buka cuti yang direkomendasikan
   - Verifikasi:
     - Rekomendasi koordinator ada
     - Dokumen lengkap
     - Timeline acceptable
     - Policy compliance

2. **Cek entitlement dan policy**
   - Aksi: Verifikasi hak cuti
   - Verifikasi:
     - Sisa cuti mencukupi
     - Jenis cuti sesuai policy
     - Notice period terpenuhi
     - No disciplinary issues

3. **Validasi dokumen pendukung**
   - Aksi: Review undangan pelatihan
   - Verifikasi:
     - Dokumen authentic
     - Dates match request
     - Institution recognized
     - Benefit to school clear

#### Bagian 2: Proses Administratif
4. **Update record kepegawaian**
   - Aksi: Process cuti di sistem HR
   - Verifikasi:
     - Leave balance updated
     - Calendar marked
     - Payroll notified
     - Benefits adjusted

5. **Generate surat cuti**
   - Aksi: Buat surat resmi cuti
   - Verifikasi:
     - Surat ter-generate
     - Details accurate
     - Digital signature
     - Nomor surat: SK-CUTI-2024-089

6. **Koordinasi dengan bagian terkait**
   - Aksi: Notify departments:
     - Payroll: Adjustment info
     - Academic: Schedule confirmed
     - IT: Access maintained
   - Verifikasi:
     - Notifications sent
     - Acknowledgments received
     - Systems updated

#### Bagian 3: Approval SDM
7. **Approve cuti secara administratif**
   - Aksi: Give HR approval
   - Verifikasi:
     - Status: "DISETUJUI_SDM"
     - Documents archived
     - Forward to management
     - Teacher notified

### Kriteria Sukses
- [ ] Policy compliance verified
- [ ] Administrative process complete
- [ ] Official letter generated
- [ ] Systems updated
- [ ] Stakeholders informed

---

## CP-HP-004: Manajemen - Persetujuan Final

### Informasi Skenario
- **ID Skenario**: CP-HP-004
- **Prioritas**: Tinggi
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherLeaveTest.managementApproval()`

### Prasyarat
- Login manajemen
- Semua approval levels complete
- Strategic view required

### Data Test
```
Login Manajemen:
Username: management.director
Password: Welcome@YSQ2024

Pengajuan Final:
Reference: TCL-2024-00089
All Approvals: Complete
Strategic Impact: Low
```

### Langkah Pengujian

1. **Review complete package**
   - Aksi: Open leave request
   - Verifikasi:
     - All recommendations present
     - Impact assessment clear
     - Substitute plan solid
     - Documents complete

2. **Strategic consideration**
   - Aksi: Evaluate strategic impact
   - Verifikasi:
     - Program continuity assured
     - Quality maintained
     - Student impact minimal
     - Development benefit clear

3. **Final approval**
   - Aksi: Grant final approval
   - Verifikasi:
     - Status: "DISETUJUI_FINAL"
     - Effective date confirmed
     - All parties notified
     - Leave activated

### Kriteria Sukses
- [ ] Strategic review complete
- [ ] Final decision recorded
- [ ] Communication sent
- [ ] Leave officially granted

---

## CP-HP-005: Pengajar - Kembali dari Cuti

### Informasi Skenario
- **ID Skenario**: CP-HP-005
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherLeaveTest.instructorReturn()`

### Prasyarat
- Pengajar on approved leave
- Return date approaching
- Re-integration needed

### Data Test
```
Leave Status:
Reference: TCL-2024-00089
Return Date: [Tomorrow]
Status: AKTIF_CUTI
Classes to Resume: 5 classes
```

### Langkah Pengujian

#### Bagian 1: Inisiasi Kembali
1. **Submit return notification**
   - Aksi: Access return form
   - Verifikasi:
     - Form pre-filled
     - Actual return date
     - Readiness confirmation
     - Schedule preview

2. **Upload return documents**
   - Aksi: Upload certificate/report
   - Verifikasi:
     - Training certificate
     - Achievement report
     - Knowledge sharing plan
     - Documents stored

#### Bagian 2: Koordinasi Kembali
3. **Confirm schedule resumption**
   - Aksi: Review upcoming schedule
   - Verifikasi:
     - Classes reassigned back
     - No conflicts
     - Students notified
     - Materials accessible

4. **Knowledge sharing commitment**
   - Aksi: Schedule sharing session
   - Verifikasi:
     - Session scheduled
     - Audience identified
     - Materials prepared
     - Calendar updated

#### Bagian 3: Aktivasi Kembali
5. **Complete return process**
   - Aksi: Finalize return
   - Verifikasi:
     - Status: "KEMBALI_AKTIF"
     - Access restored
     - Schedule active
     - Welcome back sent

### Kriteria Sukses
- [ ] Smooth transition back
- [ ] Schedule restored properly
- [ ] Knowledge transfer planned
- [ ] Full reactivation complete

---

## CP-HP-006: Sistem - Monitoring Cuti Otomatis

### Informasi Skenario
- **ID Skenario**: CP-HP-006
- **Prioritas**: Medium
- **Role**: SYSTEM
- **Estimasi Waktu**: Automated
- **Playwright Test**: `TeacherLeaveSystemTest.automaticMonitoring()`

### Proses Otomatis

#### Reminder Sebelum Cuti
1. **30 hari sebelum cuti**
   - Actions:
     - Email reminder pengajar
     - Notify coordinator
     - Substitute confirmation
     - Document check

2. **7 hari sebelum cuti**
   - Actions:
     - Final preparations
     - Handover reminder
     - Access credentials
     - Emergency contacts

#### Selama Cuti
3. **Monitoring aktif**
   - Actions:
     - Substitute attendance
     - Issue escalation
     - Progress tracking
     - Quality checks

4. **Mid-leave check**
   - Actions:
     - Status verification
     - Return confirmation
     - Schedule preparation
     - Substitute feedback

#### Reminder Kembali
5. **7 hari sebelum kembali**
   - Actions:
     - Return reminder
     - Schedule confirmation
     - Access reactivation prep
     - Briefing schedule

6. **1 hari sebelum kembali**
   - Actions:
     - Final reminder
     - Tomorrow's schedule
     - Welcome preparation
     - System ready

### Monitoring Points
- [ ] Automated reminders working
- [ ] Substitute monitoring active
- [ ] Return process triggered
- [ ] Quality maintained
- [ ] Smooth transitions

---

## CP-HP-007: Pengajar - Perpanjangan Cuti Darurat

### Informasi Skenario
- **ID Skenario**: CP-HP-007
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherLeaveTest.emergencyExtension()`

### Prasyarat
- Pengajar sedang cuti
- Situasi darurat terjadi
- Perlu perpanjangan mendadak

### Data Test
```
Current Leave:
End Date: [3 hari lagi]
Extension Request: +14 hari
Reason: Visa delay
Documentation: Embassy letter
```

### Langkah Pengujian

1. **Submit extension request**
   - Aksi: Access extension form
   - Verifikasi:
     - Current leave shown
     - Extension form available
     - Urgent flag option
     - Document upload

2. **Provide justification**
   - Aksi: Explain situation
   - Verifikasi:
     - Reason documented
     - Evidence uploaded
     - Impact assessed
     - Alternative proposed

3. **Expedited approval**
   - Aksi: Request urgent processing
   - Verifikasi:
     - Escalated to management
     - Quick decision made
     - Substitute extended
     - All parties notified

### Kriteria Sukses
- [ ] Extension process available
- [ ] Urgent handling possible
- [ ] Continuity maintained
- [ ] Quick resolution achieved

---

## Poin Integrasi dan Dependensi

### Integrasi Sistem
1. **Sistem SDM**
   - Leave balance management
   - Policy enforcement
   - Document management
   - Performance tracking

2. **Sistem Akademik**
   - Schedule management
   - Substitute assignment
   - Student notification
   - Quality monitoring

3. **Sistem Keuangan**
   - Payroll adjustment
   - Benefit calculation
   - Substitute payment
   - Travel allowance

4. **Sistem Komunikasi**
   - Multi-channel notifications
   - Calendar integration
   - Document sharing
   - Status updates

### Alur Status
```
DRAFT → DIKIRIM → MENUNGGU_KOORDINATOR → DIREKOMENDASIKAN
→ DISETUJUI_SDM → DISETUJUI_FINAL → AKTIF_CUTI → KEMBALI_AKTIF

Alternatif:
- DITOLAK_KOORDINATOR
- DITOLAK_SDM
- DITOLAK_MANAJEMEN
- DIBATALKAN
- DIPERPANJANG
```

### Aturan Bisnis
1. **Kelayakan Cuti**
   - Minimal 3 bulan kerja
   - Sisa cuti mencukupi
   - Tidak dalam probation
   - Performance satisfactory

2. **Notice Period**
   - Regular: 14 hari
   - Urgent: 7 hari
   - Emergency: Immediate
   - Study leave: 30 hari

3. **Durasi Maksimal**
   - Annual leave: 12 hari/tahun
   - Study leave: 3 bulan/tahun
   - Sick leave: Per medical cert
   - Emergency: 7 hari

4. **Penggantian**
   - Qualified substitute required
   - Workload balanced
   - Quality maintained
   - Handover documented

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi