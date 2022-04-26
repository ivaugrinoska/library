package mk.ukim.finki.library_vp.service.impl;

import mk.ukim.finki.library_vp.model.Book;
import mk.ukim.finki.library_vp.model.Category;
import mk.ukim.finki.library_vp.model.User;
import mk.ukim.finki.library_vp.model.exceptions.BookNotFoundException;
import mk.ukim.finki.library_vp.repository.BookRepository;
import mk.ukim.finki.library_vp.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    public List<Book> searchByTitleOrAuthor(String text, String sameText) {
        return bookRepository.findAllByAuthorContainingOrNameContaining(text, sameText);
    }

    @Override
    public List<Book> searchByCategory(Category category) {
        return bookRepository.findAllByCategory(category);
    }

    @Override
    public List<Book> findFirst10(Category c) {
        return bookRepository.findFirst10ByCategory(c);
    }

    @Override
    public void markAsRead(Long bookId, User user) {


    }

    @Override
    public List<Book> findTopRated() {
        return this.bookRepository.findTop3ByOrderByRatingDesc();
    }
}
