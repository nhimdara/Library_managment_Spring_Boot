package edu.ite.libraryapi.service;

import edu.ite.libraryapi.dto.LoginRequest;
import edu.ite.libraryapi.dto.LoginResponse;
import edu.ite.libraryapi.entity.Admin;
import edu.ite.libraryapi.entity.Student;
import edu.ite.libraryapi.exception.BadRequestException;
import edu.ite.libraryapi.repository.AdminRepository;
import edu.ite.libraryapi.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AdminRepository adminRepository, StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                throw new BadRequestException("Username is required for admin login");
            }
            Admin admin = adminRepository.findByUsernameIgnoreCase(request.getUsername()).orElse(null);
            if (admin != null && passwordMatches(request.getPassword(), admin.getPassword())) {
                return new LoginResponse(admin.getId(), admin.getUsername(), admin.getUsername(), "ADMIN");
            }
        } else if ("STUDENT".equalsIgnoreCase(request.getRole())) {
            if (request.getStudentId() == null) {
                throw new BadRequestException("Student ID is required for student login");
            }
            Student student = studentRepository.findById(request.getStudentId()).orElse(null);
            if (student != null && passwordMatches(request.getPassword(), student.getPassword())) {
                return new LoginResponse(student.getId(), student.getName(), String.valueOf(student.getId()), "STUDENT");
            }
        } else {
            throw new BadRequestException("Role must be ADMIN or STUDENT");
        }
        throw new BadRequestException("Invalid username/student ID or password");
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) return false;
        return storedPassword.startsWith("$2")
                ? passwordEncoder.matches(rawPassword, storedPassword)
                : storedPassword.equals(rawPassword);
    }
}
