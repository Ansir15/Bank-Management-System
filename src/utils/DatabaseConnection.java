
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseConnection {
//    private static final String DB_URL="jdbc:mysql://127.0.0.1:3306/banking_management_system";
//    private static final String DB_USER="root";
//    private static final String DB_PASSWORD="";
//    public static Connection openConnection(){
//        Connection connection=null;
//        try {
//            connection= DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
//            System.out.println("Connected successfully");
//            return connection;
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//        return connection;
//    }
//
//    static void main() {
//        DatabaseConnection.openConnection();
//    }
//}



package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/banking_management_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static volatile DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Reconnected successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                instance = null;
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
