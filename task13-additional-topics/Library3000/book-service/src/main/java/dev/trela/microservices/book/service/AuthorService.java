package dev.trela.microservices.book.service;

import dev.trela.microservices.book.model.Author;
import dev.trela.microservices.book.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthorService {


    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepositoryJPA){
        this.authorRepository = authorRepositoryJPA;
    }


    public Optional<Author> findAuthorByName(String authorName) {
        return authorRepository.findByName(authorName);

    }

    public Author addAuthorIfNotExists(String authorName) {
        return authorRepository.findByName(authorName)
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setName(authorName);
                    return authorRepository.save(newAuthor);
                });
    }



}
