package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import model.Payload;

import java.io.*;

import static java.nio.charset.StandardCharsets.*;

public class JSONParser {

    public static Payload parseJSONFile(String filename) {
        Payload payload = null;
        JsonReader reader = null;
        try {
            File positionJsonFile = new File(filename);
            InputStream stream = new FileInputStream(positionJsonFile);
            reader = new JsonReader(new BufferedReader(new InputStreamReader(stream, UTF_8)));
            Gson gson = new GsonBuilder().create();
            payload = gson.fromJson(reader, Payload.class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Failed to close reader");
                    e.printStackTrace();
                }
            }
        }
        return payload;
    }
}
