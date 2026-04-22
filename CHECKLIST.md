# Java Backend Timekeeping - Developer Checklist

## Phase 1 – Project Bootstrap
- [x] Generate Spring Boot 3.2 project (Java 17, Maven)
- [x] Add all dependencies to pom.xml (Web, Security, JPA, Validation, Mail, Thymeleaf, JWT, Flyway, Lombok, MapStruct, OpenAPI, POI)
- [x] Configure application.yml (datasource, flyway, mail, JWT, async)
- [x] Create base package structure
- [x] Implement GlobalExceptionHandler + ApiResponse wrapper
- [x] Implement common enums: RoleType, ApprovalStatus, EmployeeStatus

## Phase 2 – Security
- [x] Implement JwtUtil (generate, validate, extract claims)
- [x] Implement JwtAuthenticationFilter (reads Bearer token, sets SecurityContext)
- [x] Implement CustomUserDetails + UserDetailsServiceImpl
- [x] Configure SecurityConfig (stateless, JWT filter chain, role-based paths)
- [x] Configure CorsConfig

## Phase 3 – Database Schema
- [x] Write V1–V14 Flyway migrations for all 23 tables
- [x] Create all JPA @Entity classes (24 entities: +PublicHoliday, AuditLog, LeavePolicy)
- [x] Create all JpaRepository interfaces with custom @Query methods

## Phase 4 – Auth Module
- [x] AuthService: login, refreshToken, logout
- [x] AuthController: POST /api/auth/login, /refresh, /logout, GET /api/auth/me
- [x] RefreshTokenStore (DB-backed)

## Phase 5 – Configuration Module (Admin)
- [x] DepartmentService + CRUD endpoints
- [x] PositionService + CRUD endpoints
- [x] EmployeeService + CRUD (auto-creates UserAccount on create)
- [x] ProjectService + CRUD
- [x] ProjectAssignmentService + assign/remove
- [x] WorkingDayService + configure schedule/OT rates

## Phase 6 – Attendance Module
- [x] AttendanceService.checkIn(): validate duplicate, compute late minutes
- [x] AttendanceService.checkOut(): compute early-quit minutes, update AttendanceCheck
- [x] AttendanceService.getMonthlyAttendance(): color-coded calendar grid
- [x] AttendanceController: POST /checkin, /checkout, GET /today, /monthly
- [x] TimeExplanationService: create, list, approve, reject (with email)
- [x] TimeExplanationController

## Phase 7 – Leave Module
- [x] LeaveTypeService + controller (Admin CRUD)
- [x] LeaveBalanceService: compute available days, OT conversion credit
- [x] LeaveService: create, submit, cancel, approve (overlap guard, balance deduction)
- [x] LeaveController
- [x] Email: fire-and-forget on approve/reject

## Phase 8 – OT Module
- [x] OtService: create, approve, reject
- [x] OtConversionService: convert approved OT hours -> compensatory leave days
- [x] OtController

## Phase 9 – Worklog Module
- [x] WorklogService: CRUD for TeamWorklog entries
- [x] ConfirmWorklogService: approve/reject by Manager/Admin
- [x] WorklogController

## Phase 10 – Notification Module
- [x] NotificationService: CRUD on NativeBox (Admin/Staff)
- [x] NotificationController

## Phase 11 – Dashboard Module
- [x] DashboardService: personal info, today status, leave balance, pending counts
- [x] DashboardController

## Phase 12 – Reports Module
- [x] ReportService: daily detail, monthly summary, by-department, by-project
- [x] ExcelExportService (Apache POI)
- [x] ReportController with /export endpoints

## Phase 13 – Email Service
- [x] EmailService with @Async
- [x] Thymeleaf email templates (approval-granted, approval-rejected, submitted)

## Phase 14 – Tests & Polish
- [x] Unit tests: AttendanceServiceTest, LeaveServiceTest, WorklogServiceTest
- [x] Integration tests: AuthControllerTest, all controller happy-path tests
- [x] Springdoc OpenAPI / Swagger UI configuration
- [x] Request/response logging filter

---

