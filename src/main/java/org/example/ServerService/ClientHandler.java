package org.example.ServerService;

import org.example.MyLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final MessageCentre messageCentre;
    private final Logger logger;
    private PrintWriter out;
    private BufferedReader in;
    private String username = null;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        logger = MyLogger.getInstance().getLogger();
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            close();
            logger.log(Level.WARNING, "Произошла ошибка при подключении клиента", e);
        }
        messageCentre = new MessageCentre(this);
        logger.log(Level.INFO, "Установлено клиентское соединение");
    }

    public String getUsername() {
        return username;
    }

    public PrintWriter getOut() {
        return out;
    }

    @Override
    public void run() {
        String input = null;
        try {
            out.println("Введите username: ");
            input = in.readLine();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Произошла ошибка при регистрации клиента");
        }
        if (messageCentre.wantToLeftChat(input) || input == null) {
            logger.log(Level.INFO, "Клиент передумал регистрироваться");
            return;
        }
        logger.log(Level.INFO, "Клиент установил Username: " + username);

        registrate(input);
        listenMessage();
    }

    private void registrate(String input) {
        username = input;
        messageCentre.greeting();
        Clients.addClient(this);
        logger.log(Level.INFO, "Клиент успешно зарегестрирован: " + username);
    }

    private void listenMessage() {
        String message;
        while (!clientSocket.isClosed()) {
            try {
                message = in.readLine();
                if (message != null) {
                    messageCentre.sendMessageToChatExceptSender(message);
                }
            } catch (IOException e) {
                close();
                logger.log(Level.WARNING, "Произошла ошибка при получении сообщения от клиента");
            }
        }
    }

    void close() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Не удалось закрыть подключения клиента " + username);
        }
    }
}
