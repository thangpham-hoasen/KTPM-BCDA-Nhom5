package vn.edu.hoasen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.StudentRepository;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    


    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public List<Student> getAllStudentsOrderByCreatedAtDesc() {
        return studentRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        if (student.getBirthDate() != null) {
            validateStudentAdmission(student);
        }
        if (student.getClassName() != null) {
            validateClassCapacity(student.getClassName());
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> searchByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }

    private void validateStudentAdmission(Student student) {
        if (!student.isEligibleForAdmission()) {
            throw new IllegalArgumentException("Học sinh không đủ điều kiện nhập học (18-60 tháng tuổi)");
        }
    }

    private void validateClassCapacity(String className) {
        List<Student> studentsInClass = getStudentsByClass(className);
        if (studentsInClass.size() >= 25) {
            throw new IllegalStateException("Lớp " + className + " đã đủ sĩ số (25 học sinh)");
        }
    }

    public String suggestClassForStudent(Student student) {
        return student.suggestClass();
    }

    public int getClassCapacity(String className) {
        return getStudentsByClass(className).size();
    }

    public boolean isClassFull(String className) {
        return getClassCapacity(className) >= 25;
    }
}