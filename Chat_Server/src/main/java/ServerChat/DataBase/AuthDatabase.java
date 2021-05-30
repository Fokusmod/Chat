package ServerChat.DataBase;

import java.sql.*;

public class AuthDatabase {
    private final static String DRIVER = "org.sqlite.JDBC";
    private final static String CONNECTION = "jdbc:sqlite:chat_server/Auth.db";
    private final static String GET_USERNAME = "select nickname from Account where login = ? and password = ?;";
    private final static String CHANGE_USERNAME = "update Account set nickname = ? where nickname = ?;";
    private final static String CREATE_TABLE = "create table if not exists Account (id integer primary key autoincrement,login text UNIQUE not null, password text not null, nickname text unique not null);";
    private final static String DROP_TABLE = "drop table if exists Account;";
    private final static String ADD_CLIENT = "insert into Account  (login, password, nickname) values ('fokus', 'pass', 'user1'), ('mod', 'pass','user2')";

    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {

        try {
            connect();
//            dropTable();
            createTable();
            addClient();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

    }

    private static void connect() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        connection = DriverManager.getConnection(CONNECTION);
        statement = connection.createStatement();
    }

    private static void disconnect() {
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

    private static void createTable() throws SQLException {
        statement.execute(CREATE_TABLE);

    }


    private static void dropTable() throws SQLException {
        statement.execute(DROP_TABLE);
    }

    private static void addClient() throws SQLException {
        statement.executeUpdate(ADD_CLIENT);
    }
}