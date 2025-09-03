# Skenario Pengujian: Term Management - Happy Path

## Informasi Umum
- **Kategori**: Multi-Term Management
- **Modul**: Academic Term Management
- **Tipe Skenario**: Happy Path (Jalur Sukses)
- **Total Skenario**: 6 skenario utama untuk multi-term operations

---

## MTM-HP-001: Admin - Term Selection and Navigation

### Informasi Skenario
- **ID Skenario**: MTM-HP-001 (Multi-Term Management - Happy Path - 001)
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF
- **Estimasi Waktu**: 8-10 menit

### Prasyarat
- Admin account: `staff.admin1` / `Welcome@YSQ2024`
- Multiple academic terms available:
  - Semester 1 2024/2025 (ACTIVE)
  - Semester 2 2024/2025 (PLANNING) 
  - Intensive 2024/2025 (PLANNING)
  - Semester 1 2023/2024 (COMPLETED)
- Each term has associated data (classes, students, teachers)

### Data Test
```
Available Terms:
1. Semester 1 2023/2024 - Status: COMPLETED (historical data)
2. Semester 1 2024/2025 - Status: ACTIVE (current semester)
3. Semester 2 2024/2025 - Status: PLANNING (next semester)
4. Intensive 2024/2025 - Status: PLANNING (future intensive)

User Scenario:
Admin needs to review previous term data, monitor current operations, 
and plan upcoming semester simultaneously.
```

### Langkah Pengujian

#### Bagian 1: Basic Term Selection
1. **Login and access academic dashboard**
   - Aksi: Login sebagai admin staff
   - Navigate ke `/academic/assessment-foundation`
   - Verifikasi:
     - Page loads dengan default term (first PLANNING term)
     - Term selector dropdown visible dan accessible
     - Current selected term clearly indicated
     - Page title shows selected term context

2. **Verify term dropdown functionality**
   - Aksi: Click pada term selector dropdown
   - Verifikasi:
     - All available terms listed (4 terms total)
     - Terms sorted by start date (newest first)
     - Term status clearly indicated (ACTIVE, PLANNING, COMPLETED)
     - Visual distinction between term statuses
     - Current selected term highlighted

3. **Switch to ACTIVE term**
   - Aksi: Select "Semester 1 2024/2025 (ACTIVE)" dari dropdown
   - Verifikasi:
     - Page refreshes dengan termId parameter: `?termId=D0000000-0000-0000-0000-000000000001`
     - Data loads untuk selected ACTIVE term
     - Dashboard shows current operational data
     - Assessment foundation data for active students
     - Real-time metrics and progress indicators

#### Bagian 2: Historical Data Access  
4. **Access completed term data**
   - Aksi: Switch ke "Semester 1 2023/2024 (COMPLETED)"
   - Verifikasi:
     - URL updates dengan correct termId
     - Historical data loads successfully
     - Read-only indicators where appropriate
     - Completed semester summary statistics
     - Archive data accessibility confirmed
     - No active operations available (as expected)

5. **Navigate between different term contexts**
   - Aksi: Switch between COMPLETED → PLANNING → ACTIVE terms
   - Verifikasi:
     - Each switch loads correct term-specific data
     - URL parameters update correctly
     - No data mixing between terms
     - Page context adapts to term status
     - Performance acceptable for data switching

#### Bagian 3: Multi-Context Operations
6. **Access different modules with term context**
   - Aksi: With PLANNING term selected, navigate to:
     - `/academic/level-distribution?termId=<planning-term-id>`
     - `/academic/availability-monitoring?termId=<planning-term-id>`
     - `/academic/generation-readiness?termId=<planning-term-id>`
   - Verifikasi:
     - Term selection persists across module navigation
     - Each module shows planning-specific data
     - Term selector remains consistent
     - Breadcrumbs or context indicators accurate

#### Bagian 4: Session Persistence
7. **Verify term selection persistence**
   - Aksi: 
     - Select specific term (e.g., PLANNING term)
     - Open new browser tab
     - Navigate ke academic module
   - Verifikasi:
     - Same term selection maintained (if session-based)
     - OR defaults to system default (acceptable behavior)
     - Consistent user experience
     - No unexpected term switching

