package dev.trela.testing;

import dev.trela.testing.config.LibraryConfig;
import dev.trela.testing.model.Book;
import dev.trela.testing.repository.BookRepository;
import dev.trela.testing.service.BookService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Library3000App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(LibraryConfig.class);
        BookService bookService = context.getBean(BookService.class);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1 - Display book list");
            System.out.println("2 - Create a new book");
            System.out.println("3 - Edit a book");
            System.out.println("4 - Delete a book");
            System.out.println("5 - Search by keyword");
            System.out.println("6 - Exit");

            System.out.print("Your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear Enter

                switch (choice) {
                    case 1 -> {
                        System.out.println("\nBook List:");
                        bookService.getAllBooks().forEach(book -> System.out.println(book));
                    }
                    case 2 -> {
                        System.out.print("Enter title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter author: ");
                        String author = scanner.nextLine();
                        System.out.print("Enter description: ");
                        String description = scanner.nextLine();

                        boolean isBookAdded = bookService.addBook(new Book(-1, title, author, description));
                        if(isBookAdded)
                        System.out.println("Book added!");
                        else {
                            System.out.println("Failed to add the book. Make sure all fields are not empty.");
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter book ID to edit: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("New title: ");
                        String title = scanner.nextLine();
                        System.out.print("New author: ");
                        String author = scanner.nextLine();
                        System.out.print("New description: ");
                        String description = scanner.nextLine();

                        try {
                            bookService.updateBook(new Book(id, title, author, description));
                            System.out.println("Book successfully updated!");
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        } catch (NoSuchElementException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    case 4 -> {
                        System.out.print("Enter book ID to delete: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();

                        boolean deleted = bookService.deleteBook(id);

                        if (deleted) {
                            System.out.println("Book deleted!");
                        } else {
                            System.out.println("Book with ID " + id + " not found!");
                        }

                    }
                    case 5 -> {
                        System.out.print("Enter searching keyword: ");
                        String keyword = scanner.nextLine();
                        List<Book> foundBooks = bookService.searchByKeyword(keyword);
                        if (foundBooks.isEmpty()) {
                            System.out.println("No books found matching: " + keyword);
                        } else {
                            System.out.println("Found books:");
                            foundBooks.forEach(book-> System.out.println(book));
                        }
                    }
                    case 6 -> {
                        System.out.println("Closing...");
                        scanner.close();
                        return;
                    }

                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }
}
