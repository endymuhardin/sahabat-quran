# Implementation Backlog

This document tracks incomplete implementations, stub methods, hardcoded data, and test coverage gaps that need to be addressed before production deployment.

---

## Summary

| Category | Count | Severity |
|----------|-------|----------|
| Hardcoded/Mock Data in Services | 17+ | HIGH |
| Incomplete Service Methods (TODO) | 17 | HIGH |
| Stub Controller Methods | 10 | MEDIUM |
| Hardcoded Template Content | 50+ | MEDIUM |
| Incomplete Functional Tests | 40+ | HIGH |
| Missing CRUD Test Coverage | 20+ | HIGH |

---

## 1. Service Layer - Hardcoded/Mock Data

### 1.1 CrossTermAnalyticsService.java

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| 418-419 | Mock utilization rate | `BigDecimal.valueOf(85)` | Calculate from actual teacher availability and class assignments |
| 418-419 | Mock average performance | `BigDecimal.valueOf(4.3)` | Aggregate from feedback_answers table |
| 445-447 | Mock completion rates | `[85, 89, 91]` | Query from enrollment completion data |
| 445-447 | Mock satisfaction scores | `[4.2, 4.4, 4.3]` | Calculate from feedback responses |
| 480 | Mock student feedback | `BigDecimal.valueOf(4.4)` | Aggregate from feedback_answers |
| 492-505 | Mock financial data | Revenue: 1000000, Cost: 800000 | Query from billing/payment tables |
| 1024-1037 | Mock class breakdown | Hardcoded "Tahsin 1A", "Tahfizh 1B" | Query from ClassGroup/Enrollment tables |

### 1.2 ReportGenerationService.java

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| 360 | Mock attendance rate | `BigDecimal.valueOf(92.0)` | Query attendance records for student/term |

### 1.3 SessionMonitoringService.java

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| 384 | Mock teacher rating | `BigDecimal.valueOf(4.2)` | Aggregate from feedback_answers table |

### 1.4 ManagementController.java

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| 421-423 | Mock workflow metrics | "2.3 days", "85%", "Teacher assignment phase" | Calculate from registration workflow timestamps |

### 1.5 TranscriptService.java

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| 83-99 | Hardcoded grades | "A", "A-", scores "85", "80" | Query from student_assessments table |
| 124-137 | Hardcoded GPA | "3.45", "Good Standing" | Calculate from actual grades |

---

## 2. Service Layer - Incomplete Methods (TODO)

### 2.1 ClassGenerationService.java

| Line | Method/Field | Issue | Required Implementation |
|------|--------------|-------|------------------------|
| 469 | `.totalTeacherSlots(0)` | Hardcoded zero | Calculate from teacher availability submissions |
| 470 | `.teacherUtilizationRate(BigDecimal.ZERO)` | Hardcoded zero | Calculate utilization percentage |
| 473 | `.workloadBalance(BigDecimal.ZERO)` | Hardcoded zero | Calculate standard deviation of teacher workload |
| 477-479 | Save proposal | Empty method | Convert proposal to GeneratedClassProposal entity and persist |
| 483-485 | Load proposals | Returns empty list | Parse JSONB proposal_data field |
| 523 | Create class groups | Not implemented | Create actual ClassGroup entities from proposal data |

### 2.2 AcademicPlanningService.java

| Line | Method/Field | Issue | Required Implementation |
|------|--------------|-------|------------------------|
| 302 | `.deadline("15 Dec 2024")` | Hardcoded string | Get from term preparation deadline field |
| 331 | Notification system | Comment only | Implement notification dispatch |
| 383 | `.notificationSystemReady(false)` | Hardcoded false | Check actual notification system status |
| 439 | Go-live tasks | Comment only | Implement notification, messaging, and tracking |

### 2.3 TeacherAvailabilityService.java

