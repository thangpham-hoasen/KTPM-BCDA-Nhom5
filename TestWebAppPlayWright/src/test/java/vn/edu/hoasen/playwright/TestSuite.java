package vn.edu.hoasen.playwright;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    NavigationAndUITest.class,
    StudentManagementTest.class,
    StudentValidationTest.class,
    StudentCRUDTest.class,
    AttendanceTest.class,
    IntegrationTest.class,
    SecurityAndErrorHandlingTest.class,
    PerformanceAndLoadTest.class
})
public class TestSuite {
    // This class serves as a test suite runner for all Playwright tests
    // Run this class to execute all test cases in the proper order
}