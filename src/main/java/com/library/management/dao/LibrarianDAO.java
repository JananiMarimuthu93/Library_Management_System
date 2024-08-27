package com.library.management.dao;

import com.library.management.entity.Librarian;

import java.util.List;
import java.util.Optional;

public interface LibrarianDAO {

    void addLibrarian(Librarian librarian);

    void updateLibrarian(Librarian librarian);

    Optional<Librarian> getLibrarianById(int librarianId);

    boolean deleteLibrarian(int librarianId);

    List<Librarian> getAllLibrarians();

    Optional<Librarian> getLibrarianByEmail(String email);

    Optional<Librarian> getLibrarianByUserName(String userName);
}