| Line | Method/Field | Issue | Required Implementation |
|------|--------------|-------|------------------------|
| 107 | `.preferredLevels(new ArrayList<>())` | Empty list | Extract from teacher preferences/history |
| 294 | Student info | Missing properties | Add assessment background, special needs |
| 320 | `.canEdit(true)` | Hardcoded true | Implement proper permission checking |
| 410 | File upload | Log only | Implement actual file storage (S3/filesystem) |

### 2.4 TeacherLevelAssignmentService.java

| Line | Method/Field | Issue | Required Implementation |
|------|--------------|-------|------------------------|
| 54 | `.canEdit(true)` | Hardcoded true | Implement permission checking |
| 500 | `.suggestedLevels(new ArrayList<>())` | Empty list | Implement level suggestion algorithm |

### 2.5 TeacherPreClosureService.java

| Line | Method/Field | Issue | Required Implementation |
|------|--------------|-------|------------------------|
| 218 | `evaluationSubmitted = false` | Hardcoded false | Query evaluation entity when it exists |

### 2.6 ReportDistributionService.java

| Line | Method/Field | Issue | Required Implementation |
|------|--------------|-------|------------------------|
| 139-142 | Portal distribution | Log only | Implement actual portal notification |
| 181 | `getRecipientEmail()` | Returns null | Proper email resolution with parent contacts |
| 198 | Management email | Hardcoded string | Load from system configuration |

### 2.7 BulkReportGenerationService.java

| Line | Method/Field | Issue | Required Implementation |
|------|--------------|-------|------------------------|
| 521-522 | Report type handling | Returns placeholder | Implement specific report generation for each type |

---

## 3. Controller Layer - Stub Methods

### 3.1 ErrorController.java

| Line | Method | Issue | Required Implementation |
|------|--------|-------|------------------------|
| 12-14 | `accessDenied()` | Template-only | Add error context, user info, attempted URL to model |

### 3.2 InstructorController.java

| Line | Method | Issue | Required Implementation |
|------|--------|-------|------------------------|
| 332-347 | `classReadinessConfirmation()` | Stub | Load confirmation data, class details, checklist status |
| 353-368 | `availabilityConfirmation()` | Stub | Load availability confirmation data |
| 374-393 | `viewStudentRoster()` | Minimal | Load actual student roster data for the class |

### 3.3 ManagementController.java

| Line | Method | Issue | Required Implementation |
|------|--------|-------|------------------------|
| 364-403 | `registrationAnalyticsDashboard()` | All zeros | Query actual registration statistics |
| 409-436 | `registrationWorkflowMonitoring()` | Mock data | Calculate real workflow metrics |
| 442-462 | `registrationPolicies()` | Empty stub | Load policy configuration data |

### 3.4 EquipmentIssueController.java

| Line | Method | Issue | Required Implementation |
|------|--------|-------|------------------------|
| 31-54 | `showReportForm()` | Mock session data | Query actual session data |
| 59-100 | `reportEquipmentIssue()` | Fallback UUID | Require valid sessionId, throw error if missing |

### 3.5 SessionMonitoringController.java

| Line | Method | Issue | Required Implementation |
|------|--------|-------|------------------------|
| 297-314 | `submitSession()` | Unused parameter | Use `notes` parameter in endSession call |

---

## 4. Stub Email Service

### 4.1 NoopEmailService.java

**File**: `src/main/java/com/sahabatquran/webapp/service/impl/NoopEmailService.java`

This is a complete no-op implementation activated when `gmail.enabled=false`. All methods just log instead of sending.

| Method | Line | Required Implementation |
|--------|------|------------------------|
| `sendStudentReportEmail()` | 29-46 | Integrate with SMTP or email service provider |
| `sendReportNotification()` | 96-114 | Implement actual email delivery |

**Note**: Consider implementing a proper email queue with retry logic and delivery tracking.

---

## 5. Functional Test Coverage Gaps

### 5.1 Tests with Conditional Visibility Checks Only (Critical)

These tests only verify UI element visibility without testing actual CRUD operations:

