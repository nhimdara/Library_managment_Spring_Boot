package edu.ite.libraryapi.controller;

import edu.ite.libraryapi.dto.ApiResponse;
import edu.ite.libraryapi.entity.Student;
import edu.ite.libraryapi.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Manage library student accounts")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) { this.studentService = studentService; }

    @GetMapping
    @Operation(summary = "List all students")
    public ResponseEntity<ApiResponse<List<Student>>> getAllStudents() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Students retrieved successfully", studentService.getAllStudents()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by ID")
    public ResponseEntity<ApiResponse<Student>> getStudent(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Student retrieved successfully", studentService.getStudentById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a student")
    public ResponseEntity<ApiResponse<Student>> createStudent(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Student created successfully", studentService.createStudent(student)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student")
    public ResponseEntity<ApiResponse<Student>> updateStudent(@PathVariable Integer id, @RequestBody Student student) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Student updated successfully", studentService.updateStudent(id, student)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Student deleted successfully", null));
    }
}
