package vn.edu.hoasen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hoasen.model.Teacher;
import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByNameContainingIgnoreCase(String name);
    List<Teacher> findBySubject(String subject);
    List<Teacher> findAllByOrderByIdDesc();
}