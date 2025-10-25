package vn.edu.hoasen.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceTest {

    @Test
    void createAttendance_ShouldSetCorrectValues() {
        Student student = new Student();
        LocalDate date = LocalDate.now();
        Attendance.AttendanceStatus status = Attendance.AttendanceStatus.PRESENT;
        String notes = "Test notes";
        
        Attendance attendance = new Attendance(student, date, status, notes);
        
        assertEquals(student, attendance.getStudent());
        assertEquals(date, attendance.getDate());
        assertEquals(status, attendance.getStatus());
        assertEquals(notes, attendance.getNotes());
    }

    @Test
    void attendanceStatus_ShouldHaveCorrectDisplayNames() {
        assertEquals("attendance.present", Attendance.AttendanceStatus.PRESENT.getMessageKey());
        assertEquals("attendance.absent_with_permission", Attendance.AttendanceStatus.ABSENT_WITH_PERMISSION.getMessageKey());
        assertEquals("attendance.absent_without_permission", Attendance.AttendanceStatus.ABSENT_WITHOUT_PERMISSION.getMessageKey());
    }
}