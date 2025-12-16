# Test Cases Documentation

## Overview
This document provides a comprehensive list of all test cases in the Kindergarten Management System Playwright test suite, organized by test class.

---

## 1. Authentication Tests (`AuthenticationTest.java`)

### 1.1 testValidLogin
**Description:** Verifies that users can successfully log in with valid credentials (admin/admin123) and are redirected to the home page.

### 1.2 testInvalidCredentials
**Description:** Ensures that login fails when invalid username and password are provided, keeping the user on the login page.

### 1.3 testEmptyCredentials
**Description:** Tests login behavior when both username and password fields are left empty.

### 1.4 testEmptyUsername
**Description:** Verifies login behavior when only the username field is empty while password is provided.

### 1.5 testEmptyPassword
**Description:** Verifies login behavior when only the password field is empty while username is provided.

### 1.6 testSQLInjectionPrevention
**Description:** Tests that SQL injection attempts (e.g., `admin' OR '1'='1`) are properly prevented and do not grant unauthorized access.

### 1.7 testXSSPrevention
**Description:** Ensures that XSS attack attempts using script tags in login fields are blocked and sanitized.

### 1.8 testUnauthorizedAccess
**Description:** Verifies that unauthenticated users cannot access protected pages and are redirected to login.

### 1.9 testSessionPersistence
**Description:** Tests that user sessions persist across page navigation after successful login.

### 1.10 testLogout
**Description:** Verifies that the logout functionality works correctly and redirects users back to the login page.

### 1.11 testCaseSensitiveCredentials
**Description:** Ensures that login credentials are case-sensitive (ADMIN/ADMIN123 should fail).

### 1.12 testWhitespaceHandling
**Description:** Tests how the system handles leading/trailing whitespace in username and password fields.

### 1.13 testMultipleFailedAttempts
**Description:** Verifies system behavior after multiple consecutive failed login attempts.

### 1.14 testDirectURLAccessAfterLogin
**Description:** Tests that logged-in users accessing the login page directly are handled appropriately.

### 1.15 testPasswordFieldSecurity
**Description:** Verifies that the password field has type="password" to mask input characters.

### 1.16 testFormValidation
**Description:** Tests form validation with extremely long input strings (1000 characters).

---

## 2. Navigation and UI Tests (`NavigationAndUITest.java`)

### 2.1 testNavigationToStudents
**Description:** Verifies navigation to the Students module displays the correct page title and UI elements.

### 2.2 testNavigationToCourses
**Description:** Verifies navigation to the Courses module displays the correct page title and UI elements.

### 2.3 testNavigationToTeachers
**Description:** Verifies navigation to the Teachers module displays the correct page title and UI elements.

### 2.4 testNavigationToAttendance
**Description:** Verifies navigation to the Attendance module displays the correct page title and UI elements.

### 2.5 testSidebarNavigation
**Description:** Tests sequential navigation through all modules using the sidebar menu.

### 2.6 testConfigurationButton
**Description:** Verifies that the Configuration button or menu bar is visible in the UI.

### 2.7 testResponsiveLayout
**Description:** Tests UI responsiveness at different viewport sizes (1200x800, 800x600, 400x600).

### 2.8 testGridVisibility
**Description:** Ensures that data grids are visible in all modules (Students, Courses, Teachers, Attendance).

### 2.9 testFormLayoutResponsiveness
**Description:** Tests form layout behavior at different screen sizes in the Students module.

### 2.10 testSearchFunctionality
**Description:** Verifies that search fields and buttons are present in all applicable modules.

### 2.11 testButtonStates
**Description:** Tests that appropriate buttons (New, Cancel) are visible/hidden based on context.

### 2.12 testPageTitles
**Description:** Verifies that each module displays the correct page title.

### 2.13 testDirectURLAccess
**Description:** Tests that users can directly access module pages via URL after authentication.

### 2.14 testCompleteNavigation
**Description:** Performs a complete navigation cycle through all modules to verify routing works correctly.

