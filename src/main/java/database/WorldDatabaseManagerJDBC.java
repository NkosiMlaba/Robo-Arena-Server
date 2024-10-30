package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import domain.world.Obstacle;
import domain.world.World;

import java.io.File;

/**
 * Manages the database operations for the Worlds and Obstacles.
 */
public class WorldDatabaseManagerJDBC {
    private String URL;
    private static final String DATABASE_DIR = "database"; // Directory name

    
    /**
     * Default constructor.
     */
    public WorldDatabaseManagerJDBC() {
        setDatabaseName("worlds.db");
    }

    /**
     * Constructs a new instance of WorldDatabaseManager with the specified database name.
     *
     * @param databaseName The name of the database file to use. If the file does not exist, it will be created.
     *                     The database file will be located in the "database" directory.
     */
    public WorldDatabaseManagerJDBC(String databaseName) {
        setDatabaseName(databaseName);
    }
    

    /**
     * Sets the name of the database file and updates the URL for database connections.
     * Ensures that the "database" directory exists before setting the database name.
     *
     * @param databaseName The name of the database file to be used. The file will be
     *                     located in the "database" directory. If the file doesn't
     *                     exist, it will be created when a connection is first
     *                     established.
     */
    public void setDatabaseName(String databaseName) {
        createDatabaseDirectory(); // Ensure the directory exists
        this.URL = "jdbc:sqlite:" + DATABASE_DIR + "/" + databaseName;
    }

