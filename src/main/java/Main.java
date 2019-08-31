import model.Payload;

import java.io.File;

import static util.JSONParser.parseJSONFile;
import static util.MySQLHelper.populateDatabase;
import static util.ZipHelper.unzipFile;

public class Main {
    public static void main(String[] args) {
        File jsonFile = unzipFile(new File("positions.json.zip"));
        Payload payload = parseJSONFile(jsonFile);
        populateDatabase(payload);
    }
}
