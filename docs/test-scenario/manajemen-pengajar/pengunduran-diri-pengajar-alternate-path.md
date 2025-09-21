# Skenario Pengujian: Pengunduran Diri Pengajar - Alternate Path

## Informasi Umum
- **Kategori**: Proses Bisnis SDM - Pengunduran Diri Pengajar (Edge Cases)
- **Modul**: Manajemen Pengunduran Diri Pengajar - Alur Alternatif
- **Tipe Skenario**: Alternate Path (Jalur Alternatif & Kasus Error)
- **Total Skenario**: 8 skenario validasi dan edge cases

---

## RP-AP-001: Koordinator - Retention Attempt

### Informasi Skenario
- **ID Skenario**: RP-AP-001 (Resign Pengajar - Alternate Path - 001)
- **Prioritas**: Tinggi
- **Role**: ACADEMIC_COORDINATOR
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `TeacherResignValidationTest.retentionAttempt()`

### Prasyarat
- High-value teacher resigning
- Retention strategy available
- Counter-offer possible

### Data Test
```
High-Value Teacher:
Nama: Ustadz Bayu (Top performer)
Students: 200+ siswa love him
Performance: Excellent ratings
Reason: Better salary elsewhere
Counter-Offer: Salary increase + benefits
```

### Langkah Pengujian

1. **Identify high-value resignation**
   - Aksi: Flag valuable teacher leaving
   - Verifikasi:
     - Performance metrics shown
     - Student feedback highlighted
     - Impact assessment: HIGH
     - Retention flag triggered

2. **Initiate retention process**
   - Aksi: Start retention attempt
   - Verifikasi:
     - Retention form available
     - Counter-offer calculator
     - Benefits options shown
     - Timeline for response

3. **Create counter-offer**
   - Aksi: Propose retention package:
     - Salary increase: 25%
     - Additional benefits
     - Flexible schedule
     - Professional development
   - Verifikasi:
     - Package comprehensive
     - Within budget limits
     - Competitive analysis done
     - Management approval

4. **Schedule retention meeting**
   - Aksi: Set meeting with teacher
   - Verifikasi:
     - Meeting scheduled
     - Package presented
     - Discussion documented
     - Decision timeline set

5. **Handle decision**
   - Scenario A: Accepts retention
   - Verifikasi:
     - Resignation withdrawn
     - New contract terms
     - Status: RETAINED
   - Scenario B: Declines retention
   - Verifikasi:
     - Reason documented
     - Process continues
     - Positive separation

### Kriteria Sukses
- [ ] High-value teachers identified
- [ ] Retention attempted professionally
- [ ] Counter-offers competitive
- [ ] Decisions respected

---

## RP-AP-002: Pengajar - Notice Period Waiver

### Informasi Skenario
- **ID Skenario**: RP-AP-002
- **Prioritas**: Medium
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherResignValidationTest.noticePeriodWaiver()`

### Prasyarat
- Urgent opportunity for teacher
- Short notice resignation
- Waiver request needed

### Data Test
```
Urgent Situation:
Teacher: Ustadz Faisal
Opportunity: PhD program starts next month
Required Notice: 60 days
Available Time: 30 days
Request: Notice waiver
```

### Langkah Pengujian

1. **Request notice waiver**
   - Aksi: Submit waiver request
   - Verifikasi:
     - Waiver form available
     - Justification required
     - Evidence needed
     - Escalation automatic

2. **Provide exceptional justification**
   - Aksi: Explain urgent need:
     - PhD admission letter
     - Start date fixed
     - Once-in-lifetime opportunity
     - Willing to train successor intensively
   - Verifikasi:
     - Documentation uploaded
     - Compensation offered
     - Timeline adjusted
     - Approval sought

3. **Accelerated handover plan**
   - Aksi: Propose intensive handover:
     - 2 weeks full training
     - Detailed documentation
     - Video recordings
     - Remote support post-departure
   - Verifikasi:
     - Plan comprehensive
     - Quality maintained
     - Risks mitigated
     - Successor agreeable

4. **Management decision**
   - Aksi: Await waiver decision
   - Verifikasi:
     - Quick decision process
     - Clear conditions
     - Waiver granted/denied
     - Alternative offered

### Kriteria Sukses
- [ ] Waiver process exists
- [ ] Exceptional cases handled
- [ ] Quality maintained
- [ ] Win-win solutions found

---

## RP-AP-003: SDM - Contract Breach Issues

### Informasi Skenario
- **ID Skenario**: RP-AP-003
- **Prioritas**: High
- **Role**: HR_ADMIN
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherResignValidationTest.contractBreachHandling()`