8. **Cross-browser session handling**
   - Aksi: 
     - Login same user in different browser
     - Navigate ke academic dashboard
   - Verifikasi:
     - Independent term selection per browser session
     - No cross-session interference
     - Each session maintains its context properly

### Hasil Diharapkan
- Seamless term selection and navigation functionality
- Accurate data loading for each selected term
- Clear term context indicators throughout application
- Proper URL parameter handling dengan term IDs
- Session persistence working correctly
- No data leakage between different academic terms

### Kriteria Sukses
- [ ] Term dropdown displays all available terms correctly
- [ ] Term selection updates URL parameters appropriately
- [ ] Data loads accurately for each selected term
- [ ] Session persistence working as designed
- [ ] Navigation maintains term context consistently
- [ ] Performance acceptable for term switching operations

---

## MTM-HP-002: Admin - Concurrent Multi-Term Planning

### Informasi Skenario
- **ID Skenario**: MTM-HP-002
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF
- **Estimasi Waktu**: 12-15 menit

### Prasyarat
- Admin account dengan ACADEMIC_TERM_MANAGE permission
- ACTIVE semester running dengan ongoing classes
- PLANNING semester ready untuk preparation activities
- Teachers available untuk both terms

### Data Test
```
Concurrent Operations Scenario:
Current Term: Semester 1 2024/2025 (ACTIVE)
- 25 active classes
- 150 enrolled students  
- Daily sessions ongoing

Planning Term: Semester 2 2024/2025 (PLANNING)
- Availability collection period open
- 20 teachers need to submit availability
- Class generation pending

Admin Task: Monitor current term performance while preparing next term
```

### Langkah Pengujian

#### Bagian 1: Current Term Monitoring
1. **Access active semester dashboard**
   - Aksi: Select ACTIVE term from dropdown
   - Navigate ke academic monitoring sections
   - Verifikasi:
     - Real-time class session data
     - Student attendance metrics
     - Teacher performance indicators
     - Current semester health indicators

2. **Review current term analytics**
   - Aksi: Check various current semester reports
   - Verifikasi:
     - Live operational data accessible
     - No interference dengan planning activities
     - Current semester metrics accurate
     - Dashboard responsive dan up-to-date

#### Bagian 2: Simultaneous Next Term Planning
3. **Switch to planning semester context**
   - Aksi: Select PLANNING term dari dropdown
   - Navigate ke `/academic/availability-monitoring`
   - Verifikasi:
     - Planning term data loads correctly
     - Availability submission status shown
     - Teacher submission progress tracked
     - No current term data visible (proper isolation)

4. **Monitor planning term progress**
   - Aksi: Review planning semester preparation status
   - Verifikasi:
     - Assessment foundation data untuk next term
     - Level distribution planning
     - Teacher availability collection status
     - Class generation readiness indicators

#### Bagian 3: Rapid Context Switching
5. **Frequent term switching operations**
   - Aksi: Switch between ACTIVE and PLANNING terms multiple times
   - Check different modules: assessment, level-distribution, availability-monitoring
   - Verifikasi:
     - Data isolation maintained perfectly
     - No cross-term data contamination
     - Quick response times untuk term switching
     - Consistent UI state management

6. **Concurrent data updates**
   - Aksi: 
     - In PLANNING term: Initiate availability collection
     - Switch ke ACTIVE term: Review session reports
     - Return ke PLANNING term: Verify collection status
   - Verifikasi:
     - Planning term updates preserved
     - Active term data unaffected
     - Status changes reflected accurately
     - No data loss during context switching

### Kriteria Sukses
- [ ] Concurrent access ke multiple terms working smoothly
- [ ] Perfect data isolation between ACTIVE and PLANNING terms
- [ ] No performance degradation with frequent term switching
- [ ] Planning activities tidak interfere dengan active operations
- [ ] Data consistency maintained across term contexts
- [ ] Admin dapat effectively manage multiple terms simultaneously

---

## MTM-HP-003: System - Term Lifecycle Transition (PLANNING → ACTIVE)

