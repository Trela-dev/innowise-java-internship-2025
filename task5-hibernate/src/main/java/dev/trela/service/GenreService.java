package dev.trela.service;
import dev.trela.model.Genre;
import dev.trela.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final MessageService messageService;

    public GenreService(GenreRepository genreRepository,MessageService messageService){
        this.genreRepository = genreRepository;
        this.messageService = messageService;
    }

    public void checkIfGenreExists(String genreName) {
        genreRepository.findByGenreName(genreName).orElseThrow(() ->
                new NoSuchElementException(messageService.getMessage("error.genre.notfound")));
    }
    public Genre getGenreByName(String genreName){
        return genreRepository.findByGenreName(genreName)
                .orElseThrow(() -> new NoSuchElementException(
                        messageService.getMessage("error.genre.notfound")
                ));
    }




}
