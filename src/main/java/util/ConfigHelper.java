package util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

class ConfigHelper {

    static String getConfig(String name) {
        FileReader fileReader = null;
        Properties properties = new Properties();
        try {
            fileReader = new FileReader(new File("config.properties"));
            properties.load(fileReader);
        } catch (IOException e) {
            System.err.println("Could not read config file");
            e.printStackTrace();
            return null;
        } finally {
            if(fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    System.err.println("Could not close config file");
                    e.printStackTrace();
                }
            }
        }
        return properties.getProperty(name);
    }
}
