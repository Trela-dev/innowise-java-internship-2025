package dev.trela.testing.service;

import dev.trela.testing.model.Author;

import dev.trela.testing.repository.AuthorRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository){
        this.authorRepository = authorRepository;
    }

    public void createAuthorIfNotExists(String authorName){
        Optional<Author> author = authorRepository.findByAuthorName(authorName);
        if(author.isEmpty()){
            authorRepository.save(new Author(authorName));
        }
    }


    public List<Author> getAllAuthors(){
        return authorRepository.findAll();
    }

    public void updateAuthor(Author author) throws NoSuchElementException{
        authorRepository.update(author);
    }

    public void deleteAuthor(int id) throws NoSuchElementException{
        authorRepository.deleteById(id);
    }


}
