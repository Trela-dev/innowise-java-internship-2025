package dev.trela;


import dev.trela.config.LibraryConfig;
import dev.trela.model.Author;
import dev.trela.model.Book;
import dev.trela.model.Genre;
import dev.trela.service.AuthorService;
import dev.trela.service.BookService;
import dev.trela.service.GenreService;
import dev.trela.service.MessageService;
import dev.trela.util.GenreTranslator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.*;

public class Library3000App {



    public static void main(String[] args) {


        ApplicationContext context = new AnnotationConfigApplicationContext(LibraryConfig.class);
        BookService bookService = context.getBean(BookService.class);
        GenreService genreService = context.getBean(GenreService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthorService authorService = context.getBean(AuthorService.class);
        Scanner scanner = new Scanner(System.in);


        System.out.println();
        System.out.println("Welcome to Library 3000!");

        handleLanguageSelection(messageService,scanner);

        boolean isCurrentLanguagePolish = messageService.isCurrentLocale("pl");




        while (true) {
            printMenu(messageService);
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1 -> handleDisplayBookList(messageService,bookService);
                    case 2 -> handleCreateNewBook(messageService,bookService,genreService,authorService,scanner,isCurrentLanguagePolish);
                    case 3 -> handleEditABook(messageService,bookService,genreService,authorService,scanner,isCurrentLanguagePolish);
                    case 4 -> handleDeleteABook(messageService,bookService,scanner);
                    case 5 -> handleSearchByKeyword(messageService,bookService,scanner);
                    case 6 -> {
                        System.out.println(messageService.getMessage("app.closing"));
                        scanner.close();
                        return;
                    }
                    default -> System.out.println(messageService.getMessage("error.invalid.input"));
                }
            } catch (InputMismatchException e) {
                System.out.println(messageService.getMessage("error.invalid.input"));
                scanner.nextLine();
            }
        }
    }
    private static void handleLanguageSelection(MessageService messageService,Scanner scanner){
        System.out.println();


        System.out.println(messageService.getMessage("language.selection"));
        int languageChoice = scanner.nextInt();
        if (languageChoice == 2) {
            messageService.setLocale(Locale.forLanguageTag("pl"));
        }
    }
    private static void printMenu(MessageService messageService){System.out.println("\n" + messageService.getMessage("menu.title"));
        System.out.println(messageService.getMessage("menu.option1"));
        System.out.println(messageService.getMessage("menu.option2"));
        System.out.println(messageService.getMessage("menu.option3"));
        System.out.println(messageService.getMessage("menu.option4"));
        System.out.println(messageService.getMessage("menu.option5"));
        System.out.println(messageService.getMessage("menu.option6"));

        System.out.print(messageService.getMessage("menu.choice"));
    }

    private static void handleDisplayBookList(MessageService messageService,BookService bookService){
        System.out.println("\n" + messageService.getMessage("book.list"));
        bookService.printLocalizedBooks(bookService.getAllBooks());
    }

    private static void handleCreateNewBook(MessageService messageService,
                                           BookService bookService,
                                           GenreService genreService,
                                           AuthorService authorService,
                                           Scanner scanner,
                                           boolean isCurrentLanguagePolish){

        System.out.print(messageService.getMessage("book.add.title"));
        String title = scanner.nextLine();

        System.out.print(messageService.getMessage("book.add.author"));
        List<Author> authors = collectAuthorsFromUser(messageService,authorService,scanner);

        System.out.print(messageService.getMessage("book.add.description"));
        String description = scanner.nextLine();

        System.out.print(messageService.getMessage("book.add.genre"));
        System.out.println(messageService.getMessage("book.genre.options"));
        String genreName = scanner.nextLine();
        if(isCurrentLanguagePolish) genreName = GenreTranslator.translateToEnglish(genreName);

        System.out.print(messageService.getMessage("book.add.pages"));
        int pages = scanner.nextInt();
        scanner.nextLine();

        System.out.print(messageService.getMessage("book.add.rating"));
        BigDecimal rating = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            genreService.checkIfGenreExists(genreName);
            bookService.validateRating(rating);
            Genre chosenGenre = genreService.getGenreByName(genreName);

            Book newBook = new Book(title,description,pages,rating,authors,chosenGenre);
            bookService.addBook(newBook);
            System.out.println(messageService.getMessage("book.add.success"));
        }catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void handleEditABook(MessageService messageService,
                                        BookService bookService,
                                        GenreService genreService,
                                        AuthorService authorService,
                                        Scanner scanner,
                                        boolean isCurrentLanguagePolish) {
        System.out.print(messageService.getMessage("book.edit.id"));
        int id = scanner.nextInt();
        scanner.nextLine();


        Book existingBook = bookService.getBookById(id);

        System.out.print(messageService.getMessage("book.edit.title"));
        String title = scanner.nextLine();

        System.out.print(messageService.getMessage("book.edit.author"));
        List<Author> authors = collectAuthorsFromUser(messageService, authorService, scanner);

        System.out.print(messageService.getMessage("book.edit.description"));
        String description = scanner.nextLine();

        System.out.print(messageService.getMessage("book.edit.genre"));
        System.out.println(messageService.getMessage("book.genre.options"));
        String genreName = scanner.nextLine();
        if (isCurrentLanguagePolish) genreName = GenreTranslator.translateToEnglish(genreName);

        System.out.print(messageService.getMessage("book.edit.pages"));
        int pages = scanner.nextInt();
        scanner.nextLine();

        System.out.print(messageService.getMessage("book.edit.rating"));
        BigDecimal rating = scanner.nextBigDecimal();
        scanner.nextLine();

        try {
            genreService.checkIfGenreExists(genreName);
            bookService.validateRating(rating);


            existingBook.setTitle(title);
            existingBook.setDescription(description);
            existingBook.setPages(pages);
            existingBook.setRating(rating);
            existingBook.setGenre(genreService.getGenreByName(genreName));


            existingBook.getAuthors().clear();
            existingBook.getAuthors().addAll(authors);

            bookService.updateBook(existingBook);
            System.out.println(messageService.getMessage("book.edit.success"));
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }




    private static List<Author> collectAuthorsFromUser(MessageService messageService, AuthorService authorService, Scanner scanner){
        Set<Author> authors = new LinkedHashSet<>();
        System.out.println(messageService.getMessage("author.input.prompt"));
        String input = scanner.nextLine();
        if ("1".equals(input)) {
            System.out.println(messageService.getMessage("author.multiple.authors.prompt"));
            while (true) {
                System.out.println(messageService.getMessage("author.name.input"));
                String authorName = scanner.nextLine();
                if ("2".equalsIgnoreCase(authorName)) {
                    break;
                }
                Author newAuthor = authorService.findOrCreateAuthor(authorName);
                authors.add(newAuthor);
            }
        } else {
            Author newAuthor = authorService.findOrCreateAuthor(input);
            authors.add(newAuthor);

        }

        return new ArrayList<>(authors);
    }




    private static void handleDeleteABook(MessageService messageService, BookService bookService, Scanner scanner) {
        System.out.print(messageService.getMessage("book.delete.id"));
        int id = scanner.nextInt();
        scanner.nextLine();
        try {
            bookService.deleteBook(id);
            System.out.println(messageService.getMessage("book.delete.success"));
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

    }


    private static void handleSearchByKeyword(MessageService messageService,BookService bookService,Scanner scanner){
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



}
