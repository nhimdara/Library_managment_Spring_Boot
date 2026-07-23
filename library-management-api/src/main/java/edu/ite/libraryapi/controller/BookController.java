package edu.ite.libraryapi.controller;

import edu.ite.libraryapi.dto.ApiResponse;
import edu.ite.libraryapi.entity.Book;
import edu.ite.libraryapi.service.BookService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Search and manage the library catalogue")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) { this.bookService = bookService; }

    @GetMapping
    @Operation(summary = "List or search books")
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Books retrieved successfully", bookService.getAllBooks(search)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID")
    public ResponseEntity<ApiResponse<Book>> getBook(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book retrieved successfully", bookService.getBookById(id)));
    }

    @PostMapping
    @Operation(summary = "Add a book")
    public ResponseEntity<ApiResponse<Book>> createBook(@Valid @RequestBody Book book) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Book created successfully", bookService.createBook(book)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable Integer id, @Valid @RequestBody Book book) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Book updated successfully", bookService.updateBook(id, book)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Book deleted successfully", null));
    }
}
