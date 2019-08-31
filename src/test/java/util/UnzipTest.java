package util;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static util.ZipHelper.unzipFile;

public class UnzipTest {

    @Test
    public void unzipFileTest() {
        File jsonFile = new File("positions.json");
        if(jsonFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            jsonFile.delete();
        }
        unzipFile(new File("positions.json.zip"));
        assertThat(jsonFile.exists())
                .as("Can unzip file")
                .isTrue();
    }
}
