package vn.edu.hoasen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.StudentRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student validStudent;
    private Student invalidStudent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validStudent = new Student("ST001", "John Doe", LocalDate.now().minusMonths(24), 
                                 Student.Gender.MALE, "Address", "Jane Doe", "0901234567", 
                                 "jane@email.com", "No allergies");
        validStudent.setId(1L);
        
        // Dữ liệu tuổi không hợp lệ nhưng thông tin phụ huynh hợp lệ để test đúng BR-01
        invalidStudent = new Student("ST002", "Invalid Student", LocalDate.now().minusMonths(15), 
                                   Student.Gender.MALE, "Address", "Parent", "0901234567", 
                                   "parent@email.com", "No allergies");
    }

    @Test
    void saveStudent_WithValidStudent_ShouldSave() {
        when(studentRepository.findByClassName("class.mam")).thenReturn(Collections.emptyList());
        when(studentRepository.save(validStudent)).thenReturn(validStudent);

        Student result = studentService.saveStudent(validStudent);

        assertEquals(validStudent.getName(), result.getName());
        verify(studentRepository).save(validStudent);
    }

    @Test
    void saveStudent_WithInvalidAge_ShouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> studentService.saveStudent(invalidStudent));
        assertTrue(ex.getMessage().contains("18-60"));
    }

    @Test
    void saveStudent_WithFullClass_ShouldThrowException() {
        List<Student> fullClass = Arrays.asList(new Student[25]);
        when(studentRepository.findByClassName("class.mam")).thenReturn(fullClass);

        assertThrows(IllegalStateException.class, () -> {
            studentService.saveStudent(validStudent);
        });
    }

    @Test
    void isClassFull_WithFullClass_ShouldReturnTrue() {
        List<Student> fullClass = Arrays.asList(new Student[25]);
        when(studentRepository.findByClassName("Test Class")).thenReturn(fullClass);

        assertTrue(studentService.isClassFull("Test Class"));
    }

    @Test
    void getClassCapacity_ShouldReturnCorrectCount() {
        List<Student> students = Arrays.asList(validStudent);
        when(studentRepository.findByClassName("Test Class")).thenReturn(students);

        assertEquals(1, studentService.getClassCapacity("Test Class"));
    }

    @Test
    void suggestClassForStudent_ShouldReturnCorrectClass() {
        String suggestedClass = studentService.suggestClassForStudent(validStudent);
        assertEquals("class.mam", suggestedClass);
    }

    @Test
    void saveStudent_WithNullBirthDate_ShouldThrowException() {
        Student student = new Student();
        student.setName("Test Student");
        student.setParentName("Parent");
        student.setParentPhone("0901234567");

        assertThrows(IllegalArgumentException.class, () -> studentService.saveStudent(student));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void saveStudent_ShouldAutoAssignClassName() {
        Student student = new Student();
        student.setName("Test Student");
        student.setParentName("Parent");
        student.setParentPhone("0901234567");
        student.setBirthDate(LocalDate.now().minusMonths(24));
        student.setClassName(null); // không nhập lớp, backend sẽ tự gán

        when(studentRepository.findByClassName("class.mam")).thenReturn(Collections.emptyList());
        when(studentRepository.save(student)).thenReturn(student);

        Student result = studentService.saveStudent(student);
        assertEquals("class.mam", result.getClassName());
        verify(studentRepository).save(student);
    }
}