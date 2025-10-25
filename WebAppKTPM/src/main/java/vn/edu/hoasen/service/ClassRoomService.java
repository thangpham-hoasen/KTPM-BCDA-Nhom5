package vn.edu.hoasen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hoasen.model.ClassRoom;
import vn.edu.hoasen.model.Student;
import vn.edu.hoasen.repository.ClassRoomRepository;
import vn.edu.hoasen.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClassRoomService {
    
    @Autowired
    private ClassRoomRepository classRoomRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    public List<ClassRoom> getAllClassRooms() {
        return classRoomRepository.findAll();
    }

    public Optional<ClassRoom> getClassRoomById(Long id) {
        return classRoomRepository.findById(id);
    }

    public Optional<ClassRoom> getClassRoomByName(String name) {
        return classRoomRepository.findByName(name);
    }

    public ClassRoom saveClassRoom(ClassRoom classRoom) {
        return classRoomRepository.save(classRoom);
    }

    public void deleteClassRoom(Long id) {
        classRoomRepository.deleteById(id);
    }

    public int getClassCapacity(String className) {
        List<Student> students = studentRepository.findByClassName(className);
        return students.size();
    }

    public boolean isClassFull(String className) {
        return getClassCapacity(className) >= 25;
    }

    public boolean canAddStudentToClass(String className) {
        return !isClassFull(className);
    }
}