---

## 3. Student Management Tests (`StudentManagementTest.java`)

### 3.1 testCreateStudent
**Description:** Verifies that a new student can be created with valid data and appears in the grid.

### 3.2 testEditStudent
**Description:** Tests editing an existing student's information and verifying the changes are saved.

### 3.3 testDeleteStudent
**Description:** Verifies that a student can be deleted and no longer appears in the grid after deletion.

### 3.4 testCancelEdit
**Description:** Tests that canceling an edit operation does not save changes to the student record.

### 3.5 testSearchStudent
**Description:** Verifies search functionality filters students correctly based on search criteria.

### 3.6 testShowAllStudents
**Description:** Tests that the "Show All" button displays all students after a filtered search.

### 3.7 testValidStudentCreation
**Description:** Creates a student with all valid data to verify successful creation workflow.

### 3.8 testNameTooShort
**Description:** Tests validation when student name is only 1 character (below 2-character minimum).

### 3.9 testNameTooLong
**Description:** Tests validation when student name exceeds 50 characters maximum.

### 3.10 testInvalidPhoneNumber
**Description:** Verifies that phone numbers with less than 10 digits are rejected.

### 3.11 testPhoneWithLetters
**Description:** Tests that phone numbers containing letters are rejected.

### 3.12 testStudentTooYoung
**Description:** Verifies that students under 18 months old are rejected (age validation).

### 3.13 testStudentTooOld
**Description:** Verifies that students over 60 months old are rejected (age validation).

### 3.14 testMissingRequiredFields
**Description:** Tests that form submission fails when required fields are empty.

### 3.15 testParentNameTooShort
**Description:** Verifies validation when parent name is only 1 character.

### 3.16 testParentNameTooLong
**Description:** Verifies validation when parent name exceeds 50 characters.

---

## 4. Teacher Management Tests (`TeacherManagementTest.java`)

### 4.1 testCreateTeacher
**Description:** Verifies that a new teacher can be created with valid data including name, email, phone, subject, and hire date.

### 4.2 testEditTeacher
**Description:** Tests editing an existing teacher's information and verifying changes are saved.

### 4.3 testDeleteTeacher
**Description:** Verifies that a teacher can be deleted and no longer appears in the grid.

### 4.4 testCancelEdit
**Description:** Tests that canceling an edit operation does not save changes to the teacher record.

### 4.5 testSearchTeacher
**Description:** Verifies search functionality filters teachers correctly based on search criteria.

### 4.6 testShowAllTeachers
**Description:** Tests that the "Show All" button displays all teachers after a filtered search.

### 4.7 testEmailValidation
**Description:** Verifies that invalid email formats are rejected during teacher creation.

### 4.8 testPhoneValidation
**Description:** Tests that phone numbers with less than 10 digits are rejected.

### 4.9 testNameTooShort
**Description:** Tests validation when teacher name is only 1 character (below minimum).

### 4.10 testNameTooLong
**Description:** Tests validation when teacher name exceeds 50 characters maximum.

### 4.11 testMissingRequiredFields
**Description:** Tests that form submission fails when required fields are empty.

### 4.12 testPhoneWithLetters
**Description:** Tests that phone numbers containing letters are rejected.

### 4.13 testValidTeacherCreation
**Description:** Creates a teacher with all valid data to verify successful creation workflow.

---

## 5. Course Management Tests (`CourseManagementTest.java`)

### 5.1 testCreateCourse
**Description:** Verifies that a new course can be created with name, description, teacher, duration, and schedule.

### 5.2 testEditCourse
**Description:** Tests editing an existing course's information and verifying changes are saved.

### 5.3 testDeleteCourse
**Description:** Verifies that a course can be deleted and no longer appears in the grid.

### 5.4 testCancelEdit
**Description:** Tests that canceling an edit operation does not save changes to the course record.

### 5.5 testSearchCourse
**Description:** Verifies search functionality filters courses correctly based on search criteria.

