# Skenario Pengujian: Pengunduran Diri Pengajar - Happy Path

## Informasi Umum
- **Kategori**: Proses Bisnis SDM - Pengunduran Diri Pengajar
- **Modul**: Manajemen Pengunduran Diri Pengajar
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 7 skenario utama (Pengajar, Koordinator, Admin SDM, Keuangan, Manajemen, Exit, Sistem)

---

## RP-HP-001: Pengajar - Pengajuan Resign

### Informasi Skenario
- **ID Skenario**: RP-HP-001 (Resign Pengajar - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `TeacherResignationTest.instructorSubmitResignation()`

### Prasyarat
- Akun pengajar aktif: `ustadz.khalid` / `Welcome@YSQ2024`
- Minimal 6 bulan masa kerja
- Tidak ada kontrak binding aktif
- Tidak ada outstanding obligations

### Data Test
```
Login Pengajar:
Username: ustadz.khalid
Password: Welcome@YSQ2024

Profil Pengajar:
Nama: Khalid bin Walid, S.Pd.I
NIP: TCH-2023-00008
Jabatan: Pengajar Senior
Masa Kerja: 2.5 tahun
Status Kontrak: Tetap
Kelas Diampu: 6 kelas

Detail Pengunduran Diri:
Alasan: Melanjutkan S2 ke luar negeri
Tanggal Efektif: [Tanggal Sekarang + 60 hari]
Kategori: Pendidikan Lanjut
Notice Period: 60 hari (sesuai kontrak)
Dokumen: LoA universitas (PDF)
```

### Langkah Pengujian

#### Bagian 1: Akses Form Resign
1. **Login sebagai Pengajar**
   - Aksi: Login dengan kredensial pengajar
   - Verifikasi:
     - Login berhasil
     - Dashboard pengajar muncul
     - Status aktif terlihat
     - Menu "Layanan SDM" tersedia

2. **Navigasi ke Pengunduran Diri**
   - Aksi: Klik menu "Layanan SDM" → "Pengunduran Diri"
   - Verifikasi:
     - Halaman pengunduran diri terbuka
     - Informasi kontrak ditampilkan
     - Notice period dijelaskan
     - Konsekuensi resign tertera

#### Bagian 2: Pengisian Form Resign
3. **Review kontrak dan kewajiban**
   - Aksi: Baca terms kontrak
   - Verifikasi:
     - Notice period: 60 hari
     - Penalty clause (jika ada)
     - Outstanding obligations listed
     - Handover requirements shown

4. **Isi form pengunduran diri**
   - Aksi: Lengkapi detail resign:
     - Alasan: "Melanjutkan pendidikan S2"
     - Kategori: "Pendidikan Lanjut"
     - Tanggal efektif: [60 hari ke depan]
     - Detail alasan: [Elaborate]
     - Saran untuk institusi: [Optional]
   - Verifikasi:
     - Notice period validated
     - Date tidak kurang dari required
     - Form validation working
     - Character count untuk detail

5. **Upload dokumen pendukung**
   - Aksi: Upload Letter of Acceptance
   - Verifikasi:
     - Upload berhasil
     - Document preview
     - File validation
     - Multiple docs supported

#### Bagian 3: Handover Planning
6. **Buat rencana serah terima**
   - Aksi: Isi handover plan:
     - Knowledge transfer timeline
     - Materials to handover
     - Suggested replacement
     - Training period proposed
   - Verifikasi:
     - Timeline reasonable
     - All classes covered
     - Documentation listed
     - Successor identified

7. **Submit pengajuan resign**
   - Aksi: Review dan submit
   - Verifikasi:
     - Summary complete
     - Confirmation required
     - Password verification
     - Submit successful
     - Reference: "TRG-2024-00023"
     - Status: "MENUNGGU_KOORDINATOR"

### Kriteria Sukses
- [ ] Form resign accessible
- [ ] Notice period enforced
- [ ] Handover plan required
- [ ] Documentation complete
- [ ] Process initiated properly

### Data Verifikasi Akhir
```
Database Status:
- Tabel teacher_resignations: New entry
- Status: MENUNGGU_KOORDINATOR
- Handover plan: Created
- Timeline: 60 days set
- Notifications: Sent to coordinator
```

---

## RP-HP-002: Koordinator - Review Akademik

### Informasi Skenario
- **ID Skenario**: RP-HP-002
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_COORDINATOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `TeacherResignationTest.coordinatorAcademicReview()`

### Prasyarat
- Pengajuan dari RP-HP-001
- Akses ke teaching schedule
- Replacement planning capability

### Data Test
```
Login Koordinator:
Username: coord.academic
Password: Welcome@YSQ2024

Resignation Review:
Reference: TRG-2024-00023
Teacher: Khalid bin Walid
Classes: 6 active classes
Students Affected: 150
```

### Langkah Pengujian

#### Bagian 1: Impact Assessment
1. **Review resignation request**
   - Aksi: Open TRG-2024-00023
   - Verifikasi:
     - Full details visible
     - Reason understood
     - Timeline acceptable
     - Documents verified

2. **Analyze academic impact**
   - Aksi: Run impact analysis
   - Verifikasi:
     - Classes affected: 6
     - Students impacted: 150
     - Curriculum coverage needed
     - Special programs affected

3. **Evaluate handover plan**
   - Aksi: Review proposed handover
   - Verifikasi:
     - Timeline sufficient
     - Coverage complete
     - Documentation adequate
     - Training feasible

#### Bagian 2: Succession Planning
4. **Identify replacement options**
   - Aksi: Search qualified teachers
   - Verifikasi:
     - Internal candidates listed
     - Qualifications matched
     - Availability checked
     - Workload calculated

5. **Create transition plan**
   - Aksi: Design transition:
     - Overlap period: 2 weeks
     - Shadow teaching: 1 week
     - Independent with support: 1 week
     - Full handover: Final week
   - Verifikasi:
     - Plan comprehensive
     - Timeline realistic
     - Quality maintained
     - Students informed

6. **Approve dengan recommendations**
   - Aksi: Give academic approval
   - Verifikasi:
     - Recommendations documented
     - Successor identified
     - Timeline confirmed
     - Status: "AKADEMIK_APPROVED"

### Kriteria Sukses
- [ ] Impact fully assessed
- [ ] Succession plan solid
- [ ] Transition smooth planned
- [ ] Academic continuity assured

---

## RP-HP-003: Admin SDM - Proses Administratif

### Informasi Skenario
- **ID Skenario**: RP-HP-003
- **Prioritas**: Tinggi
- **Role**: HR_ADMIN
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `TeacherResignationTest.hrAdministrativeProcess()`

### Prasyarat
- Academic approval (RP-HP-002)
- Access to HR systems
- Policy knowledge

### Data Test
```
Login Admin SDM:
Username: hr.admin1
Password: Welcome@YSQ2024

Employment Details:
Contract Type: Permanent
Notice Period: 60 days
Benefits: Active
Leave Balance: 5 days
```

### Langkah Pengujian

#### Bagian 1: Contract Review
1. **Verify contract compliance**
   - Aksi: Check contract terms
   - Verifikasi:
     - Notice period met
     - No breach of contract
     - Obligations listed
     - Penalty not applicable

2. **Calculate final entitlements**
   - Aksi: Compute benefits:
     - Prorated salary
     - Leave encashment: 5 days
     - Gratuity (if eligible)
     - Other benefits
   - Verifikasi:
     - Calculations accurate
     - Policy compliant
     - Documentation clear
     - Total: Rp 25,000,000

#### Bagian 2: Exit Formalities
3. **Generate exit checklist**
   - Aksi: Create checklist:
     - Return laptop
     - Return ID card
     - Return books/materials
     - Clear personal items
     - Knowledge transfer
   - Verifikasi:
     - List comprehensive
     - Departments notified
     - Timeline set
     - Tracking enabled

4. **Schedule exit interview**
   - Aksi: Set exit interview
   - Verifikasi:
     - Date scheduled
     - Interviewer assigned
     - Questions prepared
     - Feedback form ready

5. **Process documentation**
   - Aksi: Prepare documents:
     - Experience certificate draft
     - Service letter draft
     - Full & final draft
     - NOC template
   - Verifikasi:
     - Drafts accurate
     - Details correct
     - Ready for finalization

6. **HR approval**
   - Aksi: Approve administratively
   - Verifikasi:
     - Status: "SDM_PROCESSED"
     - Finance notified
     - Documents queued
     - Exit process initiated

### Kriteria Sukses
- [ ] Contract compliance verified
- [ ] Entitlements calculated
- [ ] Exit process structured
- [ ] Documentation prepared

---

## RP-HP-004: Keuangan - Settlement Process

### Informasi Skenario
- **ID Skenario**: RP-HP-004
- **Prioritas**: Tinggi
- **Role**: FINANCE
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherResignationTest.financialSettlement()`

### Prasyarat
- HR processing complete
- Entitlement calculation done
- No outstanding advances

### Data Test
```
Login Keuangan:
Username: staff.finance1
Password: Welcome@YSQ2024

Financial Details:
Monthly Salary: Rp 10,000,000
Prorated Days: 15
Leave Encashment: 5 days
Total Payable: Rp 25,000,000
Deductions: Rp 0
```

### Langkah Pengujian

#### Bagian 1: Financial Review
1. **Review financial status**
   - Aksi: Check teacher account
   - Verifikasi:
     - No outstanding loans
     - No pending advances
     - Benefits calculated
     - Taxes computed

2. **Validate calculations**
   - Aksi: Verify HR calculations
   - Verifikasi:
     - Prorated salary correct
     - Leave encashment accurate
     - Benefits compliant
     - Total confirmed

#### Bagian 2: Settlement Processing
3. **Process full & final**
   - Aksi: Create settlement:
     - Gross amount: Rp 25,000,000
     - Tax deduction: Rp 2,500,000
     - Net payable: Rp 22,500,000
     - Payment date: Last working day
   - Verifikasi:
     - Calculations verified
     - Tax compliant
     - Payment scheduled
     - Documentation complete

4. **Generate settlement letter**
   - Aksi: Create final letter
   - Verifikasi:
     - All components listed
     - Amount accurate
     - Signature blocks
     - Official format

5. **Finance approval**
   - Aksi: Approve settlement
   - Verifikasi:
     - Status: "KEUANGAN_SETTLED"
     - Payment authorized
     - Letter finalized
     - Management notified

### Kriteria Sukses
- [ ] Financials accurate
- [ ] Settlement fair
- [ ] Documentation proper
- [ ] Payment scheduled

---

## RP-HP-005: Manajemen - Final Approval

### Informasi Skenario
- **ID Skenario**: RP-HP-005
- **Prioritas**: Tinggi
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherResignationTest.managementFinalApproval()`

### Prasyarat
- All departments processed
- Replacement plan ready
- Exit formalities planned

### Data Test
```
Login Manajemen:
Username: management.director
Password: Welcome@YSQ2024

Resignation Package:
Teacher: Khalid bin Walid
Reason: Higher education
Impact: Managed
Replacement: Identified
```

### Langkah Pengujian

1. **Review complete package**
   - Aksi: Open resignation file
   - Verifikasi:
     - All approvals present
     - Impact managed
     - Succession ready
     - Financials clear

2. **Strategic consideration**
   - Aksi: Evaluate loss:
     - Retention attempted?
     - Counter-offer considered?
     - Future relationship?
     - Reference policy?
   - Verifikasi:
     - Options explored
     - Decision justified
     - Relationship positive
     - Door open for return

3. **Final approval**
   - Aksi: Grant final approval
   - Verifikasi:
     - Status: "DISETUJUI_FINAL"
     - Effective date confirmed
     - All parties notified
     - Exit process active

### Kriteria Sukses
- [ ] Comprehensive review done
- [ ] Strategic aspects considered
- [ ] Positive separation
- [ ] Process completed

---

## RP-HP-006: Pengajar - Exit Interview & Handover

### Informasi Skenario
- **ID Skenario**: RP-HP-006
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 12-15 menit
- **Playwright Test**: `TeacherResignationTest.exitInterview()`

### Prasyarat
- Final approval received
- Exit date approaching
- Successor identified

### Data Test
```
Exit Details:
Last Working Day: [7 days from now]
Exit Interview: [Tomorrow]
Handover Period: Current week
Clearance: In progress
```

### Langkah Pengujian

#### Bagian 1: Exit Interview
1. **Attend exit interview**
   - Aksi: Participate in interview
   - Verifikasi:
     - Questions comprehensive
     - Feedback honest
     - Suggestions noted
     - Positives highlighted

2. **Complete feedback form**
   - Aksi: Fill exit survey:
     - Work environment
     - Management support
     - Growth opportunities
     - Compensation fairness
     - Reasons for leaving
   - Verifikasi:
     - All sections complete
     - Ratings provided
     - Comments detailed
     - Submitted successfully

#### Bagian 2: Knowledge Transfer
3. **Conduct handover sessions**
   - Aksi: Transfer knowledge:
     - Day 1-2: Curriculum overview
     - Day 3-4: Student specifics
     - Day 5: Admin processes
     - Day 6-7: Practical sessions
   - Verifikasi:
     - Sessions conducted
     - Documentation provided
     - Questions answered
     - Successor confident

4. **Complete clearance**
   - Aksi: Return items:
     - Laptop returned
     - ID card submitted
     - Books returned
     - Keys handed over
   - Verifikasi:
     - Checklist complete
     - Receipts obtained
     - No pending items
     - Clearance certified

#### Bagian 3: Final Day
5. **Receive final documents**
   - Aksi: Collect documents:
     - Experience certificate
     - Service letter
     - Full & final settlement
     - NOC if requested
   - Verifikasi:
     - Documents accurate
     - Signed properly
     - Settlement received
     - Copies retained

6. **Farewell formalities**
   - Aksi: Final goodbye
   - Verifikasi:
     - Farewell conducted
     - Contact shared
     - LinkedIn connected
     - Positive departure

### Kriteria Sukses
- [ ] Exit interview valuable
- [ ] Handover comprehensive
- [ ] Clearance complete
- [ ] Departure positive

---

## RP-HP-007: Sistem - Automated Offboarding

### Informasi Skenario
- **ID Skenario**: RP-HP-007
- **Prioritas**: Medium
- **Role**: SYSTEM
- **Estimasi Waktu**: Automated
- **Playwright Test**: `TeacherResignSystemTest.automatedOffboarding()`

### Proses Otomatis

#### Last Working Day
1. **Account deactivation**
   - Trigger: End of last day (23:59)
   - Actions:
     - Login disabled
     - Email archived
     - Access revoked
     - Data backed up

2. **System cleanup**
   - Actions:
     - Remove from groups
     - Clear assignments
     - Archive records
     - Update directories

#### Post-Departure
3. **Final settlement**
   - Trigger: Next working day
   - Actions:
     - Payment processed
     - Payslip generated
     - Tax docs created
     - Records updated

4. **Alumni management**
   - Actions:
     - Add to alumni database
     - Maintain limited access
     - Enable references
     - Track career progress

### Monitoring Points
- [ ] Deactivation timely
- [ ] Data preserved
- [ ] Settlement processed
- [ ] Alumni status active

---

## Poin Integrasi dan Dependensi

### Integrasi Sistem
1. **Sistem SDM**
   - Contract management
   - Exit processing
   - Document generation
   - Alumni tracking

2. **Sistem Akademik**
   - Schedule management
   - Succession planning
   - Student notification
   - Quality assurance

3. **Sistem Keuangan**
   - Settlement calculation
   - Payment processing
   - Tax compliance
   - Final accounting

4. **Sistem IT**
   - Access management
   - Data archival
   - Email handling
   - Asset recovery

### Alur Status
```
DRAFT → DIKIRIM → MENUNGGU_KOORDINATOR → AKADEMIK_APPROVED
→ SDM_PROCESSED → KEUANGAN_SETTLED → DISETUJUI_FINAL
→ EXIT_PROCESS → COMPLETED

Alternatif:
- DITOLAK_KOORDINATOR (rare)
- WITHDRAWN (change of mind)
- TERMINATED (disciplinary)
```

### Aturan Bisnis
1. **Notice Period**
   - Probation: 30 hari
   - Permanent: 60 hari
   - Senior: 90 hari
   - Waivable by management

2. **Exit Eligibility**
   - No disciplinary action pending
   - No financial obligations
   - Handover completed
   - Clearance done

3. **Settlement Rules**
   - Prorated salary paid
   - Leave encashed (max 30 days)
   - Gratuity if > 5 years
   - Tax as per law

4. **Reference Policy**
   - Service letter: Always
   - Experience cert: On request
   - Recommendation: Discretionary
   - Rehire status documented

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi