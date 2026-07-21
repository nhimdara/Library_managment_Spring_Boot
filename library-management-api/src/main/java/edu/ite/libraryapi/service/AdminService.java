package edu.ite.libraryapi.service;

import edu.ite.libraryapi.entity.Admin;
import edu.ite.libraryapi.entity.Borrow;
import edu.ite.libraryapi.exception.BadRequestException;
import edu.ite.libraryapi.exception.ResourceNotFoundException;
import edu.ite.libraryapi.repository.AdminRepository;
import edu.ite.libraryapi.repository.BookRepository;
import edu.ite.libraryapi.repository.BorrowRepository;
import edu.ite.libraryapi.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class AdminService {
    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, StudentRepository studentRepository,
                        BookRepository bookRepository, BorrowRepository borrowRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Admin> getAllAdmins() { return adminRepository.findAll(); }

    public Admin getAdminById(Integer id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));
    }

    @Transactional
    public Admin createAdmin(Admin admin) {
        if (adminRepository.existsByUsernameIgnoreCase(admin.getUsername())) {
            throw new BadRequestException("An admin with this username already exists");
        }
        admin.setId(null);
        admin.setUsername(admin.getUsername().trim());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    @Transactional
    public Admin updateAdmin(Integer id, Admin input) {
        Admin admin = getAdminById(id);
        if (adminRepository.existsByUsernameIgnoreCaseAndIdNot(input.getUsername(), id)) {
            throw new BadRequestException("An admin with this username already exists");
        }
        admin.setUsername(input.getUsername().trim());
        if (input.getPassword() != null && !input.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(input.getPassword()));
        }
        return adminRepository.save(admin);
    }

    @Transactional
    public void deleteAdmin(Integer id) { adminRepository.delete(getAdminById(id)); }

    public Map<String, Long> getDashboardSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("admins", adminRepository.count());
        summary.put("students", studentRepository.count());
        summary.put("books", bookRepository.count());
        summary.put("borrowed", borrowRepository.countByReturnDateIsNull());
        summary.put("overdue", borrowRepository.countByReturnDateIsNullAndDueDateBefore(LocalDate.now()));
        return summary;
    }

    public List<Borrow> getBorrowHistory() { return borrowRepository.findAllByOrderByBorrowDateDesc(); }
}
