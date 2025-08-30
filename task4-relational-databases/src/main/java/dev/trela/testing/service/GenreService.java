package dev.trela.testing.service;
import dev.trela.testing.model.Genre;
import dev.trela.testing.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }

    public void checkIfGenreExists(String genreName) {
        genreRepository.findByGenreName(genreName).orElseThrow(() ->
                new NoSuchElementException("Genre not Found."));
    }


    public List<Genre> getAllGenres(){
        return genreRepository.findAll();
    }

    public void updateGenre(Genre genre) throws NoSuchElementException{
        genreRepository.update(genre);
    }

    public void deleteGenre(int id) throws NoSuchElementException{
        genreRepository.deleteById(id);
    }





}
