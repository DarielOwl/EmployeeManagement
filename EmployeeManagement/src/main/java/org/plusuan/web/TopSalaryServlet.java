package org.plusuan.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.plusuan.config.AsyncConfig;
import org.plusuan.model.Employee;
import org.plusuan.service.EmployeeAsyncService;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


public class TopSalaryServlet extends HttpServlet {

    private final EmployeeAsyncService employeeAsyncService = new EmployeeAsyncService();

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CompletableFuture<List<Employee>> listEmployeeData1 = employeeAsyncService.getEmployeesData1();
        CompletableFuture<List<Employee>> listEmployeeData2 = employeeAsyncService.getEmployeesData2();
        CompletableFuture<List<Employee>> listEmployeeData3 = employeeAsyncService.getEmployeesData3();

        //Esperar a que los 3 finalicen
        CompletableFuture.allOf(listEmployeeData1, listEmployeeData2, listEmployeeData3).join();

        List<Employee> allEmployees;
        try {
            allEmployees = Stream.of(listEmployeeData1.get(), listEmployeeData2.get(), listEmployeeData3.get())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new ServletException("Error combinando listas de empleados", e);
        }

        //Filtrar los 10 mejores pagados
        List<Employee> bestTopTenSalary = allEmployees.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .limit(10)
                .collect(Collectors.toList());

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), bestTopTenSalary);
    }
}
