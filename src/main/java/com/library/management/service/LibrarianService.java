package com.library.management.service;

import com.library.management.dao.LibrarianDAO;
import com.library.management.entity.Librarian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class LibrarianService {

    private static final Logger logger = LoggerFactory.getLogger(LibrarianService.class);
    private final LibrarianDAO librarianDAO;

    public LibrarianService(LibrarianDAO librarianDAO) {
        this.librarianDAO = librarianDAO;
    }

    private void validateLibrarian(Librarian librarian) {
        if (librarian.getUserName() == null || librarian.getUserName().isEmpty()) {
            throw new IllegalArgumentException("Librarian name cannot be null or empty");
        }
        if (librarian.getEmail() == null || !librarian.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Librarian email is invalid");
        }
    }

    public boolean isUserNameExists(String userName) {
        if (userName == null || userName.isEmpty()) {
            logger.error("Cannot check if username exists: username is null or empty.");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            return librarianDAO.getLibrarianByUserName(userName).isPresent();
        } catch (Exception e) {
            logger.error("Error checking if username exists: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check if username exists", e);
        }
    }

    public void addLibrarian(Librarian librarian) {
        if (librarian == null) {
            logger.error("Cannot register librarian: librarian object is null.");
            throw new IllegalArgumentException("Librarian cannot be null");
        }
        try {
            validateLibrarian(librarian);
            librarianDAO.addLibrarian(librarian);
            logger.info("Successfully registered librarian with ID: {}", librarian.getLibrarianId());
        } catch (IllegalArgumentException e) {
            logger.error("Validation failed for librarian: {}", e.getMessage());
            throw e; // Re-throw to signal caller about validation issues
        } catch (Exception e) {
            logger.error("Error registering librarian: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register librarian", e);
        }
    }

    public void updateLibrarian(Librarian librarian) {
        if (librarian == null || librarian.getLibrarianId() <= 0) {
            logger.error("Cannot update librarian: librarian object is null or has invalid ID.");
            throw new IllegalArgumentException("Librarian cannot be null and must have a valid ID");
        }
        try {
            validateLibrarian(librarian);
            librarianDAO.updateLibrarian(librarian);
            logger.info("Successfully updated librarian with ID: {}", librarian.getLibrarianId());
        } catch (Exception e) {
            logger.error("Error updating librarian: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update librarian", e);
        }
    }

    public Optional<Librarian> getLibrarianById(int librarianId) {
        if (librarianId <= 0) {
            logger.error("Cannot retrieve librarian: invalid ID.");
            throw new IllegalArgumentException("Librarian ID must be positive");
        }
        try {
            Optional<Librarian> librarian = librarianDAO.getLibrarianById(librarianId);
            if (librarian.isPresent()) {
                logger.info("Successfully retrieved librarian with ID: {}", librarianId);
            } else {
                logger.warn("No librarian found with ID: {}", librarianId);
            }
            return librarian;
        } catch (Exception e) {
            logger.error("Error retrieving librarian with ID: {}", librarianId, e);
            throw new RuntimeException("Failed to retrieve librarian", e);
        }
    }

    public boolean deleteLibrarian(int librarianId) {
        if (librarianId <= 0) {
            logger.error("Cannot delete librarian: invalid ID.");
            throw new IllegalArgumentException("ID must be positive");
        }
        try {
            boolean result =librarianDAO.deleteLibrarian(librarianId);
            if (result) {
                logger.info("Member deleted successfully with ID: {}", librarianId);
            } else {
                logger.warn("Member with ID {} not found for deletion",librarianId);
            }
            return result;
        } catch (Exception e) {
            logger.error("Error deleting librarian with ID: {}", librarianId, e);
            throw new RuntimeException("Failed to delete librarian", e);
        }
    }

    public List<Librarian> getAllLibrarians() {
        try {
            List<Librarian> librarians = librarianDAO.getAllLibrarians();
            logger.info("Successfully retrieved all librarians");
            return librarians;
        } catch (Exception e) {
            logger.error("Error retrieving all librarians", e);
            throw new RuntimeException("Failed to retrieve librarians", e);
        }
    }
    public boolean isEmailExists(String email) {
        return librarianDAO.getLibrarianByEmail(email).isPresent();
    }
}
