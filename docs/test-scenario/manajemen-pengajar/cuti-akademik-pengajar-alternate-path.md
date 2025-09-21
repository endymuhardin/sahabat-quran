# Skenario Pengujian: Cuti Akademik Pengajar - Alternate Path

## Informasi Umum
- **Kategori**: Proses Bisnis SDM - Manajemen Cuti Pengajar (Edge Cases)
- **Modul**: Manajemen Cuti Pengajar - Alur Alternatif
- **Tipe Skenario**: Alternate Path (Jalur Alternatif & Kasus Error)
- **Total Skenario**: 8 skenario validasi dan edge cases

---

## CP-AP-001: Koordinator - Penolakan karena Dampak Akademik

### Informasi Skenario
- **ID Skenario**: CP-AP-001 (Cuti Pengajar - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_COORDINATOR
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherLeaveValidationTest.coordinatorRejectsCritical()`

### Prasyarat
- Pengajuan cuti di periode kritis
- Tidak ada pengganti qualified
- Ujian akhir dalam periode cuti

### Data Test
```
Pengajuan Bermasalah:
Pengajar: Ustadz Rahman (satu-satunya pengajar Tahfiz 3)
Periode: Minggu ujian akhir
Pengganti: Tidak ada yang qualified
Dampak: 80 siswa tidak bisa ujian
Keputusan: TOLAK
```

### Langkah Pengujian

1. **Review high-impact request**
   - Aksi: Buka pengajuan periode kritis
   - Verifikasi:
     - Warning indicators merah
     - Impact assessment: CRITICAL
     - No qualified substitute
     - Exam period conflict

2. **Reject dengan alasan akademik**
   - Aksi: Tolak dengan justifikasi
   - Verifikasi:
     - Detailed rejection reason
     - Alternative dates suggested
     - Policy reference cited
     - Clear communication

3. **Offer alternative solution**
   - Aksi: Suggest reschedule
   - Verifikasi:
     - Alternative dates proposed
     - After exam period
     - Substitute availability checked
     - Compromise documented

### Kriteria Sukses
- [ ] Academic integrity protected
- [ ] Clear rejection reasoning
- [ ] Alternative offered
- [ ] Professional handling

---

## CP-AP-002: Pengajar - Cuti Darurat Mendadak

### Informasi Skenario
- **ID Skenario**: CP-AP-002
- **Prioritas**: Tinggi
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherLeaveValidationTest.emergencyLeaveRequest()`

### Prasyarat
- Situasi darurat keluarga
- No advance notice possible
- Classes today need coverage

### Data Test
```
Situasi Darurat:
Pengajar: Ustadz Yusuf
Alasan: Keluarga sakit kritis
Notice: Same day
Durasi: Unknown (est. 7 hari)
Kelas Hari Ini: 3 sesi
```

### Langkah Pengujian

1. **Submit emergency leave**
   - Aksi: Akses form cuti darurat
   - Verifikasi:
     - Emergency form berbeda
     - Minimal info required
     - Immediate notification
     - Auto-escalation triggered

2. **Sistem cari pengganti urgent**
   - Aksi: Auto-search substitute
   - Verifikasi:
     - Available teachers listed
     - SMS/Call triggered
     - Standby list activated
     - Classes covered today

3. **Temporary approval**
   - Aksi: Coordinator gives temp approval
   - Verifikasi:
     - Conditional approval
     - Documentation to follow
     - Coverage arranged
     - Students notified

### Kriteria Sukses
- [ ] Emergency handled quickly
- [ ] Classes covered same day
- [ ] Documentation deferred OK
- [ ] Communication effective

---

## CP-AP-003: SDM - Cuti Melebihi Entitlement

### Informasi Skenario
- **ID Skenario**: CP-AP-003
- **Prioritas**: High
- **Role**: HR_ADMIN
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherLeaveValidationTest.exceedLeaveBalance()`

### Prasyarat
- Pengajar sisa cuti: 3 hari
- Request cuti: 10 hari
- Policy exception needed

### Data Test
```
Leave Balance:
Entitled: 12 hari/tahun
Used: 9 hari
Balance: 3 hari
Request: 10 hari
Deficit: 7 hari
```

### Langkah Pengujian

1. **Detect over-limit request**
   - Aksi: System calculates deficit
   - Verifikasi:
     - Balance insufficient warning
     - Deficit calculated
     - Options presented
     - Policy shown

2. **Evaluate options**
   - Aksi: Review alternatives:
     - Unpaid leave untuk 7 hari
     - Reduce request ke 3 hari
     - Borrow dari tahun depan
     - Special exception
   - Verifikasi:
     - Each option dijelaskan
     - Financial impact shown
     - Approval levels required

3. **Process unpaid leave**
   - Aksi: Convert excess to unpaid
   - Verifikasi:
     - 3 hari paid, 7 hari unpaid
     - Salary deduction calculated
     - Agreement documented
     - Payroll notified

### Kriteria Sukses
- [ ] Over-limit handled properly
- [ ] Options clearly presented
- [ ] Financial impact transparent
- [ ] Proper documentation

---

## CP-AP-004: Sistem - Konflik Jadwal Pengganti

### Informasi Skenario
- **ID Skenario**: CP-AP-004
- **Prioritas**: High
- **Role**: SYSTEM
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherLeaveSystemTest.substituteConflicts()`

### Prasyarat
- Multiple teachers cuti bersamaan
- Limited substitute pool
- Schedule conflicts arise

### Data Test
```
Concurrent Leaves:
Teacher A: 1-15 Juni
Teacher B: 10-20 Juni
Teacher C: 5-25 Juni

Available Substitutes: 2 orang
Conflict Period: 10-15 Juni
Classes Uncovered: 6 sesi
```

### Langkah Pengujian

1. **System detects conflicts**
   - Trigger: Overlapping leaves
   - Verifikasi:
     - Conflict alert raised
     - Uncovered classes listed
     - Timeline visualization
     - Escalation triggered

2. **Automatic resolution attempts**
   - Aksi: System tries solutions:
     - Redistribute classes
     - Combine sections
     - Online backup option
     - External substitute
   - Verifikasi:
     - Each option evaluated
     - Feasibility checked
     - Cost calculated
     - Best option selected

3. **Manual intervention required**
   - Aksi: Escalate to coordinator
   - Verifikasi:
     - Issue dashboard shown
     - Options presented
     - Decision support data
     - Quick resolution

### Kriteria Sukses
- [ ] Conflicts detected early
- [ ] Multiple solutions tried
- [ ] Escalation effective
- [ ] Coverage achieved

---

## CP-AP-005: Pengajar - Tidak Kembali Tepat Waktu

### Informasi Skenario
- **ID Skenario**: CP-AP-005
- **Prioritas**: High
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherLeaveValidationTest.lateReturnHandling()`

### Prasyarat
- Cuti berakhir hari ini
- Pengajar tidak bisa kembali
- Extension tidak diajukan

### Data Test
```
Late Return:
Expected: Hari ini
Actual: Delayed 3 hari
Reason: Flight cancelled
Classes Tomorrow: 4 sesi
Notice: Last minute
```

### Langkah Pengujian

1. **Report delayed return**
   - Aksi: Notify inability to return
   - Verifikasi:
     - Late return form
     - Reason required
     - Evidence needed
     - Auto-escalation

2. **Emergency coverage**
   - Aksi: Extend substitute
   - Verifikasi:
     - Substitute contacted
     - Extension confirmed
     - Schedule maintained
     - No disruption

3. **Administrative consequences**
   - Aksi: Process violation
   - Verifikasi:
     - Unpaid days marked
     - Warning letter generated
     - Record updated
     - Return conditions set

### Kriteria Sukses
- [ ] Continuity maintained
- [ ] Substitute extended smoothly
- [ ] Consequences applied fairly
- [ ] Documentation complete

---

## CP-AP-006: Manajemen - Cuti Berulang Excessive

### Informasi Skenario
- **ID Skenario**: CP-AP-006
- **Prioritas**: Medium
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherLeaveManagementTest.excessiveLeavePattern()`

### Prasyarat
- Pengajar frequent short leaves
- Pattern detected by system
- Performance impact visible

### Data Test
```
Leave Pattern:
Last 6 months: 8 separate leaves
Total Days: 25 hari
Average: 3 hari per leave
Impact: Student complaints
Performance: Declining
```

### Langkah Pengujian

1. **System flags pattern**
   - Trigger: Excessive leave frequency
   - Verifikasi:
     - Pattern analysis shown
     - Comparison to average
     - Impact metrics
     - Alert to management

2. **Management review**
   - Aksi: Evaluate situation
   - Verifikasi:
     - Complete history shown
     - Performance data
     - Student feedback
     - Attendance record

3. **Intervention decision**
   - Aksi: Implement measures:
     - Counseling required
     - Medical check needed
     - Performance plan
     - Leave restriction
   - Verifikasi:
     - Action plan created
     - Timeline set
     - Monitoring activated
     - HR involved

### Kriteria Sukses
- [ ] Pattern detected accurately
- [ ] Intervention appropriate
- [ ] Support offered
- [ ] Improvement plan clear

---

## CP-AP-007: Pengajar - Pembatalan Cuti Approved

### Informasi Skenario
- **ID Skenario**: CP-AP-007
- **Prioritas**: Medium
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherLeaveCancellationTest.cancelApprovedLeave()`

### Prasyarat
- Cuti sudah approved
- Start date 5 hari lagi
- Situasi berubah

### Data Test
```
Approved Leave:
Reference: TCL-2024-00100
Start: 5 hari lagi
Duration: 14 hari
Status: APPROVED
Reason Cancel: Training dibatalkan
```

### Langkah Pengujian

1. **Request cancellation**
   - Aksi: Submit cancel request
   - Verifikasi:
     - Cancel form available
     - Reason required
     - Impact shown
     - Confirmation needed

2. **Reverse arrangements**
   - Aksi: System reverses prep:
     - Cancel substitute
     - Restore schedule
     - Update calendar
     - Notify students
   - Verifikasi:
     - All changes reverted
     - Notifications sent
     - Leave balance restored
     - No conflicts created

3. **Confirmation process**
   - Aksi: Get confirmations
   - Verifikasi:
     - Substitute acknowledged
     - Students informed
     - Schedule confirmed
     - Status: CANCELLED

### Kriteria Sukses
- [ ] Cancellation smooth
- [ ] All parties notified
- [ ] Schedule restored perfectly
- [ ] No disruption caused

---

## CP-AP-008: Sistem - Integritas Data Cuti

### Informasi Skenario
- **ID Skenario**: CP-AP-008
- **Prioritas**: High
- **Role**: SYSTEM
- **Estimasi Waktu**: Automated
- **Playwright Test**: `TeacherLeaveIntegrityTest.validateDataConsistency()`

### Test Scenarios

#### Concurrent Operations
1. **Multiple approvals simultaneously**
   - Scenario: Coordinator and HR approve together
   - Verifikasi:
     - Locking mechanism works
     - Sequential processing
     - No double approval
     - Status consistent

2. **System failure during processing**
   - Scenario: Crash during approval
   - Verifikasi:
     - Transaction rollback
     - No partial updates
     - Recovery possible
     - Audit trail intact

#### Data Validation
3. **Invalid date combinations**
   - Test: End before start
   - Verifikasi:
     - Validation catches error
     - Clear error message
     - Form not submitted
     - No corrupt data

4. **Substitute double-booking**
   - Test: Same substitute, same time
   - Verifikasi:
     - Conflict detected
     - Booking prevented
     - Alternative suggested
     - Schedule integrity maintained

### System Checks
- [ ] ACID compliance
- [ ] Referential integrity
- [ ] Audit completeness
- [ ] Recovery procedures
- [ ] Performance within SLA

---

## Referensi Pesan Error

### Pesan untuk Pengajar
```
ERR_CUTI_P001: "Sisa cuti tidak mencukupi"
ERR_CUTI_P002: "Periode cuti bentrok dengan ujian"
ERR_CUTI_P003: "Tidak ada pengajar pengganti qualified"
ERR_CUTI_P004: "Notice period minimal 14 hari"
ERR_CUTI_P005: "Dokumen pendukung tidak valid"
```

### Pesan untuk Admin
```
ADM_CUTI_P001: "Pengajar dalam masa probation"
ADM_CUTI_P002: "Konflik jadwal pengganti terdeteksi"
ADM_CUTI_P003: "Approval berurutan required"
ADM_CUTI_P004: "Excessive leave pattern detected"
ADM_CUTI_P005: "Performance issue blocks leave"
```

### Pesan Sistem
```
SYS_CUTI_P001: "Concurrent modification error"
SYS_CUTI_P002: "Substitute assignment failed"
SYS_CUTI_P003: "Calendar sync error"
SYS_CUTI_P004: "Notification service down"
SYS_CUTI_P005: "Data integrity violation"
```

---

## Tolok Ukur Performa

### Response Time
- Form load: < 1 detik
- Substitute search: < 3 detik
- Approval process: < 500ms
- Notification dispatch: < 2 detik
- Report generation: < 5 detik

### Load Testing
- 50 concurrent leave requests
- 100 substitute searches/menit
- 20 approvals bersamaan
- Zero deadlock
- 100% data integrity

---

## Prosedur Recovery

### Kegagalan Sistem
1. **Approval stuck**
   - Manual override available
   - Skip to next approver
   - Maximum wait: 24 jam
   - Escalation automatic

2. **Substitute assignment failed**
   - Manual assignment backup
   - Emergency list activated
   - Coordinator intervenes
   - Resolution dalam 4 jam

3. **Calendar sync broken**
   - Manual update possible
   - Batch resync available
   - Notification workaround
   - Fix dalam 1 hari kerja

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi
**Dokumen Terkait**:
- cuti-akademik-pengajar-happy-path.md
- Kebijakan SDM Pengajar
- Manual Penggantian Pengajar