package dev.trela.repository;

import dev.trela.model.Author;
import dev.trela.model.Book;
import dev.trela.model.Genre;
import dev.trela.service.MessageService;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class BookRepository {

    private final SessionFactory sessionFactory;
    private final MessageService messageService;

    public BookRepository(SessionFactory sessionFactory, MessageService messageService){
        this.sessionFactory = sessionFactory;
        this.messageService = messageService;
    }

    public List<Book> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT DISTINCT b FROM Book b " +
                        "LEFT JOIN FETCH b.authors " +
                        "LEFT JOIN FETCH b.genre " +
                        "ORDER BY b.id", Book.class)
                .getResultList();
    }

    public Optional<Book> findById(int id){
        Session session = sessionFactory.getCurrentSession();
        Book book = session.createQuery("SELECT DISTINCT b FROM Book b " +
                "LEFT JOIN FETCH b.authors " +
                "LEFT JOIN FETCH b.genre " +
                "WHERE b.id = :id", Book.class)
                .setParameter("id",id)
                .uniqueResult();
        return Optional.ofNullable((book));}


    public void update(Book book) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(book);

    }

    public void save(Book book){
        Session session = sessionFactory.getCurrentSession();
        session.persist(book);
    }


    public void deleteById(int id){
        Session session = sessionFactory.getCurrentSession();
        Book book = session.get(Book.class,id);
        if(book == null){
            throw new NoSuchElementException(messageService.getMessage("error.no.such.book"));
        }
        session.remove(book);
    }



public List<Book> searchByKeyword(String keyword) {
    Session session = sessionFactory.getCurrentSession();
    CriteriaBuilder cb = session.getCriteriaBuilder();
    CriteriaQuery<Book> cq = cb.createQuery(Book.class);
    Root<Book> book = cq.from(Book.class);

    book.fetch("authors", JoinType.LEFT);
    book.fetch("genre", JoinType.LEFT);

    Join<Book, Author> authors = book.join("authors", JoinType.LEFT);
    Join<Book, Genre> genre = book.join("genre", JoinType.LEFT);

    String likePattern = "%" + keyword.toLowerCase() + "%";


    Predicate titlePredicate = cb.like(cb.lower(book.get("title")), likePattern);
    Predicate descPredicate = cb.like(cb.lower(book.get("description")), likePattern);
    Predicate authorPredicate = cb.like(cb.lower(authors.get("name")), likePattern);
    Predicate genrePredicate = cb.like(cb.lower(genre.get("name")), likePattern);


    cq.where(cb.or(titlePredicate, descPredicate, authorPredicate, genrePredicate))
            .distinct(true)
            .orderBy(cb.asc(book.get("id")));

    return session.createQuery(cq).getResultList();
}





}
