package edu.ite.libraryapi.service;

import edu.ite.libraryapi.entity.Book;
import edu.ite.libraryapi.exception.BadRequestException;
import edu.ite.libraryapi.exception.ResourceNotFoundException;
import edu.ite.libraryapi.repository.BookRepository;
import edu.ite.libraryapi.repository.BorrowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;

    public BookService(BookRepository bookRepository, BorrowRepository borrowRepository) {
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
    }

    public List<Book> getAllBooks(String search) {
        if (search == null || search.isBlank()) return bookRepository.findAll();
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search.trim(), search.trim());
    }

    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }

    @Transactional
    public Book createBook(Book book) {
        book.setId(null);
        synchronizeStatus(book);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Integer id, Book input) {
        Book book = getBookById(id);
        book.setTitle(input.getTitle());
        book.setAuthor(input.getAuthor());
        book.setCategory(input.getCategory());
        book.setQuantity(input.getQuantity());
        synchronizeStatus(book);
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Integer id) {
        Book book = getBookById(id);
        if (borrowRepository.existsByBookId(id)) {
            throw new BadRequestException("Cannot delete a book with borrowing history");
        }
        bookRepository.delete(book);
    }

    private void synchronizeStatus(Book book) {
        book.setStutus(book.getQuantity() > 0 ? Book.Availability.Available : Book.Availability.Unavailable);
    }
}
