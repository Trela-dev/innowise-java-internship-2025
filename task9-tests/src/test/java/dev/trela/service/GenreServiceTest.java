package dev.trela.service;

import dev.trela.model.Genre;
import dev.trela.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;
    @Mock
    private MessageService messageService;
    @InjectMocks
    private GenreService genreService;

    @Test
    void shouldReturnGenreWhenFound(){
        String genreName = "Fantasy";
        Genre genre = new Genre(1,genreName);

        when(genreRepository.findByName(genreName)).thenReturn(Optional.of(genre));

        Genre result = genreService.getGenreByName(genreName);

        assertEquals(genre,result);
        verify(genreRepository).findByName(genreName);
        verifyNoInteractions(messageService);
    }




}

