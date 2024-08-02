package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    public static void createDatabase() throws DataAccessException {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL + "/" + DATABASE_NAME, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void createTablesIfNotExists() throws DataAccessException {
        try (var conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create Users table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL
                );
            """);

            // Create Games table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Games (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    game_name VARCHAR(255) UNIQUE NOT NULL,
                    white_player VARCHAR(255),
                    black_player VARCHAR(255),
                    game_state TEXT,
                    FOREIGN KEY (white_player) REFERENCES Users(username) ON DELETE SET NULL,
                    FOREIGN KEY (black_player) REFERENCES Users(username) ON DELETE SET NULL
                );
            """);

            // Create AuthTokens table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS AuthTokens (
                    token VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    expiration DATETIME,
                    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
                );
            """);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}