### 5.6 testShowAllCourses
**Description:** Tests that the "Show All" button displays all courses after a filtered search.

### 5.7 testCourseNameMinLength
**Description:** Tests that course names with exactly 2 characters (minimum) are accepted.

### 5.8 testCourseNameTooShort
**Description:** Tests validation when course name is only 1 character (below minimum).

### 5.9 testCourseNameMaxLength
**Description:** Tests that course names with exactly 100 characters (maximum) are accepted.

### 5.10 testCourseNameTooLong
**Description:** Tests validation when course name exceeds 100 characters maximum.

### 5.11 testDurationZero
**Description:** Verifies that course duration of 0 is rejected (must be positive).

### 5.12 testDurationNegative
**Description:** Verifies that negative course duration values are rejected.

### 5.13 testDurationPositive
**Description:** Tests that positive duration values (minimum 1) are accepted.

### 5.14 testMissingCourseName
**Description:** Tests that form submission fails when course name is missing.

### 5.15 testMissingTeacherName
**Description:** Tests that form submission fails when teacher name is missing.

### 5.16 testMissingDuration
**Description:** Tests that form submission fails when duration is missing.

### 5.17 testMissingSchedule
**Description:** Tests that form submission fails when schedule is missing.

### 5.18 testAllFieldsEmpty
**Description:** Tests that form submission fails when all fields are empty.

### 5.19 testCourseNameWithSpecialCharacters
**Description:** Verifies that course names with special characters (e.g., "C++ Programming & Design!") are accepted.

### 5.20 testCourseNameWithUnicode
**Description:** Tests that course names with Unicode characters (Vietnamese, Chinese, Japanese) are handled correctly.

### 5.21 testSearchEmptyString
**Description:** Tests search behavior when an empty string is provided.

### 5.22 testSearchNonExistent
**Description:** Tests search behavior when searching for non-existent course names.

### 5.23 testSearchCaseSensitivity
**Description:** Verifies how search handles case sensitivity (lowercase vs uppercase).

### 5.24 testCancelDeleteDialog
**Description:** Tests that canceling a delete confirmation dialog preserves the course.

### 5.25 testCancelSaveDialog
**Description:** Tests that canceling a save confirmation dialog does not create the course.

### 5.26 testLongDescription
**Description:** Tests that courses with very long descriptions are handled correctly.

### 5.27 testVeryLargeDuration
**Description:** Tests that extremely large duration values (999999) are accepted.

---

## 6. Attendance Management Tests (`AttendanceTest.java`)

### 6.1 testMarkAttendancePresent
**Description:** Verifies that attendance can be marked as "Present" for a student with notes.

### 6.2 testMarkAttendanceAbsentWithPermission
**Description:** Tests marking attendance as "Absent with Permission" (excused absence).

### 6.3 testMarkAttendanceAbsentWithoutPermission
**Description:** Tests marking attendance as "Absent without Permission" (unexcused absence).

### 6.4 testEditAttendance
**Description:** Verifies that existing attendance records can be edited and changes are saved.

### 6.5 testCancelEditAttendance
**Description:** Tests that canceling an attendance edit does not save changes.

### 6.6 testWeeklyReport
**Description:** Tests the weekly attendance report functionality.

### 6.7 testTodayAttendance
**Description:** Verifies that today's attendance view displays current day's records.

### 6.8 testMissingRequiredFields
**Description:** Tests that attendance cannot be marked without selecting required fields.

### 6.9 testFormValidation
**Description:** Tests form validation when student is selected but status is missing.

### 6.10 testClearFormAfterSave
**Description:** Verifies that the form is cleared and hidden after successfully saving attendance.

---

## 7. Integration Tests (`IntegrationTest.java`)

### 7.1 testCompleteWorkflow
**Description:** Tests the complete workflow: create teacher → create course → create students → mark attendance. Verifies data consistency across all modules.

