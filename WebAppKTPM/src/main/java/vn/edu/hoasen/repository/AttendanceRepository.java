package vn.edu.hoasen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hoasen.model.Attendance;
import vn.edu.hoasen.model.Student;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);
}