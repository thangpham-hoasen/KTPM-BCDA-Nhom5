package vn.edu.hoasen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vn.edu.hoasen.model.Attendance;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class AttendanceController {
    
    @Autowired
    private AttendanceService attendanceService;

    public Attendance markAttendance(Student student, LocalDate date, 
                                   Attendance.AttendanceStatus status, String notes) {
        return attendanceService.markAttendance(student, date, status, notes);
    }

    public List<Attendance> getAttendanceByStudent(Student student) {
        return attendanceService.getAttendanceByStudent(student);
    }

    public List<Attendance> getWeeklyAttendance(LocalDate startOfWeek) {
        return attendanceService.getWeeklyAttendance(startOfWeek);
    }

    public List<Attendance> getMonthlyAttendance(int year, int month) {
        return attendanceService.getMonthlyAttendance(year, month);
    }

    public Map<Attendance.AttendanceStatus, Long> getAttendanceStatistics(
            Student student, LocalDate startDate, LocalDate endDate) {
        return attendanceService.getAttendanceStatistics(student, startDate, endDate);
    }

    public double getAttendanceRate(Student student, LocalDate startDate, LocalDate endDate) {
        return attendanceService.getAttendanceRate(student, startDate, endDate);
    }

    public Attendance updateAttendance(Attendance attendance) {
        return attendanceService.updateAttendance(attendance);
    }
}