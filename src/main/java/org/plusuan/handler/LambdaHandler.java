package org.plusuan.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.*;
import org.plusuan.web.EmployeeByIdServlet;
import org.plusuan.web.EmployeeServlet;
import org.plusuan.web.TopSalaryServlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Map<String, Servlet> servletMapping;

    public LambdaHandler() {
        servletMapping = new HashMap<>();
        servletMapping.put("/employees", new EmployeeServlet());
        servletMapping.put("/employees/*", new EmployeeByIdServlet());
        servletMapping.put("/employees/salary/top", new TopSalaryServlet());
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // 1) Convertir el evento Lambda a un HttpServletRequest personalizado
            HttpServletRequest request = new LambdaServletRequest(event);

            // 2) Crear un HttpServletResponse personalizado
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HttpServletResponse servletResponse = new LambdaServletResponse(outputStream);

            // 3) Enrutar al Servlet correspondiente
            Servlet servlet = findServlet(event.getPath());
            if (servlet != null) {
                servlet.service(request, servletResponse);
            } else {
                servletResponse.setStatus(404);
                servletResponse.getWriter().write("Endpoint not found!");
            }

            // 4) Convertir la respuesta del Servlet al formato de Lambda
            response.setStatusCode(servletResponse.getStatus());
            response.setBody(outputStream.toString());
            response.setHeaders(Map.of("Content-Type", servletResponse.getContentType()));

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("Server Internal Error: " + e.getMessage());
        }

        return response;
    }

    private Servlet findServlet(String path) {
        if (path.startsWith("/employees/")) {
            return servletMapping.get("/employees/*");
        }
        else if (path.equals("/employees")) {
            return servletMapping.get("/employees");
        }
        else if (path.equals("/employees/salary/top")) {
            return servletMapping.get("/employees/salary/top");
        }
        return null;
    }
    static class LambdaServletRequest extends HttpServletRequestWrapper {
        private final APIGatewayProxyRequestEvent event;

        public LambdaServletRequest(APIGatewayProxyRequestEvent event) {
            super(null);
            this.event = event;
        }

        @Override
        public String getMethod() {
            return event.getHttpMethod();
        }

        @Override
        public String getPathInfo() {
            return event.getPath();
        }
    }

    static class LambdaServletResponse extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream outputStream;
        private PrintWriter writer;
        private int status = 200;
        private String contentType = "application/json";

        public LambdaServletResponse(ByteArrayOutputStream outputStream) {
            super(null);
            this.outputStream = outputStream;
            this.writer = new PrintWriter(outputStream);
        }

        @Override
        public PrintWriter getWriter() {
            return writer;
        }

        @Override
        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setContentType(String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }
    }
}