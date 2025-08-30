package dev.trela.testing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.trela.testing.service.MessageService;

public class Book {

    private int id;
    private String title;
    private String author;
    private String description;
    public Book() {}


    public Book(int id, String title, String author, String description) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
