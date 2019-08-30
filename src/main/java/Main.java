import model.Response;

import static util.JSONParser.parseJSONFile;

public class Main {
    public static void main(String[] args) {
        Response response = parseJSONFile("positions.json");
        System.out.println(response.getRaceUrl());
    }
}
