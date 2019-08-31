package util;

import model.Payload;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static util.JSONParser.parseJSONFile;
import static util.MySQLHelper.parameterPlaceholder;
import static util.MySQLHelper.populateDatabase;

public class MySQLTest {

    @Test
    public void connectLocally() {
        File file = new File("src/test/resources/positions-mock.json");
        Payload payload = parseJSONFile(file);
        populateDatabase(payload);
    }

    @Test
    public void paramCount() {
        assertThat(parameterPlaceholder(3))
                .as("Parameter total")
                .isEqualTo("?, ?, ?");
        assertThat(parameterPlaceholder(4))
                .as("Parameter total")
                .isEqualTo("?, ?, ?, ?");
    }
}
