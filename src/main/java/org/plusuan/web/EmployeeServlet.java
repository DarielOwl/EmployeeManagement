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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Registrar el módulo para fechas de Java 8 (LocalDate, LocalDateTime, etc.)
            objectMapper.registerModule(new JavaTimeModule());

            // Llamar al método del DAO que obtiene todos los empleados
            List<Employee> employees = employeeDao.getAllEmployees();

            // Convertir la lista de empleados a una cadena JSON
            String json = objectMapper.writeValueAsString(employees);

            // Configurar la respuesta HTTP
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
        } catch (Exception e) {
            // En caso de error, enviar una respuesta de error
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }
}