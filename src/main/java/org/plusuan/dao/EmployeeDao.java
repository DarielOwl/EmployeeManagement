package org.plusuan.dao;

import lombok.SneakyThrows;
import org.plusuan.config.database.DatabaseConfig;
import org.plusuan.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import static org.plusuan.config.database.DatabaseConfig.*;


public class EmployeeDao {

    @SneakyThrows
    public List<Employee> getAllEmployees() throws ClassNotFoundException, Exception {
        List<Employee> employees = new ArrayList<>();
        String callProcedure = "{ call get_all_employees() }";

        try (Connection connection = DatabaseConfig.getConnection();
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

    @SneakyThrows
    public int createEmployee(Employee employee) throws ClassNotFoundException, Exception {
        int result = 0;

        String callProcedure = "{ call insert_employee(?, ?, ?, ?, ?, ?) }";
        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(callProcedure))
        {
            callableStatement.setInt(1, employee.getId());
            callableStatement.setString(2, employee.getName());
            callableStatement.setString(3, employee.getPosition());
            callableStatement.setDouble(4, employee.getSalary());
            callableStatement.setDate(5, java.sql.Date.valueOf(employee.getHireDate()));
            callableStatement.setString(6, employee.getDepartment());

            result = callableStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return result;
    }

}
