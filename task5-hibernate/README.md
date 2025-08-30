

# 📚 Library3000App

**Library3000App** is a console application for managing books, written in Java using **Spring** and **Hibernate**.
Book data is stored in a **PostgreSQL** database running in **Docker**, and accessed using **Hibernate ORM** (replacing the previous Spring JDBC implementation).

---

## ✨ Features

✅ Display the list of books
✅ Add a new book
✅ Edit a book
✅ Delete a book
✅ Search books by keyword using **Criteria API**
✅ Multi-language support (English, Polish)
✅ Logging and **three levels of caching** via Spring AOP and Hibernate

---

## 🛠 Requirements

🔹 Java 21
🔹 Gradle
🔹 Docker & Docker Compose

---

## 🚀 Setup & Running

### 1️⃣ Start the Database

Run the following command in the project root to start the PostgreSQL database with Docker Compose:

```sh
docker-compose up -d
```

### 2️⃣ Build the Project

Use Gradle to build the project and generate a fat JAR:

```sh
gradlew shadowJar
```

### 3️⃣ Run the Application

Once the database is running and the JAR is built:

```sh
java -jar build/libs/Library3000-1.0-SNAPSHOT.jar
```

📌 **Flyway** will automatically set up the database schema and populate it with sample data.

---

## 📂 Project Structure

```
src/
└── main/
    ├── java/
    │   └── testing/
    │       ├── config/           # Hibernate & Spring configuration
    │       ├── model/            # Domain models: Book, Author, Genre
    │       ├── repository/       # Hibernate-based repositories
    │       ├── service/          # Business logic layer
    │       ├── util/             # Utility/helper classes
    │       └── Library3000App.java  # Main application class
    └── resources/
        ├── migration/            # Flyway SQL scripts
        ├── messages_en.properties
        └── messages_pl.properties
```

---

## 🗃 Database Structure

The application uses a **PostgreSQL** database with the following schema:

* `books`: `id`, `title`, `description`, `genre`, `rating`, `pages`
* `authors`: `author_id`, `name`
* `books_authors`: `book_id`, `author_id` (many-to-many relationship)
* `genres`: `id`, `name`

Schema management is fully automated with **Flyway**.

---

## 🔍 Search by Keyword (Hibernate Criteria API)

The search functionality is implemented using Hibernate’s **Criteria API**, allowing advanced filtering across multiple related entities.

```java
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
```

---

## 🌍 Multi-Language Support

The application supports both **English** and **Polish**, using Spring’s `MessageSource`.
Language is selected at runtime:

```text
Select language: 1 for English, 2 for Polski
```

You can add more languages by simply creating additional properties files.

---

## 🧠 Caching and Logging with AOP

The application uses **Spring AOP** and **Hibernate caching** to improve performance and traceability.

### ✅ Three types of caching are implemented:

1. **First-Level Cache** – Hibernate session-level (automatic)
2. **Second-Level Cache** – Entity-level caching (e.g. EHCache or similar)
3. **Method-Level Cache** – via Spring AOP to cache expensive service method results

Additionally, the `LoggingAndCachingAspect` logs:

* Method calls
* Return values
* Execution time

---

## ⚙ Technologies Used

🔹 **Java 21**
🔹 **Spring Context, AOP**
🔹 **Hibernate ORM** (pure, no JdbcTemplate)
🔹 **PostgreSQL** (via Docker)
🔹 **Flyway**
🔹 **Gradle + Shadow Plugin**
🔹 **Docker Compose**
🔹 **Lombok**

---

📌 Make sure Docker is running before launching the application.

