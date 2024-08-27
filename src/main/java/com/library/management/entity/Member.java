package com.library.management.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Member {
    private int memberId;
    private String userName;
    private String password;
    private String email;
    private boolean isActive;
    private List<BookBorrow> bookBorrows;

    // Default constructor
    public Member() {
        this.bookBorrows = new ArrayList<>();
        this.isActive = true; // Assuming new members are active by default
    }

    // Parameterized constructor
    public Member(int memberId, String userName, String email, String password, boolean isActive) {
        this.memberId = memberId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.bookBorrows = new ArrayList<>();
    }

    public Member(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.isActive = true; // Assuming new members are active by default
        this.bookBorrows = new ArrayList<>();
    }

    // Getters and setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName != null && !userName.trim().isEmpty()) {
            this.userName = userName;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            this.password = password; // Set password directly without hashing
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            this.email = email;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<BookBorrow> getBookBorrows() {
        return new ArrayList<>(bookBorrows); // Return a copy to prevent modification of the internal list
    }

    public void addBookBorrow(BookBorrow bookBorrow) {
        if (bookBorrow != null) {
            bookBorrows.add(bookBorrow);
        }
    }

    public void removeBookBorrow(BookBorrow bookBorrow) {
        bookBorrows.remove(bookBorrow);
    }

    public Optional<BookBorrow> getBookBorrowByBook(int bookId) {
        return bookBorrows.stream()
                .filter(borrow -> borrow.getBook().getBookId() == bookId)
                .findFirst();
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return memberId == member.memberId &&
                isActive == member.isActive &&
                Objects.equals(userName, member.userName) &&
                Objects.equals(email, member.email) &&
                Objects.equals(password, member.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, userName, password, email, isActive);
    }
}
