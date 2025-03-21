package org.plusuan.config;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.plusuan.web.EmployeeServlet;
import org.plusuan.web.EmployeeByIdServlet;
import org.plusuan.web.TopSalaryServlet;

public class ServerConfig {

    private final int port;

    public ServerConfig(int port) {
        this.port = port;
    }

    public Server createServer() {
        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        //Cada servlet se mapea a un path distinto para tener servicios independientes.
        context.addServlet(new ServletHolder(new EmployeeServlet()), "/employees");
        context.addServlet(new ServletHolder(new EmployeeByIdServlet()), "/employees/*");
        context.addServlet(new ServletHolder(new TopSalaryServlet()), "/employees/salary/top");

        return server;
    }
}
