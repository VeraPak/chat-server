package org.example.ClientService;

import org.example.MyLogger;
import org.example.ServerService.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread {
    private static final int PORT = Settings.getInstance().getPort();
    private static final String HOST_NAME = "localhost";
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Logger logger;
    public Client() {
        try {
            clientSocket = new Socket(HOST_NAME, PORT);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger = MyLogger.getInstance().getLogger();
            logger.log(Level.INFO, "Новый клиент создан");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка создания клиента", e);
            close();
        }
    }

    @Override
    public void run() {
        Thread messageReader = new Thread(() -> {
            String input;
            try {
                while ((input = in.readLine()) != null) {
                    System.out.printf("%s [%s]\n", input, LocalDateTime.now());
                }
            } catch (IOException e) {
                close();
                logger.log(Level.INFO, "Прекращена отправка сообщений клиенту");
            }
        });

        Thread messageSender = new Thread(() -> {
            try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                while (!clientSocket.isClosed()) {
                    String message = consoleReader.readLine();
                    System.out.printf("YOU: %s [%s]\n", message, LocalDateTime.now());
                    out.printf("%s \n", message);
                    if ("/exit".equals(message)) {
                        consoleReader.close();
                        close();

                        System.out.println("Вы покинули чат");
                        close();
                        logger.log(Level.INFO, "Клиент покинул чат");
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Ошибка на стороне клиента при отправке сообщения клиента", e);
                close();
            }
        });

        messageReader.start();
        messageSender.start();
    }

    public void close() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Произошла ошибка на стороне клиента. Не удалось закрыть соединение", e);
        }
    }
}

