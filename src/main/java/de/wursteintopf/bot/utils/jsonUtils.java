package de.wursteintopf.bot.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class jsonUtils {

    private static Logger log = LoggerFactory.getLogger(jsonUtils.class);
    public static final String path = System.getProperty("configDir", "src/main/resources/");

    public static JSONArray loadJson(String filename) {
        log.info("Loading " + filename + " from disk");
        try {
            JSONParser parser = new JSONParser();
            JSONArray whitelist = (JSONArray) parser.parse(new FileReader(path + filename));
            return whitelist;
        } catch (Exception e) {
            e.printStackTrace();
            JSONArray whitelist = new JSONArray();
            return whitelist;
        }
    }

    public static void saveJson(JSONArray json, String filename) {
        log.info("Saving " + filename + " to disk");
        try (FileWriter file = new FileWriter(path + filename)) {
            file.write(json.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
