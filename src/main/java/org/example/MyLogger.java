package org.example;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/file.log";
    private static MyLogger INSTANCE = null;
    private static Logger logger = null;

    private MyLogger() {
        logger = java.util.logging.Logger.getLogger("Logger");

        FileHandler fh;
        try {
            fh = new FileHandler(FILE_PATH);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка: ", e);
        }
    }

    public static MyLogger getInstance() {
        if (INSTANCE == null) {
            synchronized (MyLogger.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MyLogger();
                    logger.log(Level.INFO, "Создан логгер");
                }
            }
        }
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
    }
}
