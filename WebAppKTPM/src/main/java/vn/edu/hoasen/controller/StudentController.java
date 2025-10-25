package vn.edu.hoasen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.service.StudentService;
import java.util.List;

@Controller
public class StudentController {
    
    @Autowired
    private StudentService studentService;

    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    public void saveStudent(Student student) {
        studentService.saveStudent(student);
    }

    public void deleteStudent(Long id) {
        studentService.deleteStudent(id);
    }

    public Student updateStudent(Student student) {
        return studentService.saveStudent(student);
    }

    public List<Student> searchStudents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentService.searchByName(searchTerm);
    }
}