package vn.edu.hoasen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hoasen.model.Student;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByNameContainingIgnoreCase(String name);
    List<Student> findByClassName(String className);
    List<Student> findAllByOrderByCreatedAtDesc();
}