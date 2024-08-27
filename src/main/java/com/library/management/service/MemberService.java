package com.library.management.service;

import com.library.management.dao.MemberDAO;
import com.library.management.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    private  final MemberDAO memberDAO;

    // Constructor
    public MemberService(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }


    private void validateMember(Member member) {
        if (member.getUserName() == null || member.getUserName().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be null or empty");
        }
        String email = member.getEmail();
        if (!isValidEmail(email)) {
            logger.error("Invalid email format: {}", email);
            throw new IllegalArgumentException("Member email is invalid");
        }
    }


    // Check if username exists
    public boolean isUserNameExists(String userName) {
        if (userName == null || userName.isEmpty()) {
            logger.error("Cannot check if username exists: username is null or empty.");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            Optional<Member> member = memberDAO.getMemberByUserName(userName);
            return member.isPresent();
        } catch (Exception e) {
            logger.error("Error checking if username exists: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check if username exists", e);
        }
    }


    // Register a new member
    public void addMember(Member member) {
        if (member == null) {
            logger.error("Cannot register member: member object is null.");
            throw new IllegalArgumentException("Member cannot be null");
        }
        try {
            validateMember(member);
            memberDAO.addMember(member);
            logger.info("Member registered successfully: {}", member);
        } catch (IllegalArgumentException e) {
            logger.error("Validation failed for member: {}", e.getMessage());
            throw e; // Re-throw to signal caller about validation issues
        } catch (Exception e) {
            logger.error("Error registering member: {}", member, e);
            throw new RuntimeException("Failed to register member", e);
        }
    }

    // Update member details
    public void updateMember(Member member) {
        if (member == null || member.getMemberId() <= 0) {
            logger.error("Cannot update member: member object is null or has invalid ID.");
            throw new IllegalArgumentException("Member cannot be null and must have a valid ID");
        }
        try {
            validateMember(member);
            memberDAO.updateMember(member);
            logger.info("Member updated successfully: {}", member);
        } catch (Exception e) {
            logger.error("Error updating member: {}", member, e);
            throw new RuntimeException("Failed to update member", e);
        }
    }

    // Retrieve member by ID
    public Optional<Member> getMemberById(int memberId) {
        if (memberId <= 0) {
            logger.error("Invalid member ID: {}", memberId);
            throw new IllegalArgumentException("Member ID must be a positive integer");
        }
        try {
            Optional<Member> member = memberDAO.getMemberById(memberId);
            if (member.isPresent()) {
                logger.info("Successfully retrieved member with ID: {}", memberId);
            } else {
                logger.warn("No member found with ID: {}", memberId);
            }
            return member;
        } catch (Exception e) {
            logger.error("Error retrieving member with ID: {}", memberId, e);
            throw new RuntimeException("Failed to retrieve member", e);
        }
    }

    // Delete a member by ID
    public boolean deleteMember(int memberId) {
        if (memberId <= 0) {
            logger.error("Invalid member ID for deletion: {}", memberId);
            throw new IllegalArgumentException("Member ID must be a positive integer");
        }
        try {
            boolean result = memberDAO.deleteMember(memberId);
            if (result) {
                logger.info("Member deleted successfully with ID: {}", memberId);
            } else {
                logger.warn("Member with ID {} not found for deletion", memberId);
            }
            return result;
        } catch (Exception e) {
            logger.error("Error deleting member with ID: {}", memberId, e);
            throw new RuntimeException("Failed to delete member", e);
        }
    }

    // Retrieve all members
    public List<Member> getAllMembers() {
        try {
            List<Member> members = memberDAO.getAllMembers();
            logger.info("Successfully retrieved all members");
            return members;
        } catch (Exception e) {
            logger.error("Error retrieving all members", e);
            throw new RuntimeException("Failed to retrieve members", e);
        }
    }

    public List<Member> getActiveMembers() {
        try {
            List<Member> members = memberDAO.getActiveMembers();
            logger.info("Successfully retrieved active members");
            return members;
        } catch (Exception e) {
            logger.error("Error retrieving active members", e);
            throw new RuntimeException("Failed to retrieve active members", e);
        }
    }

    public  boolean isEmailExists(String email) {
        return memberDAO.getMemberByEmail(email).isPresent();
    }
    public boolean deactivateMember(int memberId) {
        return memberDAO.deactivateMember(memberId);
    }
}
