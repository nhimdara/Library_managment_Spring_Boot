package edu.ite.libraryapi.controller;

import edu.ite.libraryapi.dto.ApiResponse;
import edu.ite.libraryapi.dto.BorrowRequest;
import edu.ite.libraryapi.dto.BorrowResponse;
import edu.ite.libraryapi.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
@Tag(name = "Borrowing", description = "Borrow, return, and review books")
public class BorrowController {
    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) { this.borrowService = borrowService; }

    @GetMapping
    @Operation(summary = "List borrowing records", description = "Optionally filter records by student ID.")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getAllBorrows(
            @RequestParam(required = false) Integer studentId) {
        List<BorrowResponse> data = studentId == null
                ? borrowService.getAllBorrows()
                : borrowService.getBorrowsByStudent(studentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Borrow records retrieved successfully", data));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a borrowing record by ID")
    public ResponseEntity<ApiResponse<BorrowResponse>> getBorrow(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Borrow record retrieved successfully", borrowService.getBorrowById(id)));
    }

    @PostMapping
    @Operation(summary = "Borrow a book")
    public ResponseEntity<ApiResponse<BorrowResponse>> borrowBook(@Valid @RequestBody BorrowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Book borrowed successfully", borrowService.borrowBook(request)));
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return a borrowed book")
    public ResponseEntity<ApiResponse<BorrowResponse>> returnBook(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book returned successfully", borrowService.returnBook(id)));
    }
}
