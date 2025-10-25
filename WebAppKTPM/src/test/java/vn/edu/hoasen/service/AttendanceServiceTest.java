package vn.edu.hoasen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.Attendance;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

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
    void markAttendance_ShouldSaveAttendance() {
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(testAttendance);

        Attendance result = attendanceService.markAttendance(testStudent, LocalDate.now(), 
                                                           Attendance.AttendanceStatus.PRESENT, "Test");

        assertNotNull(result);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void getWeeklyAttendance_ShouldReturnCorrectRange() {
        LocalDate startOfWeek = LocalDate.now();
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        List<Attendance> attendances = Arrays.asList(testAttendance);
        
        when(attendanceRepository.findByDateBetween(startOfWeek, endOfWeek)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getWeeklyAttendance(startOfWeek);

        assertEquals(1, result.size());
        verify(attendanceRepository).findByDateBetween(startOfWeek, endOfWeek);
    }

    @Test
    void getAttendanceRate_ShouldCalculateCorrectly() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        Attendance presentAttendance = new Attendance(testStudent, LocalDate.now(), 
                                                    Attendance.AttendanceStatus.PRESENT, "");
        Attendance absentAttendance = new Attendance(testStudent, LocalDate.now().minusDays(1), 
                                                   Attendance.AttendanceStatus.ABSENT_WITHOUT_PERMISSION, "");
        
        List<Attendance> attendances = Arrays.asList(presentAttendance, absentAttendance);
        when(attendanceRepository.findByStudentAndDateBetween(testStudent, startDate, endDate))
            .thenReturn(attendances);

        double rate = attendanceService.getAttendanceRate(testStudent, startDate, endDate);

        assertEquals(50.0, rate);
    }

    @Test
    void getAttendanceStatistics_ShouldReturnCorrectCounts() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        Attendance presentAttendance = new Attendance(testStudent, LocalDate.now(), 
                                                    Attendance.AttendanceStatus.PRESENT, "");
        List<Attendance> attendances = Arrays.asList(presentAttendance);
        
        when(attendanceRepository.findByStudentAndDateBetween(testStudent, startDate, endDate))
            .thenReturn(attendances);

        Map<Attendance.AttendanceStatus, Long> stats = 
            attendanceService.getAttendanceStatistics(testStudent, startDate, endDate);

        assertEquals(1L, stats.get(Attendance.AttendanceStatus.PRESENT));
    }

    @Test
    void updateAttendance_ShouldCallRepository() {
        when(attendanceRepository.save(testAttendance)).thenReturn(testAttendance);

        Attendance result = attendanceService.updateAttendance(testAttendance);

        assertEquals(testAttendance, result);
        verify(attendanceRepository).save(testAttendance);
    }

    @Test
    void getMonthlyAttendance_ShouldReturnCorrectRange() {
        LocalDate startOfMonth = LocalDate.of(2024, 1, 1);
        LocalDate endOfMonth = LocalDate.of(2024, 1, 31);
        List<Attendance> attendances = Arrays.asList(testAttendance);
        
        when(attendanceRepository.findByDateBetween(startOfMonth, endOfMonth)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getMonthlyAttendance(2024, 1);

        assertEquals(1, result.size());
        verify(attendanceRepository).findByDateBetween(startOfMonth, endOfMonth);
    }
}