package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import model.Payload;

import java.io.*;

import static java.nio.charset.StandardCharsets.*;

public class JSONParser {

    /**
     * With thanks to http://www.acuriousanimal.com/2015/10/23/reading-json-file-in-stream-mode-with-gson.html
     * and https://stackoverflow.com/a/19177892/5603509
     * @param jsonFile race data to parse
     * @return POJO of race data
     */
    public static Payload parseJSONFile(File jsonFile) {
        System.out.println("Parsing JSON");
        Payload payload = null;
        JsonReader reader = null;
        try {
            InputStream stream = new FileInputStream(jsonFile);
            reader = new JsonReader(new BufferedReader(new InputStreamReader(stream, UTF_8)));
            Gson gson = new GsonBuilder().create();
            payload = gson.fromJson(reader, Payload.class);
        } catch (IOException e) {
            System.err.println("Failed to read JSON file");
            e.printStackTrace();
            System.exit(100);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Failed to close reader");
                    e.printStackTrace();
                    System.exit(100);
                }
            }
        }
        System.out.println("Parsed race data for " + payload.getRaceUrl());
        return payload;
    }
}
