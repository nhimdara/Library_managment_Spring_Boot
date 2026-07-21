CREATE DATABASE IF NOT EXISTS `librarymanagementsystem`;
USE `librarymanagementsystem`;

CREATE TABLE `admin` (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `books` (
  `book_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `author` varchar(100) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `stutus` enum('Available','Unavailable') NOT NULL DEFAULT 'Unavailable',
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `students` (
  `student_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `borrow` (
  `borrow_id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `borrow_date` date NOT NULL,
  `due_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  PRIMARY KEY (`borrow_id`),
  KEY `fk_student` (`student_id`),
  KEY `fk_book` (`book_id`),
  CONSTRAINT `fk_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `admin` (`admin_id`, `username`, `password`) VALUES
(1, 'admin', '12345678');

INSERT INTO `books` (`book_id`, `title`, `author`, `category`, `quantity`, `stutus`) VALUES
(1, 'The Great Gatsby', 'F. Scott Fitzgerald', 'Fiction', 10, 'Available'),
(2, 'To Kill a Mockingbird', 'Harper Lee', 'Fiction', 8, 'Available'),
(3, '1984', 'George Orwell', 'Science Fiction', 12, 'Available'),
(4, 'The Hobbit', 'J.R.R. Tolkien', 'Fantasy', 15, 'Available'),
(5, 'Atomic Habits', 'James Clear', 'Self Help', 13, 'Available'),
(6, 'test', 'chea', 'book', 1, 'Available');

INSERT INTO `students` (`student_id`, `name`, `email`, `phone`, `password`) VALUES
(1, 'dara', 'dara@gmail.com', '0111111', '123456'),
(3, 'chea', 'chea@gmail.com', '0111111', '123456');

INSERT INTO `borrow` (`borrow_id`, `student_id`, `book_id`, `borrow_date`, `due_date`, `return_date`) VALUES
(1, 1, 6, '2026-06-30', '2026-07-14', '2026-06-30'),
(2, 3, 6, '2026-06-30', '2026-07-14', NULL),
(3, 1, 6, '2026-06-30', '2026-07-14', '2026-06-30');
