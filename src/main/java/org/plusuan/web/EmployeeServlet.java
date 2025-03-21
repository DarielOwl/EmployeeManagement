package org.plusuan.web;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.plusuan.dao.EmployeeDao;
import org.plusuan.model.Employee;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class EmployeeServlet extends HttpServlet {
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());// Esto es para las fechas

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
}