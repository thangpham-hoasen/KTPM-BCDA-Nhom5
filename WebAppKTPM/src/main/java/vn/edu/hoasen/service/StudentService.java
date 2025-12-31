package vn.edu.hoasen.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getAllStudentsOrderByCreatedAtDesc() {
        return studentRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        // === UC-02: Thêm mới học sinh (tiếp nhận) ===
        // - Bắt buộc có ngày sinh để tính tuổi (18-60 tháng)
        // - Bắt buộc thông tin phụ huynh (tên + SĐT đúng định dạng)
        // - Tự động gợi ý lớp theo độ tuổi và kiểm tra sĩ số (tối đa 25)
        validateRequiredFields(student);
        validateStudentAdmission(student); // BR-01

        // BR-02: tự gán lớp theo tuổi (ưu tiên logic trong model)
        String suggestedClass = student.suggestClass();
        student.setClassName(suggestedClass);

        // BR-03: chặn nếu lớp đã đủ 25 học sinh
        validateClassCapacity(student.getClassName());

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> searchByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }

    private void validateStudentAdmission(Student student) {
        if (!student.isEligibleForAdmission()) {
            throw new IllegalArgumentException("Học sinh không đủ điều kiện nhập học (18-60 tháng tuổi)");
        }
    }

    private void validateRequiredFields(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Dữ liệu học sinh không hợp lệ");
        }

        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên học sinh là bắt buộc");
        }

        // Path 4 trong báo cáo: birthDate == null và className == null sẽ bỏ qua validate.
        // => Fix: Ngày sinh là bắt buộc để tính tuổi và gán lớp.
        if (student.getBirthDate() == null) {
            throw new IllegalArgumentException("Ngày sinh là bắt buộc");
        }

        if (student.getParentName() == null || student.getParentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên phụ huynh là bắt buộc");
        }

        if (student.getParentPhone() == null) {
            throw new IllegalArgumentException("Số điện thoại phụ huynh là bắt buộc");
        }

        String normalizedPhone = student.getParentPhone().replaceAll("[^0-9]", "");
        if (!normalizedPhone.matches("^0[0-9]{9,10}$")) {
            throw new IllegalArgumentException("Số điện thoại phụ huynh không hợp lệ (10-11 chữ số)");
        }
        // giữ dữ liệu sạch khi lưu
        student.setParentPhone(normalizedPhone);
    }

    private void validateClassCapacity(String className) {
        List<Student> studentsInClass = getStudentsByClass(className);
        if (studentsInClass.size() >= 25) {
            throw new IllegalStateException("Class is full");
        }
    }

    public String suggestClassForStudent(Student student) {
        return student.suggestClass();
    }

    public int getClassCapacity(String className) {
        return getStudentsByClass(className).size();
    }

    public boolean isClassFull(String className) {
        return getClassCapacity(className) >= 25;
    }

    /**
     * Chức năng "Tiếp nhận học sinh mới" (Đăng ký) - dùng cho kiểm thử theo báo cáo.
     * Decision Table (PHẦN III):
     *  - Điều kiện: (1) Tuổi hợp lệ 18-60 tháng, (2) Lớp tương ứng < 25, (3) Thông tin phụ huynh đầy đủ
     *  - Hành động: Lưu thành công & xếp lớp hoặc báo lỗi/không lưu
     */
    public boolean registerStudent(Student student) {
        if (!validateStudentInfo(student)) {
            return false;
        }

        // Tự động gán lớp theo tuổi
        String suggestedClass = student.suggestClass();
        student.setClassName(suggestedClass);

        // Kiểm tra sĩ số lớp
        if (isClassFull(student.getClassName())) {
            return false;
        }

        studentRepository.save(student);
        return true;
    }

    /**
     * Validation dạng "trả về true/false" phục vụ kiểm thử hộp đen (EP/BVA) và Decision Table.
     */
    public boolean validateStudentInfo(Student student) {
        if (student == null) return false;
        if (student.getName() == null || student.getName().trim().isEmpty()) return false;
        if (student.getBirthDate() == null) return false;

        int ageInMonths = student.getAgeInMonths();
        if (ageInMonths < 18 || ageInMonths > 60) return false;

        if (student.getParentName() == null || student.getParentName().trim().isEmpty()) return false;
        if (student.getParentPhone() == null) return false;
        String normalizedPhone = student.getParentPhone().replaceAll("[^0-9]", "");
        if (!normalizedPhone.matches("^[0-9]{10,11}$")) return false;

        return true;
    }
}