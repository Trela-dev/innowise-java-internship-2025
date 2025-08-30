package dev.trela.testing.repository;

import dev.trela.testing.model.Author;
import dev.trela.testing.model.Genre;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class AuthorRepository {


    private final JdbcTemplate jdbcTemplate;

    public AuthorRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    public Optional<Author> findByAuthorName(String authorName) {
        String sql = "SELECT * FROM authors WHERE name = ?";
        try {
            Author author = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Author a = new Author();
                a.setId(rs.getInt("author_id"));
                a.setName(rs.getString("name"));
                return a;
            }, authorName);

            return Optional.ofNullable(author);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void save(Author author){
        String sql = "INSERT INTO authors (name) values(?)";
        jdbcTemplate.update(sql,author.getName());
    }

    //tested works
    public void update(Author author){
        String sql = """
        UPDATE authors
        SET name = ?
        WHERE author_id = ?
    """;
        int updatedRows = jdbcTemplate.update(sql,
                author.getName(),
                author.getId());
        if (updatedRows == 0){
            throw new NoSuchElementException("Author with id " + author.getId() + " not found");
        }
    }

    //tested works
    public List<Author> findAll(){
        String sql = """
                SELECT a.author_id, a.name FROM authors a
                ORDER BY a.author_id
                """;
        return jdbcTemplate.query(sql,(rs,rowNum) -> {
            Author author = new Author();
            author.setId(rs.getInt("author_id"));
            author.setName(rs.getString("name"));
            return author;
        });
    }

    //tested works
    public void deleteById(int id){
        String sql = "DELETE FROM authors WHERE author_id = ? ";
        int deletedRows = jdbcTemplate.update(sql,id);
        if(deletedRows == 0){
            throw new NoSuchElementException("Author with id " + id + " not found");
        }
    }



}
