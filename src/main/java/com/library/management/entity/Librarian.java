package com.library.management.entity;

import java.util.Objects;

public class Librarian {

    private int librarianId;
    private String userName;
    private String password; // Note: Handle password securely
    private String email;

    // Default constructor
    public Librarian() {}

    public Librarian(String userName, String password, String email) {
        setUserName(userName);
        setPassword(password); // Set password directly without hashing (update this)
        setEmail(email);
    }

    public int getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(int librarianId) {
        this.librarianId = librarianId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName != null && !userName.trim().isEmpty()) {
            this.userName = userName;
        } else {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            this.password = password; // Set password directly without hashing (update this)
        } else {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Email is invalid");
        }
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "librarianId=" + librarianId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Librarian librarian = (Librarian) o;
        return librarianId == librarian.librarianId &&
                Objects.equals(userName, librarian.userName) &&
                Objects.equals(password, librarian.password) &&
                Objects.equals(email, librarian.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(librarianId, userName, password, email);
    }
}
