package org.example.ServerService;

import org.example.MyLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
    private static final int PORT = Settings.getInstance().getPort();
    private static ServerSocket serverSocket = null;
    private static ExecutorService pool = null;
    private static Logger logger;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            pool = Executors.newFixedThreadPool(64);
            logger = MyLogger.getInstance().getLogger();
            logger.log(Level.INFO, "Сервер создан");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка при создании сервера", e);
        }
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Сервер запущен");
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                pool.execute(client);
            }
            pool.shutdown();
            try {
                if (!pool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
            }
            logger.log(Level.INFO, "Работа сервера прекращена");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Произошла ошибка во время работы сервера", e);
            closeClients();
        }
    }

    private void closeClients() {
        for (ClientHandler client : Clients.getClientsList()) {
            client.close();
        }
    }
}