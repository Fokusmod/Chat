package ServerChat.Auch;

import java.sql.*;
import java.util.List;

public class SimpleAuthService implements AuthService {
    private static Connection connection;
    private static Statement statement;


    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:Auth.db");
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        try {
            ResultSet rs = statement.executeQuery("Select * From Account;");
            while (rs.next()) {
                if (login.equals(rs.getString("login")) && password.equals(rs.getString("password")))
                    return rs.getString("nickname");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public void changeUsername(String oldName, String newName) {
        try {
            statement.executeUpdate("update Account set nickname = " + newName + "where nickname = " + oldName + ";");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        try {
            ResultSet rs = statement.executeQuery("Select login, password from Account;");
            while (rs.next()) {
                if (username.equals(rs.getString("login"))&& oldPassword.equals(rs.getString("password"))) {
                    statement.executeUpdate("Update Account set password = " + newPassword + ";");
                    break;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void newUser(String username, String login, String password) {
        try {
            ResultSet rs = statement.executeQuery("Select login from Account;");
            while (rs.next()) {
                if (login.equals(rs.getString(2))) return;
                statement.executeUpdate("insert into students (login, password, nickname) values (" + login + "," + password + ", " + username + ")");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}

