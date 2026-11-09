package edu.gmu.cs321;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    public static Connection getConnection() throws SQLException {
        Properties props = loadProperties();

        String host = System.getenv().getOrDefault("DB_HOST", props.getProperty("DB_HOST"));
        String port = System.getenv().getOrDefault("DB_PORT", props.getProperty("DB_PORT"));
        String dbName = System.getenv().getOrDefault("DB_NAME", props.getProperty("DB_NAME"));
        String username = System.getenv().getOrDefault("DB_USER", props.getProperty("DB_USER"));
        String password = System.getenv().getOrDefault("DB_PASSWORD", props.getProperty("DB_PASSWORD"));

        validateProperties(host, port, dbName, username, password);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        LOGGER.info("Connecting to DB URL: " + url);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found.", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    public static void initialize() {
        String createRequestTable =
            "CREATE TABLE IF NOT EXISTS g14_OriginalDocumentReturnRequest (" +
            "request_id VARCHAR(100) PRIMARY KEY, " +
            "applicant_id VARCHAR(100), " +
            "applicant_name VARCHAR(200), " +
            "email VARCHAR(200), " +
            "phone VARCHAR(50), " +
            "document_type VARCHAR(100), " +
            "submission_date TIMESTAMP, " +
            "preferred_return_method VARCHAR(100), " +
            "request_reason TEXT, " +
            "status VARCHAR(50), " +
            "current_stage VARCHAR(50))";

        String createWorkflowTable =
            "CREATE TABLE IF NOT EXISTS g14_WorkflowEntry (" +
            "workflow_id VARCHAR(100) PRIMARY KEY, " +
            "request_id VARCHAR(100), " +
            "previous_stage VARCHAR(50), " +
            "current_stage VARCHAR(50), " +
            "action VARCHAR(100), " +
            "updated_by VARCHAR(200), " +
            "updated_at TIMESTAMP)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createRequestTable);
            stmt.executeUpdate(createWorkflowTable);
            LOGGER.info("Tables initialized successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database initialization failed.", e);
            throw new RuntimeException("Database initialization failed.", e);
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseInitializer.class
                .getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                return props; // Container deployments supply configuration through environment variables.
            }
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + PROPERTIES_FILE, e);
        }
    }

    private static void validateProperties(String host, String port, String dbName,
                                           String username, String password) {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing DB_HOST (environment variable or db.properties)");
        }
        if (port == null || port.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing DB_PORT (environment variable or db.properties)");
        }
        if (dbName == null || dbName.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing DB_NAME (environment variable or db.properties)");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing DB_USER (environment variable or db.properties)");
        }
        if (password == null) {
            throw new IllegalArgumentException("Missing DB_PASSWORD (environment variable or db.properties)");
        }
    }
}