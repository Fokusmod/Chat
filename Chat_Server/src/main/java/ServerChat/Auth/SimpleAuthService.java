package ServerChat.Auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class SimpleAuthService implements AuthService {
    private static final Logger LOGGER = LogManager.getLogger(SimpleAuthService.class);
    private final static String CONNECTION = "jdbc:sqlite:chat_server/Auth.db";
    private final static String GET_USERNAME = "select nickname from Account where login = ? and password = ?;";
    private final static String CHANGE_USERNAME = "update Account set nickname = ? where nickname = ?;";
    private final static String CHANGE_PASSWORD = "update Account set password = ? where login = ? and password = ?;";
    private final static String ADD_CLIENT = "insert into Account  (login, password, nickname) values ('fokus', 'pass', 'user1'), ('mod', 'pass','user2')";

    private static Connection connection;
    private static Statement statement;


    @Override
    public void start() {
        LOGGER.info("Сервис аутентификации запущен");
        try {
            connection = DriverManager.getConnection(CONNECTION);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        LOGGER.info("Сервис аутентификации остановлен");
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
        try (PreparedStatement ps = connection.prepareStatement(GET_USERNAME)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String result = rs.getString("nickname");
                rs.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String changeUsername(String newName,String oldName) {
        try (PreparedStatement ps = connection.prepareStatement(CHANGE_USERNAME)) {
            ps.setString(1, newName);
            ps.setString(2, oldName);
            if (ps.executeUpdate() > 0) {
                ps.close();
                return newName;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oldName;
    }

    @Override
    public String changePassword(String newPassword,String currentLogin, String oldPassword) { //TODO
        try (PreparedStatement ps = connection.prepareStatement(CHANGE_PASSWORD)) {
            ps.setString(1, newPassword);
            ps.setString(2,currentLogin);
            ps.setString(3,oldPassword);
            if (ps.executeUpdate()> 0) {
                ps.close();
                return newPassword;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }return oldPassword;
    }

    @Override
    public boolean newUser(String login, String password, String username) { //TODO
        try {
            if (login.equalsIgnoreCase(username)) return false;
            ResultSet rs = statement.executeQuery("Select login, nickname from Account;");
            while (rs.next()) {
                if (login.equals(rs.getString("login"))) return false;
//                if (username.equalsIgnoreCase(login)) return false;

                statement.executeUpdate("insert into Account (login, password, nickname) values ('" + login + "', '" + password + "', '" + username + "')");
                rs.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public boolean isStart() throws SQLException {
        return !statement.isClosed();
    }

}

