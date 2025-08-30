package dev.trela.repository;

import dev.trela.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre,Integer> {
    public Optional<Genre> findByName(String genreName);
}
