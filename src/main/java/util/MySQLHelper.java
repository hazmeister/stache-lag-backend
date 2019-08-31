package util;

import model.Payload;
import model.Positions;
import model.Teams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.sql.DriverManager.getConnection;
import static util.ConfigHelper.getConfig;

/**
 * With thanks to https://crunchify.com/java-mysql-jdbc-hello-world-tutorial-create-connection-insert-data-and-retrieve-data-from-mysql/
 */
public class MySQLHelper {

    private static Connection connection = null;
    private static String tablePrefix = "";

    public static void populateDatabase(Payload payload) {
        tablePrefix = payload.getRaceUrl() + "_";
        String positionsTable = tablePrefix + "positions_";
        String teamsTable = tablePrefix + "teams";

        connectToDatabase();
        if(tableExists(positionsTable)) {
            dropTable(positionsTable);
        }
        if(tableExists(teamsTable)) {
            dropTable(teamsTable);
        }

        createTeamsTable();
        populateTeams(payload.getTeams());

        createPositionsTable();
        populatePositions(payload.getTeams());
    }

    private static void connectToDatabase() {
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
        if(connection != null) {
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
                if(match.contentEquals(table)) {
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

    /**
     * With thanks to https://stackoverflow.com/a/4355097/5603509
     */
    private static void populateTeams(List<Teams> teams) {
        System.out.println("Populating teams table");
        String query = "INSERT INTO " + tablePrefix + "teams VALUES (" + parameterPlaceholder(3)+ ")";
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
        String query = "INSERT INTO " + tablePrefix +"positions VALUES (" + parameterPlaceholder(15)+ ")";
        try{
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
}
