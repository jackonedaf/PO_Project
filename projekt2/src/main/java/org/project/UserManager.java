package org.project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

public class UserManager {
    private Connection connection;

    public UserManager() {
        try {
            // Recznie zaladuj sterownik JDBC dla MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Adres IP hosta Docker'a i port kontenera MySQL

            // Laczenie z baza danych w kontenerze Docker
            String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/myDB";
            String username = "root";
            String password = "root";

            connection = DriverManager.getConnection(jdbcUrl, username, password);
            createTableIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, salt TEXT, password TEXT)");
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(String username, char[] passwordChars) throws SQLException {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String salt = resultSet.getString("salt");
                String hashedPassword = hashPassword(passwordChars, Base64.getDecoder().decode(salt));

                return hashedPassword.equals(resultSet.getString("password"));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean registerUser(String username, char[] passwordChars) throws SQLException {
        try {
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(passwordChars, salt);

            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, salt, password) VALUES (?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, Base64.getEncoder().encodeToString(salt));
            statement.setString(3, hashedPassword);
            statement.executeUpdate();

            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isUserLoggedIn() throws SQLException {
        try {
            if (connection == null) {
                System.out.println("Connection is null!");
                return false;
            }

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE loggedIn = 1");
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // Zwróć true, jeśli istnieje zalogowany użytkownik
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private String hashPassword(char[] passwordChars, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(salt);
        byte[] hashedPassword = messageDigest.digest(new String(passwordChars).getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
