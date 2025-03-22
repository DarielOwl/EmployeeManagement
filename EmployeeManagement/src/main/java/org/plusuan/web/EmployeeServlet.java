package org.plusuan.web;

import java.io.IOException;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.plusuan.dao.EmployeeDao;
import org.plusuan.model.Employee;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

public class EmployeeServlet extends HttpServlet {
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Employee> employees = employeeDao.getAllEmployees();

            String json = objectMapper.writeValueAsString(employees);

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        try {
            // Convertir json --> employee.java
            Employee employee = objectMapper.readValue(req.getInputStream(), Employee.class);

            EmployeeDao employeeDao = new EmployeeDao();
            int result = employeeDao.createEmployee(employee);

            resp.setContentType("application/json");
            if (result > 0) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("{\"message\": \"Employee created successfully!\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Failed to create employee!\"}");
            }
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

}