package util;

import model.Payload;
import model.Positions;
import model.Teams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static java.sql.DriverManager.getConnection;
import static util.ConfigHelper.getConfig;

/**
 * With thanks to https://crunchify.com/java-mysql-jdbc-hello-world-tutorial-create-connection-insert-data-and-retrieve-data-from-mysql/
 */
public class MySQLHelper {

    private static Connection connection = null;

    public static void populateDatabase(Payload payload) {
        connectToDatabase();
        if(tableExists("POSITIONS")) {
            dropTable("POSITIONS");
        }
        if(tableExists("TEAMS")) {
            dropTable("TEAMS");
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
        System.out.println("Create TEAMS table");
        String query = "CREATE TABLE TEAMS (name VARCHAR(64), marker INT, serial INT, PRIMARY KEY (serial))";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createPositionsTable() {
        System.out.println("Create POSITIONS table");
        String query = "CREATE TABLE POSITIONS ( id INT, serial INT, latitude DOUBLE(8,5), longitude DOUBLE(8,5), PRIMARY KEY (id),"
                + "FOREIGN KEY (serial) REFERENCES TEAMS(serial))";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * With thanks to https://stackoverflow.com/a/4355097/5603509
     */
    private static void populateTeams(List<Teams> teams) {
        System.out.println("Populating TEAMS table");
        String query = "INSERT INTO TEAMS VALUES (?, ?, ?)";
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
        }
    }

    private static void populatePositions(List<Teams> teams) {
        System.out.println("Populating POSITIONS table");
        String query = "INSERT INTO POSITIONS VALUES (?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for (Teams team : teams) {
                System.out.println("Populating positions for team " + team.getName());
                int i = 0;
                int serial = team.getSerial();
                List<Positions> positions = team.getPositions();
                for (Positions position : positions) {
                    preparedStatement.setInt(1, position.getId());
                    preparedStatement.setInt(2, serial);
                    preparedStatement.setDouble(3, position.getLatitude());
                    preparedStatement.setDouble(4, position.getLongitude());
                    preparedStatement.addBatch();
                    i++;
                    if (i % 1000 == 0 || i == positions.size()) {
                        preparedStatement.executeBatch();
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            System.err.println("Failed to write positions to database");
            e.printStackTrace();
        }
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
        }
    }
}
