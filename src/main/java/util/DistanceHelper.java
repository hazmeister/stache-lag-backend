package util;

import model.Positions;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static util.MySQLHelper.*;

class DistanceHelper {

    /**
     * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude#16794680
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @return Distance in Kilometers
     */
    static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance) / 1000.0; // Back to kilometers
    }

    /**
     * Analyses the list of positions for vessels that can see each other and writes to MySQL
     * Assumes a clear day with 55KM visibility (30nKm)
     * https://rpg.stackexchange.com/a/6655
     * https://duckduckgo.com/?q=nautical+miles+to+kilometers&t=osx&ia=answer
     * @param positions A list of vessels that can see each other
     * @param sampleDate Start time of the sample
     */
    static void generateSightingData(List<Positions> positions, Date sampleDate) {
        double visibilityKm = 55.0;
        /*
        No guarantee that positions will be free of duplicate positions from the same team.
        Data is ordered by distance to finish, so we can discard any serial we see a second time.
         */
        List<Integer> seenSerials = new LinkedList<>();

        for (Positions firstYacht : positions) {
            if(!seenSerials.contains(firstYacht.getTeamSerial())) {
                List<Positions> firstYachtSighting = new LinkedList<>();
                for (Positions secondYacht : positions) {
                    if(firstYacht.getTeamSerial() != secondYacht.getTeamSerial()) {
                        double distanceM = distance(firstYacht.getLatitude(), secondYacht.getLatitude(), firstYacht.getLongitude(), secondYacht.getLongitude(), firstYacht.getAltitude(), secondYacht.getAltitude());
                        if(distanceM < visibilityKm) {
                            firstYachtSighting.add(secondYacht);
                        }
                    }
                }
                writeSightingsToDB(sampleDate, firstYacht.getTeamSerial(), firstYachtSighting);
                //System.out.println( "   * " + firstYacht.getTeamName() + " could see " +  firstYachtSighting.size() + " other participants");
                seenSerials.add(firstYacht.getTeamSerial());
            }
        }
    }
}
