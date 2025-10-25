package vn.edu.hoasen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import vn.edu.hoasen.model.Course;
import vn.edu.hoasen.service.CourseService;
import java.util.List;

@Controller
public class CourseController {
    
    @Autowired
    private CourseService courseService;

    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    public void saveCourse(Course course) {
        courseService.saveCourse(course);
    }

    public void deleteCourse(Long id) {
        courseService.deleteCourse(id);
    }

    public Course updateCourse(Course course) {
        return courseService.saveCourse(course);
    }

    public List<Course> searchCourses(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCourses();
        }
        return courseService.searchByName(searchTerm);
    }
}