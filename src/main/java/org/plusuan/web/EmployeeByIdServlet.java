package org.plusuan.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.plusuan.dao.EmployeeByIdDao;
import org.plusuan.model.Employee;

import java.io.IOException;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@WebServlet("/employee/*")
public class EmployeeByIdServlet extends HttpServlet {
    public static final String EMPLOYEE_ID_IS_MISSING = "Employee ID is missing";
    public static final String EMPLOYEE_NOT_FOUND = "Employee not found";
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private final EmployeeByIdDao employeeDao = new EmployeeByIdDao();

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (isNull(pathInfo) || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, EMPLOYEE_ID_IS_MISSING);
            return;
        }

        Integer id = verifyEmployeeId(resp, pathInfo);
        if (id == null) return;

        Employee employee = employeeDao.getEmployeeById(id).orElse(null); //Obtener empleado
        if (nonNull(employee)) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), employee);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, EMPLOYEE_NOT_FOUND);
        }
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, EMPLOYEE_ID_IS_MISSING);
            return;
        }

        Integer id = verifyEmployeeId(resp, pathInfo);
        if (id == null) return;

        Employee updatedEmployee = objectMapper.readValue(req.getInputStream(), Employee.class);
        updatedEmployee.setId(id);
        int result = employeeDao.updateEmployeeById(updatedEmployee);

        if (result > 0) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Employee updated successfully!\"}");
        }
        else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update employee!");
        }
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, EMPLOYEE_ID_IS_MISSING);
            return;
        }

        Integer id = verifyEmployeeId(resp, pathInfo);
        if (id == null) return;

        int result = employeeDao.deleteEmployeeById(id);
        if (result > 0) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\": \"Employee deleted successfully\"}");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete employee");
        }
    }

    private static Integer verifyEmployeeId(HttpServletResponse resp, String pathInfo) throws IOException {
        String idString = pathInfo.substring(1); // Sacar la barra inicial
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID");
            return null;
        }
        return id;
    }

}
