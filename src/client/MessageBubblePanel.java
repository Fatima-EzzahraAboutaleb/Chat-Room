package client;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MessageBubblePanel extends JPanel {
    
    // Color scheme
    private static final Color MY_MESSAGE_BG = new Color(244, 143, 177);      // Pink
    private static final Color OTHER_MESSAGE_BG = new Color(255, 255, 255);   // White
    private static final Color MY_MESSAGE_TEXT = Color.WHITE;
    private static final Color OTHER_MESSAGE_TEXT = new Color(33, 33, 33);    // Dark gray
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color TIMESTAMP_COLOR = new Color(120, 120, 120);
    
    private String message;
    private String username;
    private String timestamp;
    private boolean isCurrentUser;
    private long creationTime;
    private int alpha = 0; 
    private static final int ANIMATION_DURATION = 500; 
    public MessageBubblePanel(String message, String username, String timestamp, boolean isCurrentUser) {
        this.message = message;
        this.username = username;
        this.timestamp = formatTimestamp(timestamp);
        this.isCurrentUser = isCurrentUser;
        this.creationTime = System.currentTimeMillis();
        
        setOpaque(false);
        setPreferredSize(new Dimension(400, calculateHeight()));
        
        Timer animationTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - creationTime;
            if (elapsed < ANIMATION_DURATION) {
                alpha = (int) ((elapsed / (double) ANIMATION_DURATION) * 255);
            } else {
                alpha = 255;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        animationTimer.start();
    }
    
   
    private String formatTimestamp(String storedTimestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM HH:mm");
            Date date = inputFormat.parse(storedTimestamp);
            return outputFormat.format(date);
        } catch (Exception e) {
            return storedTimestamp;
        }
    }
    
    private int calculateHeight() {
        int lines = Math.max(1, (int) Math.ceil((double) message.length() / 45));
        return lines * 22 + 60; 
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int padding = 12;
        int cornerRadius = 15;
        int maxBubbleWidth = (int) (width * 0.7);
        
        Color bgColor = isCurrentUser ? MY_MESSAGE_BG : OTHER_MESSAGE_BG;
        Color textColor = isCurrentUser ? MY_MESSAGE_TEXT : OTHER_MESSAGE_TEXT;
        
        if (alpha < 255) {
            bgColor = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), alpha);
        }
        
        int bubbleHeight = height - padding - 20;
        int bubbleX, bubbleWidth;
        
        if (isCurrentUser) {
            // Right-aligned for current user
            bubbleWidth = Math.min(maxBubbleWidth, (int) (width * 0.65));
            bubbleX = width - bubbleWidth - 20;
        } else {
            // Left-aligned for other users
            bubbleWidth = Math.min(maxBubbleWidth, (int) (width * 0.65));
            bubbleX = 20;
        }
        
        g2d.setColor(bgColor);
        g2d.fillRoundRect(bubbleX, padding, bubbleWidth, bubbleHeight, cornerRadius, cornerRadius);
        
        // Draw border
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRoundRect(bubbleX, padding, bubbleWidth, bubbleHeight, cornerRadius, cornerRadius);
        
        // Draw username and message text
        int textX = bubbleX + 12;
        int textY = padding + 18;
        int maxTextWidth = bubbleWidth - 24;
        
        // Draw username
        g2d.setColor(textColor);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString(username, textX, textY);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textY += 18;
        
        String[] words = message.split(" ");
        StringBuilder line = new StringBuilder();
        FontMetrics metrics = g2d.getFontMetrics();
        
        for (String word : words) {
            String testLine = line.toString() + (line.length() == 0 ? "" : " ") + word;
            int lineWidth = metrics.stringWidth(testLine);
            
            if (lineWidth > maxTextWidth && line.length() > 0) {
                g2d.drawString(line.toString(), textX, textY);
                textY += 18;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        if (line.length() > 0) {
            g2d.drawString(line.toString(), textX, textY);
            textY += 18;
        }
        
        g2d.setColor(TIMESTAMP_COLOR);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.drawString(timestamp, textX, textY + 2);
    }
}
