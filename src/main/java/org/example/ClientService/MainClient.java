package org.example.ClientService;

import org.example.ServerService.Settings;

public class MainClient {
    public static void main(String[] args) {
        int port = Settings.getInstance().getPort();

        Client client = new Client("localhost", port);
        new Thread(client).start();
    }
}
