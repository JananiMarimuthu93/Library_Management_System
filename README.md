# Library Management System

## Overview

The Library Management System is a Java-based application designed to manage books, librarians, members, and administrators in a library. This system supports functionalities like adding, updating, deleting, and retrieving books and user information. It also includes features for searching books, managing overdue books, and more.

## Modules

### 1. **Book Management**

- **BookDAO Interface**: Defines CRUD operations for managing books.
- **BookDAOImpl Class**: Implements methods for adding, updating, deleting, and retrieving books. Includes methods for searching and retrieving overdue books.

### 2. **Librarian Management**

- **LibrarianDAO Interface**: Defines operations for managing librarians.
- **LibrarianDAOImpl Class**: Implements methods for adding, updating, deleting, and retrieving librarian information. Supports fetching librarians by email and username.

### 3. **Member Management**

- **MemberDAO Interface**: Defines operations for managing members.
- **MemberDAOImpl Class**: Implements methods for adding, updating, deleting, and retrieving member information. Includes methods for deactivating members and retrieving active members.

### 4. **Admin Management**

- **AdminDAO Interface**: Defines operations for managing admins.
- **AdminDAOImpl Class**: Implements methods for retrieving admins by email and fetching stored admin passwords.

## Setup Instructions

### Prerequisites

- Java 11 or later
- Maven (for building the project)
- MySQL (or other relational database) with appropriate schema setup

### Installation

1. **Clone the Repository**

   ```sh
   git clone https://github.com/your-username/Library_Management_System.git
   cd Library_Management_System
