package org.plusuan.config;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfig {

    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://employee-db.clwwkuq6wy3u.sa-east-1.rds.amazonaws.com:3306/management?useSSL=false";
    public static final String DB_USERNAME = "admin";
    public static final String DB_PASSWORD = "Pa$$w0rd2025Employ&&";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        // Registrar el driver de MySQL (necesario en algunas versiones de Java)
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Retornar la conexión
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

}
