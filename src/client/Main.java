package client;

import javax.swing.SwingUtilities;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame(new LoginFrame.LoginCallback() {
                    @Override
                    public void onLoginSuccess(String username) {
                        ChatClient chatClient = ChatClient.getInstance();
                        ChatFrame chatFrame = new ChatFrame(username, chatClient);
                        chatClient.setChatFrame(chatFrame);
                        chatFrame.setVisible(true);
                    }
                });
                
                loginFrame.setVisible(true);
            }
        });
    }
}
