package com.library.management.dao;

import com.library.management.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberDAO {

    void addMember(Member member);
    void updateMember(Member member);
    boolean deleteMember(int memberId); // Changed to boolean for consistency with BookDAO
    Optional<Member> getMemberById(int memberId);
    List<Member> getActiveMembers(); // Ensure this is consistent with your requirements
    List<Member> getAllMembers();
    Optional<Member> getMemberByEmail(String email);
    boolean deactivateMember(int memberId);
    Optional<Member> getMemberByUserName(String userName);
}
