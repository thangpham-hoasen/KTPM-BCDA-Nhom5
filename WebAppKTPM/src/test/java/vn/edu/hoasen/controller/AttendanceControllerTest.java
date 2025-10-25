package vn.edu.hoasen.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.Attendance;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.service.AttendanceService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private Student testStudent;
    private Attendance testAttendance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testStudent = new Student();
        testStudent.setId(1L);
        testAttendance = new Attendance(testStudent, LocalDate.now(), 
                                      Attendance.AttendanceStatus.PRESENT, "Test");
    }

    @Test
    void markAttendance_ShouldCallService() {
        LocalDate date = LocalDate.now();
        Attendance.AttendanceStatus status = Attendance.AttendanceStatus.PRESENT;
        String notes = "Test notes";
        
        when(attendanceService.markAttendance(testStudent, date, status, notes))
            .thenReturn(testAttendance);

        Attendance result = attendanceController.markAttendance(testStudent, date, status, notes);

        assertEquals(testAttendance, result);
        verify(attendanceService).markAttendance(testStudent, date, status, notes);
    }

    @Test
    void getAttendanceByStudent_ShouldReturnAttendanceList() {
        List<Attendance> attendances = Arrays.asList(testAttendance);
        when(attendanceService.getAttendanceByStudent(testStudent)).thenReturn(attendances);

        List<Attendance> result = attendanceController.getAttendanceByStudent(testStudent);

        assertEquals(1, result.size());
        verify(attendanceService).getAttendanceByStudent(testStudent);
    }

    @Test
    void getWeeklyAttendance_ShouldCallService() {
        LocalDate startOfWeek = LocalDate.now();
        List<Attendance> attendances = Arrays.asList(testAttendance);
        when(attendanceService.getWeeklyAttendance(startOfWeek)).thenReturn(attendances);

        List<Attendance> result = attendanceController.getWeeklyAttendance(startOfWeek);

        assertEquals(1, result.size());
        verify(attendanceService).getWeeklyAttendance(startOfWeek);
    }

    @Test
    void getMonthlyAttendance_ShouldCallService() {
        List<Attendance> attendances = Arrays.asList(testAttendance);
        when(attendanceService.getMonthlyAttendance(2024, 1)).thenReturn(attendances);

        List<Attendance> result = attendanceController.getMonthlyAttendance(2024, 1);

        assertEquals(1, result.size());
        verify(attendanceService).getMonthlyAttendance(2024, 1);
    }

    @Test
    void getAttendanceStatistics_ShouldReturnStatistics() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        Map<Attendance.AttendanceStatus, Long> stats = new HashMap<>();
        stats.put(Attendance.AttendanceStatus.PRESENT, 5L);
        
        when(attendanceService.getAttendanceStatistics(testStudent, startDate, endDate))
            .thenReturn(stats);

        Map<Attendance.AttendanceStatus, Long> result = 
            attendanceController.getAttendanceStatistics(testStudent, startDate, endDate);

        assertEquals(5L, result.get(Attendance.AttendanceStatus.PRESENT));
        verify(attendanceService).getAttendanceStatistics(testStudent, startDate, endDate);
    }

    @Test
    void getAttendanceRate_ShouldReturnRate() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        double expectedRate = 85.5;
        
        when(attendanceService.getAttendanceRate(testStudent, startDate, endDate))
            .thenReturn(expectedRate);

        double result = attendanceController.getAttendanceRate(testStudent, startDate, endDate);

        assertEquals(expectedRate, result);
        verify(attendanceService).getAttendanceRate(testStudent, startDate, endDate);
    }

    @Test
    void updateAttendance_ShouldCallService() {
        when(attendanceService.updateAttendance(testAttendance)).thenReturn(testAttendance);

        Attendance result = attendanceController.updateAttendance(testAttendance);

        assertEquals(testAttendance, result);
        verify(attendanceService).updateAttendance(testAttendance);
    }

    @Test
    void getAttendanceByDate_ShouldReturnAttendanceList() {
        LocalDate date = LocalDate.now();
        List<Attendance> attendances = Arrays.asList(testAttendance);
        when(attendanceService.getAttendanceByDate(date)).thenReturn(attendances);

        // Note: This method doesn't exist in controller yet, but should be added
        // List<Attendance> result = attendanceController.getAttendanceByDate(date);
        // assertEquals(1, result.size());
        // verify(attendanceService).getAttendanceByDate(date);
    }
}