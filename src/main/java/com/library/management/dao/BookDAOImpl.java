package com.library.management.dao;

import com.library.management.entity.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {

    private static final Logger logger = LoggerFactory.getLogger(BookDAOImpl.class);
    private final DataSource dataSource;

    // Constructor
    public BookDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String ADD_BOOK = "INSERT INTO books (title, author, isbn, quantity, available) VALUES (?, ?, ?, ?, ?)";

    @Override
    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        String sql = ADD_BOOK;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setBoolean(5, book.isAvailable());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setBookId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding book: {}", book, e);
            throw new RuntimeException("Failed to add book", e);
        }
    }

    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, isbn = ?, quantity = ?, available = ? WHERE book_id = ?";

    @Override
    public void updateBook(Book book) {
        if (book == null || book.getBookId() <= 0) {
            throw new IllegalArgumentException("Book cannot be null and must have a valid ID");
        }
        String sql = UPDATE_BOOK;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setBoolean(5, book.isAvailable());
            pstmt.setInt(6, book.getBookId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No book found with ID: " + book.getBookId());
            }
        } catch (SQLException e) {
            logger.error("Error updating book: {}", book, e);
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Override
    public boolean deleteBook(int bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be positive");
        }
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting book with ID: {}", bookId, e);
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    @Override
    public Optional<Book> getBookById(int bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Book ID must be a positive integer");
        }

        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving book with ID: {}", bookId, e);
            throw new RuntimeException("Failed to retrieve book", e);
        }
        return Optional.empty();
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        int bookId = rs.getInt("book_id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String isbn = rs.getString("isbn");
        int quantity = rs.getInt("quantity");
        boolean available = rs.getBoolean("available");

        // Handle possible nulls if any field in Book can be null
        return new Book(
                bookId,
                title != null ? title : "Unknown Title", // Example of handling null
                author != null ? author : "Unknown Author", // Example of handling null
                isbn != null ? isbn : "Unknown ISBN", // Example of handling null
                quantity,
                available
        );
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }

        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching books with keyword: {}", keyword, e);
            throw new RuntimeException("Failed to search books", e);
        }
        return books;
    }

    @Override
    public List<Book> getAllBooks() {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all books", e);
            throw new RuntimeException("Failed to retrieve all books", e);
        }
        return books;
    }

    @Override
    public List<Book> getOverdueBooks() {
        String sql = "SELECT b.* FROM books b " +
                "JOIN book_borrows bb ON b.book_id = bb.book_id " +
                "WHERE bb.return_date IS NULL AND bb.due_date < ?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving overdue books", e);
            throw new RuntimeException("Failed to retrieve overdue books", e);
        }
        return books;
    }
}
