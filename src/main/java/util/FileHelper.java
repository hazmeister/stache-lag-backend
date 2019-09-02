package util;

import java.io.*;

public class FileHelper {

    public static void stringToFile(String filename, String contents) {
        try {
            File file = new File(filename);
            if(file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            FileWriter writer;
            writer = new FileWriter(file);
            BufferedWriter buffer = new BufferedWriter(writer);
            PrintWriter print = new PrintWriter(buffer);
            print.println(contents);
            print.close();
            System.out.println("Average sightings data written to " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