    /**
     * Creates the "database" directory if it does not already exist.
     */
    private void createDatabaseDirectory() {
        File directory = new File(DATABASE_DIR);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Database directory created.");
            } else {
                System.out.println("Failed to create database directory.");
            }
        }
    }
    
    /**
     * Initializes the database by creating the necessary tables.
     */
    public void initialiseDatabase() {
        buildWorldsTable();
        buildObstaclesTable();
    }

    /**
     * Creates the Worlds table if it does not already exist.
     */
    private void buildWorldsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Worlds (\n"
                + " name TEXT NOT NULL PRIMARY KEY,\n"
                + " size INTEGER NOT NULL\n"
                + ");";
            
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database and Worlds table created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates the Obstacles table if it does not already exist.
     */
    private void buildObstaclesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Obstacles (\n"
                + " name_world TEXT NOT NULL,\n"
                + " bottomLeftX INTEGER,\n"
                + " bottomLeftY INTEGER,\n"
                + " size_obstacle INTEGER,\n"
                + " FOREIGN KEY (name_world) REFERENCES Worlds(name) ON DELETE CASCADE"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database and Obstacles table created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Stores the world and its obstacles in the database.
     * @param world The world object to store.
     * @throws SQLException if a database access error occurs.
     */
    public void storeWorld(World world) throws SQLException {
        storeWorldParameters(world);
        storeObstacles(world);
    }

    /**
     * Stores the obstacles of a world in the database.
     * @param world The world object containing the obstacles to store.
     */
    public void storeObstacles(World world) {
        String worldSQL2 = "INSERT INTO Obstacles (name_world, bottomLeftX, bottomLeftY, size_obstacle) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement worldStmt = conn.prepareStatement(worldSQL2)) {

            // Set parameters and execute update
            for (Obstacle obstacle : world.getObstacles()) {
                worldStmt.setString(1, world.getWorldName());
                worldStmt.setInt(2, obstacle.getBottomLeftX());
                worldStmt.setInt(3, obstacle.getBottomLeftY());
                worldStmt.setInt(4, obstacle.getSize());
                worldStmt.executeUpdate();
            }
            
            System.out.println("Obstacles stored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the parameters of a world in the database.
     * @param world The world object to store.
     */
    public void storeWorldParameters(World world) {
        String worldSQL = "INSERT INTO Worlds (name, size) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement worldStmt = conn.prepareStatement(worldSQL)) {

            // Set parameters and execute update
            worldStmt.setString(1, world.getWorldName());
            worldStmt.setInt(2, world.getSizeForDatabase());
            worldStmt.executeUpdate();
            
            System.out.println("World stored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the size of a world from the database.
     * @param worldName The name of the world.
     * @return The size of the world, or -1 if not found.
     * @throws SQLException if a database access error occurs.
     */
    public int retrieveWorldSize(String worldName) throws SQLException {
        String worldSQL = "SELECT * FROM Worlds WHERE name = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement worldStmt = conn.prepareStatement(worldSQL)) {
            worldStmt.setString(1, worldName);
            try (ResultSet worldRs = worldStmt.executeQuery()) {
                if (worldRs.next()) {
                    return worldRs.getInt("size");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * Retrieves the obstacles of a world from the database.
     * @param worldName The name of the world.
     * @return A list of obstacles, each represented as a list of integers.
     * @throws SQLException if a database access error occurs.
     */
    public ArrayList<ArrayList<Integer>> retrieveWorldObstacles(String worldName) throws SQLException {
        String worldSQL = "SELECT * FROM Obstacles WHERE name_world = ?";
        ArrayList<ArrayList<Integer>> obstacles = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement worldStmt = conn.prepareStatement(worldSQL)) {
            worldStmt.setString(1, worldName);
            try (ResultSet worldRs = worldStmt.executeQuery()) {
                
                while (worldRs.next()) {
                    ArrayList<Integer> obstaclesMetadata = new ArrayList<>();
                    obstaclesMetadata.add(worldRs.getInt("bottomLeftX"));
                    obstaclesMetadata.add(worldRs.getInt("bottomLeftY"));
                    obstaclesMetadata.add(worldRs.getInt("size_obstacle"));
                    obstacles.add(obstaclesMetadata);
                }
                return obstacles;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Checks if a world with the given name exists in the database.
     * @param worldName The name of the world.
     * @return true if the world exists, false otherwise.
     * @throws SQLException if a database access error occurs.
     * 
     */
    public boolean nameOfWorldExists(String worldName) throws SQLException {
        String worldSQL = "SELECT * FROM Worlds WHERE name = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement worldStmt = conn.prepareStatement(worldSQL)) {
            worldStmt.setString(1, worldName);
            try (ResultSet worldRs = worldStmt.executeQuery()) {
                if (worldRs.next()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieves the names of all worlds from the database.
     * @return A list of world names.
     * @throws SQLException if a database access error occurs.
     */
    public ArrayList<String> retrieveNamesOfWorlds() throws SQLException {
        String worldSQL = "SELECT * FROM Worlds WHERE name IS NOT NULL";
        ArrayList<String> names = new ArrayList<>();
    
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement worldStmt = conn.prepareStatement(worldSQL);
            ResultSet worldRs = worldStmt.executeQuery()) {
            
            // Iterate over the result set and append each name to the names list
            while (worldRs.next()) {
                String name = worldRs.getString("name");
                names.add(name);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return names;
    }

    /**
     * Updates the size of a specific world in the database.
     * @param worldName The name of the world.
     * @param world The world object containing the new size.
     */
    public void updateWorldSize(String worldName, World world) {
        String sql = "UPDATE Worlds SET size = ? WHERE name = ?";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, world.getSizeForDatabase());
            stmt.setString(2, worldName);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("World size updated successfully.");
            } else {
                System.out.println("World not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the obstacles for a specific world in the database.
     * @param worldName The name of the world.
     * @param world The world object containing the new obstacles.
     */
    public void updateWorldObstacles(String worldName, World world) {
        String deleteSql = "DELETE FROM Obstacles WHERE name_world = ?";
        String insertSql = "INSERT INTO Obstacles (name_world, bottomLeftX, bottomLeftY, size_obstacle) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            // Delete existing obstacles
            deleteStmt.setString(1, worldName);
            deleteStmt.executeUpdate();

            // Insert new obstacles
            for (Obstacle obstacle : world.getObstacles()) {
                insertStmt.setString(1, worldName);
                insertStmt.setInt(2, obstacle.getBottomLeftX());
                insertStmt.setInt(3, obstacle.getBottomLeftY());
                insertStmt.setInt(4, obstacle.getSize());
                insertStmt.executeUpdate();
            }
            System.out.println("Obstacles updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