### Prasyarat
- Teacher has binding contract
- Early termination penalties
- Negotiation required

### Data Test
```
Contract Issue:
Teacher: Ustadz Ibrahim
Contract: 2 years binding
Completed: 8 months
Penalty: 4 months salary
Amount: Rp 40,000,000
Negotiation: Needed
```

### Langkah Pengujian

1. **Identify contract breach**
   - Aksi: Check contract terms
   - Verifikasi:
     - Binding period: 2 years
     - Time remaining: 16 months
     - Penalty clause active
     - Amount calculated: Rp 40M

2. **Explain penalties**
   - Aksi: Inform teacher about consequences
   - Verifikasi:
     - Penalty clear
     - Payment terms explained
     - Negotiation possible
     - Legal implications noted

3. **Negotiate settlement**
   - Aksi: Discuss alternatives:
     - Reduced penalty (50%)
     - Training cost recovery
     - Knowledge transfer value
     - Phased payment plan
   - Verifikasi:
     - Options presented
     - Mutual agreement sought
     - Legal compliance
     - Documentation proper

4. **Reach agreement**
   - Aksi: Finalize settlement
   - Verifikasi:
     - Terms agreed: Rp 20M
     - Payment plan: 6 months
     - Legal waiver signed
     - Amicable separation

### Kriteria Sukses
- [ ] Contract terms enforced
- [ ] Penalties calculated correctly
- [ ] Negotiation fair
- [ ] Legal compliance maintained

---

## RP-AP-004: Keuangan - Outstanding Dues Issues

### Informasi Skenario
- **ID Skenario**: RP-AP-004
- **Prioritas**: High
- **Role**: FINANCE
- **Estimasi Waktu**: 8-10 menit
- **Playwright Test**: `TeacherResignValidationTest.outstandingDuesHandling()`

### Prasyarat
- Teacher has financial obligations
- Loans or advances pending
- Settlement complex

### Data Test
```
Financial Obligations:
Teacher: Ustadz Mahmud
Salary Advance: Rp 5,000,000
Laptop Loan: Rp 3,000,000
Training Cost: Rp 2,000,000
Total Dues: Rp 10,000,000
Final Settlement: Rp 15,000,000
Net Payable: Rp 5,000,000
```

### Langkah Pengujian

1. **Calculate outstanding dues**
   - Aksi: Review all obligations
   - Verifikasi:
     - Advance recovery: Rp 5M
     - Asset recovery: Rp 3M
     - Training cost: Rp 2M
     - Total dues: Rp 10M
     - Clear breakdown shown

2. **Offset against settlement**
   - Aksi: Calculate net position
   - Verifikasi:
     - Gross entitlement: Rp 15M
     - Less deductions: Rp 10M
     - Net payable: Rp 5M
     - Calculation transparent

3. **Handle insufficient funds**
   - Scenario: Dues > Entitlement
   - Aksi: Manage deficit
   - Verifikasi:
     - Recovery plan created
     - Payment schedule agreed
     - Legal backing obtained
     - Monitoring system active

4. **Generate final statement**
   - Aksi: Create detailed statement
   - Verifikasi:
     - All items listed
     - Calculations clear
     - Both parties sign
     - Dispute process available

### Kriteria Sukses
- [ ] All dues identified
- [ ] Calculations accurate
- [ ] Fair settlement reached
- [ ] Recovery ensured

---

## RP-AP-005: Manajemen - Disciplinary Resignation

### Informasi Skenario
- **ID Skenario**: RP-AP-005
- **Prioritas**: High
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `TeacherResignValidationTest.disciplinaryResignation()`

### Prasyarat
- Teacher under investigation
- Resigns before disciplinary action
- Complex legal situation

### Data Test
```
Disciplinary Case:
Teacher: Ustadz Zaid
Issue: Misconduct allegations
Investigation: 50% complete
Evidence: Circumstantial
Resignation: Submitted during process
Legal Risk: Medium
```

### Langkah Pengujian

1. **Review disciplinary status**
   - Aksi: Check investigation
   - Verifikasi:
     - Case file complete
     - Evidence documented
     - Timeline clear
     - Legal advice obtained

