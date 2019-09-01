package util;

import model.Positions;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.NOVEMBER;
import static java.util.Calendar.getInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static util.DistanceHelper.distance;
import static util.MySQLHelper.connectToDatabase;
import static util.MySQLHelper.getPositionsForMoment;

public class DistanceTest {

    @Test
    public void sanFranciscoToMountainViewStackOverflow() {
        assertThat(distance(37.774929, 37.386052,-122.419416, -122.083851,0.0,0.0))
                .as("Distance between San Fran and Mountain View is in the right ball park")
                .isBetween(52.3, 52.4);
    }

    @Test
    public void sightingsAtTime() {
        connectToDatabase();    //TODO: For a good unit test, this should be a separate database connection
        Calendar calendar = getInstance();
        //calendar.set(2017, NOVEMBER, 19, 20, 0, 0);
        calendar.set(2017, NOVEMBER, 22, 16, 0, 0);
        Date date = new Date(calendar.getTimeInMillis());
        List<Positions> positions = getPositionsForMoment(date);
        //Commented out, as will alter database
        //DistanceHelper.generateSightingData(positions, date);
    }
}
