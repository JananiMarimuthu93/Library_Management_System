package com.library.management.dao;

import com.library.management.entity.Admin;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDAOImpl implements AdminDAO {
    private static final Logger LOGGER = Logger.getLogger(AdminDAOImpl.class.getName());
    private final DataSource dataSource;

    public AdminDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Admin> getAdminByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractAdminFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving admin with email: " + email, e);
        }
        return Optional.empty();
    }

    private Admin extractAdminFromResultSet(ResultSet resultSet) throws SQLException {
        int adminId = resultSet.getInt("adminId");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        return new Admin(adminId, name, email, password);
    }

    private static final String SELECT_ADMIN_PASSWORD_SQL = "SELECT password FROM admins WHERE adminId = ?";

    @Override
    public Optional<String> getStoredAdminPassword(int adminId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ADMIN_PASSWORD_SQL)) {

            statement.setInt(1, adminId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getString("password"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving stored admin password for ID: " + adminId, e);
        }
        return Optional.empty();
    }
}