### Informasi Skenario
- **ID Skenario**: MTM-HP-003
- **Prioritas**: Tinggi
- **Role**: ADMIN_STAFF with SYSTEM_GOLIVE_MANAGE
- **Estimasi Waktu**: 15-20 menit

### Prasyarat
- Admin account dengan highest privileges
- PLANNING term dengan complete preparation:
  - Assessment foundation: 100%
  - Teacher availability: 100%  
  - Class generation: Approved
  - Final review: Completed
- Current ACTIVE term ready untuk transition ke COMPLETED

### Data Test
```
Term Transition Scenario:
Current State:
- Semester 1 2024/2025: ACTIVE → transition to COMPLETED
- Semester 2 2024/2025: PLANNING → transition to ACTIVE

Prerequisites Completed:
- 150 students assessed and level-assigned
- 20 teachers submitted availability
- 25 classes generated and approved
- Schedule conflicts resolved
- System implementation tests passed

Ready for Go-Live: Semester 2 2024/2025
```

### Langkah Pengujian

#### Bagian 1: Pre-Transition Validation
1. **Verify PLANNING term readiness**
   - Aksi: Select PLANNING term
   - Navigate ke `/academic/system-golive`
   - Verifikasi:
     - Go-live readiness checklist complete
     - All prerequisites met (green indicators)
     - System health checks passed
     - No blocking issues identified
     - Go-live button enabled and accessible

2. **Review current ACTIVE term status**
   - Aksi: Switch ke ACTIVE term
   - Check semester completion status
   - Verifikasi:
     - Current semester dapat be closed
     - Final grades submitted
     - Student evaluations completed
     - No pending administrative tasks
     - Ready untuk archival

#### Bagian 2: Term Lifecycle Transition
3. **Initiate PLANNING term activation**
   - Aksi: Return ke PLANNING term context
   - Click "Execute System Go-Live" button
   - Verifikasi:
     - Confirmation dialog dengan comprehensive summary
     - Impact assessment displayed
     - Rollback options mentioned
     - Final confirmation required

4. **Execute go-live process**
   - Aksi: Confirm go-live execution
   - Verifikasi:
     - Progress indicator untuk go-live steps
     - Status updates untuk each component
     - Database updates processing
     - System configuration changes
     - No error messages during transition

5. **Verify term status changes**
   - Aksi: Refresh page and check term dropdown
   - Verifikasi:
     - Previous PLANNING term now shows ACTIVE
     - Previous ACTIVE term now shows COMPLETED (if applicable)
     - Term dropdown updates reflect new statuses
     - Default term selection now points to new ACTIVE term

#### Bagian 3: Post-Transition Validation
6. **Access newly ACTIVE term**
   - Aksi: Select newly activated term
   - Navigate ke various academic modules
   - Verifikasi:
     - All generated classes visible dan active
     - Student enrollments processed
     - Teacher assignments confirmed
     - Schedule published and accessible
     - Operational features enabled

7. **Verify previous term archival**
   - Aksi: Select newly COMPLETED term
   - Verifikasi:
     - Historical data preserved dan accessible
     - Read-only mode enforced properly
     - Reports and analytics available
     - No active operational functions
     - Archive status clearly indicated

8. **System integration validation**
   - Aksi: Test key system functions dengan new ACTIVE term
   - Verifikasi:
     - Class sessions can be created
     - Attendance tracking functional
     - Student feedback systems active
     - All semester activities operational
     - Integration dengan external systems working

### Kriteria Sukses
- [ ] Go-live process executes successfully without errors
- [ ] Term status transitions happen atomically
- [ ] No data loss during term lifecycle changes
- [ ] All system functions operational with new ACTIVE term
- [ ] Historical data properly archived dan accessible
- [ ] User experience seamless through transition
- [ ] Rollback procedures available if needed

---

## MTM-HP-004: Teacher - Multi-Term Availability Management

### Informasi Skenario
- **ID Skenario**: MTM-HP-004
- **Prioritas**: Sedang
- **Role**: INSTRUCTOR/TEACHER
- **Estimasi Waktu**: 10-12 menit

