package org.example.ServerService;

import org.example.MyLogger;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageCentre {

    private final ClientHandler sender;
    private final Logger logger;

    public MessageCentre(ClientHandler sender) {
        this.sender = sender;
        logger = MyLogger.getInstance().getLogger();
    }

    public void sendMessageToChatExceptSender(String message) {
        if (message == null) {
            return;
        }
        if (wantToLeftChat(message)) {
            return;
        }
        for (ClientHandler client : Clients.getClientsList()) {
            if (client.getUsername() != null & client != sender) {
                sendMessageToConcreteClient(client, message);
            }
        }
        logger.info("[" + sender.getUsername() + "] отправлено сообщение в чат: \"" + message + "\"");
    }

    public void greeting() {
        for (ClientHandler client : Clients.getClientsList()) {
            if (client.getUsername() != null & client != sender) {
                sendMessageToConcreteClient(client, "Пользователь присоединился к чату");
            }
        }
    }

    private void sendMessageToConcreteClient(ClientHandler client, String message) {
        client.getOut().println(sender.getUsername() + ": " + message);
    }

    boolean wantToLeftChat(String message) {
        if (!("/exit".equals(message))) {
            return false;
        }

        for (ClientHandler client : Clients.getClientsList()) {
            if (sender.getUsername() != null & client.getUsername() != null & client != sender) {
                sendMessageToConcreteClient(client, "Пользователь покинул чат");
            }
        }
        Clients.removeClient(sender);
        logger.log(Level.INFO, sender.getUsername() + " покинул чат");
        return true;
    }
}
