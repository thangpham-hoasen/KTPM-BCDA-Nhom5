package vn.edu.hoasen.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
public class StudentRegistrationTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student validStudent;

    @BeforeEach
    void setUp() {
        validStudent = new Student();
        validStudent.setName("Be A");
        validStudent.setParentName("Nguyen Van A");
        validStudent.setParentPhone("0901234567");
        validStudent.setBirthDate(LocalDate.now().minusMonths(36)); // 36 tháng
        validStudent.setClassName(null); // backend sẽ tự gán theo tuổi
    }

    // --- Black Box: Equivalence Partitioning & Boundary Value Analysis ---

    @Test
    void testAgeBoundaryValues() {
        // Theo SRS/Report (PHẦN III): Valid Range 18 to 60 months

        // Biên dưới -1: 17 tháng -> False
        validStudent.setBirthDate(LocalDate.now().minusMonths(17));
        assertFalse(studentService.validateStudentInfo(validStudent), "Age 17 months should be invalid");

        // Biên dưới: 18 tháng -> True
        validStudent.setBirthDate(LocalDate.now().minusMonths(18));
        assertTrue(studentService.validateStudentInfo(validStudent), "Age 18 months should be valid");

        // Biên dưới +1: 19 tháng -> True
        validStudent.setBirthDate(LocalDate.now().minusMonths(19));
        assertTrue(studentService.validateStudentInfo(validStudent), "Age 19 months should be valid");

        // Trong khoảng: 30 tháng -> True
        validStudent.setBirthDate(LocalDate.now().minusMonths(30));
        assertTrue(studentService.validateStudentInfo(validStudent), "Age 30 months should be valid");

        // Biên trên -1: 59 tháng -> True
        validStudent.setBirthDate(LocalDate.now().minusMonths(59));
        assertTrue(studentService.validateStudentInfo(validStudent), "Age 59 months should be valid");

        // Biên trên: 60 tháng -> True
        validStudent.setBirthDate(LocalDate.now().minusMonths(60));
        assertTrue(studentService.validateStudentInfo(validStudent), "Age 60 months should be valid");

        // Biên trên +1: 61 tháng -> False
        validStudent.setBirthDate(LocalDate.now().minusMonths(61));
        assertFalse(studentService.validateStudentInfo(validStudent), "Age 61 months should be invalid");
    }

    @Test
    void testParentNameValidation() {
        // Empty Name -> False
        validStudent.setParentName("");
        assertFalse(studentService.validateStudentInfo(validStudent));

        // Null Name -> False
        validStudent.setParentName(null);
        assertFalse(studentService.validateStudentInfo(validStudent));

        // Valid Name -> True
        validStudent.setParentName("Tran Van B");
        assertTrue(studentService.validateStudentInfo(validStudent));
    }

    @Test
    void testParentPhoneValidation() {
        // Invalid Format (text) -> False
        validStudent.setParentPhone("090abcxyz");
        assertFalse(studentService.validateStudentInfo(validStudent));

        // Invalid Length (9 digits) -> False
        validStudent.setParentPhone("090123456");
        assertFalse(studentService.validateStudentInfo(validStudent));

        // Valid -> True
        validStudent.setParentPhone("0987654321");
        assertTrue(studentService.validateStudentInfo(validStudent));
    }

    // --- Decision Table Tests ---

    @Test
    void testDecisionTable_HappyPath() {
        // TC4: Age hợp lệ + lớp còn chỗ + parent info đầy đủ => Approve
        validStudent.setBirthDate(LocalDate.now().minusMonths(40)); // Lớp Chồi

        // Mock Capacity not full (class.choi)
        when(studentRepository.findByClassName("class.choi")).thenReturn(Collections.emptyList());

        boolean result = studentService.registerStudent(validStudent);
        assertTrue(result);
        verify(studentRepository).save(validStudent);
    }

    @Test
    void testDecisionTable_InvalidAge() {
        // TC1: Tuổi không hợp lệ => Reject
        validStudent.setBirthDate(LocalDate.now().minusMonths(10));

        boolean result = studentService.registerStudent(validStudent);
        assertFalse(result);
        verify(studentRepository, never()).save(any());
    }

    @Test
    void testDecisionTable_MissingParentInfo() {
        // TC3: Tuổi hợp lệ + lớp còn chỗ nhưng thiếu thông tin phụ huynh => Reject
        validStudent.setBirthDate(LocalDate.now().minusMonths(30));
        validStudent.setParentName("");
        // Mock repository không cần thiết vì validateStudentInfo sẽ trả về false trước khi gọi repository
        // when(studentRepository.findByClassName("class.mam")).thenReturn(Collections.emptyList());

        boolean result = studentService.registerStudent(validStudent);
        assertFalse(result);
        verify(studentRepository, never()).save(any());
    }

    @Test
    void testDecisionTable_ClassFull() {
        // TC2: Tuổi hợp lệ nhưng lớp đầy => Reject
        validStudent.setBirthDate(LocalDate.now().minusMonths(30)); // class.mam

        // Mock Capacity FULL (25 students)
        List<Student> fullClass = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            fullClass.add(new Student());
        }
        when(studentRepository.findByClassName("class.mam")).thenReturn(fullClass);

        boolean result = studentService.registerStudent(validStudent);
        assertFalse(result);
        // validateStudentInfo True nhưng register false vì lớp đầy
        assertTrue(studentService.validateStudentInfo(validStudent));
        verify(studentRepository, never()).save(any());
    }
}
