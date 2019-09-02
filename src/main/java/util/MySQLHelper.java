package util;

import model.Payload;
import model.Positions;
import model.Teams;

import java.sql.*;
import java.util.Date;
import java.util.*;

import static java.sql.DriverManager.getConnection;
import static util.ConfigHelper.getConfig;
import static util.DateHelper.addFifteenMinutes;
import static util.DistanceHelper.generateSightingData;

/**
 * With thanks to https://crunchify.com/java-mysql-jdbc-hello-world-tutorial-create-connection-insert-data-and-retrieve-data-from-mysql/
 */
public class MySQLHelper {

    private static Connection connection = null;
    private static String tablePrefix = "arc2017_"; //TODO: read in from file when analysing data separately from import

    public static void populateDatabase(Payload payload) {
        tablePrefix = payload.getRaceUrl() + "_";
        String positionsTable = tablePrefix + "positions";
        String teamsTable = tablePrefix + "teams";
        String sightingsTable = tablePrefix + "sightings";

        connectToDatabase();
        if (tableExists(sightingsTable)) {
            dropTable(sightingsTable);
        }
        if (tableExists(positionsTable)) {
            dropTable(positionsTable);
        }
        if (tableExists(teamsTable)) {
            dropTable(teamsTable);
        }

        createTeamsTable();
        createPositionsTable();
        createSightingsTable();

        populateTeams(payload.getTeams());
        populatePositions(payload.getTeams());
        populateSightings(raceStart(), raceFinish());
    }

    private static Date raceStart() {
        return raceBoundary("MIN");
    }

    private static Date raceFinish() {
        return raceBoundary("MAX");
    }

    private static Date raceBoundary(String stat) {
        String query = "SELECT " + stat.toUpperCase() + "(gpsAt) AS boundary FROM " + tablePrefix + "positions";
        Date date = new Date();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                date = resultSet.getDate("boundary");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return date;
    }

