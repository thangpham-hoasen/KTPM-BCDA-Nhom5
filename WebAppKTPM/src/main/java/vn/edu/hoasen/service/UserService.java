package vn.edu.hoasen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hoasen.model.User;
import vn.edu.hoasen.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean validateLogin(String username, String password) {
        Optional<User> user = findByUsername(username);
        return user.isPresent() && user.get().getPassword().equals(password);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createDefaultUser() {
        if (!existsByUsername("admin")) {
            User admin = new User("admin", "admin123", "Administrator", "admin@school.com", "0123456789");
            return saveUser(admin);
        }
        return findByUsername("admin").orElse(null);
    }
}