package ServerChat.DataBase;

import java.sql.*;

public class AuthDatabase {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {

        try {
            connect ();
//            dropTable();
//            createTable();
            addClient();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:Auth.db");
        statement = connection.createStatement();
    }

    private static void disconnect () {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection!= null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable () throws SQLException {
        statement.execute("create table if not exists Account (id integer primary key autoincrement, " +
                "login text UNIQUE, " + "password text, " + "nickname text);");

    }

    private static void dropTable () throws SQLException {
        statement.execute("drop table if exists Account;");
    }

    private static void addClient () throws SQLException {
        statement.executeUpdate("insert into Account  (login, password, nickname) values ('fokus', 'pass', 'user1'), ('mod', 'pass','user2')");
    }

//    private static void findName () {
//        ResultSet rs = statement.executeQuery("Select * From Account;");
//        while (rs.next()) {
//            System.out.println(rs.getInt(2));
//        }
//    }

}


//

//        statement.execute("create table if not exists Account (id integer primary key autoincrement, " +
//                "login text, password text, nickname text);");

//    public static connect() throws SQLException {
//
//    }