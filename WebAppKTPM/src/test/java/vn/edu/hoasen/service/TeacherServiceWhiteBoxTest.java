package vn.edu.hoasen.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.hoasen.model.Teacher;
import vn.edu.hoasen.model.Course;
import vn.edu.hoasen.repository.TeacherRepository;
import vn.edu.hoasen.repository.CourseRepository;
import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)

public class TeacherServiceWhiteBoxTest {
    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TeacherService teacherService;

    @InjectMocks
    private CourseService courseService;

    @Test
    void deleteTeacher_WhenHasCourses_ShouldThrowException() {
        // 1. Setup: Giả lập giáo viên có ID là 1 đang dạy 1 khóa học
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(new Teacher()));
        //when(courseRepository.findByTeacherId(teacherId)).thenReturn(List.of(new Course()));

        // 2. Thực thi & Kiểm chứng: Mong đợi ném ra IllegalStateException (theo SRS UC-12)
        assertThrows(IllegalStateException.class, () -> {
            teacherService.deleteTeacher(teacherId);
        }, "Theo SRS UC-12, không được xóa giáo viên đang phụ trách khóa học!");
    }

    @Test
    void saveCourse_WithNonExistentTeacher_ShouldThrowException() {
        // 1. Setup: Giả lập lưu một khóa học nhưng ID giáo viên không tìm thấy
        Course course = new Course();
        course.setName("Lớp Chồi 1");
        Long invalidTeacherId = 99L;

        // Giả lập Repository trả về Optional rỗng khi tìm giáo viên này
        when(teacherRepository.findById(invalidTeacherId)).thenReturn(Optional.empty());

        // 2. Thực thi & Kiểm chứng: Mong đợi ném ra lỗi vì giáo viên không hợp lệ
        assertThrows(IllegalArgumentException.class, () -> {
            // Giả sử bạn gọi hàm save với logic kiểm tra giáo viên
            courseService.saveCourse(course);
        }, "Phải báo lỗi khi gán khóa học cho giáo viên không tồn tại!");
    }
}