### Prasyarat
- Teacher account: `ustadz.ahmad` / `Welcome@YSQ2024`
- Currently teaching in ACTIVE semester
- Next PLANNING semester availability collection open
- Historical data from previous COMPLETED semester

### Data Test
```
Multi-Term Teacher Context:
1. Completed Semester (Semester 2 2023/2024):
   - Taught 4 classes (Tahsin 1, Tahsin 2)
   - Performance rating: Excellent
   - Student feedback: 4.8/5.0

2. Current Active Semester (Semester 1 2024/2025):
   - Teaching 5 classes currently
   - Schedule: Mon/Wed/Fri mornings, Tue/Thu evenings
   - Current workload: High but manageable

3. Planning Semester (Semester 2 2024/2025):
   - Availability submission required
   - Deadline: 2 weeks from now
   - Need to balance dengan current teaching load
```

### Langkah Pengujian

#### Bagian 1: Review Historical Performance
1. **Access completed semester data**
   - Aksi: Login sebagai teacher
   - Navigate ke instructor dashboard
   - Select COMPLETED term from dropdown (if available)
   - Verifikasi:
     - Historical teaching assignments visible
     - Previous semester performance metrics
     - Student feedback summaries
     - Class completion rates
     - Teaching load analysis

2. **Analyze previous availability vs actual assignment**
   - Aksi: Review previous semester availability submission
   - Compare dengan actual classes assigned
   - Verifikasi:
     - Original availability submission data preserved
     - Actual teaching schedule comparison available
     - Lessons learned dari previous semester
     - Performance correlation dengan availability

#### Bagian 2: Current Term Context Awareness
3. **Review current semester status**
   - Aksi: Switch ke ACTIVE term
   - Check current teaching assignments
   - Verifikasi:
     - Current class schedule clearly displayed
     - Real-time teaching load information
     - Ongoing semester performance metrics
     - Current student enrollment per class
     - Schedule conflicts atau issues highlighted

4. **Monitor current semester workload**
   - Aksi: Access workload analytics untuk current term
   - Verifikasi:
     - Weekly schedule visualization
     - Class preparation time estimates
     - Student interaction metrics
     - Overall satisfaction ratings
     - Workload sustainability indicators

#### Bagian 3: Future Term Planning
5. **Access availability submission untuk planning term**
   - Aksi: Switch ke PLANNING term
   - Navigate ke availability submission form
   - Verifikasi:
     - Planning term context clearly indicated
     - Historical availability patterns shown (helpful hints)
     - Current semester workload displayed untuk reference
     - Recommendations based on previous performance

6. **Submit informed availability**
   - Aksi: Complete availability submission using:
     - Current semester experience
     - Previous semester lessons learned
     - Personal capacity assessment
   - Verifikasi:
     - Historical data helps inform decisions
     - System provides intelligent suggestions
     - Workload balancing recommendations
     - Submission successful untuk future term

#### Bagian 4: Cross-Term Comparison
7. **Compare teaching load across terms**
   - Aksi: Use term selector untuk compare:
     - COMPLETED term: actual teaching load
     - ACTIVE term: current teaching load  
     - PLANNING term: projected availability
   - Verifikasi:
     - Cross-term comparison tools available
     - Teaching load trends visible
     - Capacity planning insights provided
     - Professional development tracking

### Kriteria Sukses
- [ ] Teacher dapat access historical performance data
- [ ] Current semester context informs future planning
- [ ] Cross-term comparison tools functional
- [ ] Availability submission uses historical insights
- [ ] Workload balancing recommendations accurate
- [ ] Multi-term career progression visible

---

## MTM-HP-005: Management - Cross-Term Analytics and Reporting

### Informasi Skenario
- **ID Skenario**: MTM-HP-005
- **Prioritas**: Sedang
- **Role**: MANAGEMENT
- **Estimasi Waktu**: 15-18 menit

### Prasyarat
- Management account: `management.director` / `Welcome@YSQ2024`
- Multiple completed semesters dengan rich data
- Current active semester dengan ongoing metrics
- Access ke advanced reporting features

