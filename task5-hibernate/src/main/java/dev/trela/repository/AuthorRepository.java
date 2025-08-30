package dev.trela.repository;

import dev.trela.model.Author;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class AuthorRepository {


    private final SessionFactory sessionFactory;

    public AuthorRepository(SessionFactory sessionFactory){

        this.sessionFactory = sessionFactory;
    }


    public Optional<Author> findByName(String authorName) {
        Session session = sessionFactory.getCurrentSession();
        try {
            Author author = session.createQuery(
                            "FROM Author WHERE LOWER(name) = LOWER(:name)", Author.class)
                    .setParameter("name", authorName)
                    .getSingleResult();
            return Optional.of(author);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }



}
