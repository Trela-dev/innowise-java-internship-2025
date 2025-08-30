package dev.trela.microservices.book.service;


import dev.trela.microservices.book.model.Genre;
import dev.trela.microservices.book.repository.GenreRepository;
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

