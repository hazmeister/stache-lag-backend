import model.Payload;

import static util.JSONParser.parseJSONFile;

public class Main {
    public static void main(String[] args) {
        Payload payload = parseJSONFile("positions.json");
        System.out.println(payload.getRaceUrl());

    }
}
