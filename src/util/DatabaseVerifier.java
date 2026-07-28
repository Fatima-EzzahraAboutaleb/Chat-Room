package util;

import java.sql.*;


public class DatabaseVerifier {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_room_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; 
    
    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL JDBC Driver found\n");
        } catch (ClassNotFoundException e) {
            System.out.println("✗ ERROR: MySQL JDBC Driver not found!");
            System.out.println("  Add mysql-connector-java-*.jar to lib/ folder\n");
            return;
        }
        
        System.out.println("Step 2: Testing database connection...");
        System.out.println("  Connecting to: " + DB_URL);
        System.out.println("  Username: " + DB_USER);
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("✓ Successfully connected to database\n");
            
            
            // Check users table
            if (tableExists(conn, "users")) {
                System.out.println("✓ 'users' table exists");
                printTableStructure(conn, "users");
            } else {
                System.out.println("✗ ERROR: 'users' table not found!");
                System.out.println("  Run: sql/create_tables.sql");
            }
            
            // Check messages table
            if (tableExists(conn, "messages")) {
                System.out.println("\n✓ 'messages' table exists");
                printTableStructure(conn, "messages");
            } else {
                System.out.println("\n✗ ERROR: 'messages' table not found!");
                System.out.println("  Run: sql/create_tables.sql");
            }
            
            System.out.println("  Users registered: " + countRows(conn, "users"));
            System.out.println("  Messages stored: " + countRows(conn, "messages"));
            
            System.out.println("\n✓ Database verification complete!");
            
        } catch (SQLException e) {
            System.out.println("✗ ERROR: Failed to connect to database");
            System.out.println("  Error: " + e.getMessage());
            System.out.println("\n  Make sure:");
            System.out.println("  1. MySQL is running");
            System.out.println("  2. Database 'chat_room_db' exists");
            System.out.println("  3. Username and password are correct in DatabaseManager.java");
        }
    }
    
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + tableName + "'")) {
            return rs.next();
        }
    }
    
    private static void printTableStructure(Connection conn, String tableName) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("DESCRIBE " + tableName)) {
            
            System.out.println("  Columns:");
            while (rs.next()) {
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                System.out.println("    - " + field + " (" + type + ")");
            }
        }
    }
    
    private static int countRows(Connection conn, String tableName) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
