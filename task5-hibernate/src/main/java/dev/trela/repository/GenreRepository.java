package dev.trela.repository;


import dev.trela.model.Genre;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class GenreRepository {

    private final SessionFactory sessionFactory;

    public GenreRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    public Optional<Genre> findByGenreName(String genreName) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Genre g WHERE g.name = :genreName";
        Query<Genre> query = session.createQuery(hql,Genre.class);
        query.setParameter("genreName", genreName);
        query.setCacheable(true);
        Genre result = query.uniqueResult();
        return Optional.ofNullable(result);

    }






}
