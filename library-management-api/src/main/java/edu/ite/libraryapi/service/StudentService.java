package edu.ite.libraryapi.service;

import edu.ite.libraryapi.entity.Student;
import edu.ite.libraryapi.exception.BadRequestException;
import edu.ite.libraryapi.exception.ResourceNotFoundException;
import edu.ite.libraryapi.repository.BorrowRepository;
import edu.ite.libraryapi.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;
    private final BorrowRepository borrowRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, BorrowRepository borrowRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.borrowRepository = borrowRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Student> getAllStudents() { return studentRepository.findAll(); }

    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }

    @Transactional
    public Student createStudent(Student student) {
        validateUnique(student, null);
        student.setId(null);
        student.setEmail(student.getEmail().trim().toLowerCase());
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Integer id, Student input) {
        Student student = getStudentById(id);
        validateUnique(input, id);
        student.setName(input.getName());
        student.setEmail(input.getEmail().trim().toLowerCase());
        student.setPhone(input.getPhone());
        if (input.getPassword() != null && !input.getPassword().isBlank()) {
            student.setPassword(passwordEncoder.encode(input.getPassword()));
        }
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Integer id) {
        Student student = getStudentById(id);
        if (borrowRepository.existsByStudentId(id)) {
            throw new BadRequestException("Cannot delete a student with borrowing history");
        }
        studentRepository.delete(student);
    }

    private void validateUnique(Student student, Integer currentId) {
        boolean duplicateEmail = currentId == null
                ? studentRepository.existsByEmailIgnoreCase(student.getEmail())
                : studentRepository.existsByEmailIgnoreCaseAndIdNot(student.getEmail(), currentId);
        if (duplicateEmail) throw new BadRequestException("A student with this email already exists");
    }
}
