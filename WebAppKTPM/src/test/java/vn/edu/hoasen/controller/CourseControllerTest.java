package vn.edu.hoasen.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.Course;
import vn.edu.hoasen.service.CourseService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testCourse = new Course("Math", "Basic mathematics", "John Smith", 40, "Mon-Wed-Fri 9AM");
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.getAllCourses()).thenReturn(courses);

        List<Course> result = courseController.getAllCourses();

        assertEquals(1, result.size());
        assertEquals(testCourse.getName(), result.get(0).getName());
        verify(courseService).getAllCourses();
    }

    @Test
    void saveCourse_ShouldCallService() {
        courseController.saveCourse(testCourse);
        verify(courseService).saveCourse(testCourse);
    }

    @Test
    void deleteCourse_ShouldCallService() {
        courseController.deleteCourse(1L);
        verify(courseService).deleteCourse(1L);
    }

    @Test
    void searchCourses_WithEmptyTerm_ShouldReturnAllCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.getAllCourses()).thenReturn(courses);

        List<Course> result = courseController.searchCourses("");

        assertEquals(1, result.size());
        verify(courseService).getAllCourses();
    }

    @Test
    void searchCourses_WithSearchTerm_ShouldReturnFilteredCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.searchByName("Math")).thenReturn(courses);

        List<Course> result = courseController.searchCourses("Math");

        assertEquals(1, result.size());
        verify(courseService).searchByName("Math");
    }
}