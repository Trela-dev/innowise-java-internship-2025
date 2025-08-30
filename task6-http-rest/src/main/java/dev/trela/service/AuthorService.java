package dev.trela.service;

import dev.trela.model.Author;


import dev.trela.repository.AuthorRepositoryJPA;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthorService {


    private final SessionFactory sessionFactory;
    private final AuthorRepositoryJPA authorRepositoryJPA;
    private final MessageService messageService;

    public AuthorService(SessionFactory sessionFactory, AuthorRepositoryJPA authorRepositoryJPA, MessageService messageService){
        this.sessionFactory = sessionFactory;
        this.authorRepositoryJPA = authorRepositoryJPA;
        this.messageService = messageService;
    }


    public Optional<Author> findAuthorByName(String authorName) {
        return authorRepositoryJPA.findByName(authorName);

    }

    public Author addAuthorIfNotExists(String authorName) {
        return authorRepositoryJPA.findByName(authorName)
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setName(authorName);
                    return authorRepositoryJPA.save(newAuthor);
                });
    }



}
