# Kindergarten Management System - Playwright Test Suite

## Overview
This test suite provides comprehensive end-to-end testing for the Kindergarten Management System using Microsoft Playwright. It covers all major functionalities including user authentication, student management, teacher management, course management, and attendance tracking.

## Test Categories

### 1. Authentication Tests (`LoginTest.java`)
- Valid login with correct credentials
- Invalid login attempts
- Empty credential validation
- Individual field validation (username/password)

### 2. Navigation and UI Tests (`NavigationAndUITest.java`)
- Navigation between different modules
- Sidebar navigation functionality
- Responsive layout testing
- UI component visibility
- Configuration button functionality
- Direct URL access testing

### 3. Student Management Tests
#### Basic CRUD Operations (`StudentCRUDTest.java`)
- Create new students
- Edit existing students
- Delete students
- Search functionality
- Show all students

#### Validation Tests (`StudentValidationTest.java`)
- Name length validation (2-50 characters)
- Phone number format validation (10-11 digits)
- Age validation (18-60 months)
- Required field validation
- XSS prevention testing

#### Enhanced Management (`StudentManagementTest.java`)
- Multiple student creation
- Advanced search scenarios
- Edit and cancel operations
- Age-based class suggestions

### 4. Teacher Management Tests (`TeacherCRUDTest.java`)
- Teacher creation with full details
- Email validation
- Phone number validation
- Subject and hire date requirements
- Search and filter functionality
- Edit and delete operations

### 5. Course Management Tests (`CourseCRUDTest.java`)
- Course creation with descriptions
- Duration validation (positive integers)
- Teacher assignment
- Schedule management
- Search and filter functionality
- Edit and delete operations

### 6. Attendance Management Tests (`AttendanceTest.java`)
- Mark attendance with different statuses:
  - Present
  - Absent with Permission
  - Absent without Permission
- Edit attendance records
- Weekly attendance reports
- Today's attendance view
- Attendance notes functionality

### 7. Integration Tests (`IntegrationTest.java`)
- Complete workflow testing (Teacher → Course → Student → Attendance)
- Data consistency across modules
- Cross-module search functionality
- Attendance reporting workflows
- Edit workflows with data relationships

### 8. Security and Error Handling Tests (`SecurityAndErrorHandlingTest.java`)
- Unauthorized access prevention
- SQL injection prevention
- XSS attack prevention
- Session timeout handling
- Concurrent user actions
- Invalid data type handling
- Large data input handling
- Network error handling
- Browser navigation testing

### 9. Performance and Load Tests (`PerformanceAndLoadTest.java`)
- Page load time validation (< 5 seconds)
- Navigation performance (< 3 seconds)
- Bulk data creation testing
- Search performance (< 2 seconds)
- Grid rendering performance
- Form validation performance
- Memory usage with large datasets
- Concurrent operations testing
- Database operation performance

## Test Data Scenarios

### Student Age Categories
- **Lớp Mầm (Nursery)**: 18-36 months
- **Lớp Chồi (Sprout)**: 37-48 months  
- **Lớp Lá (Leaf)**: 49-60 months

### Attendance Statuses
- **Present (Có mặt)**: Student attended class
- **Absent with Permission (Vắng có phép)**: Excused absence
- **Absent without Permission (Vắng không phép)**: Unexcused absence

### Validation Rules Tested
- Student names: 2-50 characters
- Phone numbers: 10-11 digits, numbers only
- Email addresses: Valid email format
- Ages: 18-60 months for enrollment eligibility
- Course duration: Positive integers only

## Running the Tests

### Prerequisites
1. Java 17 or higher
2. Maven 3.6 or higher
3. Kindergarten Management System running on `http://localhost:8080`
4. Default admin credentials: username=`admin`, password=`admin123`

### Installation
```bash
# Install Playwright browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Execution Options

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=LoginTest
mvn test -Dtest=StudentCRUDTest
mvn test -Dtest=IntegrationTest
```

#### Run Test Suite
```bash
mvn test -Dtest=TestSuite
```

#### Run with Headed Browser (for debugging)
Modify `BaseTest.java` and change:
```java
browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
```

### Test Configuration

#### Base URL Configuration
Default: `http://localhost:8080`
To change, modify `BASE_URL` in `BaseTest.java`

#### Browser Configuration
- Default: Chromium (headless)
- Supported: Chromium, Firefox, WebKit
- Modify in `BaseTest.java` for different browsers

## Test Coverage

### Functional Coverage
- ✅ User Authentication
- ✅ Student Management (CRUD)
- ✅ Teacher Management (CRUD)
- ✅ Course Management (CRUD)
- ✅ Attendance Management
- ✅ Search and Filter Operations
- ✅ Data Validation
- ✅ Navigation and UI

### Non-Functional Coverage
- ✅ Performance Testing
- ✅ Security Testing
- ✅ Error Handling
- ✅ Cross-browser Compatibility
- ✅ Responsive Design
- ✅ Data Integrity

### Edge Cases Covered
- Invalid input data
- Boundary value testing
- Concurrent operations
- Network failures
- Large datasets
- XSS and SQL injection attempts

## Test Reports

Test results are generated in:
- `target/surefire-reports/` - JUnit XML reports
- Console output with detailed test execution logs

## Maintenance

### Adding New Tests
1. Create test class extending `BaseTest`
2. Add `@BeforeEach` method for login if needed
3. Follow naming convention: `test[Functionality][Scenario]()`
4. Add to `TestSuite.java` if part of main suite

### Updating Selectors
If UI changes, update selectors in test files:
- Use stable attributes like `label`, `data-testid`
- Prefer semantic selectors over CSS classes
- Use Vaadin component selectors when possible

### Performance Thresholds
Current thresholds (adjust as needed):
- Page load: < 5 seconds
- Navigation: < 3 seconds  
- Search: < 2 seconds
- Form validation: < 2 seconds
- Database operations: < 10 seconds

## Troubleshooting

### Common Issues
1. **Tests fail with timeout**: Increase wait times or check application startup
2. **Element not found**: Verify selectors match current UI
3. **Login fails**: Confirm default credentials are correct
4. **Performance tests fail**: Adjust thresholds based on environment

### Debug Mode
Run with headed browser and add breakpoints:
```java
page.pause(); // Pauses execution for manual inspection
```

## Contributing

When adding new features to the main application:
1. Add corresponding test cases
2. Update validation tests for new rules
3. Add integration tests for new workflows
4. Update this README with new test scenarios