    static void connectToDatabase() {
        String username = getConfig("mysql.username");
        String password = getConfig("mysql.password");
        String connectionURL = "jdbc:mysql://" + getConfig("mysql.hostname") + ":"
                + Integer.valueOf(Objects.requireNonNull(getConfig("mysql.port"))) + "/" + getConfig("mysql.database");
        try {
            connection = getConnection(connectionURL, username, password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Could not connect to MySQL");
            e.printStackTrace();
        }
        if (connection != null) {
            System.out.println("Connection succeeded");
        }
    }

    /**
     * https://stackoverflow.com/a/8829109/5603509
     */
    private static boolean tableExists(String table) {
        String query = "SELECT * FROM information_schema.tables WHERE table_schema = ? AND table_name = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, getConfig("mysql.database"));
            preparedStatement.setString(2, table);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String match = resultSet.getString("TABLE_NAME");
                if (match.contentEquals(table)) {
                    System.out.println(table + " table already exists");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void createTeamsTable() {
        System.out.println("Create teams table");
        String query = "CREATE TABLE " + tablePrefix + "teams (" +
                "name VARCHAR(64)," +
                "marker INT," +
                "serial INT," +
                "PRIMARY KEY (serial))";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    private static void createPositionsTable() {
        System.out.println("Create positions table");
        String query = "CREATE TABLE " + tablePrefix + "positions (" +
                "id INT," +
                "serial INT," +
                "gpsAt TIMESTAMP," +
                "txAt TIMESTAMP," +
                "latitude DOUBLE," +
                "longitude DOUBLE," +
                "altitude INT," +
                "alert BOOLEAN," +
                "type VARCHAR(9)," +
                "dtfKm DOUBLE," +
                "dtfNm DOUBLE," +
                "sogKmph DOUBLE," +
                "sogKnots DOUBLE," +
                "battery INT," +
                "cog INT," +
                "PRIMARY KEY (id), FOREIGN KEY (serial) REFERENCES " + tablePrefix + "teams(serial))";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    private static void createSightingsTable() {
        System.out.println("Create sightings table");
        String query = "CREATE TABLE " + tablePrefix + "sightings (" +
                "serial INT," +
                "serial_seen INT," +
                "sightedAt TIMESTAMP," +
                "FOREIGN KEY (serial) REFERENCES " + tablePrefix + "teams(serial)," +
                "FOREIGN KEY (serial_seen) REFERENCES " + tablePrefix + "teams(serial))";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    /**
     * With thanks to https://stackoverflow.com/a/4355097/5603509
     */
    private static void populateTeams(List<Teams> teams) {
        System.out.println("Populating teams table");
        String query = "INSERT INTO " + tablePrefix + "teams VALUES (" + parameterPlaceholder(3) + ")";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int i = 0;
            for (Teams team : teams) {
                preparedStatement.setString(1, team.getName());
                preparedStatement.setInt(2, team.getMarker());
                preparedStatement.setInt(3, team.getSerial());
                preparedStatement.addBatch();
                i++;
                if (i % 1000 == 0 || i == teams.size()) {
                    preparedStatement.executeBatch();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            System.err.println("Failed to write teams to database");
            e.printStackTrace();
            System.exit(100);
        }
    }

    private static void populatePositions(List<Teams> teams) {
        System.out.println("Populating positions table");
        String query = "INSERT INTO " + tablePrefix + "positions VALUES (" + parameterPlaceholder(15) + ")";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (Teams team : teams) {
                System.out.println(" * " + team.getName());
                int i = 0;
                int serial = team.getSerial();
                List<Positions> positions = team.getPositions();
                for (Positions position : positions) {
                    preparedStatement.setInt(1, position.getId());
                    preparedStatement.setInt(2, serial);
                    preparedStatement.setTimestamp(3, position.getGpsAt());
                    preparedStatement.setTimestamp(4, position.getTxAt());
                    preparedStatement.setDouble(5, position.getLatitude());
                    preparedStatement.setDouble(6, position.getLongitude());
                    preparedStatement.setInt(7, position.getAltitude());
                    preparedStatement.setBoolean(8, position.isAlert());
                    preparedStatement.setString(9, position.getType());
                    preparedStatement.setDouble(10, position.getDtfKm());
                    preparedStatement.setDouble(11, position.getDtfNm());
                    preparedStatement.setDouble(12, position.getSogKmph());
                    preparedStatement.setDouble(13, position.getSogKnots());
                    preparedStatement.setInt(14, position.getBattery());
                    preparedStatement.setInt(15, position.getCog());
                    preparedStatement.addBatch();
                    i++;
                    if (i % 1000 == 0 || i == positions.size()) {
                        preparedStatement.executeBatch();
                    }
                }
            }
            connection.commit();
            System.out.println("Positions data fully populated");
        } catch (SQLException e) {
            System.err.println("Failed to write positions to database");
            e.printStackTrace();
            System.exit(100);
        }
    }

    private static void populateSightings(Date raceStart, Date raceFinish) {
        System.out.println("Populating sighting data:");
        Date date = raceStart;
        while (date.before(raceFinish)) {
            System.out.println(" * Populating sighting at " + date);
            List<Positions> moment = getPositionsForMoment(date);
            generateSightingData(moment, date);
            date = DateHelper.addFourHours(date);
        }
    }

    static String parameterPlaceholder(int parameters) {
        return String.join(" ", Collections.nCopies(parameters - 1, "?,")) + " ?";
    }

    private static void dropTable(String table) {
        System.out.println("Dropping " + table + " table");
        String query = "DROP TABLE " + table;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(100);
        }
    }

    /**
     * @param date start of 15 minute range
     * @return positions of all vessels that have reported within 15 minutes
     */
    static List<Positions> getPositionsForMoment(Date date) {
        List<Positions> positions = new LinkedList<>();
        Date dateInFifteenMinutes = addFifteenMinutes(date);
        String query = "SELECT id, " + tablePrefix + "teams.serial, name, latitude, longitude, altitude\n" +
                "FROM " + tablePrefix + "positions, " + tablePrefix + "teams\n" +
                "WHERE (\n" +
                "  gpsAt >= ?\n" +
                "  AND gpsAt < ?\n" +
                ") and type = 'automatic'\n" +
                "AND " + tablePrefix + "teams.serial = " + tablePrefix + "positions.serial\n" +
                "ORDER BY dtfKm";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
            preparedStatement.setTimestamp(2, new Timestamp(dateInFifteenMinutes.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Positions position = new Positions();
                position.setLatitude(resultSet.getDouble("latitude"));
                position.setLongitude(resultSet.getDouble("longitude"));
                position.setAltitude(resultSet.getInt("altitude"));
                position.setTeamSerial(resultSet.getInt("serial"));
                position.setTeamName(resultSet.getString("name"));
                positions.add(position);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

    static void writeSightingsToDB(Date sampleDate, int teamSerial, List<Positions> seen) {
        String query = "INSERT INTO " + tablePrefix + "sightings VALUES(" + parameterPlaceholder(3) + ")";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            int i = 0;
            for (Positions each : seen) {
                preparedStatement.setInt(1, teamSerial);
                preparedStatement.setInt(2, each.getTeamSerial());
                preparedStatement.setTimestamp(3, new Timestamp(sampleDate.getTime()));
                preparedStatement.addBatch();
                i++;
                if (i % 1000 == 0 || i == seen.size()) {
                    preparedStatement.executeBatch();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getAverageSightingsPerDay() {
        String query = "SELECT DATE(sightedAt) AS sighted, COUNT(*)/2 AS total FROM " + tablePrefix + "sightings GROUP BY DATE(sightedAt)";
        StringBuilder csv = new StringBuilder("Date,Average Sightings\n");
        int totalTeams = getTotalTeams();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                csv.append(resultSet.getString("sighted"));
                csv.append(",");
                int totalSightings = resultSet.getInt("total");
                csv.append(totalSightings / totalTeams);
                csv.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return csv.toString();
    }

    private static int getTotalTeams() {
        String query = "SELECT COUNT(*) AS total FROM " + tablePrefix + "teams";
        int teams = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                teams = resultSet.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
}
