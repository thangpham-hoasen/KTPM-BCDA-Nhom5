package vn.edu.hoasen.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.Teacher;
import vn.edu.hoasen.service.TeacherService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTeacher = new Teacher("Jane Smith", "jane@school.com", "123456789", "Mathematics", LocalDate.of(2020, 1, 15));
    }

    @Test
    void getAllTeachers_ShouldReturnAllTeachers() {
        List<Teacher> teachers = Arrays.asList(testTeacher);
        when(teacherService.getAllTeachers()).thenReturn(teachers);

        List<Teacher> result = teacherController.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals(testTeacher.getName(), result.get(0).getName());
        verify(teacherService).getAllTeachers();
    }

    @Test
    void saveTeacher_ShouldCallService() {
        teacherController.saveTeacher(testTeacher);
        verify(teacherService).saveTeacher(testTeacher);
    }

    @Test
    void deleteTeacher_ShouldCallService() {
        teacherController.deleteTeacher(1L);
        verify(teacherService).deleteTeacher(1L);
    }

    @Test
    void searchTeachers_WithEmptyTerm_ShouldReturnAllTeachers() {
        List<Teacher> teachers = Arrays.asList(testTeacher);
        when(teacherService.getAllTeachers()).thenReturn(teachers);

        List<Teacher> result = teacherController.searchTeachers("");

        assertEquals(1, result.size());
        verify(teacherService).getAllTeachers();
    }

    @Test
    void searchTeachers_WithSearchTerm_ShouldReturnFilteredTeachers() {
        List<Teacher> teachers = Arrays.asList(testTeacher);
        when(teacherService.searchByName("Jane")).thenReturn(teachers);

        List<Teacher> result = teacherController.searchTeachers("Jane");

        assertEquals(1, result.size());
        verify(teacherService).searchByName("Jane");
    }
}