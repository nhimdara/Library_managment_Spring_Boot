package edu.ite.libraryapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "A book in the library catalogue")
@Entity
@Table(name = "books")
public class Book {
    public enum Availability { Available, Unavailable }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Integer id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    @Schema(example = "Clean Code")
    private String title;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    @Schema(example = "Robert C. Martin")
    private String author;

    @Size(max = 100)
    @Column(length = 100)
    @Schema(example = "Software Engineering")
    private String category;

    @Min(0)
    @Column(nullable = false)
    @Schema(example = "3", minimum = "0")
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "stutus", nullable = false, length = 20)
    @Schema(description = "Automatically synchronized from quantity", accessMode = Schema.AccessMode.READ_ONLY)
    private Availability stutus;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Availability getStutus() { return stutus; }
    public void setStutus(Availability stutus) { this.stutus = stutus; }
}
