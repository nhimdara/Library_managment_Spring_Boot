package edu.ite.libraryapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ite.libraryapi.entity.Admin;
import edu.ite.libraryapi.entity.Book;
import edu.ite.libraryapi.entity.Student;
import edu.ite.libraryapi.repository.AdminRepository;
import edu.ite.libraryapi.repository.BookRepository;
import edu.ite.libraryapi.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LibraryApiApplicationTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired StudentRepository studentRepository;
    @Autowired BookRepository bookRepository;
    @Autowired AdminRepository adminRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void openApiDocumentationIsAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Library Management API"))
                .andExpect(jsonPath("$.paths['/api/books']").exists())
                .andExpect(jsonPath("$.paths['/api/borrows']").exists())
                .andExpect(jsonPath("$.paths['/api/auth/login']").exists());
    }

    @Test
    @Transactional
    void assignmentOnePlaintextAdminCanLogin() throws Exception {
        Admin admin = new Admin();
        admin.setUsername("legacy-admin");
        admin.setPassword("12345678");
        adminRepository.save(admin);

        String request = objectMapper.writeValueAsString(Map.of(
                "role", "ADMIN",
                "username", "legacy-admin",
                "password", "12345678"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.username").value("legacy-admin"));
    }

    @Test
    @Transactional
    void studentCanBorrowAndReturnAnAvailableBook() throws Exception {
        Student student = new Student();
        student.setName("Test Student");
        student.setEmail("student@test.local");
        student.setPassword("encoded-for-fixture");
        student = studentRepository.save(student);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setQuantity(1);
        book.setStutus(Book.Availability.Available);
        book = bookRepository.save(book);

        String request = objectMapper.writeValueAsString(Map.of(
                "studentId", student.getId(),
                "bookId", book.getId(),
                "dueDate", LocalDate.now().plusDays(14).toString()));

        String response = mockMvc.perform(post("/api/borrows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("BORROWED"))
                .andReturn().getResponse().getContentAsString();

        int borrowId = objectMapper.readTree(response).path("data").path("id").asInt();
        assertThat(bookRepository.findById(book.getId()).orElseThrow().getQuantity()).isZero();

        mockMvc.perform(post("/api/borrows/{id}/return", borrowId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("RETURNED"));

        assertThat(bookRepository.findById(book.getId()).orElseThrow().getQuantity()).isEqualTo(1);
    }
}
