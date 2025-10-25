package vn.edu.hoasen.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "classrooms")
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Student.ClassType classType;
    
    @Transient
    private List<Student> students;
    
    private static final int MAX_CAPACITY = 25;

    public ClassRoom() {}

    public ClassRoom(String name, Student.ClassType classType) {
        this.name = name;
        this.classType = classType;
    }

    public boolean canAddStudent() {
        return getCurrentCapacity() < MAX_CAPACITY;
    }

    public int getCurrentCapacity() {
        return students != null ? students.size() : 0;
    }

    public int getAvailableSlots() {
        return MAX_CAPACITY - getCurrentCapacity();
    }

    public boolean isFull() {
        return getCurrentCapacity() >= MAX_CAPACITY;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Student.ClassType getClassType() { return classType; }
    public void setClassType(Student.ClassType classType) { this.classType = classType; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public static int getMaxCapacity() { return MAX_CAPACITY; }
}