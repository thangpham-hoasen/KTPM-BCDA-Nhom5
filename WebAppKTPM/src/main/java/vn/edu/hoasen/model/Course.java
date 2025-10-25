package vn.edu.hoasen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "teacher_name")
    private String teacherName;
    
    private Integer duration;
    
    private String schedule;

    public Course() {}

    public Course(String name, String description, String teacherName, Integer duration, String schedule) {
        this.name = name;
        this.description = description;
        this.teacherName = teacherName;
        this.duration = duration;
        this.schedule = schedule;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
}