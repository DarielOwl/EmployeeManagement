package org.plusuan.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.plusuan.web.EmployeeServlet;
import org.plusuan.web.EmployeeByIdServlet;
import org.plusuan.web.TopSalaryServlet;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final EmployeeServlet employeeServlet = new EmployeeServlet();
    private final EmployeeByIdServlet employeeByIdServlet = new EmployeeByIdServlet();
    private final TopSalaryServlet topSalaryServlet = new TopSalaryServlet();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent lambdaResponse = new APIGatewayProxyResponseEvent();
        try {
            HttpServletRequest request = new LambdaServletRequest(event);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HttpServletResponse response = new LambdaServletResponse(outputStream);

            Servlet servlet = getServletForPath(event.getPath());
            if (servlet != null) {
                servlet.service(request, response);
            } else {
                response.setStatus(404);
                response.getWriter().write("Endpoint not found!");
            }

            lambdaResponse.setStatusCode(response.getStatus());
            lambdaResponse.setBody(outputStream.toString());
            lambdaResponse.setHeaders(Map.of("Content-Type", response.getContentType()));
        } catch (Exception e) {
            lambdaResponse.setStatusCode(500);
            lambdaResponse.setBody("Server Internal Error: " + e.getMessage());
        }
        return lambdaResponse;
    }

    private Servlet getServletForPath(String path) {
        if ("/employees/salary/top".equals(path)) {
            return topSalaryServlet;
        }
        else if ("/employees".equals(path)) {
            return employeeServlet;
        }
        else if (path.startsWith("/employees/")) {
            return employeeByIdServlet;
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
        private final PrintWriter writer;
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