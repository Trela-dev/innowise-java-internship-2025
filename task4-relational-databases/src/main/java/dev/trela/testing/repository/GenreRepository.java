package dev.trela.testing.repository;


import dev.trela.testing.model.Genre;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    public GenreRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Genre> findByGenreName(String genreName) {
        String sql = "SELECT * FROM genres WHERE name = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Genre g = new Genre();
                g.setId(rs.getInt("genre_id"));
                g.setName(rs.getString("name"));
                return g;
            }, genreName);

            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    //tested works
    public void save(Genre genre){
        String sql = "INSERT INTO genres (name) VALUES (?)";
        jdbcTemplate.update(sql, genre.getName());
    }


    //tested works
    public void deleteById(int id){
        String sql = "DELETE FROM genres WHERE genre_id = ?";
        int deletedRows = jdbcTemplate.update(sql,id);
        if(deletedRows == 0){
            throw new NoSuchElementException("Genre with id " + id + " not found");
        }
    }


    //tested works
    public List<Genre> findAll(){
        String sql = """
                SELECT g.genre_id, g.name FROM genres g
                ORDER BY g.genre_id
                """;
        return jdbcTemplate.query(sql,(rs,rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        });
    }

    //tested works
    public void update(Genre genre){
        String sql = """
        UPDATE genres
        SET name = ?
        WHERE genre_id = ?
    """;
        int updatedRows = jdbcTemplate.update(sql,
                genre.getName(),
                genre.getId());

        if (updatedRows == 0){
            throw new NoSuchElementException("Genre with id " + genre.getId() + " not found");
        }
    }






}
