package com.library.management.dao;

import com.library.management.entity.Admin;

import java.util.Optional;

public interface AdminDAO {
    Optional<Admin> getAdminByEmail(String email);
    Optional<String> getStoredAdminPassword(int adminId);
}
