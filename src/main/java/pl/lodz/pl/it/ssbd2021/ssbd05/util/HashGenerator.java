package pl.lodz.pl.it.ssbd2021.ssbd05.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

public class HashGenerator {

    private HashGenerator() {
    }

    public static String generateHash(String value) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(value, salt);
    }

    public static boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    public static String generateSecureRandomToken() {
        var secureRandom = new SecureRandom();
        float token = secureRandom.nextFloat();
        String tokenString = Float.toString(token);
        return Base64.getEncoder().encodeToString(tokenString.getBytes());
    }
}
