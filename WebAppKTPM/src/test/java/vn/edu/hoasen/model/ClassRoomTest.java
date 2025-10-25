package vn.edu.hoasen.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class ClassRoomTest {

    @Test
    void canAddStudent_WithEmptyClass_ShouldReturnTrue() {
        ClassRoom classRoom = new ClassRoom("Test Class", Student.ClassType.MAM);
        classRoom.setStudents(new ArrayList<>());
        
        assertTrue(classRoom.canAddStudent());
    }

    @Test
    void canAddStudent_WithFullClass_ShouldReturnFalse() {
        ClassRoom classRoom = new ClassRoom("Test Class", Student.ClassType.MAM);
        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            students.add(new Student());
        }
        classRoom.setStudents(students);
        
        assertFalse(classRoom.canAddStudent());
    }

    @Test
    void getCurrentCapacity_ShouldReturnCorrectCount() {
        ClassRoom classRoom = new ClassRoom("Test Class", Student.ClassType.MAM);
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student());
        students.add(new Student());
        classRoom.setStudents(students);
        
        assertEquals(2, classRoom.getCurrentCapacity());
    }

    @Test
    void getAvailableSlots_ShouldCalculateCorrectly() {
        ClassRoom classRoom = new ClassRoom("Test Class", Student.ClassType.MAM);
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student());
        classRoom.setStudents(students);
        
        assertEquals(24, classRoom.getAvailableSlots());
    }

    @Test
    void isFull_WithMaxCapacity_ShouldReturnTrue() {
        ClassRoom classRoom = new ClassRoom("Test Class", Student.ClassType.MAM);
        ArrayList<Student> students = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            students.add(new Student());
        }
        classRoom.setStudents(students);
        
        assertTrue(classRoom.isFull());
    }
}