2. **Evaluate resignation timing**
   - Aksi: Assess situation:
     - Investigation incomplete
     - Evidence inconclusive
     - Legal risks assessed
     - Options evaluated
   - Verifikasi:
     - Timing suspicious
     - Investigation can continue
     - Resignation not automatic escape
     - Legal protection maintained

3. **Management decision options**
   - Option A: Accept resignation
   - Verifikasi:
     - Quick resolution
     - No admission of guilt
     - References restricted
     - Case closed

   - Option B: Continue investigation
   - Verifikasi:
     - Investigation proceeds
     - Resignation on hold
     - Due process followed
     - Decision after completion

4. **Implement chosen option**
   - Aksi: Execute decision
   - Verifikasi:
     - Clear communication
     - Legal compliance
     - Documentation complete
     - Stakeholders informed

### Kriteria Sukses
- [ ] Due process followed
- [ ] Legal protection maintained
- [ ] Fair handling
- [ ] Institutional integrity preserved

---

## RP-AP-006: Pengajar - Immediate Resignation (Emergency)

### Informasi Skenario
- **ID Skenario**: RP-AP-006
- **Prioritas**: High
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherResignValidationTest.emergencyResignation()`

### Prasyarat
- Family emergency situation
- Immediate departure needed
- No notice period possible

### Data Test
```
Emergency Situation:
Teacher: Ustadz Nashir
Emergency: Parent critically ill
Location: Different city
Departure: Today
Classes: 8 sesi this week
```

### Langkah Pengujian

1. **Report emergency situation**
   - Aksi: Submit emergency resignation
   - Verifikasi:
     - Emergency form different
     - Minimal info required
     - Immediate processing
     - Supervisor notified

2. **Arrange immediate coverage**
   - Aksi: Find emergency substitute
   - Verifikasi:
     - Substitute pool contacted
     - Today's classes covered
     - Materials shared
     - Students informed

3. **Defer formal process**
   - Aksi: Process later
   - Verifikasi:
     - Emergency acknowledged
     - Formal paperwork later
     - Compensation fair
     - Support provided

4. **Complete remotely**
   - Aksi: Finish process online
   - Verifikasi:
     - Digital signatures accepted
     - Video interviews possible
     - Documents couriered
     - Settlement processed

### Kriteria Sukses
- [ ] Emergency handled compassionately
- [ ] Immediate coverage arranged
- [ ] Process flexible
- [ ] Remote completion possible

---

## RP-AP-007: Sistem - Mass Resignation Event

### Informasi Skenario
- **ID Skenario**: RP-AP-007
- **Prioritas**: High
- **Role**: SYSTEM → MANAGEMENT
- **Estimasi Waktu**: 10-12 menit
- **Playwright Test**: `TeacherResignSystemTest.massResignationHandling()`

### Prasyarat
- Multiple teachers resign together
- Systemic issues possible
- Crisis management needed

### Data Test
```
Mass Event:
Period: 1 week
Resignations: 5 teachers
Departments: 3 different
Pattern: Similar reasons
Impact: 300+ students
Alert: Triggered
```

### Langkah Pengujian

1. **System detects pattern**
   - Trigger: >3 resignations in 7 days
   - Verifikasi:
     - Alert generated
     - Pattern analysis shown
     - Management notified
     - Emergency protocol active

2. **Analyze root causes**
   - Aksi: Investigation triggered
   - Verifikasi:
     - Common factors identified
     - Exit interview rush
     - Satisfaction survey deployed
     - External factors checked

3. **Crisis management mode**
   - Aksi: Activate protocols:
     - Retention task force
     - Emergency hiring
     - Student communication
     - Stakeholder management
   - Verifikasi:
     - Response coordinated
     - Resources mobilized
     - Communication clear
     - Stability maintained

4. **Implement corrective actions**
   - Aksi: Address root causes
   - Verifikasi:
     - Policy changes made
     - Improvements implemented
     - Prevention measures
     - Monitoring enhanced

### Kriteria Sukses
- [ ] Early detection works
- [ ] Crisis response effective
- [ ] Root causes addressed
- [ ] Future prevention planned

---

## RP-AP-008: Pengajar - Withdrawal of Resignation

### Informasi Skenario
- **ID Skenario**: RP-AP-008
- **Prioritas**: Medium
- **Role**: INSTRUCTOR
- **Estimasi Waktu**: 6-8 menit
- **Playwright Test**: `TeacherResignValidationTest.resignationWithdrawal()`

### Prasyarat
- Resignation submitted
- Still in notice period
- Change of circumstances

### Data Test
```
Withdrawal Request:
Original: TRG-2024-00030
Submitted: 15 days ago
Reason: Family circumstances changed
Status: Under processing
Request: Withdraw resignation
```

### Langkah Pengujian

1. **Request withdrawal**
   - Aksi: Submit withdrawal request
   - Verifikasi:
     - Withdrawal form available
     - Reason required
     - Impact assessed
     - Approval needed

2. **Evaluate withdrawal impact**
   - Aksi: Check current status:
     - Replacement search started
     - Students notified
     - Handover begun
     - Costs incurred
   - Verifikasi:
     - Impact manageable
     - Reversible actions
     - Costs acceptable
     - Students benefit

3. **Approve withdrawal**
   - Aksi: Accept withdrawal
   - Verifikasi:
     - Status: WITHDRAWN
     - Teacher status: ACTIVE
     - Replacement search stopped
     - Students re-notified

4. **Restore normal operations**
   - Aksi: Revert changes
   - Verifikasi:
     - Schedule restored
     - Access maintained
     - Contracts continued
     - Team informed

### Kriteria Sukses
- [ ] Withdrawal process exists
- [ ] Impact manageable
- [ ] Quick restoration
- [ ] Minimal disruption

---

## Referensi Pesan Error

### Pesan untuk Pengajar
```
ERR_RESIGN_T001: "Notice period belum terpenuhi (minimum 60 hari)"
ERR_RESIGN_T002: "Ada kewajiban kontrak yang belum selesai"
ERR_RESIGN_T003: "Outstanding financial obligations detected"
ERR_RESIGN_T004: "Disciplinary action pending - cannot resign"
ERR_RESIGN_T005: "Handover plan incomplete"
```

### Pesan untuk Admin
```
ADM_RESIGN_T001: "High-value teacher - retention required"
ADM_RESIGN_T002: "Contract penalty calculation needed"
ADM_RESIGN_T003: "No qualified replacement available"
ADM_RESIGN_T004: "Financial settlement exceeds limits"
ADM_RESIGN_T005: "Legal clearance required"
```

### Pesan Sistem
```
SYS_RESIGN_T001: "Mass resignation alert triggered"
SYS_RESIGN_T002: "Settlement calculation error"
SYS_RESIGN_T003: "Integration with payroll failed"
SYS_RESIGN_T004: "Document generation timeout"
SYS_RESIGN_T005: "Approval workflow stuck"
```

---

## Tolok Ukur Performa

### Response Time
- Resignation form: < 2 detik
- Settlement calculation: < 3 detik
- Document generation: < 5 detik
- Approval processing: < 1 detik
- Exit checklist: < 2 detik

### Load Handling
- 10 concurrent resignations
- 5 settlement calculations bersamaan
- 20 document generations/jam
- Zero calculation errors
- 100% audit trail

---

## Prosedur Recovery

### Critical Failures
1. **Settlement calculation error**
   - Manual calculation backup
   - Finance team override
   - Audit trail maintained
   - Resolution: 4 jam

2. **Document generation failure**
   - Template backup system
   - Manual document creation
   - Digital signature alternative
   - Resolution: 8 jam

3. **Approval workflow stuck**
   - Manual approval path
   - Emergency authorization
   - System fix parallel
   - Resolution: 24 jam

---

## Compliance & Legal

### Data Protection
- Personal data encrypted
- Access logs maintained
- Retention policy followed
- GDPR compliance

### Legal Requirements
- Contract law compliance
- Labor law adherence
- Tax regulation followed
- Audit trail complete

### Reference Management
- Service letters automated
- Experience certificates accurate
- Recommendations documented
- Rehire status tracked

---

**Versi Dokumen**: 1.0.0
**Terakhir Diperbarui**: September 2024
**Penulis**: Tim Pengujian Sistem
**Status Peninjauan**: Siap untuk Implementasi
**Dokumen Terkait**:
- pengunduran-diri-pengajar-happy-path.md
- Kebijakan SDM Pengajar
- Manual Exit Process
- Legal Compliance Guide