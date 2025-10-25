package vn.edu.hoasen.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String email;
    
    private String phone;
    
    private String subject;
    
    @Column(name = "hire_date")
    private LocalDate hireDate;

    public Teacher() {}

    public Teacher(String name, String email, String phone, String subject, LocalDate hireDate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.subject = subject;
        this.hireDate = hireDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
}