package dev.trela.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int id;
    private String title;
    private String description;
    private int pages;
    private BigDecimal rating;
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public Book(String title, String description, int pages, BigDecimal rating, List<Author> authors , Genre genre) {
        this.title = title;
        this.description = description;
        this.pages = pages;
        this.rating = rating;
        this.authors = authors;
        this.genre = genre;
    }




}
