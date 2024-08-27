package com.library.management.ui;

import com.library.management.dao.AdminDAOImpl;
import com.library.management.dao.BookDAOImpl;
import com.library.management.dao.LibrarianDAOImpl;
import com.library.management.dao.MemberDAOImpl;

import com.library.management.entity.Admin;
import com.library.management.entity.Librarian;
import com.library.management.entity.Member;

import com.library.management.service.AuthenticationService;
import com.library.management.service.BookService;
import com.library.management.service.LibrarianService;
import com.library.management.service.MemberService;

import com.library.management.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiFunction;

public class MainMenu {

    private static final Logger logger = LoggerFactory.getLogger(MainMenu.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static BookService bookService;
    private static MemberService memberService;
    private static LibrarianService librarianService;
    private static AuthenticationService authenticationService;
    private static Object currentUser = null; // Can be a Member, Librarian, or Admin

    static {
        DataSource dataSource = DatabaseConnection.getDataSource();
        try {
            bookService = new BookService(new BookDAOImpl(dataSource));
            memberService = new MemberService(new MemberDAOImpl(dataSource));
            librarianService = new LibrarianService(new LibrarianDAOImpl(dataSource));
            authenticationService = new AuthenticationService(
                    new MemberDAOImpl(dataSource),
                    new LibrarianDAOImpl(dataSource),
                    new AdminDAOImpl(dataSource)
            );
        } catch (Exception e) {
            logger.error("Error initializing services", e);
            System.exit(1); // Exit if unable to initialize services
        }
    }

    public static void main(String[] args) {
        try {
            runApplication();
        } finally {
            scanner.close();
        }
    }

    private static void runApplication() {
        boolean continueRunning = true;
        while (continueRunning) {
            try {
                continueRunning = displayMainMenu();
            } catch (Exception e) {
                logger.error("An unexpected error occurred", e);
                System.out.println("An unexpected error occurred. Please try again.");
            }
        }
    }

    private static boolean displayMainMenu() {
        System.out.println("Library Management System");
        System.out.println("1. Member Menu");
        System.out.println("2. Librarian Menu");
        System.out.println("3. Admin Menu");
        System.out.println("4. Exit");

        int choice = getValidChoice(4);
        switch (choice) {
            case 1 -> showMemberMenu();
            case 2 -> showLibrarianMenu();
            case 3 -> showAdminMenu();
            case 4 -> {
                System.out.println("Exiting the system.");
                exitApplication();
                return false;
            }
            default -> System.out.println("Invalid choice! Please try again.");
        }
        return true;
    }

    private static void showMemberMenu() {
        displaySubMenu("Member Menu", 3, (choice) -> {
            switch (choice) {
                case 1 -> loginUser(authenticationService::authenticateMember, () -> {
                    if (currentUser instanceof Member) {
                        new MemberDashboard(bookService, memberService, (Member) currentUser).showMenu();
                    } else {
                        System.out.println("User is not a member.");
                    }
                });
                case 2 -> memberSignUp();
                case 3 -> System.out.println("Returning to Main Menu.");
            }
        });
    }

    private static void showLibrarianMenu() {
        displaySubMenu("Librarian Menu", 3, (choice) -> {
            switch (choice) {
                case 1 -> loginUser(authenticationService::authenticateLibrarian, () -> {
                    if (currentUser instanceof Librarian) {
                        new LibrarianDashboard(bookService, memberService, librarianService, (Librarian) currentUser).showMenu();
                    } else {
                        System.out.println("User is not a librarian.");
                    }
                });
                case 2 -> librarianSignUp();
                case 3 -> System.out.println("Returning to Main Menu.");
            }
        });
    }

    private static void showAdminMenu() {
        displaySubMenu("Admin Menu", 2, (choice) -> {
            switch (choice) {
                case 1 -> loginUser(authenticationService::authenticateAdmin, () -> {
                    if (currentUser instanceof Admin) {
                        new AdminDashboard(librarianService).showMenu();
                    } else {
                        System.out.println("User is not an admin.");
                    }
                });
                case 2 -> System.out.println("Returning to Main Menu.");
            }
        });
    }

    private static void displaySubMenu(String menuTitle, int maxOption, SubMenuHandler handler) {
        while (true) {
            try {
                System.out.println(menuTitle);
                for (int i = 1; i <= maxOption; i++) {
                    System.out.println(i + ". " + getMenuOption(menuTitle, i));
                }

                int choice = getValidChoice(maxOption);
                handler.handleChoice(choice);
                if (choice == maxOption) {
                    return;
                }
            } catch (Exception e) {
                logger.error("An error occurred: {}", e.getMessage());
                System.out.println("An unexpected error occurred. Please try again.");
            }
        }
    }

    private static String getMenuOption(String menuTitle, int optionNumber) {
        return switch (menuTitle) {
            case "Member Menu", "Librarian Menu" -> switch (optionNumber) {
                case 1 -> "Login";
                case 2 -> "Sign Up";
                case 3 -> "Back to Main Menu";
                default -> "Unknown Option";
            };
            case "Admin Menu" -> switch (optionNumber) {
                case 1 -> "Login";
                case 2 -> "Back to Main Menu";
                default -> "Unknown Option";
            };
            default -> "Unknown Option";
        };
    }

    private static <T> void loginUser(BiFunction<String, String, Optional<T>> authenticate, Runnable dashboardAction) {
        try {
            System.out.println("Enter the Email:");
            String email = scanner.nextLine();
            System.out.println("Enter the Password:");
            String password = scanner.nextLine();

            Optional<T> userOpt = authenticate.apply(email, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get(); // Set the current user
                System.out.println("Login successful. Welcome, " + getUserName(currentUser) + "!");
                dashboardAction.run();
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (Exception e) {
            logger.error("Error during login", e);
            System.out.println("An error occurred during login. Please try again.");
        }
    }

    private static String getUserName(Object user) {
        if (user instanceof Member) {
            return ((Member) user).getUserName();
        } else if (user instanceof Librarian) {
            return ((Librarian) user).getUserName();
        } else if (user instanceof Admin) {
            return ((Admin) user).getUserName();
        }
        return "User";
    }

    private static void memberSignUp() {
        try {
            System.out.println("Enter the Username:");
            String userName = scanner.nextLine();
            System.out.println("Enter the Email:");
            String email = scanner.nextLine();
            System.out.println("Enter the Password:");
            String password = scanner.nextLine();

            if (isValidEmail(email)) { // Check if email is valid
                Member member = new Member(userName, email, password);
                member.setActive(true);
                memberService.addMember(member);
                System.out.println("Member registered successfully!");
            } else {
                System.out.println("Invalid email format.");
            }
        } catch (Exception e) {
            logger.error("Error during member registration", e);
            System.out.println("An error occurred during registration. Please try again.");
        }
    }

    private static void librarianSignUp() {
        try {
            System.out.println("Enter the Username:");
            String userName = scanner.nextLine();
            System.out.println("Enter the Email:");
            String email = scanner.nextLine();
            System.out.println("Enter the Password:");
            String password = scanner.nextLine();

            if (isValidEmail(email)) { // Check if email is valid
                Librarian librarian = new Librarian(userName,password,email);
                librarianService.addLibrarian(librarian);
                System.out.println("Librarian registered successfully!");
            } else {
                System.out.println("Invalid email format.");
            }
        } catch (Exception e) {
            logger.error("Error during librarian registration", e);
            System.out.println("An error occurred during registration. Please try again.");
        }
    }

    private static int getValidChoice(int maxOption) {
        int choice;
        do {
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character
                if (choice >= 1 && choice <= maxOption) {
                    return choice;
                } else {
                    System.out.println("Invalid choice! Please enter a number between 1 and " + maxOption + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        } while (true);
    }

    private static void exitApplication() {
        System.out.println("Shutting down the system...");
        // Perform any necessary cleanup here
        System.exit(0);
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
    // Functional interface to handle menu choices
    @FunctionalInterface
    private interface SubMenuHandler {
        void handleChoice(int choice);
    }
}
