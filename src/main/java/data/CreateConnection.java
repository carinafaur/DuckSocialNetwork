package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateConnection {
    private static Connection conn;
    private static final String url = "jdbc:postgresql://localhost:5432/DuckSocialNetwork";
    private static final String user = "postgres";
    private static final String password = "";

    public CreateConnection() {
    }

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("New connection established with database.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println(e.getMessage());
        }
        return conn;
    }
}
