package vn.edu.hoasen.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.service.StudentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private Student testStudent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testStudent = new Student("John Doe", LocalDate.of(2018, 5, 15), "Jane Doe", "123456789", "Class A");
    }

    @Test
    void getAllStudents_ShouldReturnAllStudents() {
        List<Student> students = Arrays.asList(testStudent);
        when(studentService.getAllStudents()).thenReturn(students);

        List<Student> result = studentController.getAllStudents();

        assertEquals(1, result.size());
        assertEquals(testStudent.getName(), result.get(0).getName());
        verify(studentService).getAllStudents();
    }

    @Test
    void saveStudent_ShouldCallService() {
        studentController.saveStudent(testStudent);
        verify(studentService).saveStudent(testStudent);
    }

    @Test
    void deleteStudent_ShouldCallService() {
        studentController.deleteStudent(1L);
        verify(studentService).deleteStudent(1L);
    }

    @Test
    void searchStudents_WithEmptyTerm_ShouldReturnAllStudents() {
        List<Student> students = Arrays.asList(testStudent);
        when(studentService.getAllStudents()).thenReturn(students);

        List<Student> result = studentController.searchStudents("");

        assertEquals(1, result.size());
        verify(studentService).getAllStudents();
    }

    @Test
    void searchStudents_WithSearchTerm_ShouldReturnFilteredStudents() {
        List<Student> students = Arrays.asList(testStudent);
        when(studentService.searchByName("John")).thenReturn(students);

        List<Student> result = studentController.searchStudents("John");

        assertEquals(1, result.size());
        verify(studentService).searchByName("John");
    }
}