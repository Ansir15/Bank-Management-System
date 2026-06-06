package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL="jdbc:mysql://127.0.0.1:3306/banking_management_system";
    private static final String DB_USER="root";
    private static final String DB_PASSWORD="";
    public static Connection openConnection(){
        Connection connection=null;
        try {
            connection= DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
            System.out.println("Connected successfully");
            return connection;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    static void main() {
        DatabaseConnection.openConnection();
    }
}
