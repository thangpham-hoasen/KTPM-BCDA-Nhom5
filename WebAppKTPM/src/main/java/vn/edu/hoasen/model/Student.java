package vn.edu.hoasen.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_code")
    private String studentCode;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private String address;
    
    @Column(name = "parent_name")
    private String parentName;
    
    @Column(name = "parent_phone")
    private String parentPhone;
    
    @Column(name = "parent_email")
    private String parentEmail;
    
    @Column(name = "health_notes")
    private String healthNotes;
    
    @Column(name = "class_name")
    private String className;

    public enum Gender {
        MALE("gender.male"),
        FEMALE("gender.female");
        
        private final String messageKey;
        
        Gender(String messageKey) {
            this.messageKey = messageKey;
        }
        
        public String getMessageKey() {
            return messageKey;
        }
    }

    public enum ClassType {
        MAM("class.mam", 18, 36),
        CHOI("class.choi", 37, 48),
        LA("class.la", 49, 60);
        
        private final String messageKey;
        private final int minMonths;
        private final int maxMonths;
        
        ClassType(String messageKey, int minMonths, int maxMonths) {
            this.messageKey = messageKey;
            this.minMonths = minMonths;
            this.maxMonths = maxMonths;
        }
        
        public String getMessageKey() { return messageKey; }
        public int getMinMonths() { return minMonths; }
        public int getMaxMonths() { return maxMonths; }
    }

    public Student() {}

    public Student(String name, LocalDate birthDate, String parentName, String parentPhone, String className) {
        this.name = name;
        this.birthDate = birthDate;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.className = className;
    }

    public Student(String studentCode, String name, LocalDate birthDate, Gender gender, 
                  String address, String parentName, String parentPhone, String parentEmail, 
                  String healthNotes) {
        this.studentCode = studentCode;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
        this.parentEmail = parentEmail;
        this.healthNotes = healthNotes;
        if (isEligibleForAdmission()) {
            this.className = suggestClass();
        }
    }

    public int getAgeInMonths() {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears() * 12 + 
               Period.between(birthDate, LocalDate.now()).getMonths();
    }

    public boolean isEligibleForAdmission() {
        int ageInMonths = getAgeInMonths();
        return ageInMonths >= 18 && ageInMonths <= 60;
    }

    public String suggestClass() {
        ClassType classType = suggestClassType();
        return classType != null ? classType.getMessageKey() : null;
    }
    
    public ClassType suggestClassType() {
        if (birthDate == null || !isEligibleForAdmission()) {
            throw new IllegalArgumentException("Student not eligible for admission (18-60 months)");
        }
        
        int ageInMonths = getAgeInMonths();
        for (ClassType classType : ClassType.values()) {
            if (ageInMonths >= classType.getMinMonths() && ageInMonths <= classType.getMaxMonths()) {
                return classType;
            }
        }
        return null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }

    public String getParentEmail() { return parentEmail; }
    public void setParentEmail(String parentEmail) { this.parentEmail = parentEmail; }

    public String getHealthNotes() { return healthNotes; }
    public void setHealthNotes(String healthNotes) { this.healthNotes = healthNotes; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}