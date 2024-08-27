package com.library.management.entity;

import java.util.Objects;

public class Admin {

    private int adminId;

    // Getter for ID
    public int getAdminId() {
        return adminId;
    }

    // Setter for ID
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    private String userName;

    // Getter for Name
    public String getUserName() {
        return userName;
    }

    // Setter for Name
    public void setUserName(String userName) {
        if (userName != null && !userName.trim().isEmpty()) {
            this.userName = userName;
        }
    }

    private String password;

    // Getter for Password
    public String getPassword() {
        return password;
    }

    // Setter for Password
    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            this.password = password; // Set password directly without hashing
        }
    }

    private String email;

    // Getter for Email
    public String getEmail() {
        return email;
    }

    // Setter for Email
    public void setEmail(String email) {
        if (email != null && email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            this.email = email;
        }
    }

    // Constructor with parameters
    public Admin(int adminId, String userName, String email, String password) {
        this.adminId = adminId;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }


    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return adminId == admin.adminId &&
                Objects.equals(userName, admin.userName) &&
                Objects.equals(password, admin.password) &&
                Objects.equals(email, admin.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId, userName, password, email);
    }
}