### Data Test
```
Multi-Term Analytics Scenario:
Available Data:
- Semester 1 2023/2024: COMPLETED (full cycle data)
- Semester 2 2023/2024: COMPLETED (full cycle data)
- Semester 1 2024/2025: ACTIVE (real-time data)
- Semester 2 2024/2025: PLANNING (preparation data)

Analysis Goals:
1. Student retention trends across semesters
2. Teacher performance consistency
3. Class size optimization patterns
4. Level progression success rates
5. Resource utilization efficiency
```

### Langkah Pengujian

#### Bagian 1: Student Progression Analytics
1. **Access cross-term student analytics**
   - Aksi: Login sebagai management
   - Navigate ke analytics dashboard
   - Select "Student Progression Analysis"
   - Verifikasi:
     - Multi-term data selection interface
     - Student retention rate calculations
     - Level advancement tracking
     - Cohort analysis capabilities

2. **Generate retention reports**
   - Aksi: Generate reports comparing:
     - Semester 1 2023 → Semester 2 2023 retention
     - Semester 2 2023 → Semester 1 2024 retention
   - Verifikasi:
     - Cross-semester student flow visualization
     - Retention percentage calculations
     - Drop-out pattern analysis
     - Success factor correlations

#### Bagian 2: Teacher Performance Trends
3. **Multi-term teacher performance analysis**
   - Aksi: Access teacher performance reports
   - Select multiple terms untuk comparison
   - Verifikasi:
     - Consistent performance tracking across terms
     - Performance trend analysis (improving/declining)
     - Workload correlation dengan performance
     - Professional development impact measurement

4. **Teacher capacity utilization**
   - Aksi: Analyze teacher workload patterns
   - Compare planned vs actual across terms
   - Verifikasi:
     - Availability vs assignment correlation
     - Optimal workload identification
     - Resource allocation efficiency
     - Teacher satisfaction trend analysis

#### Bagian 3: Operational Efficiency Metrics
5. **Class size optimization analysis**
   - Aksi: Review class size trends across multiple terms
   - Verifikasi:
     - Optimal class size identification
     - Student success rate correlation dengan class size
     - Resource utilization optimization
     - Cost-effectiveness analysis

6. **Level distribution effectiveness**
   - Aksi: Analyze level distribution success across terms
   - Verifikasi:
     - Placement test accuracy improvement
     - Level progression success rates
     - Student satisfaction correlation
     - Teaching effectiveness per level

#### Bagian 4: Strategic Planning Insights
7. **Generate strategic recommendations**
   - Aksi: Create comprehensive multi-term analysis report
   - Verifikasi:
     - Data-driven insights untuk future planning
     - Resource allocation recommendations
     - Process improvement suggestions
     - Growth opportunity identification

8. **Export comprehensive reports**
   - Aksi: Export multi-term analytics untuk board presentation
   - Verifikasi:
     - Professional report formatting
     - Executive summary dengan key metrics
     - Trend visualizations and charts
     - Actionable recommendations included

### Kriteria Sukses
- [ ] Cross-term analytics generate meaningful insights
- [ ] Student and teacher trend analysis accurate
- [ ] Operational efficiency metrics comprehensive
- [ ] Strategic planning recommendations valuable
- [ ] Report generation and export functional
- [ ] Data visualization clear dan professional

---

## MTM-HP-006: System - Multi-Term Data Integrity and Performance

### Informasi Skenario
- **ID Skenario**: MTM-HP-006
- **Prioritas**: Tinggi
- **Role**: SYSTEM_ADMIN
- **Estimasi Waktu**: 20-25 menit

### Prasyarat
- System admin access
- Multiple terms dengan varying data volumes
- Database performance monitoring tools
- Data integrity validation scripts

### Data Test
```
System Load Scenario:
Terms in Database:
1. 8 COMPLETED terms (historical data)
2. 1 ACTIVE term (live operations)
3. 2 PLANNING terms (preparation data)

Data Volume:
- 1,200+ students across all terms
- 150+ teachers with multi-term history
- 300+ classes across all terms
- 50,000+ session records
- 200,000+ attendance records

Performance Targets:
- Term switching: < 2 seconds
- Report generation: < 30 seconds
- Data integrity: 100% consistent
```

### Langkah Pengujian

