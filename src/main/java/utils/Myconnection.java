package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Myconnection {
    private static Myconnection instance;
    private Connection connection;

    private Myconnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionrh", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Myconnection getInstance() {
        if (instance == null) {
            instance = new Myconnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
