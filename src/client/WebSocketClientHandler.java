package client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;


public class WebSocketClientHandler extends WebSocketClient {
    
    private String username;
    private ChatClient chatClient;
    private Set<String> processedMessages; 
    
    private static final String SERVER_URI = "ws://localhost:9001";
    
    public WebSocketClientHandler(String username, ChatClient chatClient) {
        super(URI.create(SERVER_URI));
        this.username = username;
        this.chatClient = chatClient;
        this.processedMessages = new HashSet<>();
    }
    
    public String getUsername() {
        return username;
    }
    
 
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("[WebSocket] Connected to " + SERVER_URI);
    }
 
    @Override
    public void onMessage(String message) {
        System.out.println("[WebSocket] Received: " + message);
        
        String messageId = generateMessageHash(message);
        
        synchronized (processedMessages) {
            if (processedMessages.contains(messageId)) {
                System.out.println("[WebSocket] Duplicate message ignored: " + messageId);
                return;
            }
            processedMessages.add(messageId);
        }
        
        chatClient.handleServerMessage(message);
    }
    
 
    private String generateMessageHash(String message) {
        try {
          
            return String.valueOf(message.hashCode());
        } catch (Exception e) {
            return String.valueOf(message.hashCode());
        }
    }
    
   
    @Override
    public void onClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "client";
        System.out.println("[WebSocket] Connection closed by " + closedBy + " (code: " + code + "): " + reason);
    }
    
   
    @Override
    public void onError(Exception ex) {
        System.err.println("[WebSocket] Error: " + ex.getMessage());
        if (ex.getCause() != null) {
            System.err.println("[WebSocket] Cause: " + ex.getCause().getMessage());
        }
    }
    
  
    public void clearMessageCache() {
        synchronized (processedMessages) {
            processedMessages.clear();
        }
    }
    
   
    public int getProcessedMessageCount() {
        synchronized (processedMessages) {
            return processedMessages.size();
        }
    }
}
