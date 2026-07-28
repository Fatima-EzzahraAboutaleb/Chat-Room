package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.util.Arrays;


public class LoginFrame extends JFrame {
    
    private static final Color PRIMARY_COLOR = new Color(244, 143, 177);      // Pink
    private static final Color SECONDARY_COLOR = new Color(206, 147, 216);    // Light purple
    private static final Color TEXT_COLOR = new Color(33, 33, 33);            // Dark gray
    private static final Color LIGHT_BG = new Color(255, 255, 255);           // White
    private static final Color ACCENT_BG = new Color(245, 245, 245);          // Light gray
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;
    
    private LoginCallback callback;
    
    public interface LoginCallback {
        void onLoginSuccess(String username);
    }
    
    public LoginFrame(LoginCallback callback) {
        this.callback = callback;
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Chat Room");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);
        
        // Header panel with title and gradient
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Chat Room");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setBackground(LIGHT_BG);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Login tab
        JPanel loginPanel = createLoginPanel();
        tabbedPane.addTab("Login", loginPanel);
        
        // Register tab
        JPanel registerPanel = createRegisterPanel();
        tabbedPane.addTab("Register", registerPanel);
        
        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(ACCENT_BG);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Username field
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel, gbc);
        
        gbc.gridy = 1;
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(0, 40));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(usernameField, gbc);
        
        // Password field
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(TEXT_COLOR);
        panel.add(passwordLabel, gbc);
        
        gbc.gridy = 3;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(0, 40));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(passwordField, gbc);
        
        // Login button
        gbc.gridy = 4;
        gbc.insets = new Insets(25, 0, 12, 0);
        loginButton = createStyledButton("Login", PRIMARY_COLOR);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        panel.add(loginButton, gbc);
        
        return panel;
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Username field
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel, gbc);
        
        gbc.gridy = 1;
        JTextField regUsernameField = new JTextField();
        regUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regUsernameField.setPreferredSize(new Dimension(0, 40));
        regUsernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(regUsernameField, gbc);
        
        // Password field
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(TEXT_COLOR);
        panel.add(passwordLabel, gbc);
        
        gbc.gridy = 3;
        JPasswordField regPasswordField = new JPasswordField();
        regPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        regPasswordField.setPreferredSize(new Dimension(0, 40));
        regPasswordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(regPasswordField, gbc);
        
        // Register button
        gbc.gridy = 4;
        gbc.insets = new Insets(25, 0, 12, 0);
        registerButton = createStyledButton("Create Account", SECONDARY_COLOR);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister(regUsernameField.getText().trim(), regPasswordField.getPassword());
            }
        });
        panel.add(registerButton, gbc);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Darken button on hover
                if (bgColor.equals(PRIMARY_COLOR)) {
                    button.setBackground(new Color(220, 100, 150)); // Darker pink
                } else {
                    button.setBackground(new Color(180, 120, 200)); // Darker purple
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Restore original color
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        
        if (username.isEmpty() || password.length == 0) {
            statusLabel.setText("Please enter username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        String passwordHash = hashPassword(password);
        Arrays.fill(password, ' ');
        
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(new Color(100, 100, 100));
        
        ChatClient.getInstance().loginWithWebSocket(username, passwordHash, new ChatClient.LoginListener() {
            @Override
            public void onLoginSuccess(String user) {
                statusLabel.setText("Login successful!");
                statusLabel.setForeground(new Color(76, 175, 80));
                callback.onLoginSuccess(user);
                dispose();
            }
            
            @Override
            public void onLoginFailure(String reason) {
                statusLabel.setText(reason);
                statusLabel.setForeground(Color.RED);
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
            }
        });
    }
    
    private void handleRegister(String username, char[] password) {
        if (username.isEmpty() || password.length == 0) {
            statusLabel.setText("Please enter username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (username.length() < 3) {
            statusLabel.setText("Username must be at least 3 characters");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (password.length < 6) {
            statusLabel.setText("Password must be at least 6 characters");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        String passwordHash = hashPassword(password);
        Arrays.fill(password, ' ');
        
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
        statusLabel.setText("Registering...");
        statusLabel.setForeground(new Color(100, 100, 100));
        
        ChatClient.getInstance().registerWithWebSocket(username, passwordHash, new ChatClient.RegisterListener() {
            @Override
            public void onRegisterSuccess(String user) {
                statusLabel.setText("Registration successful! Switch to Login tab");
                statusLabel.setForeground(new Color(76, 175, 80));
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
                tabbedPane.setSelectedIndex(0); 
            }
            
            @Override
            public void onRegisterFailure(String reason) {
                statusLabel.setText(reason);
                statusLabel.setForeground(Color.RED);
                loginButton.setEnabled(true);
                registerButton.setEnabled(true);
            }
        });
    }
    
    private String hashPassword(char[] password) {
        try {
            String passwordString = new String(password);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passwordString.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return "";
        }
    }
}
