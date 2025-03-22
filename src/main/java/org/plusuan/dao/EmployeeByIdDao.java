package org.plusuan.dao;

import lombok.SneakyThrows;
import org.plusuan.config.database.DatabaseConfig;
import org.plusuan.model.Employee;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import static org.plusuan.config.database.DatabaseConfig.*;

public class EmployeeByIdDao {

    @SneakyThrows
    public Optional<Employee> getEmployeeById(int id) throws ClassNotFoundException, Exception {
        Employee employee = null;
        String callProcedure = "{ call get_employee_by_id(?) }";

        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(callProcedure))
        {
            callableStatement.setInt(1, id);
            try (ResultSet rs = callableStatement.executeQuery()) {
                if (rs.next()) {
                    int empId = rs.getInt("id");
                    String name = rs.getString("name");
                    String position = rs.getString("position");
                    double salary = rs.getDouble("salary");
                    LocalDate hireDate = rs.getDate("hire_date").toLocalDate();
                    String department = rs.getString("department");

                    employee = new Employee(empId, name, position, salary, hireDate, department);
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return Optional.ofNullable(employee);
    }

    @SneakyThrows
    public int updateEmployeeById(Employee employee) throws ClassNotFoundException, Exception {
        int result = 0;
        Class.forName("com.mysql.cj.jdbc.Driver");
        String callProcedure = "{ call update_employee_by_id(?, ?, ?, ?, ?, ?) }";
        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(callProcedure)) {

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

    @SneakyThrows
    public int deleteEmployeeById(int id) throws ClassNotFoundException, Exception {
        int result = 0;
        Class.forName("com.mysql.cj.jdbc.Driver");
        String callProcedure = "{ call delete_employee_by_id(?) }";
        try (Connection connection = DatabaseConfig.getConnection();
             CallableStatement callableStatement = connection.prepareCall(callProcedure)) {

            callableStatement.setInt(1, id);
            result = callableStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
        return result;
    }
}
