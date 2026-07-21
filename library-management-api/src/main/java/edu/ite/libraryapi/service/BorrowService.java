package edu.ite.libraryapi.service;

import edu.ite.libraryapi.dto.BorrowRequest;
import edu.ite.libraryapi.dto.BorrowResponse;
import edu.ite.libraryapi.entity.Book;
import edu.ite.libraryapi.entity.Borrow;
import edu.ite.libraryapi.entity.Student;
import edu.ite.libraryapi.exception.BadRequestException;
import edu.ite.libraryapi.exception.ResourceNotFoundException;
import edu.ite.libraryapi.repository.BookRepository;
import edu.ite.libraryapi.repository.BorrowRepository;
import edu.ite.libraryapi.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BorrowService {
    private final BorrowRepository borrowRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    public BorrowService(BorrowRepository borrowRepository, StudentRepository studentRepository,
                         BookRepository bookRepository) {
        this.borrowRepository = borrowRepository;
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
    }

    public List<BorrowResponse> getAllBorrows() {
        return borrowRepository.findAllByOrderByBorrowDateDesc().stream().map(BorrowResponse::new).toList();
    }

    public List<BorrowResponse> getBorrowsByStudent(Integer studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id " + studentId);
        }
        return borrowRepository.findByStudentIdOrderByBorrowDateDesc(studentId).stream().map(BorrowResponse::new).toList();
    }

    public BorrowResponse getBorrowById(Integer id) { return new BorrowResponse(findBorrow(id)); }

    @Transactional
    public BorrowResponse borrowBook(BorrowRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + request.getStudentId()));
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + request.getBookId()));
        if (book.getQuantity() <= 0) throw new BadRequestException("No copies of this book are available");
        if (borrowRepository.existsByStudentIdAndBookIdAndReturnDateIsNull(student.getId(), book.getId())) {
            throw new BadRequestException("This student already has an active loan for this book");
        }

        Borrow borrow = new Borrow();
        borrow.setStudent(student);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDueDate(request.getDueDate());
        book.setQuantity(book.getQuantity() - 1);
        book.setStutus(book.getQuantity() > 0 ? Book.Availability.Available : Book.Availability.Unavailable);
        bookRepository.save(book);
        return new BorrowResponse(borrowRepository.save(borrow));
    }

    @Transactional
    public BorrowResponse returnBook(Integer id) {
        Borrow borrow = findBorrow(id);
        if (borrow.getReturnDate() != null) {
            throw new BadRequestException("This book has already been returned");
        }
        borrow.setReturnDate(LocalDate.now());
        Book book = borrow.getBook();
        book.setQuantity(book.getQuantity() + 1);
        book.setStutus(Book.Availability.Available);
        bookRepository.save(book);
        return new BorrowResponse(borrowRepository.save(borrow));
    }

    private Borrow findBorrow(Integer id) {
        return borrowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with id " + id));
    }
}
