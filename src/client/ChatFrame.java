package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFrame extends JFrame {
    
    private static final Color HEADER_BG = new Color(244, 143, 177);
    private static final Color SIDEBAR_BG = new Color(236, 239, 241);
    private static final Color MESSAGE_BG = new Color(253, 242, 245);
    private static final Color ACCENT = new Color(206, 147, 216);
    
    private JPanel messageContainer;
    private JScrollPane messageScrollPane;
    private JTextField messageInputField;
    private JButton sendButton;
    private JLabel usernameLabel;
    private JLabel statusLabel;
    private JPanel userListPanel;
    private JScrollPane userScrollPane;
    
    private String currentUsername;
    private ChatClient chatClient;
    private List<String> displayedMessages = new ArrayList<>();
    private Map<String, Boolean> userStatusMap = new HashMap<>();
    
    public ChatFrame(String username, ChatClient client) {
        this.currentUsername = username;
        this.chatClient = client;
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Chat Room - " + currentUsername);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Main container
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Content area with sidebar
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        
        // Sidebar with user list
        JPanel sidebarPanel = createSidebarPanel();
        contentPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Chat area
        JPanel chatAreaPanel = createChatAreaPanel();
        contentPanel.add(chatAreaPanel, BorderLayout.CENTER);
        
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(mainContainer);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        usernameLabel = new JLabel("Welcome, " + currentUsername);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        statusLabel = new JLabel("Connected");
        statusLabel.setForeground(new Color(76, 175, 80));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        headerPanel.add(usernameLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(SIDEBAR_BG);
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        
        JLabel usersTitle = new JLabel("Users");
        usersTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        usersTitle.setForeground(new Color(33, 33, 33));
        panel.add(usersTitle, BorderLayout.NORTH);
        
        // User list container with scroll
        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setBackground(SIDEBAR_BG);
        
        userScrollPane = new JScrollPane(userListPanel);
        userScrollPane.setBackground(SIDEBAR_BG);
        userScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        userScrollPane.getVerticalScrollBar().setUnitIncrement(3);
        panel.add(userScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createChatAreaPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(MESSAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
        messageContainer.setBackground(MESSAGE_BG);
        messageContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        messageScrollPane = new JScrollPane(messageContainer);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messageScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(3);
        
        messageScrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar bar = (JScrollBar) e.getSource();
            if (!bar.getModel().getValueIsAdjusting() && 
                bar.getValue() + bar.getVisibleAmount() == bar.getMaximum()) {
                SwingUtilities.invokeLater(() -> {
                    bar.setValue(bar.getMaximum());
                });
            }
        });
        
        panel.add(messageScrollPane, BorderLayout.CENTER);
        
        // Input 
        JPanel inputPanel = createInputPanel();
        panel.add(inputPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        messageInputField = new JTextField();
        messageInputField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageInputField.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        messageInputField.setPreferredSize(new Dimension(0, 40));
        messageInputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    sendMessage();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        sendButton = new JButton("Send");
        sendButton.setBackground(ACCENT);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(80, 40));
        sendButton.setBorder(BorderFactory.createEmptyBorder());
        // Add hover effect
        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(new Color(190, 120, 200));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(ACCENT);
            }
        });
        
        sendButton.addActionListener(e -> sendMessage());
        
        panel.add(messageInputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void sendMessage() {
        String message = messageInputField.getText().trim();
        
        if (message.isEmpty()) {
            return;
        }
        
        chatClient.sendMessage(message);
        messageInputField.setText("");
        messageInputField.requestFocus();
    }
    
   
    public void appendMessage(final String message, final String sender, final String timestamp, final boolean isCurrentUser) {
        SwingUtilities.invokeLater(() -> {
           
            String messageId = sender + "|" + message + "|" + timestamp;
            
           
            if (displayedMessages.contains(messageId)) {
                return;
            }
            
            displayedMessages.add(messageId);
            
            // bubble panel
            MessageBubblePanel bubblePanel = new MessageBubblePanel(message, sender, timestamp, isCurrentUser);
            bubblePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, bubblePanel.getPreferredSize().height));
            bubblePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            messageContainer.add(bubblePanel);
            messageContainer.add(Box.createVerticalStrut(8));
            
            messageContainer.revalidate();
            messageContainer.repaint();
            
            // Auto-scroll to bottom
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = messageScrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });
    }
    
   
    public void appendMessage(final String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("***")) {
                // System message
                JLabel systemLabel = new JLabel(message);
                systemLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                systemLabel.setForeground(new Color(120, 120, 120));
                systemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                systemLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                
                messageContainer.add(systemLabel);
                messageContainer.add(Box.createVerticalStrut(5));
            } else {
                String[] parts = message.split(" \\| ", 2);
                if (parts.length >= 2) {
                    String timestamp = parts[0].trim();
                    String rest = parts[1];
                    
                    int colonIndex = rest.indexOf(": ");
                    if (colonIndex > 0) {
                        String sender = rest.substring(0, colonIndex).trim();
                        String content = rest.substring(colonIndex + 2).trim();
                        boolean isCurrent = sender.equals(currentUsername);
                        
                        appendMessage(content, sender, timestamp, isCurrent);
                        return;
                    }
                }
                
                JLabel fallbackLabel = new JLabel(message);
                fallbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                fallbackLabel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                messageContainer.add(fallbackLabel);
            }
            
            messageContainer.revalidate();
            messageContainer.repaint();
            
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = messageScrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });
    }
    
    public void setStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            statusLabel.setForeground(color);
        });
    }
    
    
    public void addUserToList(String username, boolean isOnline) {
        if (!userStatusMap.containsKey(username)) {
            userStatusMap.put(username, isOnline);
            updateUserListDisplay();
        }
    }
    
   
    public void updateUserStatus(String username, boolean isOnline) {
        userStatusMap.put(username, isOnline);
        updateUserListDisplay();
    }
    
   
    private void updateUserListDisplay() {
        SwingUtilities.invokeLater(() -> {
            userListPanel.removeAll();
            
            List<String> sortedUsers = new ArrayList<>(userStatusMap.keySet());
            sortedUsers.sort((u1, u2) -> {
                boolean online1 = userStatusMap.get(u1);
                boolean online2 = userStatusMap.get(u2);
                
                if (online1 != online2) {
                    return online1 ? -1 : 1; 
                }
                return u1.compareToIgnoreCase(u2);
            });
            
            // Add users 
            for (String user : sortedUsers) {
                boolean isOnline = userStatusMap.get(user);
                JPanel userPanel = createUserPanel(user, isOnline);
                userPanel.setName(user);
                userListPanel.add(userPanel);
            }
            
            userListPanel.revalidate();
            userListPanel.repaint();
        });
    }
    
  
    public void updateUserList(String username, boolean isOnline) {
        updateUserStatus(username, isOnline);
    }
    
  
    private JPanel createUserPanel(String username, boolean isOnline) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(8, 0));
        panel.setBackground(SIDEBAR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        
        // hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(220, 230, 235));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(SIDEBAR_BG);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("[v0] User clicked: " + username);
            }
        });
        
        JPanel indicatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Color dotColor = isOnline ? new Color(76, 175, 80) : new Color(150, 150, 150);
                g.setColor(dotColor);
                g.fillOval(2, 2, 8, 8);
            }
        };
        indicatorPanel.setBackground(SIDEBAR_BG);
        indicatorPanel.setPreferredSize(new Dimension(12, 12));
        
        // Username label
        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(isOnline ? new Color(33, 33, 33) : new Color(150, 150, 150));
        
        panel.add(indicatorPanel, BorderLayout.WEST);
        panel.add(userLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
  
    private class MessageBubblePanel extends JPanel {
        private String message;
        private String sender;
        private String timestamp;
        private boolean isCurrentUser;
        
        public MessageBubblePanel(String message, String sender, String timestamp, boolean isCurrentUser) {
            this.message = message;
            this.sender = sender;
            this.timestamp = timestamp;
            this.isCurrentUser = isCurrentUser;
            
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            JPanel bubbleContainer = new JPanel();
            bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
            bubbleContainer.setOpaque(true);
            bubbleContainer.setBackground(isCurrentUser ? new Color(220, 190, 230) : Color.WHITE);
            bubbleContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            // Sender label (if not current user)
            if (!isCurrentUser) {
                JLabel senderLabel = new JLabel(sender);
                senderLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                senderLabel.setForeground(ACCENT);
                senderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                bubbleContainer.add(senderLabel);
                bubbleContainer.add(Box.createVerticalStrut(3));
            }
            
            // Message content
            JLabel messageLabel = new JLabel("<html><body style='width: 400px'>" + message + "</body></html>");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            bubbleContainer.add(messageLabel);
            
            // Timestamp
            JLabel timeLabel = new JLabel(timestamp);
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            timeLabel.setForeground(new Color(120, 120, 120));
            timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            bubbleContainer.add(Box.createVerticalStrut(3));
            bubbleContainer.add(timeLabel);
            
            // Alignment
            JPanel wrapper = new JPanel(new FlowLayout(isCurrentUser ? FlowLayout.RIGHT : FlowLayout.LEFT));
            wrapper.setOpaque(false);
            wrapper.add(bubbleContainer);
            
            add(wrapper, BorderLayout.CENTER);
        }
    }
}