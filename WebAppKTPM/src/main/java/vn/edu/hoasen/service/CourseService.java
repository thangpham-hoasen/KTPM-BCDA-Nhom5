package vn.edu.hoasen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hoasen.model.Course;
import vn.edu.hoasen.repository.CourseRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public List<Course> getAllCoursesOrderByCreatedAtDesc() {
        return courseRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> searchByName(String name) {
        return courseRepository.findByNameContainingIgnoreCase(name);
    }
}