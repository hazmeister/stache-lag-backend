import model.Payload;

import java.io.File;

import static util.JSONParser.parseJSONFile;

public class Main {
    public static void main(String[] args) {
        //TODO: Unzip file
        File file = new File("positions.json");
        Payload payload = parseJSONFile(file);
    }
}
