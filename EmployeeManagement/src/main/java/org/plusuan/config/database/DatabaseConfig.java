package org.plusuan.config.database;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfig {

    public DatabaseConfig() {}

    public static Connection getConnection() throws Exception {

        DBSecret secret = SecretsManagerUtil.getDBSecret();

        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false",
                secret.getHost(), secret.getPort(), secret.getDatabase());

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(url, secret.getUsername(), secret.getPassword());
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