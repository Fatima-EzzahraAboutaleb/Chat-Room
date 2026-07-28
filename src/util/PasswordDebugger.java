package util;

import java.security.MessageDigest;


public class PasswordDebugger {
    
  
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return "";
        }
    }
    
    public static void main(String[] args) {
        
        String testPassword = "password123";
        
        System.out.println("Testing with password: " + testPassword);
        String hash1 = hashPassword(testPassword);
        String hash2 = hashPassword(testPassword);
        
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println("Hashes match: " + hash1.equals(hash2));
        
      
    }
}
