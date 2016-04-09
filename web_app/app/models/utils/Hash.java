package models.utils;

import org.mindrot.jbcrypt.BCrypt;

public final class Hash {

    public static String createPassword(String clearString) throws AppException {
        if (clearString == null) throw new AppException("No password defined!");
        return BCrypt.hashpw(clearString, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidate, String encryptedPassword) {
        if (candidate == null) return false;
        if (encryptedPassword == null) return false;
        return BCrypt.checkpw(candidate, encryptedPassword);
    }
}
