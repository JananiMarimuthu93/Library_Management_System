package com.library.management.ui;

import com.library.management.entity.Librarian;
import com.library.management.service.LibrarianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class AdminDashboard {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboard.class);
    private final LibrarianService librarianService;
    private final Scanner scanner;

    // Constructor
    public AdminDashboard(LibrarianService librarianService) {
        this.librarianService = librarianService;
        this.scanner = new Scanner(System.in);
    }

    // Show Admin Dashboard
    public void showMenu() {
        while (true) {
            displayMenuOptions();
            int choice = getValidChoice();
            if (choice == 5) {
                System.out.println("Exiting Admin Dashboard.");
                break;
            }
            handleMenuChoice(choice);
        }
        scanner.close(); // Close the scanner when done
    }

    private void displayMenuOptions() {
        System.out.println("Admin Dashboard - Manage Librarians");
        System.out.println("1. Add Librarian");
        System.out.println("2. Update Librarian Information");
        System.out.println("3. Delete Librarian");
        System.out.println("4. View All Librarians");
        System.out.println("5. Exit");
    }

    private void handleMenuChoice(int choice) {
        switch (choice) {
            case 1 -> addLibrarian();
            case 2 -> updateLibrarian();
            case 3 -> deleteLibrarian();
            case 4 -> viewAllLibrarians();
            default -> System.out.println("Invalid choice! Please try again.");
        }
    }

    private void addLibrarian() {
        try {
            // Prompt for username and validate it
            String userName = getStringInput("Enter Username:");
            System.out.println("Received username: " + userName); // Debug statement
            if (userName == null || userName.trim().isEmpty()) {
                System.out.println("Username cannot be null or empty. Please try again.");
                return;
            }

            // Check if the username already exists
            if (librarianService.isUserNameExists(userName)) {
                System.out.println("Username already exists. Please try another.");
                return;
            }

            // Prompt for password
            String password = getStringInput("Enter Password:");
            if (password == null || password.trim().isEmpty()) {
                System.out.println("Password cannot be null or empty. Please try again.");
                return;
            }

            // Prompt for email and validate it
            String email = getStringInput("Enter Email:");
            if (!isValidEmail(email)) {
                System.out.println("Invalid email format. Please enter a valid email.");
                return;
            }

            // Check if the email already exists
            if (librarianService.isEmailExists(email)) {
                System.out.println("Email already exists. Please try another.");
                return;
            }

            // Create a new Librarian object and add it
            Librarian librarian = new Librarian(userName, password, email);
            librarianService.addLibrarian(librarian);
            System.out.println("Librarian added successfully!");
        } catch (Exception e) {
            logger.error("Error while adding librarian: {}", e.getMessage());
            System.out.println("An error occurred while adding the librarian. Please try again.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    // Update Librarian Information
    private void updateLibrarian() {
        try {
            int librarianId = getIntInput("Enter Librarian ID to update:");
            Optional<Librarian> optionalLibrarian = librarianService.getLibrarianById(librarianId);

            if (optionalLibrarian.isPresent()) {
                Librarian librarian = optionalLibrarian.get();

                String newUserName = getStringInput("Enter new username (or press Enter to keep current):");
                if (!newUserName.isEmpty()) librarian.setUserName(newUserName);

                String newPassword = getStringInput("Enter new password (or press Enter to keep current):");
                if (!newPassword.isEmpty()) librarian.setPassword(newPassword);

                String newEmail = getStringInput("Enter new email (or press Enter to keep current):");
                if (!newEmail.isEmpty()) {
                    if (!isValidEmail(newEmail)) {
                        System.out.println("Invalid email format. Please enter a valid email.");
                        return;
                    }
                    if (librarianService.isEmailExists(newEmail)) {
                        System.out.println("Email already exists. Please try another.");
                        return;
                    }
                    librarian.setEmail(newEmail);
                }

                librarianService.updateLibrarian(librarian);
                System.out.println("Librarian information updated successfully!");
            } else {
                System.out.println("Librarian with ID " + librarianId + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            logger.error("Error while updating librarian: {}", e.getMessage());
            System.out.println("An error occurred while updating the librarian. Please try again.");
        }
    }

    // Delete a Librarian
    private void deleteLibrarian() {
        try {
            int librarianId = getIntInput("Enter Librarian ID to delete:");
            if (librarianId <= 0) {
                System.out.println("Invalid ID. Please enter a positive integer.");
                return;
            }

            System.out.println("Are you sure you want to delete this librarian? (yes/no)");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if ("yes".equals(confirmation)) {
                boolean success = librarianService.deleteLibrarian(librarianId);
                if (success) {
                    System.out.println("Librarian deleted successfully!");
                } else {
                    System.out.println("Librarian not found or could not be deleted.");
                }
            } else {
                System.out.println("Delete operation canceled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            logger.error("Error while deleting librarian with ID: {}", e.getMessage());
            System.out.println("An error occurred while deleting the librarian. Please try again.");
        }
    }

    // View All Librarians
    private void viewAllLibrarians() {
        try {
            List<Librarian> librarians = librarianService.getAllLibrarians();

            if (librarians.isEmpty()) {
                System.out.println("No librarians found.");
            } else {
                System.out.println("Librarians List:");
                librarians.forEach(librarian ->
                        System.out.printf("ID: %d, Email: %s, Username: %s%n",
                                librarian.getLibrarianId(),
                                librarian.getEmail(),
                                librarian.getUserName())
                );
            }
        } catch (Exception e) {
            logger.error("Error while retrieving librarians: {}", e.getMessage());
            System.out.println("An error occurred while retrieving the librarians. Please try again.");
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " ");
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private int getValidChoice() {
        return getIntInput("Please enter your choice (1-5):");
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine().trim();
    }
}
