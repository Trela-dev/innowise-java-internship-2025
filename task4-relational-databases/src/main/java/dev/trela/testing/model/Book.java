package dev.trela.testing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {

    private int id;
    private String title;
    private String description;
    private int pages;
    private BigDecimal rating;
    private List<Author> authors;
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
