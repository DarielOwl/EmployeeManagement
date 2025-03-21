package org.plusuan;

import org.eclipse.jetty.server.Server;
import org.plusuan.config.ServerConfig;

public class App 
{
    public static void main(String[] args) {
        int port = 8080;
        ServerConfig serverConfig = new ServerConfig(port);
        Server server = serverConfig.createServer();
        try {
            server.start();
            System.out.println("Server started on port: " + port);
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}