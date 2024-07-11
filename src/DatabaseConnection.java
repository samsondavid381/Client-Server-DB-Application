/*
Name: David Samson
Course: CNT 4714 Summer 2024
Assignment title: Project 2 – A Two-tier Client-Server Application
Date: July 7, 2024
Class: DatabaseConnection
*/
import java.sql.*;

public class DatabaseConnection {
    private Connection connection;


    public void connect(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        if (sql.toLowerCase().startsWith("select")) {
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } else {
            stmt.executeUpdate();

            return null;
        }
    }

}
