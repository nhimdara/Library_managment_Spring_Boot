package edu.ite.libraryapi.controller;

import edu.ite.libraryapi.dto.ApiResponse;
import edu.ite.libraryapi.entity.Admin;
import edu.ite.libraryapi.entity.Borrow;
import edu.ite.libraryapi.service.AdminService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
@Tag(name = "Admins", description = "Manage administrators and view library reports")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @Operation(summary = "List all admins")
    public ResponseEntity<ApiResponse<List<Admin>>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(new ApiResponse<>(true, "Admins retrieved successfully", admins));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "View dashboard totals")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboardSummary() {
        Map<String, Long> summary = adminService.getDashboardSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard summary retrieved successfully", summary));
    }

    @GetMapping("/borrow-history")
    @Operation(summary = "View complete borrowing history")
    public ResponseEntity<ApiResponse<List<Borrow>>> getBorrowHistory() {
        List<Borrow> borrowHistory = adminService.getBorrowHistory();
        return ResponseEntity.ok(new ApiResponse<>(true, "Borrow history retrieved successfully", borrowHistory));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an admin by ID")
    public ResponseEntity<ApiResponse<Admin>> getAdminById(@PathVariable Integer id) {
        Admin admin = adminService.getAdminById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin retrieved successfully", admin));
    }

    @PostMapping
    @Operation(summary = "Create an admin")
    public ResponseEntity<ApiResponse<Admin>> createAdmin(@Valid @RequestBody Admin admin) {
        Admin createdAdmin = adminService.createAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Admin created successfully", createdAdmin));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an admin")
    public ResponseEntity<ApiResponse<Admin>> updateAdmin(
            @PathVariable Integer id,
            @RequestBody Admin admin
    ) {
        Admin updatedAdmin = adminService.updateAdmin(id, admin);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin updated successfully", updatedAdmin));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an admin")
    public ResponseEntity<ApiResponse<Void>> deleteAdmin(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin deleted successfully", null));
    }
}
