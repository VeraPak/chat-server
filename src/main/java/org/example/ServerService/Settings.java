package org.example.ServerService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {
    private static final String SETTINGS_FILE = System.getProperty("user.dir") + "/src/main/resources/settings.json";
    private static Settings INSTANCE = null;
    private static JSONParser parser = null;
    private static Object obj = null;
    private static JSONObject jsonObject = null;
    private static JSONObject server = null;

    private Settings() {
        parser = new JSONParser();
        try (FileReader reader = new FileReader(SETTINGS_FILE)) {
            obj = parser.parse(reader);
            jsonObject = (JSONObject) obj;
            server = (JSONObject) jsonObject.get("server");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Settings getInstance() {
        if (INSTANCE == null) {
            synchronized (Settings.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Settings();
                }
            }
        }
        return INSTANCE;
    }

    public int getPort() {
        Long port = (Long) server.get("port");
        return port.intValue();
    }

    void setPort(Long port) {
        server.put("port", port);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            writer.write(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
