package com.library.management.ui;

import com.library.management.entity.Book;
import com.library.management.entity.Librarian;
import com.library.management.entity.Member;
import com.library.management.service.BookService;
import com.library.management.service.LibrarianService;
import com.library.management.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LibrarianDashboard {
    private static final Logger logger = LoggerFactory.getLogger(LibrarianDashboard.class);
    private final BookService bookService;
    private final MemberService memberService;
    private final LibrarianService librarianService;
    private final Librarian loggedInLibrarian;
    private final Scanner scanner;

    public LibrarianDashboard(BookService bookService, MemberService memberService, LibrarianService librarianService, Librarian loggedInLibrarian) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.librarianService = librarianService;
        this.loggedInLibrarian = loggedInLibrarian;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        try {
            while (true) {
                displayDashboardMenu();
                int choice = getValidChoice(4);

                switch (choice) {
                    case 1 -> manageBooks();
                    case 2 -> manageMembers();
                    case 3 -> updateLibrarianInformation();
                    case 4 -> {
                        System.out.println("Logging out...");
                        return; // Exit to the previous menu
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            scanner.close(); // Ensure the scanner is closed when done
        }
    }

    private void displayDashboardMenu() {
        System.out.println("\nLibrarian Dashboard");
        System.out.println("1. Manage Books");
        System.out.println("2. Manage Members");
        System.out.println("3. Update Librarian Information");
        System.out.println("4. Logout");
        System.out.print("Enter your choice: ");
    }

    private void updateLibrarianInformation() {
        try {
            System.out.println("Updating information for Librarian: " + loggedInLibrarian.getUserName());
            System.out.print("Enter new Username (current: " + loggedInLibrarian.getUserName() + "): ");
            String userName = scanner.nextLine().trim();
            if (!userName.isEmpty()) {
                loggedInLibrarian.setUserName(userName);
            }
            System.out.print("Enter new Password: ");
            String password = scanner.nextLine().trim();
            if (!password.isEmpty()) {
                loggedInLibrarian.setPassword(password);
            }
            System.out.print("Enter new Email (current: " + loggedInLibrarian.getEmail() + "): ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) {
                loggedInLibrarian.setEmail(email);
            }
            librarianService.updateLibrarian(loggedInLibrarian);
            System.out.println("Librarian information updated successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred while updating the librarian. Please try again.");
        }
    }

    private void manageBooks() {
        while (true) {
            try {
                displayBookManagementMenu();
                int choice = getValidChoice(8);

                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> updateBook();
                    case 3 -> deleteBook();
                    case 4 -> viewBookById();
                    case 5 -> viewAllBooks();
                    case 6 -> searchBooks();
                    case 7 -> getOverdueBooks();
                    case 8 -> {
                        return; // Back to Dashboard
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void displayBookManagementMenu() {
        System.out.println("\nBook Management");
        System.out.println("1. Add Book");
        System.out.println("2. Update Book");
        System.out.println("3. Delete Book");
        System.out.println("4. Get Book by ID");
        System.out.println("5. View All Books");
        System.out.println("6. Search Books");
        System.out.println("7. Get Overdue Books");
        System.out.println("8. Back to Dashboard");
        System.out.print("Enter your choice: ");
    }

    private void addBook() {
        try {
            System.out.print("Enter Book Title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Enter Author: ");
            String author = scanner.nextLine().trim();
            System.out.print("Enter ISBN: ");
            String isbn = scanner.nextLine().trim();
            System.out.print("Enter Quantity: ");
            int quantity = getValidInt();

            Book book = new Book(title, author, isbn, quantity);
            bookService.addBook(book);
            System.out.println("Book added successfully!");
        } catch (Exception e) {
            logger.error("Error while adding book: {}", e.getMessage());
            System.out.println("An error occurred while adding the book: " + e.getMessage());
        }
    }

    private void updateBook() {
        try {
            System.out.print("Enter Book ID to Update: ");
            int bookId = getValidInt();
            scanner.nextLine(); // Consume newline character

            Optional<Book> optionalBook = bookService.getBookById(bookId);

            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();

                System.out.print("Enter new title (leave blank to keep current): ");
                String title = scanner.nextLine().trim();
                if (!title.isEmpty()) book.setTitle(title);

                System.out.print("Enter new author (leave blank to keep current): ");
                String author = scanner.nextLine().trim();
                if (!author.isEmpty()) book.setAuthor(author);

                System.out.print("Enter new ISBN (leave blank to keep current): ");
                String isbn = scanner.nextLine().trim();
                if (!isbn.isEmpty()) book.setIsbn(isbn);

                bookService.updateBook(book);
                System.out.println("Book updated successfully!");
            } else {
                System.out.println("Book with ID " + bookId + " not found.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid data.");
            scanner.nextLine(); // Clear invalid input
        } catch (Exception e) {
            logger.error("Error while updating book: {}", e.getMessage());
            System.out.println("An error occurred while updating the book: " + e.getMessage());
        }
    }

    private void deleteBook() {
        int bookId=0;
        try {
            System.out.print("Enter Book ID to Delete: ");
            bookId = getValidInt();
            scanner.nextLine(); // Consume newline character

            if (bookId <= 0) {
                System.out.println("Invalid ID. Please enter a positive integer.");
                return; // Exit the method if ID is invalid
            }

            System.out.print("Are you sure you want to delete this book? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("yes".equals(confirmation)) {
                boolean success = bookService.deleteBook(bookId);
                if (success) {
                    System.out.println("Book deleted successfully!");
                } else {
                    System.out.println("Book not found or could not be deleted.");
                }
            } else {
                System.out.println("Book deletion canceled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        }catch (Exception e) {
            logger.error("Error while deleting book with ID: {}", bookId, e);
            System.out.println("An error occurred while deleting the book. Please try again.");
        }
    }

    private void viewAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("No books found.");
            } else {
                System.out.println("Books:");
                books.forEach(book ->
                        System.out.println(String.format("ID: %d, Title: %s, Author: %s, ISBN: %s, Quantity: %d",
                                book.getBookId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getQuantity()))
                );
            }
        } catch (Exception e) {
            logger.error("Error while retrieving books: {}", e.getMessage());
            System.out.println("An error occurred while retrieving the books: " + e.getMessage());
        }
    }


    private void viewBookById() {
        try {
            System.out.print("Enter Book ID: ");
            int bookId = getValidInt();

            // Retrieve the book
            Optional<Book> optionalBook = bookService.getBookById(bookId);
            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();
                System.out.println("Book Details:");
                System.out.println("ID: " + book.getBookId() +
                        ", Title: " + book.getTitle() +
                        ", Author: " + book.getAuthor() +
                        ", ISBN: " + book.getIsbn() +
                        ", Quantity: " + book.getQuantity() +
                        ", Available: " + (book.isAvailable() ? "Yes" : "No"));
            } else {
                System.out.println("Book not found.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving the book: " + e.getMessage());
        }
    }

    private void searchBooks() {
        try{
            System.out.print("Enter a keyword to search for books: ");
            String keyword = scanner.nextLine().trim();

            List<Book> books = bookService.searchBooks(keyword);
            if (books.isEmpty()) {
                System.out.println("No books found matching the keyword.");
            } else {
                System.out.println("Search Results:");
                for (Book book : books) {
                    System.out.println(STR."Book ID: \{book.getBookId()}, Title: \{book.getTitle()}, Author: \{book.getAuthor()}, ISBN: \{book.getIsbn()}");
                }
            }
        }catch (Exception e) {
            System.out.println("An error occurred while searching for books: " + e.getMessage());
        }

    }

    private void getOverdueBooks() {
        try {
            List<Book> overdueBooks = bookService.getOverdueBooks();
            if (overdueBooks.isEmpty()) {
                System.out.println("No overdue books.");
            } else {
                System.out.println("Overdue Books:");
                overdueBooks.forEach(book -> System.out.println(STR."ID: \{book.getBookId()}, Title: \{book.getTitle()}, Author: \{book.getAuthor()}, ISBN: \{book.getIsbn()}"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving overdue books: " + e.getMessage());
        }
    }

    private void manageMembers() {
        while (true) {
            try {
                displayMemberManagementMenu();
                int choice = getValidChoice(8);

                switch (choice) {
                    case 1 -> addMember();
                    case 2 -> updateMember();
                    case 3 -> deleteMember();
                    case 4 -> viewMemberById();
                    case 5 -> viewAllMembers();
                    case 6 -> displayActiveMembers();
                    case 7 ->deactivateMember();
                    case 8 -> {
                        return; // Back to Dashboard
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private void displayMemberManagementMenu() {
        System.out.println("\nMember Management");
        System.out.println("1. Add Member");
        System.out.println("2. Update Member");
        System.out.println("3. Delete Member");
        System.out.println("4. Get Member by ID");
        System.out.println("5. View All Members");
        System.out.println("6. View Active Members");
        System.out.println("7. DeActivate Members");
        System.out.println("8. Back to Dashboard");
        System.out.print("Enter your choice: ");
    }

    private void addMember() {
        try {
            System.out.println("Enter Username:");
            String userName = scanner.nextLine().trim();
            if (memberService.isUserNameExists(userName)) {
                System.out.println("Username already exists. Please try another.");
                return;
            }

            System.out.println("Enter Email:");
            String email = scanner.nextLine().trim();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email format. Please enter a valid email.");
                return;
            }
            // Check if email already exists
            if (memberService.isEmailExists(email)) {
                System.out.println("Email already exists. Please try another.");
                return;
            }
            System.out.println("Enter Password:");
            String password = scanner.nextLine().trim();

            Member member = new Member(userName, email,password);
            memberService.addMember(member);
            System.out.println("Member added successfully!");
        } catch (Exception e) {
            logger.error("Error while adding member: {}", e.getMessage());
            System.out.println("An error occurred while adding the member: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private void updateMember() {
        try {
            System.out.print("Enter Member ID to Update: ");
            int memberId = getValidInt();
            scanner.nextLine(); // Consume newline character

            Optional<Member> optionalMember = memberService.getMemberById(memberId);
            if (optionalMember.isPresent()) {
                Member member = optionalMember.get();

                System.out.print("Enter new name (leave blank to keep current): ");
                String newUserName = scanner.nextLine().trim();
                if (!newUserName.isEmpty()) {
                    member.setUserName(newUserName);
                }

                System.out.print("Enter new email (leave blank to keep current): ");
                String newEmail = scanner.nextLine().trim();
                if (!newEmail.isEmpty()) {
                    if (!isValidEmail(newEmail)) {
                        System.out.println("Invalid email format. Please enter a valid email.");
                        return;
                    }
                    if (memberService.isEmailExists(newEmail)) {
                        System.out.println("Email already exists. Please try another.");
                        return;
                    }
                    member.setEmail(newEmail);
                }

                System.out.println("Enter new password (or press Enter to keep current):");
                String newPassword = scanner.nextLine().trim();
                if (!newPassword.isEmpty()) {
                    member.setPassword(newPassword);
                }

                memberService.updateMember(member);
                System.out.println("Member updated successfully!");
            } else {
                System.out.println("Member with ID " + memberId + " not found.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid data.");
            scanner.nextLine(); // Clear invalid input
        } catch (Exception e) {
            logger.error("Error while updating member: {}", e.getMessage());
            System.err.println("An error occurred while updating the member: " + e.getMessage());
        }
    }

    private void deleteMember() {
        try {
            System.out.print("Enter Member ID to Delete: ");
            int memberId = getValidInt();

            if (memberId <= 0) {
                System.out.println("Invalid ID. Please enter a positive integer.");
                return;
            }

            while (true) {
                System.out.print("Are you sure you want to delete this member? (yes/no): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if ("yes".equals(confirmation)) {
                    boolean success = memberService.deleteMember(memberId);
                    if (success) {
                        System.out.println("Member deleted successfully!");
                    } else {
                        System.out.println("Member not found or could not be deleted.");
                    }
                    break;
                } else if ("no".equals(confirmation)) {
                    System.out.println("Delete operation canceled.");
                    break;
                } else {
                    System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            logger.error("Error while deleting member with ID: {}", e.getMessage());
            System.out.println("An error occurred while deleting the member: " + e.getMessage());
        }
    }

    private void viewAllMembers() {
        try {
            List<Member> members = memberService.getAllMembers();
            if (members.isEmpty()) {
                System.out.println("No members found.");
            } else {
                System.out.println("Members:");
                members.forEach(member -> System.out.println(STR."ID: \{member.getMemberId()}, Name: \{member.getUserName()}, Email: \{member.getEmail()}"));
            }
        } catch (Exception e) {
            logger.error("Error while retrieving members: {}", e.getMessage());
            System.out.println("An error occurred while retrieving the members: " + e.getMessage());
        }
    }

    private void viewMemberById() {
        try {
            System.out.print("Enter Member ID: ");
            int memberId = getValidInt();

            Optional<Member> optionalMember = memberService.getMemberById(memberId);
            if (optionalMember.isPresent()) {
                Member member = optionalMember.get();
                System.out.println("Member Details:");
                System.out.println("ID: " + member.getMemberId() + ", Name: " + member.getUserName() + ", Email: " + member.getEmail());
            } else {
                System.out.println("Member not found.");
            }
        } catch (Exception e) {
            logger.error("Error while viewing member: {}", e.getMessage());
            System.out.println("An error occurred while retrieving the member: " + e.getMessage());
        }
    }


    private int getValidInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private int getValidChoice(int maxOption) {
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (choice >= 1 && choice <= maxOption) {
                    return choice;
                } else {
                    System.out.println("Invalid choice! Please enter a number between 1 and " + maxOption + ".");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }


    // Method to get and display active members
    public void displayActiveMembers() {
        try {
            List<Member> activeMembers = memberService.getActiveMembers();
            // Code to display active members, e.g., printing or populating a UI component
            for (Member member : activeMembers) {
                System.out.println("Member ID: " + member.getMemberId() +
                        ", Name: " + member.getUserName() +
                        ", Email: " + member.getEmail() +
                        ", Active: " + member.isActive());
            }
        } catch (Exception e) {
            // Handle or log the exception as needed
            e.printStackTrace();
        }
    }

    private void deactivateMember() {
        try {
            System.out.print("Enter Member ID to Deactivate: ");
            int memberId = getValidInt();
            scanner.nextLine(); // Consume newline character

            Optional<Member> optionalMember = memberService.getMemberById(memberId);
            if (optionalMember.isPresent()) {
                Member member = optionalMember.get();
                memberService.deactivateMember(memberId);
                System.out.println("Member deactivated successfully!");
            } else {
                System.out.println("Member with ID " + memberId + " not found.");
            }
        } catch (Exception e) {
            logger.error("Error while deactivating member: {}", e.getMessage());
            System.out.println("An error occurred while deactivating the member: " + e.getMessage());
        }
    }

}

