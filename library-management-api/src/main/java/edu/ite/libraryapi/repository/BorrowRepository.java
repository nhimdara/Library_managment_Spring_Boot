package edu.ite.libraryapi.repository;

import edu.ite.libraryapi.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    List<Borrow> findAllByOrderByBorrowDateDesc();
    List<Borrow> findByStudentIdOrderByBorrowDateDesc(Integer studentId);
    boolean existsByStudentIdAndBookIdAndReturnDateIsNull(Integer studentId, Integer bookId);
    long countByReturnDateIsNull();
    long countByReturnDateIsNullAndDueDateBefore(LocalDate date);
    boolean existsByStudentId(Integer studentId);
    boolean existsByBookId(Integer bookId);
}
