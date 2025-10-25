package vn.edu.hoasen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vn.edu.hoasen.model.Teacher;
import vn.edu.hoasen.service.TeacherService;
import java.util.List;

@Controller
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;

    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    public void saveTeacher(Teacher teacher) {
        teacherService.saveTeacher(teacher);
    }

    public void deleteTeacher(Long id) {
        teacherService.deleteTeacher(id);
    }

    public Teacher updateTeacher(Teacher teacher) {
        return teacherService.saveTeacher(teacher);
    }

    public List<Teacher> searchTeachers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllTeachers();
        }
        return teacherService.searchByName(searchTerm);
    }
}