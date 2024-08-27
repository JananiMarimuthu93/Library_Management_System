package com.library.management.service;

import com.library.management.dao.BookDAO;
import com.library.management.entity.Book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    private final BookDAO bookDAO;

    // Constructor
    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    // Add a new book
    public void addBook(Book book) {
        if (book == null) {
            logger.error("Cannot add book: book object is null.");
            throw new IllegalArgumentException("Book cannot be null");
        }
        try {
            bookDAO.addBook(book);
            logger.info("Book added successfully with ID: {}", book.getBookId());
        } catch (Exception e) {
            logger.error("Error adding book: {}", book, e);
            throw new RuntimeException("Failed to add book", e);
        }
    }

    public void updateBook(Book book) {
        if (book == null || book.getBookId() <= 0) {
            logger.error("Cannot update book: book object is null or has invalid ID.");
            throw new IllegalArgumentException("Book cannot be null and must have a valid ID");
        }
        try {
            bookDAO.updateBook(book);
            logger.info("Book updated successfully: {}", book);
        }catch(Exception e){
            logger.error("Error updating book: {}", book, e);
            throw new RuntimeException("Failed to update book", e);
        }
    }

    public Optional<Book> getBookById(int bookId) {
        if (bookId <= 0) {
            logger.error("Cannot retrieve book: invalid ID. Provided ID: {}", bookId);
            throw new IllegalArgumentException("Book ID must be a positive integer");
        }
        try {
            Optional<Book> optionalBook = bookDAO.getBookById(bookId);
            if (optionalBook.isEmpty()) {
                logger.warn("Book with ID {} not found", bookId);
            }
            return optionalBook;
        } catch (Exception e) {
            logger.error("Error retrieving book with ID: {}", bookId, e);
            throw new RuntimeException("Failed to retrieve book", e);
        }
    }

    // Delete a book by ID
    public boolean deleteBook(int bookId) {
        if (bookId <= 0) {
            logger.error("Cannot delete book: invalid ID.");
            throw new IllegalArgumentException("Book ID must be a positive integer");
        }
        try {
            boolean result = bookDAO.deleteBook(bookId);
            if (result) {
                logger.info("Book deleted successfully with ID: {}", bookId);
            } else {
                logger.warn("Book with ID {} not found for deletion", bookId);
            }
            return result;
        } catch (Exception e) {
            logger.error("Error deleting book with ID: {}", bookId, e);
            throw new RuntimeException("Failed to delete book", e);
        }
    }


    // Search for books by keyword
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.error("Cannot search books: keyword is null or empty.");
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }
        try {
            return bookDAO.searchBooks(keyword);
        } catch (Exception e) {
            logger.error("Error searching books with keyword: {}", keyword, e);
            throw new RuntimeException("Failed to search books", e);
        }
    }

    // Retrieve all books
    public List<Book> getAllBooks() {
        try {
            return bookDAO.getAllBooks();
        } catch (Exception e) {
            logger.error("Error retrieving all books", e);
            throw new RuntimeException("Failed to retrieve all books", e);
        }
    }

    // Get overdue books
    public List<Book> getOverdueBooks() {
        try {
            return bookDAO.getOverdueBooks();
        } catch (Exception e) {
            logger.error("Error retrieving overdue books", e);
            throw new RuntimeException("Failed to retrieve overdue books", e);
        }
    }
}
