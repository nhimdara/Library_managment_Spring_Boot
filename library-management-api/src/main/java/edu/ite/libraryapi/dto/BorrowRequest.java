package edu.ite.libraryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Information needed to borrow a book")
public class BorrowRequest {
    @NotNull
    @Schema(example = "1")
    private Integer studentId;

    @NotNull
    @Schema(example = "1")
    private Integer bookId;

    @NotNull
    @Future
    @Schema(description = "Must be a future date", example = "2026-12-31")
    private LocalDate dueDate;

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Integer getBookId() { return bookId; }
    public void setBookId(Integer bookId) { this.bookId = bookId; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
