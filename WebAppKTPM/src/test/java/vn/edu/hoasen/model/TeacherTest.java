package vn.edu.hoasen.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {

    @Test
    void createTeacher_WithAllFields_ShouldSetCorrectValues() {
        String name = "John Smith";
        String email = "john@school.com";
        String phone = "123456789";
        String subject = "Mathematics";
        LocalDate hireDate = LocalDate.of(2020, 1, 15);
        
        Teacher teacher = new Teacher(name, email, phone, subject, hireDate);
        
        assertEquals(name, teacher.getName());
        assertEquals(email, teacher.getEmail());
        assertEquals(phone, teacher.getPhone());
        assertEquals(subject, teacher.getSubject());
        assertEquals(hireDate, teacher.getHireDate());
    }

    @Test
    void createTeacher_WithDefaultConstructor_ShouldHaveNullValues() {
        Teacher teacher = new Teacher();
        
        assertNull(teacher.getId());
        assertNull(teacher.getName());
        assertNull(teacher.getEmail());
        assertNull(teacher.getPhone());
        assertNull(teacher.getSubject());
        assertNull(teacher.getHireDate());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        Teacher teacher = new Teacher();
        
        teacher.setId(1L);
        teacher.setName("Jane Doe");
        teacher.setEmail("jane@school.com");
        teacher.setPhone("987654321");
        teacher.setSubject("Science");
        teacher.setHireDate(LocalDate.of(2021, 3, 10));
        
        assertEquals(1L, teacher.getId());
        assertEquals("Jane Doe", teacher.getName());
        assertEquals("jane@school.com", teacher.getEmail());
        assertEquals("987654321", teacher.getPhone());
        assertEquals("Science", teacher.getSubject());
        assertEquals(LocalDate.of(2021, 3, 10), teacher.getHireDate());
    }
}