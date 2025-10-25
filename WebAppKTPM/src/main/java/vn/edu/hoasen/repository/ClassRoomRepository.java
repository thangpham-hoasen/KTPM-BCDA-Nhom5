package vn.edu.hoasen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.hoasen.model.ClassRoom;

import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    Optional<ClassRoom> findByName(String name);
}