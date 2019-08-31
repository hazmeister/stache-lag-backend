package util;

import model.Payload;
import org.junit.Test;

import java.io.File;

import static util.JSONParser.parseJSONFile;
import static util.MySQLHelper.populateDatabase;

public class MySQLTest {

    @Test
    public void connectLocally() {
        File file = new File("src/test/resources/positions-mock.json");
        Payload payload = parseJSONFile(file);
        populateDatabase(payload);
    }
}
