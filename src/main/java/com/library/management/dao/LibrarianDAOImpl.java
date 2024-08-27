package com.library.management.dao;

import com.library.management.entity.Librarian;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibrarianDAOImpl implements LibrarianDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibrarianDAOImpl.class);
    private final DataSource dataSource;

    // Constructor
    public LibrarianDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Librarian> getLibrarianByEmail(String email) {
        String sql = "SELECT * FROM librarians WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractLibrarianFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving librarian with email: {}", email, e);
        }
        return Optional.empty();
    }

    @Override
    public void addLibrarian(Librarian librarian) {
        if (librarian == null) {
            throw new IllegalArgumentException("Librarian cannot be null");
        }
        String sql = "INSERT INTO librarians (userName, password, email) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setLibrarianParameters(statement, librarian);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    librarian.setLibrarianId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error adding librarian: {}", librarian, e);
            throw new RuntimeException("Failed to add librarian", e);
        }
    }

    @Override
    public void updateLibrarian(Librarian librarian) {
        if (librarian == null || librarian.getLibrarianId() <= 0) {
            throw new IllegalArgumentException("Librarian cannot be null and must have a valid ID");
        }
        String sql = "UPDATE librarians SET userName = ?, password = ?, email = ? WHERE librarianId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setLibrarianParameters(statement, librarian);
            statement.setInt(4, librarian.getLibrarianId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No librarian found with ID: " + librarian.getLibrarianId());
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating librarian with ID: {}", librarian.getLibrarianId(), e);
            throw new RuntimeException("Failed to update librarian", e);
        }
    }

    @Override
    public Optional<Librarian> getLibrarianById(int librarianId) {
        String sql = "SELECT * FROM librarians WHERE librarianId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, librarianId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractLibrarianFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving librarian with ID: {}", librarianId, e);
            throw new RuntimeException("Failed to retrieve librarian", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteLibrarian(int librarianId) {
        if (librarianId <= 0) {
            throw new IllegalArgumentException("Librarian ID must be positive");
        }
        String sql = "DELETE FROM librarians WHERE librarianId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, librarianId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.error("Error deleting librarian with ID: {}", librarianId, e);
            throw new RuntimeException("Failed to delete librarian", e);
        }
    }

    @Override
    public List<Librarian> getAllLibrarians() {
        List<Librarian> librarians = new ArrayList<>();
        String sql = "SELECT * FROM librarians";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                librarians.add(extractLibrarianFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving all librarians", e);
            throw new RuntimeException("Failed to retrieve librarians", e);
        }
        return librarians;
    }

    @Override
    public Optional<Librarian> getLibrarianByUserName(String userName) {
        String sql = "SELECT * FROM librarians WHERE userName = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractLibrarianFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error retrieving librarian with username: {}", userName, e);
        }
        return Optional.empty();
    }

    private void setLibrarianParameters(PreparedStatement statement, Librarian librarian) throws SQLException {
        statement.setString(1, librarian.getUserName());
        statement.setString(2, librarian.getPassword());
        statement.setString(3, librarian.getEmail());
    }

    private Librarian extractLibrarianFromResultSet(ResultSet resultSet) throws SQLException {
        int librarianId = resultSet.getInt("librarianId"); // Make sure this matches your column name
        String userName = resultSet.getString("userName"); // Make sure this matches your column name
        String email = resultSet.getString("email"); // Make sure this matches your column name
        String password = resultSet.getString("password"); // Make sure this matches your column name
        Librarian librarian = new Librarian();
        librarian.setLibrarianId(librarianId);
        librarian.setUserName(userName);
        librarian.setEmail(email);
        librarian.setPassword(password);
        return librarian;
    }
}
