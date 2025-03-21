package org.plusuan;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.plusuan.web.EmployeeServlet;

public class App 
{
    public static void main(String[] args) throws Exception {
        // Crear el servidor Jetty en el puerto 8080
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Crear el contexto de servlets
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Agregar el servlet en la ruta "/employees"
        context.addServlet(new ServletHolder(new EmployeeServlet()), "/employees");

        // Iniciar el servidor
        server.start();
        server.join();
    }
}