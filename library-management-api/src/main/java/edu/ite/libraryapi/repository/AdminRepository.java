package edu.ite.libraryapi.repository;

import edu.ite.libraryapi.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Integer id);
}