### 7.2 testEditWorkflow
**Description:** Tests editing a student and verifies that the changes are reflected in the attendance records.

### 7.3 testDataConsistency
**Description:** Verifies data consistency between Teachers and Courses modules when a teacher is assigned to a course.

### 7.4 testCrossModuleSearch
**Description:** Tests search functionality across different modules (Teachers and Students) using unique identifiers.

### 7.5 testAttendanceReporting
**Description:** Creates multiple students, marks their attendance, and verifies all records appear in the attendance grid.

---

## 8. Security and Error Handling Tests (`SecurityAndErrorHandlingTest.java`)

### 8.1 testUnauthorizedAccess
**Description:** Verifies that unauthenticated users cannot access protected pages without logging in.

### 8.2 testInvalidURLs
**Description:** Tests that accessing non-existent URLs is handled gracefully (404 or redirect).

### 8.3 testSQLInjectionAttempts
**Description:** Tests that SQL injection attempts in search fields are prevented and don't break the application.

### 8.4 testXSSAttempts
**Description:** Verifies that XSS script injection attempts in form fields are sanitized and handled safely.

### 8.5 testSessionTimeout
**Description:** Tests that session timeout is handled gracefully by clearing cookies and requiring re-authentication.

### 8.6 testConcurrentUserActions
**Description:** Simulates rapid concurrent form submissions to test race condition handling.

### 8.7 testInvalidDataTypes
**Description:** Tests that invalid data types (e.g., text in number fields) are rejected by validation.

### 8.8 testLargeDataInput
**Description:** Tests that extremely large input values (1000 characters) are handled appropriately.

### 8.9 testNetworkErrorHandling
**Description:** Simulates network errors by going offline and verifies graceful error handling.

### 8.10 testBrowserBackButton
**Description:** Tests that browser back/forward navigation works correctly across different pages.

---

## Test Execution Summary

### Total Test Cases: 108

- **Authentication Tests:** 16 test cases
- **Navigation and UI Tests:** 14 test cases
- **Student Management Tests:** 16 test cases
- **Teacher Management Tests:** 13 test cases
- **Course Management Tests:** 27 test cases
- **Attendance Management Tests:** 10 test cases
- **Integration Tests:** 5 test cases
- **Security and Error Handling Tests:** 10 test cases

### Test Coverage Areas

1. **Functional Testing**
   - CRUD operations (Create, Read, Update, Delete)
   - Search and filter functionality
   - Form validation
   - Navigation and routing

2. **Security Testing**
   - Authentication and authorization
   - SQL injection prevention
   - XSS attack prevention
   - Session management

3. **Validation Testing**
   - Field length constraints
   - Data type validation
   - Required field validation
   - Format validation (email, phone)

4. **Integration Testing**
   - Cross-module workflows
   - Data consistency
   - Relationship integrity

5. **UI/UX Testing**
   - Responsive design
   - Button states
   - Form behavior
   - Grid visibility

6. **Error Handling**
   - Network errors
   - Invalid input
   - Concurrent operations
   - Browser navigation

### Test Data

- **Admin Credentials:** username=`admin`, password=`admin123`
- **Database:** SQLite (`kindergarten.db`)
- **Base URL:** `http://localhost:8080`

### Validation Rules

- **Student Names:** 2-50 characters
- **Parent Names:** 2-50 characters
- **Teacher Names:** 2-50 characters
- **Course Names:** 2-100 characters
- **Phone Numbers:** 10-11 digits, numbers only
- **Email:** Valid email format
- **Student Age:** 18-60 months
- **Course Duration:** Positive integers only

### Age-Based Class Categories

- **Lớp Mầm (Nursery):** 18-36 months
- **Lớp Chồi (Sprout):** 37-48 months
- **Lớp Lá (Leaf):** 49-60 months

### Attendance Status Options

- **Có mặt (Present):** Student attended class
- **Vắng có phép (Absent with Permission):** Excused absence
- **Vắng không phép (Absent without Permission):** Unexcused absence
