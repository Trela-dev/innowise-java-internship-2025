CREATE TABLE authors (
    author_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE genres (
    genre_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE books (
    book_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre_id INTEGER NOT NULL REFERENCES genres(genre_id) ON DELETE RESTRICT,
    pages INTEGER CHECK (pages > 0),
    rating DECIMAL(3, 2) CHECK (rating >= 0 AND rating <= 5),
    image_file_id VARCHAR(100)
);


CREATE TABLE book_authors (
    book_id INTEGER NOT NULL REFERENCES books(book_id) ON DELETE CASCADE,
    author_id INTEGER NOT NULL REFERENCES authors(author_id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);
