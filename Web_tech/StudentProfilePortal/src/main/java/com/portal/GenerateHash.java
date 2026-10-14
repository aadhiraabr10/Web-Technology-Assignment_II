package com.portal;import org.mindrot.jbcrypt.BCrypt;public class GenerateHash {    public static void main(String[] args) {
        String[] plaintextPasswords = {
            "admin123",
            "teach456",
            "newpassword"   
        };        System.out.println("=== BCrypt Hash Generator ===\n");        for (String pwd : plaintextPasswords) {
            String hash = BCrypt.hashpw(pwd, BCrypt.gensalt(10));
            System.out.printf("%-20s → %s%n", pwd, hash);
        }        System.out.println("\nPaste the hash values into student_portal_setup.sql");
    }
}
