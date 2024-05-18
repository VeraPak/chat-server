package org.example;

import org.example.ServerService.Server;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}