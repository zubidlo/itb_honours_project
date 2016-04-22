package models.utils;

import org.junit.Assert;
import org.junit.Test;
import models.utils.*;

public class HashTest {

    @Test
    public void getHashString() throws AppException {
        
    	String password = Hash.createPassword("hello world");
        Assert.assertNotNull(password);

        boolean matches = Hash.checkPassword("hello world", password);
        Assert.assertTrue(matches);

        boolean badPassword = Hash.checkPassword("some other password", password);
        Assert.assertFalse(badPassword);
    }
}
