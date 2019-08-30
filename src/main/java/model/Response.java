package model;

import java.util.List;

public class Response {
    private List<Teams> teams;
    private String raceUrl;

    public List<Teams> getTeams() {
        return teams;
    }

    public void setTeams(List<Teams> teams) {
        this.teams = teams;
    }

    public String getRaceUrl() {
        return raceUrl;
    }

    public void setRaceUrl(String raceUrl) {
        this.raceUrl = raceUrl;
    }
}
