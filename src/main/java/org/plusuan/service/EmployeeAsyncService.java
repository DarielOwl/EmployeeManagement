package org.plusuan.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import org.plusuan.model.Employee;

import static java.util.Objects.isNull;

public class EmployeeAsyncService {

    private final ExecutorService executorService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public EmployeeAsyncService() {
        this.executorService = new ThreadPoolExecutor(
                3,
                3,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100)
        );
    }

    public CompletableFuture<List<Employee>> getEmployeesData1() {
        return CompletableFuture.supplyAsync(loadEmployees("employees_data1.json"), executorService);
    }

    public CompletableFuture<List<Employee>> getEmployeesData2() {
        return CompletableFuture.supplyAsync(loadEmployees("employees_data2.json"), executorService);
    }

    public CompletableFuture<List<Employee>> getEmployeesData3() {
        return CompletableFuture.supplyAsync(loadEmployees("employees_data3.json"), executorService);
    }

    private Supplier<List<Employee>> loadEmployees(String fileName) {
        return () -> {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (isNull(input)) {
                    throw new IOException("No se encontró el archivo: " + fileName);
                }
                return objectMapper.readValue(input, new TypeReference<List<Employee>>() {});
            } catch (IOException e) {
                throw new RuntimeException("Error leyendo el archivo " + fileName, e);
            }
        };
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
