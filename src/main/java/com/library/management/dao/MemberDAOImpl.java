package com.library.management.dao;

import com.library.management.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAOImpl implements MemberDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemberDAOImpl.class);
    private final DataSource dataSource;

    // Constructor
    public MemberDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Member> getMemberByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Member member = extractMemberFromResultSet(resultSet);
                    return Optional.of(member);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving member with email: {}", email, e);
        }
        return Optional.empty();
    }

    @Override
    public void addMember(Member member) {
        String sql = "INSERT INTO members (userName, email, password, isActive) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, member.getUserName());
            statement.setString(2, member.getEmail());
            statement.setString(3, member.getPassword());
            statement.setBoolean(4, member.isActive());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setMemberId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding member: {}", member, e);
            throw new RuntimeException("Failed to add member", e);
        }
    }

    @Override
    public void updateMember(Member member) {
        String sql = "UPDATE members SET userName = ?, email = ?, password = ?, isActive = ? WHERE memberId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, member.getUserName());
            statement.setString(2, member.getEmail());
            statement.setString(3, member.getPassword());
            statement.setBoolean(4, member.isActive());
            statement.setInt(5, member.getMemberId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No member found with ID: " + member.getMemberId());
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating member with ID: {}", member.getMemberId(), e);
            throw new RuntimeException("Failed to update member", e);
        }
    }

    @Override
    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE memberId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting member with ID: {}", memberId, e);
            throw new RuntimeException("Failed to delete member", e);
        }
    }

    @Override
    public Optional<Member> getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE memberId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractMemberFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving member with ID: {}", memberId, e);
            throw new RuntimeException("Failed to retrieve member", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                members.add(extractMemberFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all members", e);
            throw new RuntimeException("Failed to retrieve members", e);
        }
        return members;
    }

    @Override
    public List<Member> getActiveMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE isActive = TRUE";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                members.add(extractMemberFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving active members", e);
            throw new RuntimeException("Failed to retrieve active members", e);
        }
        return members;
    }

    @Override
    public boolean deactivateMember(int memberId) {
        String sql = "UPDATE members SET isActive = FALSE WHERE memberId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, memberId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deactivating member with ID: {}", memberId, e);
            throw new RuntimeException("Failed to deactivate member", e);
        }
    }

    private Member extractMemberFromResultSet(ResultSet resultSet) throws SQLException {
        return new Member(
                resultSet.getInt("memberId"),
                resultSet.getString("userName"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getBoolean("isActive")
        );
    }

        @Override
        public Optional<Member> getMemberByUserName(String userName) {
            String sql = "SELECT * FROM members WHERE username = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, userName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Member member = new Member();
                    member.setMemberId(rs.getInt("member_id"));
                    member.setUserName(rs.getString("username"));
                    member.setEmail(rs.getString("email"));
                    member.setPassword(rs.getString("password"));
                    // Set other fields as needed
                    return Optional.of(member);
                } else {
                    return Optional.empty();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to retrieve member by username", e);
            }
        }

    }

