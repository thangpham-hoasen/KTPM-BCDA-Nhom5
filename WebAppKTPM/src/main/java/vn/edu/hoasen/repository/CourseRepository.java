package vn.edu.hoasen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hoasen.model.Course;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByNameContainingIgnoreCase(String name);
    List<Course> findByTeacherName(String teacherName);
}