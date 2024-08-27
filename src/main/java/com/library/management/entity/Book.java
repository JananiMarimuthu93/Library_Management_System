package com.library.management.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private boolean available; // Indicates whether the book is available
    private int borrowCount;
    private int quantity;
    private LocalDate borrowDate;

    // Default constructor
    public Book() {
        this.available = true;
        this.borrowCount = 0;
    }

    // Parameterized constructor (excluding bookId)
    public Book(String title, String author, String isbn, int quantity) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.available = true;
        this.borrowCount = 0;
    }

    // Parameterized constructor (including bookId)
    public Book(int bookId, String title, String author, String isbn, int quantity, boolean available) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.available = available;
        this.borrowCount = 0; // Default value; can be set explicitly if needed
    }

    // Getters and setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getBorrowCount() { return borrowCount; }
    public void setBorrowCount(int borrowCount) { this.borrowCount = borrowCount; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    // Override toString method for better readability
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", quantity=" + quantity +
                ", available=" + available +
                ", borrowCount=" + borrowCount +
                ", borrowDate=" + borrowDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return bookId == book.bookId &&
                quantity == book.quantity &&
                available == book.available &&
                borrowCount == book.borrowCount &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(isbn, book.isbn) &&
                Objects.equals(borrowDate, book.borrowDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, title, author, isbn, quantity, available, borrowCount, borrowDate);
    }
}
