package com.library.management.dao;

import com.library.management.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookDAO {
    void addBook(Book book);
    void updateBook(Book book);
    boolean deleteBook(int bookId);
    Optional<Book> getBookById(int bookId);
    List<Book> searchBooks(String keyword);
    List<Book> getAllBooks();
    List<Book> getOverdueBooks();
}
