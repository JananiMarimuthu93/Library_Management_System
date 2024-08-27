package com.library.management.service;

import com.library.management.dao.AdminDAO;
import com.library.management.dao.LibrarianDAO;
import com.library.management.dao.MemberDAO;
import com.library.management.entity.Admin;
import com.library.management.entity.Librarian;
import com.library.management.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthenticationService
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final MemberDAO memberDAO;
    private final LibrarianDAO librarianDAO;
    private final AdminDAO adminDAO;

    public AuthenticationService(MemberDAO memberDAO, LibrarianDAO librarianDAO, AdminDAO adminDAO)
    {
        this.memberDAO = memberDAO;
        this.librarianDAO = librarianDAO;
        this.adminDAO = adminDAO;
    }

    public Optional<Member> authenticateMember(String email, String password)
    {
        Optional<Member> memberOpt = memberDAO.getMemberByEmail(email);
        if (memberOpt.isPresent() && checkPassword(password, memberOpt.get().getPassword())) {
            return memberOpt;
        }
        logger.warn("Failed authentication attempt for member with email: {}", email);
        return Optional.empty();
    }

    public Optional<Librarian> authenticateLibrarian(String email, String password)
    {
        Optional<Librarian> librarianOpt = librarianDAO.getLibrarianByEmail(email);
        if (librarianOpt.isPresent() && checkPassword(password, librarianOpt.get().getPassword())) {
            return librarianOpt;
        }
        logger.warn("Failed authentication attempt for librarian with email: {}", email);
        return Optional.empty();
    }

    public Optional<Admin> authenticateAdmin(String email, String password)
    {
        Optional<Admin> adminOpt = adminDAO.getAdminByEmail(email);
        if (adminOpt.isPresent() && checkPassword(password, adminOpt.get().getPassword())) {
            return adminOpt;
        }
        logger.warn("Failed authentication attempt for admin with email: {}", email);
        return Optional.empty();
    }

    private boolean checkPassword(String inputPassword, String storedPassword)
    {
        // Simple password check, comparing input password with stored password
        return inputPassword.equals(storedPassword);
    }
}