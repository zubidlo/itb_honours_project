package models.utils;

import org.mindrot.jbcrypt.BCrypt;

public final class Hash {

	//tested
    public static String createPassword(String s) throws AppException {
        if (s != null) return BCrypt.hashpw(s, BCrypt.gensalt());
        throw new AppException("password: null"); 
    }

    //tested
    public static boolean checkPassword(String passwd, String encrypted) {
    	return (passwd == null || encrypted == null) ? false : BCrypt.checkpw(passwd, encrypted);
    }
}
