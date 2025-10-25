package vn.edu.hoasen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.Course;
import vn.edu.hoasen.repository.CourseRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testCourse = new Course("Math", "Basic mathematics", "John Smith", 40, "Mon-Wed-Fri 9AM");
        testCourse.setId(1L);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals(testCourse.getName(), result.get(0).getName());
        verify(courseRepository).findAll();
    }

    @Test
    void saveCourse_ShouldReturnSavedCourse() {
        when(courseRepository.save(testCourse)).thenReturn(testCourse);

        Course result = courseService.saveCourse(testCourse);

        assertEquals(testCourse.getName(), result.getName());
        verify(courseRepository).save(testCourse);
    }

    @Test
    void getCourseById_ShouldReturnCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        Optional<Course> result = courseService.getCourseById(1L);

        assertTrue(result.isPresent());
        assertEquals(testCourse.getName(), result.get().getName());
        verify(courseRepository).findById(1L);
    }

    @Test
    void deleteCourse_ShouldCallRepository() {
        courseService.deleteCourse(1L);
        verify(courseRepository).deleteById(1L);
    }
}