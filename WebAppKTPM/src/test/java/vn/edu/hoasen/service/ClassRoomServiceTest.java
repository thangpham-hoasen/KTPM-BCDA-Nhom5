package vn.edu.hoasen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.hoasen.model.ClassRoom;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.ClassRoomRepository;
import vn.edu.hoasen.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassRoomServiceTest {

    @Mock
    private ClassRoomRepository classRoomRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private ClassRoomService classRoomService;

    private ClassRoom testClassRoom;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testClassRoom = new ClassRoom("Test Class", Student.ClassType.MAM);
        testClassRoom.setId(1L);
    }

    @Test
    void getAllClassRooms_ShouldReturnAllClassRooms() {
        List<ClassRoom> classRooms = Arrays.asList(testClassRoom);
        when(classRoomRepository.findAll()).thenReturn(classRooms);

        List<ClassRoom> result = classRoomService.getAllClassRooms();

        assertEquals(1, result.size());
        verify(classRoomRepository).findAll();
    }

    @Test
    void getClassRoomById_ShouldReturnClassRoom() {
        when(classRoomRepository.findById(1L)).thenReturn(Optional.of(testClassRoom));

        Optional<ClassRoom> result = classRoomService.getClassRoomById(1L);

        assertTrue(result.isPresent());
        assertEquals(testClassRoom.getName(), result.get().getName());
        verify(classRoomRepository).findById(1L);
    }

    @Test
    void getClassCapacity_ShouldReturnCorrectCount() {
        List<Student> students = Arrays.asList(new Student(), new Student());
        when(studentRepository.findByClassName("Test Class")).thenReturn(students);

        int capacity = classRoomService.getClassCapacity("Test Class");

        assertEquals(2, capacity);
        verify(studentRepository).findByClassName("Test Class");
    }

    @Test
    void isClassFull_WithFullClass_ShouldReturnTrue() {
        List<Student> fullClass = Arrays.asList(new Student[25]);
        when(studentRepository.findByClassName("Test Class")).thenReturn(fullClass);

        boolean isFull = classRoomService.isClassFull("Test Class");

        assertTrue(isFull);
        verify(studentRepository).findByClassName("Test Class");
    }

    @Test
    void canAddStudentToClass_WithAvailableSpace_ShouldReturnTrue() {
        List<Student> students = Arrays.asList(new Student());
        when(studentRepository.findByClassName("Test Class")).thenReturn(students);

        boolean canAdd = classRoomService.canAddStudentToClass("Test Class");

        assertTrue(canAdd);
        verify(studentRepository).findByClassName("Test Class");
    }
}