**ManagementTest.java** - 13 methods affected:
- `assignTeachersToPrograms()` - No actual teacher assignment CRUD
- `reviewTeacherAvailabilityAndAssignments()` - Visibility assertions only
- `approveResourceAllocationAndBudget()` - Try-catch fallback, no DB verification
- `monitorTermPreparationProgress()` - Element visibility only
- `finalApprovalForTermActivation()` - No state verification
- `generateStrategicReportsAndAnalytics()` - No report generation verification
- `completeManagementWorkflowIntegration()` - URL verification only
- `accessTeacherLevelAssignmentInterface()` - 404 fallback acceptance
- `assignTeachersToCompetencyLevels()` - Try-catch, no verification
- `dragAndDropTeacherAssignmentTesting()` - Conditional checks, no persistence check
- `realTimeDashboardMonitoringTesting()` - Fallback logging instead of failure
- `reviewTeacherCompetencyAssessment()` - Visibility assertions only
- `completeTeacherLevelAssignmentWorkflow()` - URL/title verification only

### 5.2 Tests with Try-Catch Fallbacks (Silent Failures)

These tests pass even when features aren't implemented:

**AcademicAdminTest.java**:
- `initiateNewTermPlanning()` - Line 48-60
- `configureAssessmentRequirements()` - Try-catch wrapper
- `createClassesAndAssignTeachers()` - Line 118 fallback
- `enrollStudentsInClasses()` - Line 146 fallback

**InstructorTest.java**:
- `shouldSuccessfullyCompleteSessionExecutionWorkflow()` - Lines 76-84 warnings
- `shouldSuccessfullyHandleSessionRescheduleRequest()` - Lines 126-136 warnings

### 5.3 Tests Missing Database State Verification

**StudentReportAlternateTest.java**:
- `shouldHandleIncompleteStudentDataGracefully()` - No PDF quality verification
- `shouldHandleSystemErrorsGracefully()` - No actual error simulation
- `shouldHandleEmptyTermScenarios()` - Conditional skip logic
- `shouldHandleNonExistentReportRegeneration()` - Conditional fallback
- `shouldHandleNetworkInterruptions()` - Skips polling check entirely
- `shouldHandleConcurrentUserAccess()` - Single user tested, no race detection

### 5.4 Tests with Incomplete Assertions

**LoginValidationTest.java**:
- `shouldShowErrorForEmptyUsername()` - No error message content check
- `shouldShowErrorForEmptyPassword()` - No client-side validation check
- `shouldShowErrorForEmptyCredentials()` - No form validation assertions

**CrossTermAnalyticsTest.java**:
- `shouldCompareHistoricalPerformance()` - Duplicate assertions, no data validation

### 5.5 Tests with Commented/Disabled Code

**ManagementTest.java**:
- Line 653-654: `// TODO: This endpoint is not implemented yet`
- Line 686-688: `// TODO: This endpoint is not implemented yet`

---

## 6. Test Coverage Requirements

Each functional test should verify:

1. **Pre-condition**: Database state before operation
2. **Action**: User interaction (form submission, button click)
3. **Post-condition**: Database state after operation
4. **UI Feedback**: Success/error message display
5. **Navigation**: Correct redirect/page update

### 6.1 Missing CRUD Test Coverage

| Feature | Create | Read | Update | Delete | Notes |
|---------|--------|------|--------|--------|-------|
| Teacher Assignment | Partial | Yes | No | No | Only visibility tested |
| Resource Allocation | No | Partial | No | No | Try-catch fallback |
| Report Generation | Partial | Yes | No | No | No content verification |
| Session Management | Partial | Yes | Partial | No | Notes parameter unused |
| Equipment Issues | Yes | Yes | No | No | Mock session data |
| Semester Closure | Partial | Yes | No | No | Debug logs present |

---

## 7. Priority Matrix

