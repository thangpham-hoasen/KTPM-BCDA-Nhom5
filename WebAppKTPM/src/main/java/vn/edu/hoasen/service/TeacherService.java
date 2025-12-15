package vn.edu.hoasen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hoasen.model.Teacher;
import vn.edu.hoasen.repository.TeacherRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    
    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAllByOrderByIdDesc();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    public List<Teacher> searchByName(String name) {
        return teacherRepository.findByNameContainingIgnoreCase(name);
    }
}