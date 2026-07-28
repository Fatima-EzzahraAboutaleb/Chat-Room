package shared;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        LOGIN,           // User trying to log in
        REGISTER,        // User trying to register
        MESSAGE,         // Regular chat message
        USER_JOINED,     // User joined notification
        USER_LEFT,       // User left notification
        ERROR,           // Error message
        SUCCESS,         // Success confirmation
        USER_LIST,       // List of online users
        USER_STATUS      // User online/offline status
    }
    
    private MessageType type;
    private String username;
    private String content;
    private String timestamp;
    private int chatId;
    private boolean isCurrentUser;
    
    public Message(MessageType type, String username, String content) {
        this.type = type;
        this.username = username;
        this.content = content;
        this.chatId = 1; 
        this.isCurrentUser = false;
        this.timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }
    
    public Message(MessageType type, String username, String content, int chatId) {
        this(type, username, content);
        this.chatId = chatId;
    }
    
    public MessageType getType() { return type; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public int getChatId() { return chatId; }
    public boolean isCurrentUser() { return isCurrentUser; }
    
    public void setCurrentUser(boolean isCurrentUser) { this.isCurrentUser = isCurrentUser; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp, username, content);
    }
}
