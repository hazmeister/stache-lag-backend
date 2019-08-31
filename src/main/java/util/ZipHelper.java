package util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * https://www.baeldung.com/java-compress-and-uncompress
 */
public class ZipHelper {
    public static File unzipFile(File zipFile) {
        System.out.println("Unzipping " + zipFile.getName());
        File jsonFile = new File("positions.json");
        if (jsonFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            jsonFile.delete();
        }
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            byte[] buffer = new byte[1024];
            boolean found = false;
            while (zipEntry != null) {
                if (zipEntry.getName().contentEquals("positions.json")) {
                    found = true;
                    FileOutputStream fos = new FileOutputStream(jsonFile);
                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                    break;
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
            if(!found) {
                System.err.println("Did not find positions.json");
                System.exit(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(100);
        }
        return jsonFile;
    }
}