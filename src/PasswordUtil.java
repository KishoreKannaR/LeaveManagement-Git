package project2;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hash a password for storing in DB
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Check plain password against hashed one
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