#### Bagian 1: Data Isolation Validation
1. **Cross-term data isolation testing**
   - Aksi: Execute data isolation validation scripts
   - Query data across all terms simultaneously
   - Verifikasi:
     - No data leakage between terms
     - Foreign key constraints properly enforced
     - Term-specific queries return correct scope
     - Data integrity maintained perfectly

2. **Concurrent multi-term access testing**
   - Aksi: Simulate multiple users accessing different terms
   - Verifikasi:
     - No cross-term data contamination
     - Database performance remains stable
     - Transaction isolation working correctly
     - No deadlock conditions

#### Bagian 2: Performance Under Load
3. **Multi-term query performance**
   - Aksi: Execute complex queries across multiple terms
   - Measure response times untuk various scenarios
   - Verifikasi:
     - Single-term queries: < 1 second
     - Multi-term analytics: < 30 seconds  
     - Report generation: < 60 seconds
     - Index optimization working effectively

4. **Term switching performance**
   - Aksi: Rapid term switching simulation
   - Measure page load times dan data retrieval
   - Verifikasi:
     - Term selection UI responsive (< 0.5s)
     - Data loading per term efficient (< 2s)
     - Cache mechanisms working properly
     - No memory leaks during frequent switching

#### Bagian 3: System Scalability Testing
5. **Large dataset handling**
   - Aksi: Test system behavior dengan maximum expected data
   - Verifikasi:
     - Database performance stable under load
     - UI remains responsive dengan large datasets
     - Pagination and filtering work effectively
     - Export functions handle large volumes

6. **Historical data archival validation**
   - Aksi: Test archival procedures untuk old completed terms
   - Verifikasi:
     - Archival process maintains data integrity
     - Archived data remains accessible
     - System performance improves after archival
     - Recovery procedures functional

### Kriteria Sukses
- [ ] Perfect data isolation between all terms
- [ ] System performance meets defined targets
- [ ] Scalability requirements satisfied
- [ ] Data integrity validation passes 100%
- [ ] Archival and recovery procedures working
- [ ] No performance degradation dengan multiple terms

---

## Business Process Integration

### Integration dengan Academic Planning Workflow:
1. **Multi-Term Context Awareness**: All academic processes aware of term context
2. **Cross-Term Analytics**: Historical data informs current decisions
3. **Concurrent Operations**: Multiple terms can be managed simultaneously
4. **Data Integrity**: Perfect isolation between different academic terms
5. **Performance Optimization**: Efficient handling of multi-term scenarios

### Key Benefits:
- **Informed Decision Making**: Historical data guides future planning
- **Operational Efficiency**: Simultaneous management of multiple terms
- **Data Integrity**: Complete isolation prevents cross-term contamination
- **Scalability**: System handles multiple terms efficiently
- **User Experience**: Seamless navigation between different term contexts

### Technical Implementation:
- **Controllers**: Term-aware routing dengan termId parameters
- **Services**: Multi-term data isolation and validation
- **Repositories**: Optimized queries untuk cross-term analytics
- **Security**: Term-based access control and data protection
- **Performance**: Caching and optimization untuk multi-term scenarios

---

## Catatan untuk Tester

### Implementation Reference
- **URL Pattern**: `/academic/*?termId=UUID` throughout application
- **Database**: Complete term isolation dengan foreign keys
- **Security**: `@PreAuthorize` dengan term context validation
- **Performance**: Index optimization untuk multi-term queries

### Focus Areas
- **Data Isolation**: Critical - no cross-term contamination allowed
- **Performance**: Multi-term operations must meet response time targets
- **User Experience**: Seamless term navigation and context switching
- **Analytics**: Cross-term reporting must provide valuable insights
- **Scalability**: System must handle growing number of terms efficiently

### Critical Success Factors
- **Perfect Data Isolation**: Zero tolerance untuk cross-term data mixing
- **Consistent Performance**: Response times must remain acceptable
- **Intuitive Navigation**: Users must easily understand term contexts
- **Reliable Analytics**: Cross-term insights must be accurate dan actionable
- **Future-Proof Scalability**: Architecture must support unlimited terms

This comprehensive happy path testing ensures robust multi-term support that will guide implementation of this critical architectural feature.