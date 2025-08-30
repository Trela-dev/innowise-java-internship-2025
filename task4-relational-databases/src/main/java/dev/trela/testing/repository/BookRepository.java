package dev.trela.testing.repository;

import dev.trela.testing.model.Author;
import dev.trela.testing.model.Book;
import dev.trela.testing.model.Genre;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }



    public List<Book> findAll() {
        String sql = """
        SELECT b.book_id, b.title, b.description, b.pages, b.rating,
               g.genre_id, g.name AS genre_name,
               a.author_id, a.name AS author_name
        FROM books b
        JOIN genres g ON b.genre_id = g.genre_id
        JOIN book_authors ba ON b.book_id = ba.book_id
        JOIN authors a ON ba.author_id = a.author_id
        ORDER BY b.book_id, a.author_id
        """;

        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Book> bookMap = new LinkedHashMap<>();
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                Book book = bookMap.get(bookId);

                // if book is not in bookMap

                if (book == null) {
                    book = new Book();
                    book.setId(bookId);
                    book.setTitle(rs.getString("title"));
                    book.setDescription(rs.getString("description"));
                    book.setPages(rs.getInt("pages"));
                    book.setRating(rs.getBigDecimal("rating"));

                    Genre genre = new Genre();
                    genre.setId(rs.getInt("genre_id"));
                    genre.setName(rs.getString("genre_name"));
                    book.setGenre(genre);

                    book.setAuthors(new ArrayList<>());
                    bookMap.put(bookId, book);
                }

                // if book is already in map just add author
                Author author = new Author();
                author.setId(rs.getInt("author_id"));
                author.setName(rs.getString("author_name"));
                book.getAuthors().add(author);
            }

            return new ArrayList<>(bookMap.values());
        });
    }

    public void update(Book book) {
        String updateBookSql = """
        UPDATE books
        SET title = ?,
            description = ?,
            pages = ?,
            rating = ?,
            genre_id = (SELECT genre_id FROM genres WHERE name = ?)
        WHERE book_id = ?
    """;

        int updatedRows = jdbcTemplate.update(updateBookSql,
                book.getTitle(),
                book.getDescription(),
                book.getPages(),
                book.getRating(),
                book.getGenre().getName(),
                book.getId());

        if (updatedRows == 0) {
            throw new NoSuchElementException("Book with id " + book.getId() + " not found");
        }

        // update ManyToMany relation book_authors (delete old records,add new)
        String deleteAuthorsSql = "DELETE FROM book_authors WHERE book_id = ?";
        jdbcTemplate.update(deleteAuthorsSql, book.getId());

        String insertAuthorSql = """
        INSERT INTO book_authors (book_id, author_id)
        VALUES (?, (SELECT author_id FROM authors WHERE name = ?))
    """;
        for (Author author : book.getAuthors()) {
            jdbcTemplate.update(insertAuthorSql, book.getId(), author.getName());
        }
    }

    public void save(Book book){
        // using returing to get book_id
        String sql = """
        INSERT INTO books (title, description, pages, rating, genre_id)
        VALUES (?, ?, ?, ?, (SELECT genre_id FROM genres WHERE name = ?))
        RETURNING book_id
        """;

        Integer bookId = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                book.getTitle(),
                book.getDescription(),
                book.getPages(),
                book.getRating(),
                book.getGenre().getName()
        );

        if (bookId == null) {
            throw new IllegalStateException("Failed to insert book");
        }

        String insertAuthorSql = """
        INSERT INTO book_authors (book_id, author_id)
        VALUES (?, (SELECT author_id FROM authors WHERE name = ?))
    """;
        for (Author author : book.getAuthors()) {
            jdbcTemplate.update(insertAuthorSql, bookId, author.getName());
        }


    }


    public void deleteById(int id){
        String sql = "DELETE FROM books WHERE book_id = ?";
        int deletedRows = jdbcTemplate.update(sql,id);
        if (deletedRows == 0){
            throw new NoSuchElementException("Book with id " + id + " not found");
        }
    }



    public List<Book> searchByKeyword(String keyword){
        String sql = """
        SELECT b.book_id, b.title, b.description, b.pages, b.rating,
               a.author_id, a.name AS author_name,
               g.genre_id, g.name AS genre_name
        FROM books b
        JOIN book_authors ba ON b.book_id = ba.book_id
        JOIN authors a ON ba.author_id = a.author_id
        JOIN genres g ON b.genre_id = g.genre_id
        WHERE LOWER(b.title) LIKE LOWER(?) OR LOWER(b.description) LIKE LOWER(?)
        ORDER BY b.book_id
    """;

        String likePattern = "%" + keyword.toLowerCase() + "%";


        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, Book> bookMap = new LinkedHashMap<>();
            while (rs.next()) {
                int bookId = rs.getInt("book_id");
                Book book = bookMap.get(bookId);

                // if book is not in bookMap

                if (book == null) {
                    book = new Book();
                    book.setId(bookId);
                    book.setTitle(rs.getString("title"));
                    book.setDescription(rs.getString("description"));
                    book.setPages(rs.getInt("pages"));
                    book.setRating(rs.getBigDecimal("rating"));

                    Genre genre = new Genre();
                    genre.setId(rs.getInt("genre_id"));
                    genre.setName(rs.getString("genre_name"));
                    book.setGenre(genre);

                    book.setAuthors(new ArrayList<>());
                    bookMap.put(bookId, book);
                }

                // if book is already in map just add author
                Author author = new Author();
                author.setId(rs.getInt("author_id"));
                author.setName(rs.getString("author_name"));
                book.getAuthors().add(author);
            }

            return new ArrayList<>(bookMap.values());
        },likePattern,likePattern);


    }


}
