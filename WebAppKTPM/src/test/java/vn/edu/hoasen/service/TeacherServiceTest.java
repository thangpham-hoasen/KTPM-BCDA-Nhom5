package vn.edu.hoasen.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import vn.edu.hoasen.model.Teacher;
import vn.edu.hoasen.repository.TeacherRepository;

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testTeacher = new Teacher("Jane Smith", "jane@school.com", "123456789", "Mathematics", LocalDate.of(2020, 1, 15));
        testTeacher.setId(1L);
    }

    @Test
    void getAllTeachers_ShouldReturnAllTeachers() {
        List<Teacher> teachers = Arrays.asList(testTeacher);
        when(teacherRepository.findAllByOrderByIdDesc()).thenReturn(teachers);

        List<Teacher> result = teacherService.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals(testTeacher.getName(), result.get(0).getName());
        verify(teacherRepository).findAllByOrderByIdDesc();
    }

    @Test
    void saveTeacher_ShouldReturnSavedTeacher() {
        when(teacherRepository.save(testTeacher)).thenReturn(testTeacher);

        Teacher result = teacherService.saveTeacher(testTeacher);

        assertEquals(testTeacher.getName(), result.getName());
        verify(teacherRepository).save(testTeacher);
    }

    @Test
    void getTeacherById_ShouldReturnTeacher() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));

        Optional<Teacher> result = teacherService.getTeacherById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTeacher.getName(), result.get().getName());
        verify(teacherRepository).findById(1L);
    }

    @Test
    void deleteTeacher_ShouldCallRepository() {
        teacherService.deleteTeacher(1L);
        verify(teacherRepository).deleteById(1L);
    }
}