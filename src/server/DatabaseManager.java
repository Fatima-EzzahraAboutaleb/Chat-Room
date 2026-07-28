package server;

import java.sql.*;
import java.util.*;


public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_room_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add mysql-connector-java to your classpath.");
            e.printStackTrace();
        }
    }
    
    public static boolean registerUser(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean verifyUser(String username, String passwordHash) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                System.out.println("[DEBUG] User '" + username + "' found in database");
                System.out.println("[DEBUG] Hashes match: " + storedHash.equals(passwordHash));
                return storedHash.equals(passwordHash);
            }
            System.out.println("[DEBUG] User '" + username + "' NOT found in database");
            return false;
            
        } catch (SQLException e) {
            System.err.println("Login verification failed: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean saveMessage(String username, String messageText) {
        return saveMessage(username, messageText, 1);
    }
    
    public static boolean saveMessage(String username, String messageText, int chatId) {
        String sql = "INSERT INTO messages (username, message, chat_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, messageText);
            pstmt.setInt(3, chatId);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Message save failed: " + e.getMessage());
            return false;
        }
    }
    
    public static String getRecentMessages(int limit) {
        return getRecentMessages(1, limit);
    }
    
    public static String getRecentMessages(int chatId, int limit) {
        String sql = "SELECT username, message, timestamp FROM messages WHERE chat_id = ? ORDER BY id DESC LIMIT ?";
        StringBuilder messages = new StringBuilder();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, chatId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            List<String> messageList = new ArrayList<>();
            while (rs.next()) {
                String username = rs.getString("username");
                String message = rs.getString("message");
                String timestamp = rs.getString("timestamp");
                messageList.add(timestamp + " | " + username + ": " + message);
            }
            
                        for (int i = messageList.size() - 1; i >= 0; i--) {
                messages.append(messageList.get(i)).append("\n");
            }
            
        } catch (SQLException e) {
            System.err.println("Retrieve messages failed: " + e.getMessage());
        }
        
        return messages.toString();
    }
    
    
       public static int createGroup(String groupName, String createdBy) {
        String sql = "INSERT INTO chats (chat_name, chat_type, created_by) VALUES (?, 'GROUP', ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, groupName);
            pstmt.setString(2, createdBy);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
            
        } catch (SQLException e) {
            System.err.println("Create group failed: " + e.getMessage());
            return -1;
        }
    }
    
    
    public static boolean addUserToGroup(int chatId, String username) {
        String sql = "INSERT INTO chat_members (chat_id, username) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, chatId);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Add user to group failed: " + e.getMessage());
            return false;
        }
    }
    
    
    public static List<String> getAllGroups() {
        String sql = "SELECT id, chat_name FROM chats WHERE chat_type = 'GROUP' ORDER BY created_at DESC";
        List<String> groups = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("chat_name");
                groups.add(id + ":" + name);
            }
            
        } catch (SQLException e) {
            System.err.println("Get groups failed: " + e.getMessage());
        }
        
        return groups;
    }
   
    public static List<String> getGroupMembers(int chatId) {
        String sql = "SELECT username FROM chat_members WHERE chat_id = ? ORDER BY joined_at ASC";
        List<String> members = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, chatId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                members.add(rs.getString("username"));
            }
            
        } catch (SQLException e) {
            System.err.println("Get group members failed: " + e.getMessage());
        }
        
        return members;
    }
}
