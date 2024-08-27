package com.library.management.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
public class DatabaseConnection
{
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/librarydb");
        config.setUsername(System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root");
        config.setPassword(System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "root");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Optional configuration settings
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000); // 30 seconds
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
