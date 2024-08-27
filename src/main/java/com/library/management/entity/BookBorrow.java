package com.library.management.entity;

import java.time.LocalDate;
import java.util.Objects;

public class BookBorrow {
    private final Member member;
    private final Book book;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public BookBorrow(Member member, Book book, LocalDate borrowDate, LocalDate dueDate) {
        this.member = member;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null; // Default value
    }

    // Getters
    public Member getMember() { return member; }
    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    // Check if the book is overdue
    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    // Override toString for better readability
    @Override
    public String toString() {
        return "BookBorrow{" +
                "member=" + member +
                ", book=" + book +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookBorrow that = (BookBorrow) o;
        return Objects.equals(member, that.member) &&
                Objects.equals(book, that.book) &&
                Objects.equals(borrowDate, that.borrowDate) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(returnDate, that.returnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, book, borrowDate, dueDate, returnDate);
    }
}
