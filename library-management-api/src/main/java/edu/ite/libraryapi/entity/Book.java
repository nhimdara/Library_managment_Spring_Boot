package edu.ite.libraryapi.entity;

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

@Entity
@Table(name = "books")
public class Book {
    public enum Availability { Available, Unavailable }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String author;

    @Size(max = 100)
    @Column(length = 100)
    private String category;

    @Min(0)
    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "stutus", nullable = false, columnDefinition = "enum('Available','Unavailable')")
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
