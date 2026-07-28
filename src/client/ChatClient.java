package client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import shared.Message;
import com.google.gson.Gson;
import java.net.URI;
import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.JList;


public class ChatClient {
    
    // Singleton instance
    private static ChatClient instance;
    private DefaultListModel<String> userListModel;
    private JList<String> userList;
    private Map<String, Boolean> userStatusMap = new HashMap<>();

    // WebSocket connection
    private WebSocketClientHandler wsHandler;
    
    // Callbacks for login/register 
    private LoginListener loginListener;
    private RegisterListener registerListener;
    
    // Current chat frame for message display
    private ChatFrame chatFrame;
    
    private Set<String> onlineUsers = new HashSet<>();
    
    // JSON r
    private Gson gson = new Gson();
    
    //login callbacks
    public interface LoginListener {
        void onLoginSuccess(String username);
        void onLoginFailure(String reason);
    }
    
    // register callbacks
    public interface RegisterListener {
        void onRegisterSuccess(String username);
        void onRegisterFailure(String reason);
    }
    
    private ChatClient() {
    }
    
    public static synchronized ChatClient getInstance() {
        if (instance == null) {
            instance = new ChatClient();
        }
        return instance;
    }
    
   
    public void loginWithWebSocket(String username, String passwordHash, LoginListener listener) {
        this.loginListener = listener;
        
        try {
            wsHandler = new WebSocketClientHandler(username, this);
            wsHandler.connect();
            
            Thread.sleep(500);
            
            Message loginMsg = new Message(Message.MessageType.LOGIN, username, passwordHash);
            wsHandler.send(gson.toJson(loginMsg));
            
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            if (listener != null) {
                listener.onLoginFailure("Failed to connect to server: " + e.getMessage());
            }
        }
    }
    
    
    public void registerWithWebSocket(String username, String passwordHash, RegisterListener listener) {
        this.registerListener = listener;
        
        try {
            WebSocketClientHandler tempHandler = new WebSocketClientHandler(username, this);
            tempHandler.connect();
            
            Thread.sleep(500);
            
            Message registerMsg = new Message(Message.MessageType.REGISTER, username, passwordHash);
            tempHandler.send(gson.toJson(registerMsg));
            
            wsHandler = tempHandler;
            
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
            if (listener != null) {
                listener.onRegisterFailure("Failed to connect to server: " + e.getMessage());
            }
        }
    }
    
   
    public void sendMessage(String messageText) {
        if (wsHandler != null && wsHandler.isOpen()) {
            Message msg = new Message(Message.MessageType.MESSAGE, "", messageText);
            wsHandler.send(gson.toJson(msg));
        }
    }
    
   
    public void setChatFrame(ChatFrame frame) {
        this.chatFrame = frame;
    }
   
    public void handleServerMessage(String jsonMessage) {
        try {
            Message msg = gson.fromJson(jsonMessage, Message.class);
            
            switch (msg.getType()) {
                case SUCCESS:
                    handleSuccess(msg);
                    break;
                case ERROR:
                    handleError(msg);
                    break;
                case MESSAGE:
                    handleChatMessage(msg);
                    break;
                case USER_JOINED:
                    handleUserJoined(msg);
                    break;
                case USER_LEFT:
                    handleUserLeft(msg);
                    break;
                case USER_LIST:
                    handleUserList(msg);
                    break;
                case USER_STATUS:
                    handleUserStatus(msg);
                    break;
                default:
                    System.out.println("Unknown message type: " + msg.getType());
            }
        } catch (Exception e) {
            System.err.println("Error handling server message: " + e.getMessage());
        }
    }
    
    private void handleSuccess(Message msg) {
        if (loginListener != null) {
            loginListener.onLoginSuccess(wsHandler.getUsername());
            loginListener = null;
        } else if (registerListener != null) {
            registerListener.onRegisterSuccess(wsHandler.getUsername());
            registerListener = null;
        }
    }
    
    private void handleError(Message msg) {
        if (loginListener != null) {
            loginListener.onLoginFailure(msg.getContent());
            loginListener = null;
        } else if (registerListener != null) {
            registerListener.onRegisterFailure(msg.getContent());
            registerListener = null;
        }
    }
    
    private void handleChatMessage(Message msg) {
        if (chatFrame != null) {
            String displayText = msg.getTimestamp() + " | " + msg.getUsername() + ": " + msg.getContent();
            chatFrame.appendMessage(displayText);
        }
    }
    
    private void handleUserJoined(Message msg) {
        String username = msg.getContent();
        onlineUsers.add(username);
        
        if (chatFrame != null) {
            chatFrame.appendMessage("*** " + username + " joined the chat ***");
            chatFrame.addUserToList(username, true);
        }
    }
    
    private void handleUserLeft(Message msg) {
        String username = msg.getContent();
        onlineUsers.remove(username);
        
        if (chatFrame != null) {
            chatFrame.appendMessage("*** " + username + " left the chat ***");
            chatFrame.updateUserStatus(username, false);
        }
    }
    
    private void handleUserList(Message msg) {
        try {
            String[] users = msg.getContent().split(",");
            for (String user : users) {
                String trimmedUser = user.trim();
                onlineUsers.add(trimmedUser);
                if (chatFrame != null) {
                    chatFrame.addUserToList(trimmedUser, true);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing user list: " + e.getMessage());
        }
    }
    
    private void handleUserStatus(Message msg) {
        try {
            String[] parts = msg.getContent().split(":");
            if (parts.length == 2) {
                String username = parts[0].trim();
                boolean isOnline = "online".equalsIgnoreCase(parts[1].trim());
                
                if (isOnline) {
                    onlineUsers.add(username);
                } else {
                    onlineUsers.remove(username);
                }
                
                if (chatFrame != null) {
                    chatFrame.updateUserStatus(username, isOnline);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing user status: " + e.getMessage());
        }
    }
}
