package vn.edu.hoasen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hoasen.model.Attendance;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;

    public Attendance markAttendance(Student student, LocalDate date, 
                                   Attendance.AttendanceStatus status, String notes) {
        Attendance attendance = new Attendance(student, date, status, notes);
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByStudent(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public List<Attendance> getWeeklyAttendance(LocalDate startOfWeek) {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return attendanceRepository.findByDateBetween(startOfWeek, endOfWeek);
    }

    public List<Attendance> getMonthlyAttendance(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        return attendanceRepository.findByDateBetween(startOfMonth, endOfMonth);
    }

    public Map<Attendance.AttendanceStatus, Long> getAttendanceStatistics(
            Student student, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository
            .findByStudentAndDateBetween(student, startDate, endDate);
        
        return attendances.stream()
            .collect(Collectors.groupingBy(
                Attendance::getStatus, 
                Collectors.counting()
            ));
    }

    public double getAttendanceRate(Student student, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository
            .findByStudentAndDateBetween(student, startDate, endDate);
        
        long totalDays = attendances.size();
        long presentDays = attendances.stream()
            .mapToLong(a -> a.getStatus() == Attendance.AttendanceStatus.PRESENT ? 1 : 0)
            .sum();
        
        return totalDays > 0 ? (double) presentDays / totalDays * 100 : 0;
    }

    public Attendance updateAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }
}