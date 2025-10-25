package vn.edu.hoasen.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void getAgeInMonths_ShouldCalculateCorrectAge() {
        LocalDate birthDate = LocalDate.now().minusMonths(30);
        Student student = new Student("ST001", "Test Student", birthDate, Student.Gender.MALE, 
                                    "Address", "Parent", "123456789", "parent@email.com", "No allergies");
        
        assertEquals(30, student.getAgeInMonths());
    }

    @Test
    void isEligibleForAdmission_WithValidAge_ShouldReturnTrue() {
        LocalDate birthDate = LocalDate.now().minusMonths(24);
        Student student = new Student("ST001", "Test Student", birthDate, Student.Gender.MALE, 
                                    "Address", "Parent", "123456789", "parent@email.com", "No allergies");
        
        assertTrue(student.isEligibleForAdmission());
    }

    @Test
    void isEligibleForAdmission_WithTooYoung_ShouldReturnFalse() {
        LocalDate birthDate = LocalDate.now().minusMonths(15);
        Student student = new Student("ST001", "Test Student", birthDate, Student.Gender.MALE, 
                                    "Address", "Parent", "123456789", "parent@email.com", "No allergies");
        
        assertFalse(student.isEligibleForAdmission());
    }

    @Test
    void isEligibleForAdmission_WithTooOld_ShouldReturnFalse() {
        LocalDate birthDate = LocalDate.now().minusMonths(65);
        Student student = new Student("ST001", "Test Student", birthDate, Student.Gender.MALE, 
                                    "Address", "Parent", "123456789", "parent@email.com", "No allergies");
        
        assertFalse(student.isEligibleForAdmission());
    }

    @Test
    void suggestClass_ForMamAge_ShouldReturnMamClass() {
        LocalDate birthDate = LocalDate.now().minusMonths(24);
        Student student = new Student("ST001", "Test Student", birthDate, Student.Gender.MALE, 
                                    "Address", "Parent", "123456789", "parent@email.com", "No allergies");
        
        assertEquals("class.mam", student.suggestClass());
    }

    @Test
    void suggestClass_ForChoiAge_ShouldReturnChoiClass() {
        LocalDate birthDate = LocalDate.now().minusMonths(42);
        Student student = new Student("ST001", "Test Student", birthDate, Student.Gender.MALE, 
                                    "Address", "Parent", "123456789", "parent@email.com", "No allergies");
        
        assertEquals("class.choi", student.suggestClass());
    }

    @Test
    void suggestClass_ForLaAge_ShouldReturnLaClass() {
        LocalDate birthDate = LocalDate.now().minusMonths(54);
        Student student = new Student("ST001", "Test Student", birthDate, Student.Gender.MALE, 
                                    "Address", "Parent", "123456789", "parent@email.com", "No allergies");
        
        assertEquals("class.la", student.suggestClass());
    }

    @Test
    void suggestClass_WithIneligibleAge_ShouldThrowException() {
        LocalDate birthDate = LocalDate.now().minusMonths(15);
        Student student = new Student();
        student.setBirthDate(birthDate);
        
        assertThrows(IllegalArgumentException.class, () -> student.suggestClass());
    }
}