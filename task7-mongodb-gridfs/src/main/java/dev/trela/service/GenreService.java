package dev.trela.service;
import dev.trela.model.Genre;
import dev.trela.repository.GenreRepositoryJPA;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class GenreService {

    private final MessageService messageService;
    private final GenreRepositoryJPA genreRepositoryJPA;

    public GenreService(MessageService messageService, GenreRepositoryJPA genreRepositoryJPA){
        this.messageService = messageService;
        this.genreRepositoryJPA = genreRepositoryJPA;
    }

    public Genre getGenreByName(String genreName) throws NoSuchElementException{
        return genreRepositoryJPA.findByName(genreName)
                .orElseThrow(() -> new NoSuchElementException(
                        messageService.getMessage("error.genre.notfound")
                ));
    }




}
