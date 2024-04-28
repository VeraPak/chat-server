package org.example.ServerService;

import org.example.MyLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static ServerSocket serverSocket = null;
    private static ExecutorService pool = null;
    private static Logger logger;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            pool = Executors.newCachedThreadPool();
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