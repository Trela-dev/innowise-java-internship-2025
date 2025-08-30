package dev.trela.testing;

import dev.trela.testing.config.LibraryConfig;
import dev.trela.testing.learning.Calculator;
import dev.trela.testing.model.Book;
import dev.trela.testing.service.BookService;
import dev.trela.testing.service.MessageService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;

public class Library3000App {
    public static void main(String[] args) {

        // Inicjalizacja kontekstu aplikacji Spring
        ApplicationContext context = new AnnotationConfigApplicationContext(LibraryConfig.class);
        BookService bookService = context.getBean(BookService.class);
        MessageService messageService = context.getBean(MessageService.class);
        Scanner scanner = new Scanner(System.in);

        System.out.println(messageService.getMessage("language.selection"));
        int languageChoice = scanner.nextInt();
        if (languageChoice == 2) {
            messageService.setLocale(Locale.forLanguageTag("pl")); // Ustawienie języka na Polski
        }

        while (true) {

            System.out.println("\n" + messageService.getMessage("menu.title"));
            System.out.println(messageService.getMessage("menu.option1"));
            System.out.println(messageService.getMessage("menu.option2"));
            System.out.println(messageService.getMessage("menu.option3"));
            System.out.println(messageService.getMessage("menu.option4"));
            System.out.println(messageService.getMessage("menu.option5"));
            System.out.println(messageService.getMessage("menu.option6"));

            System.out.print(messageService.getMessage("menu.choice"));

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.println("\n" + messageService.getMessage("book.list"));
                        bookService.printLocalizedBooks(bookService.getAllBooks());
                    }
                    case 2 -> {
                        System.out.print(messageService.getMessage("book.add.title"));
                        String title = scanner.nextLine();
                        System.out.print(messageService.getMessage("book.add.author"));
                        String author = scanner.nextLine();
                        System.out.print(messageService.getMessage("book.add.description"));
                        String description = scanner.nextLine();

                        try {
                            bookService.addBook(new Book(-1, title, author, description));
                            System.out.println(messageService.getMessage("book.add.success"));
                        }catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case 3 -> {
                        System.out.print(messageService.getMessage("book.edit.id"));
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print(messageService.getMessage("book.edit.title"));
                        String title = scanner.nextLine();
                        System.out.print(messageService.getMessage("book.edit.author"));
                        String author = scanner.nextLine();
                        System.out.print(messageService.getMessage("book.edit.description"));
                        String description = scanner.nextLine();

                        try {
                            bookService.updateBook(new Book(id, title, author, description));
                            System.out.println(messageService.getMessage("book.edit.success"));
                        } catch (IllegalArgumentException | NoSuchElementException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case 4 -> {
                        System.out.print(messageService.getMessage("book.delete.id"));
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        try {bookService.deleteBook(id);
                        System.out.println(messageService.getMessage("book.delete.success"));}
                        catch(NoSuchElementException e){
                            System.out.println(e.getMessage());
                        }
                    }
                    case 5 -> {
                        System.out.print(messageService.getMessage("search.keyword"));
                        String keyword = scanner.nextLine();
                        List<Book> foundBooks = bookService.searchByKeyword(keyword);
                        if (foundBooks.isEmpty()) {
                            System.out.println(messageService.getMessage("search.noresults", keyword));
                        } else {
                            System.out.println(messageService.getMessage("search.results"));
                            bookService.printLocalizedBooks(foundBooks);
                        }
                    }
                    case 6 -> {
                        System.out.println(messageService.getMessage("app.closing"));
                        scanner.close();
                        return;
                    }
                    default -> System.out.println(messageService.getMessage("error.invalid.choice"));
                }
            } catch (InputMismatchException e) {
                System.out.println(messageService.getMessage("error.invalid.input"));
                scanner.nextLine();
            }
        }
    }
}