### Priority 1: Data Integrity (Must Fix)
1. Remove all hardcoded mock data from analytics services
2. Implement actual database queries for metrics
3. Fix email service to actually send emails (or clearly document as intentional)

### Priority 2: Feature Completion (Should Fix)
1. Complete ClassGenerationService persistence methods
2. Implement permission checking in TeacherAvailabilityService
3. Complete InstructorController stub methods
4. Implement ManagementController analytics queries

### Priority 3: Test Quality (Should Fix)
1. Remove try-catch fallbacks from functional tests
2. Add database state verification to all CRUD tests
3. Implement actual error simulation in error handling tests
4. Remove conditional visibility check patterns

### Priority 4: Code Quality (Nice to Have)
1. Remove debug emoji logs from SemesterClosureController
2. Standardize error response format in ReportsController
3. Remove placeholder term creation in AcademicPlanningController

---

## 10. Verification Checklist

Before marking items complete, verify:

- [ ] No hardcoded values remain in the implementation
- [ ] Database queries return actual data
- [ ] Unit tests cover the new implementation
- [ ] Functional tests verify the complete workflow
- [ ] Error handling is properly implemented
- [ ] Logging is appropriate (no debug logs in production)

---

## 9. Template Layer - Hardcoded Content

### 9.1 instructor/weekly-progress.html

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| Student Names | Hardcoded student list | "Ali Abdullah", "Fatima Zahra", "Omar Hassan" | Iterate over `students` model attribute |
| Progress Status | Static progress bars | Fixed percentages (75%, 60%, 90%) | Dynamic calculation from session attendance |
| Grade Options | Duplicate/hardcoded options | Repeated A/B/C/D/E values | Use Thymeleaf iteration with enum values |

### 9.2 analytics/cross-term-analytics.html

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| Enrollment Stats | Hardcoded counts | "120→150", "+25%", "+12%" | Bind to analytics DTO fields |
| Satisfaction Scores | Static values | "4.2→4.5", "+7%" | Query from feedback_answers aggregate |
| Teacher Performance | Mock metrics | Fixed percentage bars | Calculate from actual performance data |
| Trend Charts | Sample data | Hardcoded chart datasets | Pass dynamic data from controller |

### 9.3 monitoring/feedback-analytics.html

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| Filter Dropdowns | Hardcoded options | Static term/program options | Dynamic options from database |
| Summary Cards | Mock statistics | "245 responses", "4.3 avg" | Real-time aggregation |
| Category Breakdown | Static percentages | Fixed pie chart values | Calculate from feedback_answers |

### 9.4 instructor/class-attendance.html

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| Student Roster | Sample students | Hardcoded names and attendance | Load from class enrollment |
| Attendance Rate | Static percentage | Fixed "92%" | Calculate from session records |

### 9.5 management/dashboard.html

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| KPI Cards | Mock metrics | Static numbers | Query from respective services |
| Quick Actions | Placeholder links | Non-functional buttons | Wire to actual endpoints |
| Recent Activity | Sample data | Hardcoded activity list | Load from audit trail |

### 9.6 reports/student-report.html

| Line | Issue | Current Value | Required Implementation |
|------|-------|---------------|------------------------|
| Grade Table | Sample grades | Hardcoded A/B values | Load from student_assessments |
| Teacher Comments | Placeholder text | "Lorem ipsum" style content | Load from actual feedback |

### 9.7 Common Patterns Across Templates

**Repeated Issues:**
1. **Static select options** - Many dropdowns have hardcoded `<option>` tags instead of Thymeleaf `th:each`
2. **Sample statistics** - Dashboard cards show placeholder numbers
3. **Hardcoded table rows** - Data tables with static `<tr>` elements instead of iteration
4. **Mock chart data** - JavaScript chart configurations with inline sample data

---

## 11. Related Documentation

- [Testing Strategy](./TESTING.md)
- [Security Architecture](./SECURITY.md)
- [API Documentation](./API.md)

---

*Last Updated: 2025-12-30*
*Generated from codebase analysis*
