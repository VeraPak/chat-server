package org.example.ServerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Clients {
    private static final List<ClientHandler> clientsList = new ArrayList<>();

    public static synchronized List<ClientHandler> getClientsList() {
        return Collections.unmodifiableList(clientsList);
    }

    public static synchronized void addClient(ClientHandler client) {
        clientsList.add(client);
    }

    public static synchronized void removeClient(ClientHandler client) {
        clientsList.remove(client);
    }
}
