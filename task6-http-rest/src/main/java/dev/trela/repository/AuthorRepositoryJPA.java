package dev.trela.repository;

import dev.trela.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepositoryJPA extends JpaRepository<Author,Integer> {
    public Optional<Author> findByName(String AuthorName);
}