## REST API Summary

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| POST | /api/auth/login | Public | JWT login |
| POST | /api/auth/refresh | Public | Refresh access token |
| POST | /api/auth/logout | Any | Revoke refresh token |
| GET | /api/auth/me | Any | Current user profile |
| GET | /api/dashboard | Any | Personal dashboard |
| GET | /api/dashboard/stats | ADMIN, MANAGER | Org-wide KPIs |
| POST | /api/attendance/checkin | Any | Check in |
| POST | /api/attendance/checkout | Any | Check out |
| GET | /api/attendance/today | Any | Today's status |
| GET | /api/attendance/monthly | Any | Monthly grid (own) |
| GET | /api/attendance/monthly/{userId} | ADMIN,MANAGER,STAFF | Monthly grid (any) |
| GET | /api/attendance | ADMIN,MANAGER,STAFF | Paginated attendance list |
| POST | /api/time-explanations | Any | Create explanation |
| GET | /api/time-explanations | Any | List own |
| GET | /api/time-explanations/pending | ADMIN,MANAGER,STAFF | Approval queue |
| POST | /api/time-explanations/{id}/approve | ADMIN,MANAGER,STAFF | Approve |
| POST | /api/time-explanations/{id}/reject | ADMIN,MANAGER,STAFF | Reject |
| POST | /api/notifications | ADMIN,STAFF | Create notice |
| GET | /api/notifications | Any | List notices |
| PUT | /api/notifications/{id} | ADMIN,STAFF | Update notice |
| DELETE | /api/notifications/{id} | ADMIN,STAFF | Delete notice |
| GET | /api/leave/balance | Any | Own leave balance |
| POST | /api/leave/requests | Any | Create leave request |
| GET | /api/leave/requests | Any | List own requests |
| POST | /api/leave/requests/{id}/submit | Any | Submit for approval |
| POST | /api/leave/requests/{id}/cancel | Any | Cancel |
| GET | /api/leave/requests/pending | ADMIN,MANAGER,STAFF | Approval queue |
| POST | /api/leave/requests/{id}/approve | ADMIN,MANAGER,STAFF | Approve |
| POST | /api/leave/requests/{id}/reject | ADMIN,MANAGER,STAFF | Reject |
| POST | /api/ot | Any | Create OT record |
| GET | /api/ot | Any | List own OT |
| GET | /api/ot/pending | ADMIN,MANAGER | Approval queue |
| POST | /api/ot/{id}/approve | ADMIN,MANAGER | Approve OT |
| POST | /api/ot/{id}/reject | ADMIN,MANAGER | Reject OT |
| POST | /api/ot/{id}/convert-to-leave | ADMIN,STAFF | Convert OT to leave |
| POST | /api/worklogs | Any | Create worklog |
| GET | /api/worklogs | Any | List own |
| PUT | /api/worklogs/{id} | Any | Edit own pending |
| DELETE | /api/worklogs/{id} | Any | Delete own pending |
| GET | /api/worklogs/pending | ADMIN,MANAGER | Approval queue |
| POST | /api/worklogs/{id}/approve | ADMIN,MANAGER | Approve |
| POST | /api/worklogs/{id}/reject | ADMIN,MANAGER | Reject |
| GET | /api/config/departments | Any | List departments |
| POST | /api/config/departments | ADMIN | Create dept |
| PUT | /api/config/departments/{id} | ADMIN | Update dept |
| DELETE | /api/config/departments/{id} | ADMIN | Delete dept |
| GET | /api/config/positions | Any | List positions |
| POST | /api/config/positions | ADMIN | Create position |
| GET | /api/config/employees | ADMIN,MANAGER,STAFF | List employees |
| POST | /api/config/employees | ADMIN | Create employee |
| PUT | /api/config/employees/{id} | ADMIN | Update employee |
| GET | /api/config/projects | ADMIN,MANAGER | List projects |
| POST | /api/config/projects | ADMIN | Create project |
| POST | /api/config/projects/{id}/assignments | ADMIN,MANAGER | Assign member |
| DELETE | /api/config/projects/{id}/assignments/{userId} | ADMIN,MANAGER | Remove member |
| GET | /api/reports/attendance/daily | ADMIN,MANAGER,STAFF | Daily report |
| GET | /api/reports/attendance/monthly | ADMIN,MANAGER,STAFF | Monthly report |
| GET | /api/reports/work/by-department | ADMIN | By-dept report |
| GET | /api/reports/work/by-project | ADMIN,MANAGER | By-project report |
| GET | /api/reports/attendance/daily/export | ADMIN,MANAGER,STAFF | Export Excel |
| GET | /api/reports/attendance/monthly/export | ADMIN,MANAGER,STAFF | Export Excel |
