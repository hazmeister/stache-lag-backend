package util;

import model.Payload;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static util.JSONParser.parseJSONFile;

public class JSONParserTest {

    private static Payload payload;

    @BeforeClass
    public static void setup() {
        File file = new File("src/test/resources/positions-mock.json");
        payload = parseJSONFile(file);
    }

    @Test
    public void assertRaceUrl() {
        assertThat(payload.getRaceUrl())
                .as("Race URL")
                .isEqualTo("test2017");
    }

    @Test
    public void assertTeams() {
        assertThat(payload.getTeams().size())
                .as("Team size")
                .isEqualTo(1);
        assertThat(payload.getTeams().get(0).getName())
                .as("Team name")
                .isEqualTo("Rock 7");
    }

    @Test
    public void assertPositions() {
        assertThat(payload.getTeams().get(0).getPositions().size())
                .as("Positions reported")
                .isEqualTo(4);
    }
}
