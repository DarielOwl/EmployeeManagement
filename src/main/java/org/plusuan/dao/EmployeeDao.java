package org.plusuan.dao;

import org.plusuan.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class EmployeeDao {

    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/example?useSSL=false";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Passw0rd";

    public int registerEmployee(Employee employee) throws ClassNotFoundException {
        String INSERT_USERS_SQL = "INSERT INTO employees (id, name, position, salary, hire_date, department) VALUES (?, ?, ?, ?, ?, ?);";
        int result = 0;

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/example?useSSL=false", "root", "Passw0rd");
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            preparedStatement.setInt(1, employee.getId());
            preparedStatement.setString(2, employee.getName());
            preparedStatement.setString(3, employee.getPosition());
            preparedStatement.setDouble(4, employee.getSalary());
            preparedStatement.setDate(5, Date.valueOf(employee.getHireDate()));
            preparedStatement.setString(6, employee.getDepartment());

            System.out.println(preparedStatement);
            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
        return result;
    }

    public List<Employee> getAllEmployees() throws ClassNotFoundException { //Method Complete
        List<Employee> employees = new ArrayList<>();
        Class.forName(MYSQL_DRIVER);
        String callProcedure = "{ call get_all_employees() }";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             CallableStatement callableStatement = connection.prepareCall(callProcedure);
             ResultSet rs = callableStatement.executeQuery())
        {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String position = rs.getString("position");
                double salary = rs.getDouble("salary");
                LocalDate hireDate = rs.getDate("hire_date").toLocalDate();
                String department = rs.getString("department");

                Employee employee = new Employee(id, name, position, salary, hireDate, department);
                employees.add(employee);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return employees;
    }

    private void printSQLException(SQLException ex) {
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
