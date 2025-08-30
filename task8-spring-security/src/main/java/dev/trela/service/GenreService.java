package dev.trela.service;
import dev.trela.model.Genre;
import dev.trela.repository.GenreRepository;
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
