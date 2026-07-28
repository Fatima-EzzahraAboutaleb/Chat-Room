package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import shared.Message;
import com.google.gson.Gson;


public class ChatServer extends WebSocketServer {
    
    private static final CopyOnWriteArraySet<WebSocket> clients = new CopyOnWriteArraySet<>();
    
    private static final Map<WebSocket, String> userMap = new HashMap<>();
    
    private static final Gson gson = new Gson();
    
        public ChatServer(int port) {
        super(new InetSocketAddress(port));
    }
    
    @Override
    public void onStart() {
        System.out.println("Chat Server started on port: " + getPort());
        System.out.println("Waiting for client connections...");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New client connected from: " + conn.getRemoteSocketAddress());
        clients.add(conn);
    }
    
  
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            Message msg = gson.fromJson(message, Message.class);
            
            System.out.println("Received [" + msg.getType() + "] from " + conn.getRemoteSocketAddress());
            
            switch (msg.getType()) {
                case LOGIN:
                    handleLogin(conn, msg);
                    break;
                case REGISTER:
                    handleRegister(conn, msg);
                    break;
                case MESSAGE:
                    handleChatMessage(conn, msg);
                    break;
                default:
                    System.out.println("Unknown message type: " + msg.getType());
            }
            
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
   
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = userMap.remove(conn);
        clients.remove(conn);
        
        if (username != null) {
            System.out.println("User disconnected: " + username);
            
            Message userLeftMsg = new Message(Message.MessageType.USER_LEFT, "System", username + " left the chat");
            broadcastMessage(userLeftMsg);
            
            // Broadcast updated user status
            Message statusMsg = new Message(Message.MessageType.USER_STATUS, "System", username + ":offline");
            broadcastMessage(statusMsg);
            
            // Broadcast updated user list
            broadcastUserList();
        }
    }
    
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error: " + ex.getMessage());
        ex.printStackTrace();
    }
    
   
    private void handleLogin(WebSocket conn, Message msg) {
        String username = msg.getUsername();
        String passwordHash = msg.getContent();
        
        if (DatabaseManager.verifyUser(username, passwordHash)) {
            userMap.put(conn, username);
            
            Message response = new Message(Message.MessageType.SUCCESS, "System", "Login successful");
            conn.send(gson.toJson(response));
            
            broadcastUserList();
            
            Message userJoinedMsg = new Message(Message.MessageType.USER_JOINED, "System", username + " joined the chat");
            broadcastMessage(userJoinedMsg);
            
            Message statusMsg = new Message(Message.MessageType.USER_STATUS, "System", username + ":online");
            broadcastMessage(statusMsg);
            
            String history = DatabaseManager.getRecentMessages(50);
            Message historyMsg = new Message(Message.MessageType.MESSAGE, "System", "History:\n" + history);
            conn.send(gson.toJson(historyMsg));
            
            System.out.println("User logged in: " + username);
        } else {
            Message response = new Message(Message.MessageType.ERROR, "System", "Invalid username or password");
            conn.send(gson.toJson(response));
        }
    }
    
   
    private void handleRegister(WebSocket conn, Message msg) {
        String username = msg.getUsername();
        String passwordHash = msg.getContent();
        
        if (DatabaseManager.registerUser(username, passwordHash)) {
            Message response = new Message(Message.MessageType.SUCCESS, "System", "Registration successful");
            conn.send(gson.toJson(response));
            System.out.println("User registered: " + username);
        } else {
            Message response = new Message(Message.MessageType.ERROR, "System", "Username already exists");
            conn.send(gson.toJson(response));
        }
    }
    
   
    private void handleChatMessage(WebSocket conn, Message msg) {
        String username = userMap.get(conn);
        
        if (username == null) {
            Message response = new Message(Message.MessageType.ERROR, "System", "Not logged in");
            conn.send(gson.toJson(response));
            return;
        }
        
        DatabaseManager.saveMessage(username, msg.getContent());
        
        msg = new Message(Message.MessageType.MESSAGE, username, msg.getContent());
        
        broadcastMessage(msg);
        
        System.out.println("Message from " + username + ": " + msg.getContent());
    }
    
   
    private void broadcastMessage(Message msg) {
        String jsonMessage = gson.toJson(msg);
        
        for (WebSocket client : clients) {
            client.send(jsonMessage);
        }
    }
    
    
    private void broadcastUserList() {
        StringBuilder userList = new StringBuilder();
        Collection<String> onlineUserNames = userMap.values();

        for (String username : onlineUserNames) {
            if (userList.length() > 0) {
                userList.append(", ");
            }
            userList.append(username);
        }

        if (userList.length() > 0) {
            Message userListMsg =
                new Message(Message.MessageType.USER_LIST, "System", userList.toString());
            broadcastMessage(userListMsg);
        }
    }

    
    public static void main(String[] args) {
        int port = 9001; // port du serveur
        ChatServer server = new ChatServer(port);

        try {
            // Démarrer le serveur
            server.start();

            System.out.println("Chat Server started on port: " + port);
            System.out.println("Waiting for client connections...");
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
