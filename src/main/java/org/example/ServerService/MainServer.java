package org.example.ServerService;

public class MainServer {
    public static void main(String[] args) {
        Settings.getInstance().setPort(9090L);

        int port = Settings.getInstance().getPort();

        Server server = new Server(port);
        new Thread(server).start();

    }
}