package com.library.management.ui;

import com.library.management.entity.Book;
import com.library.management.entity.BookBorrow;
import com.library.management.entity.Member;
import com.library.management.service.BookService;
import com.library.management.service.MemberService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MemberDashboard {
    private final MemberService memberService;
    private final BookService bookService;
    private Member currentMember; // This should be used consistently
    private final Scanner scanner = new Scanner(System.in);

    public MemberDashboard(BookService bookService, MemberService memberService, Member currentMember) {
        this.bookService = bookService;
        this.memberService = memberService;
        this.currentMember = currentMember;
    }

    public void showMenu() {
        while (true) {
            System.out.println("Member Dashboard");
            System.out.println("1. Update Member Information");
            System.out.println("2. Search Books");
            System.out.println("3. View All Books");
            System.out.println("4. View Borrowed Books");
            System.out.println("5. Borrow a Book");
            System.out.println("6. Return a Book");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = getValidChoice(7);

            switch (choice) {
                case 1 -> updateMemberInformation();
                case 2 -> searchBooks();
                case 3 -> viewAllBooks();
                case 4 -> viewBorrowedBooks();
                case 5 -> borrowBook();
                case 6-> returnBook();
                case 7 -> {
                    System.out.println("Exiting Member Dashboard.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void updateMemberInformation() {
        if (currentMember == null) {
            System.out.println("No member logged in.");
            return;
        }

        try {
            System.out.println("Updating information for Member: " + currentMember.getUserName());
            System.out.println("Enter new Username (current: " + currentMember.getUserName() + "):");
            String userName = scanner.nextLine().trim();
            if (!userName.isEmpty()) {
                currentMember.setUserName(userName);
            }

            System.out.println("Enter new Password:");
            String password = scanner.nextLine().trim();
            if (!password.isEmpty()) {
                currentMember.setPassword(password);
            }

            System.out.println("Enter new Email (current: " + currentMember.getEmail() + "):");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) {
                if (isValidEmail(email)) { // Validate email
                    currentMember.setEmail(email);
                } else {
                    System.out.println("Invalid email format.");
                    return;
                }
            }

            memberService.updateMember(currentMember);
            System.out.println("Member information updated successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred while updating the member. Please try again.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private void searchBooks() {
        try {
            System.out.print("Enter a keyword to search for books: ");
            String keyword = scanner.nextLine().trim();

            List<Book> books = bookService.searchBooks(keyword);
            if (books.isEmpty()) {
                System.out.println("No books found matching the keyword.");
            } else {
                System.out.println("Search Results:");
                for (Book book : books) {
                    System.out.println("Book ID: " + book.getBookId() + ", Title: " + book.getTitle() +
                            ", Author: " + book.getAuthor() + ", ISBN: " + book.getIsbn());
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while searching for books: " + e.getMessage());
        }
    }

    private void viewAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("No books available.");
            } else {
                System.out.println("All Books:");
                for (Book book : books) {
                    System.out.println("Book ID: " + book.getBookId() + ", Title: " + book.getTitle() +
                            ", Author: " + book.getAuthor() + ", ISBN: " + book.getIsbn());
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving the list of books: " + e.getMessage());
        }
    }

    private void viewBorrowedBooks() {
        if (currentMember == null) {
            System.out.println("No member logged in.");
            return;
        }

        try {
            List<BookBorrow> borrowedBooks = currentMember.getBookBorrows();
            if (borrowedBooks.isEmpty()) {
                System.out.println("You have no borrowed books.");
            } else {
                System.out.println("Borrowed Books:");
                for (BookBorrow borrow : borrowedBooks) {
                    Book book = borrow.getBook();
                    System.out.println("Book ID: " + book.getBookId() + ", Title: " + book.getTitle() +
                            ", Borrow Date: " + borrow.getBorrowDate() + ", Due Date: " + borrow.getDueDate() +
                            ", Return Date: " + borrow.getReturnDate());
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while retrieving borrowed books: " + e.getMessage());
        }
    }

    private void borrowBook() {
        if (currentMember == null) {
            System.out.println("No member logged in.");
            return;
        }

        try {
            System.out.print("Enter the Book ID to borrow: ");
            int bookId = getValidInt();

            if (bookId <= 0) {
                System.out.println("Invalid Book ID. Please enter a positive number.");
                return;
            }

            Optional<Book> optionalBook = bookService.getBookById(bookId);
            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();

                if (book.isAvailable() && book.getQuantity() > 0) {
                    BookBorrow borrow = new BookBorrow(currentMember, book, LocalDate.now(), LocalDate.now().plusWeeks(2));
                    currentMember.addBookBorrow(borrow);
                    book.setAvailable(false);
                    book.setQuantity(book.getQuantity() - 1);
                    book.setBorrowCount(book.getBorrowCount() + 1);

                    memberService.updateMember(currentMember);
                    bookService.updateBook(book);
                    System.out.println("Successfully borrowed the book: " + book.getTitle());
                } else {
                    System.out.println("The book is not available or all copies are borrowed.");
                }
            } else {
                System.out.println("Book not found.");
            }
        } catch (Exception e) {
            System.out.println("Failed to borrow the book: " + e.getMessage());
        }
    }

    private void returnBook() {
        if (currentMember == null) {
            System.out.println("No member logged in.");
            return;
        }

        try {
            System.out.print("Enter the Book ID to return: ");
            int bookId = getValidInt();

            Optional<BookBorrow> optionalBorrow = currentMember.getBookBorrowByBook(bookId);
            if (optionalBorrow.isPresent()) {
                BookBorrow borrow = optionalBorrow.get();
                Book book = borrow.getBook();
                currentMember.removeBookBorrow(borrow);
                book.setAvailable(true);
                bookService.updateBook(book);
                memberService.updateMember(currentMember);
                System.out.println("Successfully returned the book: " + book.getTitle());
            } else {
                System.out.println("You have not borrowed this book.");
            }
        } catch (Exception e) {
            System.out.println("Failed to return the book: " + e.getMessage());
        }
    }

    private int getValidInt() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                return input;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private int getValidChoice(int maxOption) {
        int choice;
        do {
            choice = getValidInt();
            if (choice < 1 || choice > maxOption) {
                System.out.println("Invalid choice. Please enter a number between 1 and " + maxOption + ".");
            }
        } while (choice < 1 || choice > maxOption);
        return choice;
    }
}
