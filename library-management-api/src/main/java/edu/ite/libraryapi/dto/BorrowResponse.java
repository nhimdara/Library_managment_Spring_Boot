package edu.ite.libraryapi.dto;

import edu.ite.libraryapi.entity.Borrow;

import java.time.LocalDate;

public class BorrowResponse {
    private final Integer id;
    private final Integer studentId;
    private final String studentName;
    private final Integer bookId;
    private final String bookTitle;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;
    private final String status;

    public BorrowResponse(Borrow borrow) {
        this.id = borrow.getId();
        this.studentId = borrow.getStudent().getId();
        this.studentName = borrow.getStudent().getName();
        this.bookId = borrow.getBook().getId();
        this.bookTitle = borrow.getBook().getTitle();
        this.borrowDate = borrow.getBorrowDate();
        this.dueDate = borrow.getDueDate();
        this.returnDate = borrow.getReturnDate();
        this.status = borrow.getReturnDate() != null
                ? "RETURNED"
                : borrow.getDueDate().isBefore(LocalDate.now()) ? "OVERDUE" : "BORROWED";
    }

    public Integer getId() { return id; }
    public Integer getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public Integer getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
}
