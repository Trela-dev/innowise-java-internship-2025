package dev.trela.microservices.book.service;

import dev.trela.microservices.book.model.Genre;
import dev.trela.microservices.book.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class GenreService {

    private final MessageService messageService;
    private final GenreRepository genreRepository;

    public GenreService(MessageService messageService, GenreRepository genreRepositoryJPA){
        this.messageService = messageService;
        this.genreRepository = genreRepositoryJPA;
    }

    public Genre getGenreByName(String genreName) throws NoSuchElementException{
        return genreRepository.findByName(genreName)
                .orElseThrow(() -> new NoSuchElementException(
                        messageService.getMessage("error.genre.notfound")
                ));
    }




}
